package ar.edu.itba.sds_2021_q1_g02.models;

import java.math.BigDecimal;

public class ParticleConfiguration {
    private final double minRadius;
    private final double maxRadius;
    private final double beta;
    private final double vh;
    private final double vz;
    private final double zombieFOV;

    public ParticleConfiguration(double minRadius, double maxRadius, double beta, double vh, double vz, double zombieFOV) {
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.beta = beta;
        this.vh = vh;
        this.vz = vz;
        this.zombieFOV = zombieFOV;
    }

    public double getMinRadius() {
        return this.minRadius;
    }

    public double getMaxRadius() {
        return this.maxRadius;
    }

    public double getBeta() {
        return this.beta;
    }

    public double getVh() {
        return this.vh;
    }

    public double getVz() {
        return this.vz;
    }

    public double getZombieFOV() {
        return this.zombieFOV;
    }
}
