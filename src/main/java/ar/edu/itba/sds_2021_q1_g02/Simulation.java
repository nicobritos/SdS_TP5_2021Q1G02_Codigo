package ar.edu.itba.sds_2021_q1_g02;

import ar.edu.itba.sds_2021_q1_g02.models.*;
import ar.edu.itba.sds_2021_q1_g02.serializer.Serializable;
import ar.edu.itba.sds_2021_q1_g02.utils.Vector2DUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class Simulation extends Serializable {
    private final Collection<Particle> zombieParticles;
    private final Collection<Particle> humanParticles;
    private final Collection<Particle> allParticles;
    private final SimulationConfiguration configuration;
    private final Random random;
    private double lastHumanSpawned = Double.NEGATIVE_INFINITY;
    private int totalHumanCount;
    private int totalCount;
    private double maxTime = 100;
    private final TreeMap<Double, Collection<Particle>> bittenHumansTime;

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

        Iterator<Map.Entry<Double, Collection<Particle>>> iterator =
                this.bittenHumansTime.tailMap(toKey, true).entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Double, Collection<Particle>> entry = iterator.next();
            entry.getValue().forEach(particle -> {
                particle.setType(Type.ZOMBIE);
                this.humanParticles.remove(particle);
                this.zombieParticles.add(particle);
                this.putZombieVelocity(particle, dt);
            });
            iterator.remove();
        }
    }

    private void moveParticles(double dt) {
        this.moveHumans(dt);
        this.moveZombies(dt);
    }

    private void moveHumans(double dt) {
        Iterator<Particle> iterator = this.humanParticles.iterator();
        while (iterator.hasNext()) {
            Particle particle = iterator.next();
            if (Simulation.isBittenHuman(particle)) {
                // Si el humano fue mordido por un zombie no lo movemos, sin importar su entorno.
                continue;
            }
            // Movemos particula
            particle.setPosition(Contractile.calculatePosition(particle.getPosition(), particle.getVelocity(), dt));
            // TODO: Poner humanParticles y cambiar el endPosition por un target que escape los zombies
            // Calculamos nueva velocidad y radio
            this.putHumanVelocity(particle, dt);
            if (this.hasReachedDoor(particle)) {
                iterator.remove();
            }
        }
    }

    private void moveZombies(double dt) {
        Iterator<Particle> iterator = this.zombieParticles.iterator();
        while (iterator.hasNext()) {
            Particle particle = iterator.next();
            // Movemos particula
            particle.setPosition(Contractile.calculatePosition(particle.getPosition(), particle.getVelocity(), dt));

            // Calculamos nueva velocidad y radio
            this.putZombieVelocity(particle, dt);
        }
    }

    private void putHumanVelocity(Particle particle, double dt) {
        List<Particle> neighbors = this.computeNeighbors(particle.getPosition(), this.getEndPosition(),
                this.allParticles);
        List<Particle> inContactParticles = this.isInContact(neighbors, particle);


        particle.getRadius().setCurrentRadius(Contractile.calculateRadius(particle.getRadius(), dt, 0.5,
                !inContactParticles.isEmpty()));
        particle.setVelocity(Contractile.calculateVelocity(
                particle.getPosition(),
                !inContactParticles.isEmpty() ? inContactParticles : neighbors,
                this.getEndPosition(),
                this.configuration.getParticleConfiguration().getVh(),
                particle.getRadius(),
                this.configuration.getParticleConfiguration().getBeta(),
                !inContactParticles.isEmpty()
        ));
    }

    private void putZombieVelocity(Particle particle, double dt) {
        final Position humanTargetPosition = this.getNearestHumanPosition(particle);
        List<Particle> neighbors = this.computeNeighbors(particle.getPosition(), humanTargetPosition,
                this.zombieParticles);
        List<Particle> inContactParticles = this.isInContact(neighbors, particle);

        particle.getRadius().setCurrentRadius(Contractile.calculateRadius(particle.getRadius(), dt, 0.5,
                !inContactParticles.isEmpty()));
        particle.setVelocity(Contractile.calculateVelocity(
                particle.getPosition(),
                !inContactParticles.isEmpty() ? inContactParticles : neighbors,
                humanTargetPosition,
                this.configuration.getParticleConfiguration().getVz(),
                particle.getRadius(),
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
            this.humanParticles.add(this.spawnParticle(this.getHumanStartingPosition(), Type.HUMAN));
            this.totalHumanCount++;
        }

        this.lastHumanSpawned = absoluteTime;
    }

    private Position getHumanStartingPosition() {
        double minY = (this.configuration.getBounds().getHeight() - this.configuration.getBounds().getDoorsSize()) / 2
                + this.configuration.getParticleConfiguration().getMaxRadius();
        double maxY = (this.configuration.getBounds().getHeight() + this.configuration.getBounds().getDoorsSize()) / 2
                - this.configuration.getParticleConfiguration().getMaxRadius();
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
            this.zombieParticles.add(this.spawnParticle(position, Type.ZOMBIE));
        }
    }

    private List<Position> getZombieStartingPositions() {
        double minY = (this.configuration.getBounds().getHeight() - this.configuration.getBounds().getDoorsSize()) / 2
                + this.configuration.getParticleConfiguration().getMaxRadius();
        double maxY = (this.configuration.getBounds().getHeight() + this.configuration.getBounds().getDoorsSize()) / 2
                - this.configuration.getParticleConfiguration().getMaxRadius();
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

    private Particle spawnParticle(Position position, Type type) {
        // TODO: Check starting radius
        Radius radius = new Radius(
                this.configuration.getParticleConfiguration().getMinRadius(),
                this.configuration.getParticleConfiguration().getMaxRadius(),
                this.configuration.getParticleConfiguration().getMaxRadius()
        );

        Particle particle = new Particle(
                this.totalCount + 1,
                radius,
                position,
                Contractile.calculateVelocity(
                        position,
                        Collections.emptyList(),
                        type.equals(Type.HUMAN) ? this.getEndPosition() : position,
                        this.configuration.getParticleConfiguration().getVh(),
                        radius,
                        this.configuration.getParticleConfiguration().getBeta(),
                        false
                ),
                type
        );

        this.allParticles.add(particle);
        this.totalCount++;
        return particle;
    }

    private Position getEndPosition() {
        return new Position(
                this.configuration.getBounds().getWidth(),
                (this.configuration.getBounds().getHeight() + this.configuration.getBounds().getDoorsSize()) / 2
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

    private List<Particle> isInContact(Collection<Particle> neighbors, Particle particle) {
        return neighbors.stream().filter(p -> !p.equals(particle) && p.isInContact(particle)).collect(Collectors.toList());
    }

    private List<Particle> computeNeighbors(Position position, Position targetPosition,
                                            Collection<Particle> particles) {
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

    private boolean hasReachedDoor(Particle particle) {
        return Simulation.isHuman(particle)
                && particle.getPosition().getX() >= this.configuration.getBounds().getWidth()
                && particle.getPosition().getY() >= (this.configuration.getBounds().getHeight() - this.configuration.getBounds().getDoorsSize()) / 2
                && particle.getPosition().getY() <= (this.configuration.getBounds().getHeight() + this.configuration.getBounds().getDoorsSize()) / 2;
    }

    private void calculateBittenZombies(double absoluteTime) {
        this.humanParticles
                .stream()
                .filter(particle -> !this.isInContact(this.zombieParticles, particle).isEmpty())
                .forEach(particle -> {
                    particle.setType(Type.BITTEN_HUMAN);
                    this.bittenHumansTime.computeIfAbsent(absoluteTime, c -> new LinkedList<>()).add(particle);
                });
    }

    private static boolean isHuman(Particle particle) {
        return particle.getType().equals(Type.HUMAN);
    }

    private static boolean isBittenHuman(Particle particle) {
        return particle.getType().equals(Type.BITTEN_HUMAN);
    }

    private static boolean isParticleIn(Particle particle, double x, double y) {
        return (particle.getPosition().getX() - particle.getRadius().getCurrentRadius() <= x && x <= particle.getPosition().getX() + particle.getRadius().getCurrentRadius()) && (particle.getPosition().getY() - particle.getRadius().getCurrentRadius() <= y && y <= particle.getPosition().getY() + particle.getRadius().getCurrentRadius());
    }
}
