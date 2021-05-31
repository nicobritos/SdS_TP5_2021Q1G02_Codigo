package ar.edu.itba.sds_2021_q1_g02.models;

import java.math.BigDecimal;
import java.util.Map;

public class Step {
    private final Map<Particle, Position> previousParticlesPosition;
    private final Map<Particle, Velocity> previousParticlesVelocity;
    private BigDecimal deltaTime;
    private BigDecimal absoluteTime;
    private final int stepNumber;
    private double energyDifference;
    private boolean isLastStep = false;
    private boolean impactParticleEscaped = false;
    private final double v0;
    private BigDecimal impactParticleTrajectory = BigDecimal.ZERO;

    public Step(Map<Particle, Position> previousParticlesPosition, Map<Particle,
            Velocity> previousParticlesVelocity, BigDecimal deltaTime, BigDecimal absoluteTime, int stepNumber) {
        this(previousParticlesPosition, previousParticlesVelocity, deltaTime, absoluteTime, stepNumber, 0);
    }

    public Step(Map<Particle, Position> previousParticlesPosition, Map<Particle,
            Velocity> previousParticlesVelocity, BigDecimal deltaTime, BigDecimal absoluteTime, int stepNumber, double v0) {
        this.previousParticlesPosition = previousParticlesPosition;
        this.previousParticlesVelocity = previousParticlesVelocity;

        this.deltaTime = deltaTime;
        this.absoluteTime = absoluteTime;
        this.stepNumber = stepNumber;
        this.v0 = v0;
    }

    public BigDecimal getRelativeTime() {
        return this.deltaTime;
    }

    public double getV0() {
        return this.v0;
    }

    public BigDecimal getAbsoluteTime() {
        return this.absoluteTime;
    }

    public BigDecimal getImpactParticleTotalTrajectory() {
        return this.impactParticleTrajectory;
    }

    public void addImpactParticleTrajectory(BigDecimal impactParticleTrajectory) {
        this.impactParticleTrajectory = this.impactParticleTrajectory.add(impactParticleTrajectory);
    }

    public int getStepNumber() {
        return this.stepNumber;
    }

    public Position getPreviousPosition(Particle particle) {
        return this.previousParticlesPosition.get(particle);
    }

    public boolean containsPreviousPosition(Particle particle) {
        return this.previousParticlesPosition.containsKey(particle);
    }

    public Velocity getPreviousVelocity(Particle particle) {
        return this.previousParticlesVelocity.get(particle);
    }

    public double getEnergyDifference() {
        return this.energyDifference;
    }

    public void setEnergyDifference(double energyDifference) {
        this.energyDifference = energyDifference;
    }

    public boolean containsPreviousVelocity(Particle particle) {
        return this.previousParticlesVelocity.containsKey(particle);
    }

    public Step copy() {
        Step step = new Step(
                this.previousParticlesPosition,
                this.previousParticlesVelocity,
                this.deltaTime,
                this.absoluteTime,
                this.stepNumber,
                this.v0
        );
        step.addImpactParticleTrajectory(this.impactParticleTrajectory);
        return step;
    }

    public void setAbsoluteTime(BigDecimal absoluteTime) {
        this.absoluteTime = absoluteTime;
    }

    public void setRelativeTime(BigDecimal relativeTime) {
        this.deltaTime = relativeTime;
    }

    public boolean isLastStep() {
        return this.isLastStep;
    }

    public void setLastStep(boolean lastStep) {
        this.isLastStep = lastStep;
    }

    public boolean hasImpactParticleEscaped() {
        return this.impactParticleEscaped;
    }

    public void setImpactParticleEscaped(boolean escaped) {
        this.impactParticleEscaped = escaped;
    }
}
