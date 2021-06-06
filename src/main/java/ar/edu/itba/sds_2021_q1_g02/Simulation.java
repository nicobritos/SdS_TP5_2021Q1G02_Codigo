package ar.edu.itba.sds_2021_q1_g02;

import ar.edu.itba.sds_2021_q1_g02.models.*;
import ar.edu.itba.sds_2021_q1_g02.serializer.Serializable;
import ar.edu.itba.sds_2021_q1_g02.utils.Vector2DUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Simulation extends Serializable {
    private final Collection<Zombie> zombieParticles;
    private final Collection<Human> healthyHumanParticles;
    private final Collection<Particle> allParticles;
    private final SimulationConfiguration configuration;
    private final Random random;
    private double lastHumanSpawned = Double.NEGATIVE_INFINITY;
    private int totalHumanCount;
    private int totalCount;
    private final TreeMap<Double, Collection<Human>> bittenHumansTime;

    public Simulation(SimulationConfiguration configuration) {
        this.zombieParticles = new HashSet<>();
        this.healthyHumanParticles = new HashSet<>();
        this.allParticles = new HashSet<>();
        this.configuration = configuration;
        this.random = new Random();
        this.bittenHumansTime = new TreeMap<>(Double::compare);
    }

    public void simulate() {
        this.serializeSystem(this.allParticles, this.configuration);
        Step step = this.calculateFirstStep();
        this.serialize(this.allParticles, step);

        while (!this.healthyHumanParticles.isEmpty()) {
            step = this.simulateStep(step);
            if (this.healthyHumanParticles.isEmpty())
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
                    this.allParticles.remove(human);

                    Zombie zombie = human.toZombie(this.getStartingParticleZone(),
                            this.configuration.getZombieRadius());
                    this.zombieParticles.add(zombie);
                    this.allParticles.add(zombie);
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
        Iterator<Human> iterator = this.healthyHumanParticles.iterator();
        while (iterator.hasNext()) {
            Human human = iterator.next();

            // Movemos particula
            human.setPosition(Contractile.calculatePosition(human.getPosition(), human.getVelocity(), dt));
            // TODO: Poner humanParticles y cambiar el endPosition por un target que escape los zombies
            // Calculamos nueva velocidad y radio
            this.putHumanVelocity(human, dt);

            if (this.hasReachedDoor(human)) {
                iterator.remove();
                this.allParticles.remove(human);
                human.getChasingZombies().forEach(zombie -> zombie.setChasing(null));
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
        Position exitDoorPosition = this.getHumanExitDoorPosition(human.getPosition(), human.getRadius());
        List<Particle> neighbors = this.computeNeighbors(human, exitDoorPosition, this.allParticles);
        List<Particle> inContactParticles = this.getParticlesInContact(neighbors, human);
        List<Position> inContactWalls = this.getNearestPositionOfWallInContact(human);
        human.getParticleZone().setCurrentRadius(Contractile.calculateParticleZoneRadius(human.getParticleZone(), dt,
                0.5,
                !inContactParticles.isEmpty()));
        human.setVelocity(Contractile.calculateVelocity(
                human.getPosition(),
                !inContactParticles.isEmpty() ? inContactParticles : neighbors,
                inContactWalls,
                exitDoorPosition,
                this.configuration.getParticleConfiguration().getVh(),
                human.getParticleZone(),
                human.getRadius(),
                this.configuration.getParticleConfiguration().getBeta(),
                !inContactParticles.isEmpty(),
                !inContactWalls.isEmpty()
        ));
    }

    private void putZombieVelocity(Zombie zombie, double dt) {
        final Human humanTarget = this.getNearestHuman(zombie);
        final Position targetPosition;
        if (humanTarget == null) {
            targetPosition = zombie.getPosition();
        } else if (!humanTarget.hasBeenBitten()) {
            targetPosition = humanTarget.getPosition();
        } else {
            // Encontrar una targetPosition que haga que el radio de ambas particulas se toquen
            targetPosition = this.nearestPositionWithRadius(zombie, humanTarget);
        }

        this.setChasing(zombie, humanTarget);

        List<Particle> neighbors = this.computeNeighbors(zombie, targetPosition,
                this.zombieParticles);
        List<Particle> inContactParticles = this.getParticlesInContact(neighbors, zombie);
        List<Position> inContactWalls = this.getNearestPositionOfWallInContact(zombie);

        zombie.getParticleZone().setCurrentRadius(Contractile.calculateParticleZoneRadius(zombie.getParticleZone(),
                dt, 0.5,
                !inContactParticles.isEmpty()));
        zombie.setVelocity(Contractile.calculateVelocity(
                zombie.getPosition(),
                !inContactParticles.isEmpty() ? inContactParticles : neighbors,
                inContactWalls,
                targetPosition,
                this.configuration.getParticleConfiguration().getVz(),
                zombie.getParticleZone(),
                zombie.getRadius(),
                this.configuration.getParticleConfiguration().getBeta(),
                !inContactParticles.isEmpty(),
                !inContactWalls.isEmpty()
        ));
    }

    private void setChasing(Zombie zombie, Human humanTarget) {
        if (
                (zombie.getChasing() == null && humanTarget == null)
                        || (zombie.getChasing() != null && zombie.getChasing().equals(humanTarget))
        ) {
            return;
        }

        if (zombie.getChasing() != null)
            zombie.getChasing().removeChasingZombie(zombie);
        if (humanTarget != null)
            humanTarget.addChasingZombie(zombie);
        zombie.setChasing(humanTarget);
    }

    private boolean shouldSpawnHumans(double absTime) {
        return this.totalHumanCount < this.configuration.getMaxHumans() && absTime - this.configuration.getSpawnHumansEvery() > this.lastHumanSpawned;
    }

    private void spawnHumans(double absoluteTime) {
        int max = this.configuration.getHumansPerSpawn() + this.totalHumanCount > this.configuration.getMaxHumans()
                ? (this.configuration.getMaxHumans() - this.totalHumanCount)
                : this.configuration.getHumansPerSpawn();

        for (int i = 0; i < max; i++) {
            Human human = this.spawnHuman(this.getHumanStartingPosition(), this.configuration.getHumanRadius());

            this.allParticles.add(human);
            this.healthyHumanParticles.add(human);
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

        double x = this.configuration.getParticleConfiguration().getMaxRadius() + 0.001;
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
            Zombie zombie = this.spawnZombie(position, this.configuration.getZombieRadius());

            this.allParticles.add(zombie);
            this.zombieParticles.add(zombie);
            this.totalCount++;
        }
    }

    private List<Position> getZombieStartingPositions() {
        double minY = this.configuration.getParticleConfiguration().getMaxRadius();
        double maxY =
                this.configuration.getBounds().getHeight() - this.configuration.getParticleConfiguration().getMaxRadius();
        double maxX =
                this.configuration.getBounds().getWidth() - this.configuration.getParticleConfiguration().getMaxRadius();
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

    private Human spawnHuman(Position position, double radius) {
        ParticleZone particleZone = this.getStartingParticleZone();

        return new Human(
                this.totalCount + 1,
                particleZone,
                radius,
                position,
                Contractile.calculateVelocity(
                        position,
                        Collections.emptyList(),
                        Collections.emptyList(),
                        this.getHumanExitDoorPosition(position, radius),
                        this.configuration.getParticleConfiguration().getVh(),
                        particleZone,
                        radius,
                        this.configuration.getParticleConfiguration().getBeta(),
                        false,
                        false
                )
        );
    }

    private Zombie spawnZombie(Position position, double radius) {
        ParticleZone particleZone = this.getStartingParticleZone();

        return new Zombie(
                this.totalCount + 1,
                particleZone,
                radius,
                position,
                Contractile.calculateVelocity(
                        position,
                        Collections.emptyList(),
                        Collections.emptyList(),
                        position,
                        this.configuration.getParticleConfiguration().getVz(),
                        particleZone,
                        radius,
                        this.configuration.getParticleConfiguration().getBeta(),
                        false,
                        false
                )
        );
    }

    private Position getHumanExitDoorPosition(Position humanPosition, double radius) {
        double doorStartY = this.configuration.getBounds().getDoorsStartY() + radius;
        double doorEndY = this.configuration.getBounds().getDoorsEndY() - radius;
        double y;
        if (humanPosition.getY() >= doorStartY && humanPosition.getY() <= doorEndY) {
            y = humanPosition.getY();
        } else if (humanPosition.getY() > doorEndY) {
            y = doorStartY;
        } else {
            y = doorEndY;
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

    private List<Particle> getParticlesInContact(Collection<? extends Particle> neighbors, Particle particle) {
        return neighbors.parallelStream().filter(p -> !p.equals(particle) && p.isInContact(particle)).collect(Collectors.toList());
    }

    private List<Position> getNearestPositionOfWallInContact(Particle particle) {
        List<Position> wallsInContact = new ArrayList<>();
        final double pos_x = particle.getPosition().getX();
        final double pos_y = particle.getPosition().getY();
        final double r = particle.getRadius();
        final double min_x = 0;
        final double min_y = 0;
        final double max_x = this.configuration.getBounds().getWidth();
        final double max_y = this.configuration.getBounds().getHeight();

        if (particle instanceof Human && this.hasReachedDoor((Human) particle)) {
            return wallsInContact;
        }
        if (pos_x - r <= min_x || pos_x + r >= max_x) {
            wallsInContact.add(new Position(pos_x - r <= min_x ? min_x : max_x, pos_y));
        } else if (pos_y - r <= min_y || pos_y + r >= max_y) {
            wallsInContact.add(new Position(pos_x, pos_y - r <= min_y ? min_y : max_y));
        }
        return wallsInContact;
    }

    private List<Particle> computeNeighbors(Particle from, Position targetPosition,
                                            Collection<? extends Particle> particles) {
        final Position position = from.getPosition();
        final double m_y = targetPosition.getY() - position.getY();
        final double m_x = targetPosition.getX() - position.getX();
        final double m = m_y / m_x;
        final double p_m = -1 / m;
        final double b = position.getY() - p_m * position.getX();
        List<Particle> validParticles = new ArrayList<>();
        for (Particle particle : particles) {
            if (from.equals(particle))
                continue;

            final Position pos = particle.getPosition();
            if (Double.isInfinite(p_m)) {
                // Es una recta vertical
                if (pos.getX() >= position.getX()) validParticles.add(particle);
            } else {
                final double y = p_m * pos.getX() + b;
                if (p_m > 0) {
                    if (pos.getY() <= y)
                        validParticles.add(particle);
                } else {
                    if (pos.getY() >= y)
                        validParticles.add(particle);
                }
            }
        }
        return validParticles;
    }

    private Human getNearestHuman(Particle zombie) {
        return Optional.ofNullable(
                this.healthyHumanParticles
                        .parallelStream()
                        .collect(Collectors.toMap(human -> human.distanceTo(zombie), human -> human, (o, o2) -> o,
                                TreeMap::new))
                        .firstEntry()
        )
                .filter(entry -> entry.getKey() <= this.configuration.getParticleConfiguration().getZombieFOV())
                .map(Entry::getValue)
                .orElse(null);
    }

    private boolean hasReachedDoor(Human human) {
        // Consideramos que al menos mitad del humano tiene que estar por fuera (por eso x >= width)
        return human.getPosition().getX() >= this.configuration.getBounds().getWidth() - human.getRadius()
                && human.getPosition().getY() >= this.configuration.getBounds().getDoorsStartY() + human.getRadius()
                && human.getPosition().getY() <= this.configuration.getBounds().getDoorsEndY() - human.getRadius();
    }

    private void calculateBittenZombies(double absoluteTime) {
        Iterator<Human> humanIterator = this.healthyHumanParticles.iterator();
        while (humanIterator.hasNext()) {
            Human human = humanIterator.next();

            if (!this.getParticlesInContact(this.zombieParticles, human).isEmpty()) {
                human.bite();
                this.bittenHumansTime.computeIfAbsent(absoluteTime, c -> new LinkedList<>()).add(human);
                humanIterator.remove();
            }
        }
    }

    private ParticleZone getStartingParticleZone() {
        // TODO: Check starting radius
        return new ParticleZone(
                this.configuration.getParticleConfiguration().getMinRadius(),
                this.configuration.getParticleConfiguration().getMaxRadius(),
                this.configuration.getParticleConfiguration().getMaxRadius()
        );
    }

    private Position nearestPositionWithRadius(Particle sourceParticle, Particle targetParticle) {
        // Target - source would yield a vector point towards the target particle
        Vector2D positionVector = Vector2DUtils.calculateVectorFromTwoPositions(targetParticle.getPosition(),
                sourceParticle.getPosition());
        positionVector = Vector2DUtils.calculateNormalizedVector(positionVector);

        // TODO: Ver si usar el current, el max o una combinacion, ni idea
        double totalRadius = sourceParticle.getRadius() + targetParticle.getRadius();
        return new Position(
                targetParticle.getPosition().getX() + positionVector.getX() * totalRadius,
                targetParticle.getPosition().getY() + positionVector.getY() * totalRadius
        );
    }

    private static boolean isParticleIn(Particle particle, double x, double y) {
        return (particle.getPosition().getX() - particle.getRadius() <= x && x <= particle.getPosition().getX() + particle.getRadius()) && (particle.getPosition().getY() - particle.getRadius() <= y && y <= particle.getPosition().getY() + particle.getRadius());
    }
}
