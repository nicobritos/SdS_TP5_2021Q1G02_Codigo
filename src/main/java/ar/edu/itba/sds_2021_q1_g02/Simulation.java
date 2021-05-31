package ar.edu.itba.sds_2021_q1_g02;

import ar.edu.itba.sds_2021_q1_g02.models.*;
import ar.edu.itba.sds_2021_q1_g02.serializer.Serializable;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class Simulation extends Serializable {
    private final Collection<Particle> particles;
    private final SimulationConfiguration configuration;
    private double lastHumanSpawned = Double.NEGATIVE_INFINITY;
    private int totalHumanCount;
    private double maxTime = 100;

    public Simulation(SimulationConfiguration configuration) {
        this.particles = new LinkedList<>();
        this.configuration = configuration;
    }

    public void simulate() {
        this.serializeSystem(this.particles, this.configuration);
        Step step = this.calculateFirstStep();
        this.serialize(this.particles, step);

        while (step.getAbsoluteTime().doubleValue() < this.maxTime) {
            step = this.simulateStep(step);
            if (step.getAbsoluteTime().doubleValue() >= this.maxTime)
                step.setLastStep(true);

            this.serialize(this.particles, step);
        }
    }

    private Step simulateStep(Step previousStep) {
        Step actualStep = new Step(
                this.configuration.getDtAsBigDecimal(),
                previousStep.getAbsoluteTime().add(this.configuration.getDtAsBigDecimal()),
                previousStep.getStepNumber() + 1
        );

        if (this.shouldSpawnHumans(previousStep.getAbsoluteTime().doubleValue())) {
            this.spawnHumans(actualStep.getAbsoluteTime().doubleValue());
            this.lastHumanSpawned = actualStep.getAbsoluteTime().doubleValue();
        }

        // Mover particulas
        this.moveParticles(this.configuration.getDt());

        return actualStep;
    }

    private Step calculateFirstStep() {
        this.spawnHumans(0);
        this.lastHumanSpawned = 0;

        return new Step(
                this.configuration.getDtAsBigDecimal(),
                BigDecimal.ZERO,
                0
        );
    }

    private void moveParticles(double dt) {
        Iterator<Particle> iterator = this.particles.iterator();
        while (iterator.hasNext()) {
            Particle particle = iterator.next();
            particle.move(dt);
            if (this.hasReachedDoor(particle)) {
                iterator.remove();
            }
        }
    }

    private boolean shouldSpawnHumans(double absTime) {
        return this.totalHumanCount < this.configuration.getMaxHumans() && absTime - this.configuration.getSpawnHumansEvery() > this.lastHumanSpawned;
    }

    private void spawnHumans(double absoluteTime) {
        int max = this.configuration.getHumansPerSpawn() + this.totalHumanCount > this.configuration.getMaxHumans()
                ? (this.configuration.getMaxHumans() - this.totalHumanCount)
                : this.configuration.getHumansPerSpawn();

        for (int i = 0; i < max; i++) {
            // TODO: Check starting radius
            Position startPosition = this.getHumanStartingPosition();
            Radius radius = new Radius(
                    this.configuration.getParticleConfiguration().getMinRadius(),
                    this.configuration.getParticleConfiguration().getMaxRadius(),
                    this.configuration.getParticleConfiguration().getMaxRadius()
            );

            this.particles.add(new Particle(
                    this.particles.size() + 1,
                    radius,
                    startPosition,
                    Contractile.calculateVelocity(
                            startPosition,
                            Collections.singletonList(this.getEndPosition()),
                            this.configuration.getParticleConfiguration().getVh(),
                            radius,
                            this.configuration.getParticleConfiguration().getBeta(),
                            false
                    ),
                    State.HUMAN
            ));

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

        boolean positionTaken = !this.particles.isEmpty();
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

    private Position getEndPosition() {
        return new Position(
                this.configuration.getBounds().getWidth(),
                (this.configuration.getBounds().getHeight() + this.configuration.getBounds().getDoorsSize()) / 2
        );
    }

    private boolean isPositionTaken(double x, double y) {
        boolean positionTaken = false;
        for (Particle particle : this.particles) {
            if (!Simulation.isParticleIn(particle, x, y)) {
                if (positionTaken)
                    positionTaken = false;
            } else {
                if (!positionTaken)
                    positionTaken = true;
            }
        }
        return positionTaken;
    }

    private boolean hasReachedDoor(Particle particle) {
        return Simulation.isHuman(particle)
                && particle.getPosition().getX() >= this.configuration.getBounds().getWidth()
                && particle.getPosition().getY() >= (this.configuration.getBounds().getHeight() - this.configuration.getBounds().getDoorsSize()) / 2
                && particle.getPosition().getY() <= (this.configuration.getBounds().getHeight() + this.configuration.getBounds().getDoorsSize()) / 2;
    }

    private static boolean isHuman(Particle particle) {
        return particle.getState().equals(State.HUMAN);
    }

    private static boolean isParticleIn(Particle particle, double x, double y) {
        return (
                particle.getPosition().getX() - particle.getRadius().getCurrentRadius() <= x
                && x <= particle.getPosition().getX() + particle.getRadius().getCurrentRadius()
        ) || (
                particle.getPosition().getY() + particle.getRadius().getCurrentRadius() <= y
                && y <= particle.getPosition().getY() + particle.getRadius().getCurrentRadius()
        );
    }
}
