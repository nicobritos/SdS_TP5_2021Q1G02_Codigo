package ar.edu.itba.sds_2021_q1_g02;

import ar.edu.itba.sds_2021_q1_g02.models.*;
import ar.edu.itba.sds_2021_q1_g02.serializer.Serializable;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class Simulation extends Serializable {
    private final Collection<Zombie> zombieParticles;
    private final Collection<Human> humanParticles;
    private final Collection<Particle> allParticles;
    private final SimulationConfiguration configuration;
    private final Random random;
    private double lastHumanSpawned = Double.NEGATIVE_INFINITY;
    private int totalHumanCount;
    private int totalCount;
    private double maxTime = 100;
    private final TreeMap<Double, Collection<Human>> bittenHumansTime;

    public Simulation(SimulationConfiguration configuration) {
        this.zombieParticles = new LinkedList<>();
        this.humanParticles = new LinkedList<>();
        this.allParticles = new LinkedList<>();
        this.configuration = configuration;
        this.random = new Random();
        this.bittenHumansTime = new TreeMap<>(Double::compare);
    }

    public void simulate() {
        this.serializeSystem(this.allParticles, this.configuration);
        Step step = this.calculateFirstStep();
        this.serialize(this.allParticles, step);

        while (step.getAbsoluteTime().doubleValue() < this.maxTime) {
            step = this.simulateStep(step);
            if (step.getAbsoluteTime().doubleValue() >= this.maxTime)
                step.setLastStep(true);

            this.serialize(this.allParticles, step);
        }
    }

    private Step simulateStep(Step previousStep) {
        Step actualStep = new Step(
                this.configuration.getDtAsBigDecimal(),
                previousStep.getAbsoluteTime().add(this.configuration.getDtAsBigDecimal()),
                previousStep.getStepNumber() + 1
        );

        // Humanos que deberian de convertirse se convierten aca
        this.calculateNewZombies(actualStep.getAbsoluteTime().doubleValue(), this.configuration.getDt());

        // Spawneamos mas humanos si es necesario
        if (this.shouldSpawnHumans(previousStep.getAbsoluteTime().doubleValue())) {
            this.spawnHumans(actualStep.getAbsoluteTime().doubleValue());
            this.lastHumanSpawned = actualStep.getAbsoluteTime().doubleValue();
        }

        // Movemos las particulas por un dt y calculamos nuevas velocidades
        this.moveParticles(this.configuration.getDt());

        // De las colisiones que surgieron por mover las particulas, chequeamos cuales de esas son humanos
        // contra zombies y los anotamos para luego convertirlos en zombies dado un tiempo especificado
        this.calculateBittenZombies(actualStep.getAbsoluteTime().doubleValue());

        return actualStep;
    }

    private Step calculateFirstStep() {
        this.spawnHumans(0);
        this.spawnZombies();
        this.lastHumanSpawned = 0;

        return new Step(
                this.configuration.getDtAsBigDecimal(),
                BigDecimal.ZERO,
                0
        );
    }

    private void calculateNewZombies(double absoluteTime, double dt) {
        double toKey = absoluteTime - this.configuration.getZombieTurnTime();
        if (toKey < 0)
            return;

        Iterator<Map.Entry<Double, Collection<Human>>> iterator =
                this.bittenHumansTime.headMap(toKey, true).entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Double, Collection<Human>> entry = iterator.next();
            entry.getValue().forEach(human -> {
                if (human.hasBeenBitten()) {
                    Zombie zombie = human.convertToZombie();
                    this.humanParticles.remove(human);
                    this.zombieParticles.add(zombie);
                    this.putZombieVelocity(zombie, dt);
                }
            });
            iterator.remove();
        }
    }

    private void moveParticles(double dt) {
        this.moveHumans(dt);
        this.moveZombies(dt);
    }

    private void moveHumans(double dt) {
        Iterator<Human> iterator = this.humanParticles.iterator();
        while (iterator.hasNext()) {
            Human human = iterator.next();
            if (human.hasBeenBitten()) {
                // Si el humano fue mordido por un zombie no lo movemos, sin importar su entorno.
                continue;
            }
            // Movemos particula
            human.setPosition(Contractile.calculatePosition(human.getPosition(), human.getVelocity(), dt));
            // TODO: Poner humanParticles y cambiar el endPosition por un target que escape los zombies
            // Calculamos nueva velocidad y radio
            this.putHumanVelocity(human, dt);
            if (this.hasReachedDoor(human)) {
                iterator.remove();
                this.allParticles.remove(human);
            }
        }
    }

    private void moveZombies(double dt) {
        for (Zombie zombie : this.zombieParticles) {
            // Movemos particula
            zombie.setPosition(Contractile.calculatePosition(zombie.getPosition(), zombie.getVelocity(), dt));

            // Calculamos nueva velocidad y radio
            this.putZombieVelocity(zombie, dt);
        }
    }

    private void putHumanVelocity(Human human, double dt) {
        Position exitDoorPosition = this.getHumanExitDoorPosition(human.getPosition(),
                human.getRadius().getMaxRadius());
        List<Particle> neighbors = this.computeNeighbors(human.getPosition(), exitDoorPosition,
                this.allParticles);
        List<Particle> inContactParticles = this.isInContact(neighbors, human);
        human.getRadius().setCurrentRadius(Contractile.calculateRadius(human.getRadius(), dt, 0.5,
                !inContactParticles.isEmpty()));
        human.setVelocity(Contractile.calculateVelocity(
                human.getPosition(),
                !inContactParticles.isEmpty() ? inContactParticles : neighbors,
                exitDoorPosition,
                this.configuration.getParticleConfiguration().getVh(),
                human.getRadius(),
                this.configuration.getParticleConfiguration().getBeta(),
                !inContactParticles.isEmpty()
        ));
    }

    private void putZombieVelocity(Zombie zombie, double dt) {
        final Position humanTargetPosition = this.getNearestHumanPosition(zombie);
        List<Particle> neighbors = this.computeNeighbors(zombie.getPosition(), humanTargetPosition,
                this.zombieParticles);
        List<Particle> inContactParticles = this.isInContact(neighbors, zombie);

        zombie.getRadius().setCurrentRadius(Contractile.calculateRadius(zombie.getRadius(), dt, 0.5,
                !inContactParticles.isEmpty()));
        zombie.setVelocity(Contractile.calculateVelocity(
                zombie.getPosition(),
                !inContactParticles.isEmpty() ? inContactParticles : neighbors,
                humanTargetPosition,
                this.configuration.getParticleConfiguration().getVz(),
                zombie.getRadius(),
                this.configuration.getParticleConfiguration().getBeta(),
                !inContactParticles.isEmpty()
        ));
    }

    private boolean shouldSpawnHumans(double absTime) {
        return this.totalHumanCount < this.configuration.getMaxHumans() && absTime - this.configuration.getSpawnHumansEvery() > this.lastHumanSpawned;
    }

    private void spawnHumans(double absoluteTime) {
        int max = this.configuration.getHumansPerSpawn() + this.totalHumanCount > this.configuration.getMaxHumans()
                ? (this.configuration.getMaxHumans() - this.totalHumanCount)
                : this.configuration.getHumansPerSpawn();

        for (int i = 0; i < max; i++) {
            Human human = this.spawnHuman(this.getHumanStartingPosition());

            this.allParticles.add(human);
            this.humanParticles.add(human);
            this.totalHumanCount++;
            this.totalCount++;
        }

        this.lastHumanSpawned = absoluteTime;
    }

    private Position getHumanStartingPosition() {
        double minY =
                this.configuration.getBounds().getDoorsStartY() + this.configuration.getParticleConfiguration().getMaxRadius();
        double maxY =
                this.configuration.getBounds().getDoorsEndY() - this.configuration.getParticleConfiguration().getMaxRadius();
        double maxX = this.configuration.getBounds().getWidth() - this.configuration.getBounds().getZombieBoundWidth();
        double diameter = this.configuration.getParticleConfiguration().getMaxRadius() * 2;

        double x = this.configuration.getParticleConfiguration().getMaxRadius();
        double y = minY;

        boolean positionTaken = !this.allParticles.isEmpty();
        while (positionTaken) {
            positionTaken = this.isPositionTaken(x, y);
            if (!positionTaken)
                break;

            if (y + diameter >= maxY) {
                if (minY - diameter > 0) {
                    y = minY -= diameter;
                    maxY += diameter;
                } else {
                    y = minY;
                }
                x += diameter;

                if (x >= maxX) {
                    throw new IllegalStateException("No hay mas espacio para meter humanos!");
                }
            } else {
                y += diameter;
            }
        }

        return new Position(x, y);
    }

    private void spawnZombies() {
        int max = this.configuration.getMaxZombies();
        List<Position> startingPositions = this.getZombieStartingPositions();
        if (startingPositions.size() < max)
            throw new RuntimeException("No hay lugar para zombies! Se necesitan " + max + " posiciones y hay " +
                    "solamente " + startingPositions.size());

        for (int i = 0; i < max; i++) {
            Position position = startingPositions.remove(this.random.nextInt(startingPositions.size()));
            Zombie zombie = this.spawnZombie(position);

            this.allParticles.add(zombie);
            this.zombieParticles.add(zombie);
            this.totalHumanCount++;
            this.totalCount++;
        }
    }

    private List<Position> getZombieStartingPositions() {
        double minY =
                this.configuration.getBounds().getDoorsStartY() + this.configuration.getParticleConfiguration().getMaxRadius();
        double maxY =
                this.configuration.getBounds().getDoorsEndY() - this.configuration.getParticleConfiguration().getMaxRadius();
        double maxX = this.configuration.getBounds().getWidth();
        double diameter = this.configuration.getParticleConfiguration().getMaxRadius() * 2;

        double x = this.configuration.getBounds().getWidth() - this.configuration.getBounds().getZombieBoundWidth()
                + this.configuration.getParticleConfiguration().getMaxRadius();

        List<Position> positions = new LinkedList<>();
        while (x < maxX) {
            double y = minY;

            while (y < maxY) {
                positions.add(new Position(x, y));

                y += diameter;
            }

            x += diameter;
        }

        return positions;
    }

    private Human spawnHuman(Position position) {
        Radius radius = this.getStartingRadius();

        return new Human(
                this.totalCount + 1,
                radius,
                position,
                Contractile.calculateVelocity(
                        position,
                        Collections.emptyList(),
                        this.getHumanExitDoorPosition(position, radius.getMaxRadius()),
                        this.configuration.getParticleConfiguration().getVh(),
                        radius,
                        this.configuration.getParticleConfiguration().getBeta(),
                        false
                )
        );
    }

    private Zombie spawnZombie(Position position) {
        Radius radius = this.getStartingRadius();

        return new Zombie(
                this.totalCount + 1,
                radius,
                position,
                Contractile.calculateVelocity(
                        position,
                        Collections.emptyList(),
                        position,
                        this.configuration.getParticleConfiguration().getVz(),
                        radius,
                        this.configuration.getParticleConfiguration().getBeta(),
                        false
                )
        );
    }

    private Position getHumanExitDoorPosition(Position humanPosition, double maxRadius) {
        double doorStartY = this.configuration.getBounds().getDoorsStartY() + maxRadius;
        double doorEndY = this.configuration.getBounds().getDoorsEndY() - maxRadius;
        double y;
        if (humanPosition.getY() >= doorStartY && humanPosition.getY() <= doorEndY) {
            y = humanPosition.getY();
        } else if (humanPosition.getY() > doorEndY) {
            y = doorEndY;
        } else {
            y = doorStartY;
        }

        return new Position(
                this.configuration.getBounds().getWidth(),
                y
        );
    }

    private boolean isPositionTaken(double x, double y) {
        for (Particle particle : this.allParticles) {
            if (Simulation.isParticleIn(particle, x, y)) {
                return true;
            }
        }
        return false;
    }

    private List<Particle> isInContact(Collection<? extends Particle> neighbors, Particle particle) {
        return neighbors.stream().filter(p -> !p.equals(particle) && p.isInContact(particle)).collect(Collectors.toList());
    }

    private List<Particle> computeNeighbors(Position position, Position targetPosition,
                                            Collection<? extends Particle> particles) {
        final double m_y = targetPosition.getY() - position.getY();
        final double m_x = targetPosition.getX() - position.getX();
        final double m = m_y / m_x;
        final double p_m = -1 / m;
        final double b = position.getY() - p_m * position.getX();
        List<Particle> validParticles = new ArrayList<>();
        for (Particle particle : particles) {
            final Position pos = particle.getPosition();
            if (Double.isInfinite(p_m)) {
                // Es una recta vertical
                if (pos.getX() >= position.getX()) validParticles.add(particle);
            } else {
                final double y = p_m * pos.getX() + b;
                if (pos.getY() >= y) validParticles.add(particle);
            }
        }
        return validParticles;
    }

    private Position getNearestHumanPosition(Particle zombie) {
        double minDistance = Double.POSITIVE_INFINITY;
        Particle nearestHuman = null;

        for (Particle human : this.humanParticles) {
            double distance = human.distanceTo(zombie);
            if (distance <= this.configuration.getParticleConfiguration().getZombieFOV() && distance < minDistance) {
                nearestHuman = human;
                minDistance = distance;
            }
        }

        return nearestHuman == null ? zombie.getPosition() : nearestHuman.getPosition();
    }

    private boolean hasReachedDoor(Human human) {
        // Consideramos que al menos mitad del humano tiene que estar por fuera (por eso x >= width)
        return human.getPosition().getX() >= this.configuration.getBounds().getWidth()
                && human.getPosition().getY() >= this.configuration.getBounds().getDoorsStartY() + human.getRadius().getCurrentRadius()
                && human.getPosition().getY() <= this.configuration.getBounds().getDoorsEndY() - human.getRadius().getCurrentRadius();
    }

    private void calculateBittenZombies(double absoluteTime) {
        this.humanParticles
                .stream()
                .filter(human -> !this.isInContact(this.zombieParticles, human).isEmpty() && !human.hasBeenBitten())
                .forEach(human -> {
                    human.bite();
                    this.bittenHumansTime.computeIfAbsent(absoluteTime, c -> new LinkedList<>()).add(human);
                });
    }

    private Radius getStartingRadius() {
        // TODO: Check starting radius
        return new Radius(
                this.configuration.getParticleConfiguration().getMinRadius(),
                this.configuration.getParticleConfiguration().getMaxRadius(),
                this.configuration.getParticleConfiguration().getMaxRadius()
        );
    }

    private static boolean isParticleIn(Particle particle, double x, double y) {
        return (particle.getPosition().getX() - particle.getRadius().getCurrentRadius() <= x && x <= particle.getPosition().getX() + particle.getRadius().getCurrentRadius()) && (particle.getPosition().getY() - particle.getRadius().getCurrentRadius() <= y && y <= particle.getPosition().getY() + particle.getRadius().getCurrentRadius());
    }
}
