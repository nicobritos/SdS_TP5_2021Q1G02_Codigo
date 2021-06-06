package ar.edu.itba.sds_2021_q1_g02.models;

import java.math.BigDecimal;

public class SimulationConfiguration {
    private final double dt;
    private final BigDecimal dtAsBigDecimal;
    private final ParticleConfiguration particleConfiguration;
    private final double zombieTurnTime;
    private final int maxZombies;
    private final int maxHumans;
    private final double spawnHumansEvery;
    private final int humansPerSpawn;
    private final Bounds bounds;
    private final double humanRadius;
    private final double zombieRadius;

    public SimulationConfiguration(double dt, ParticleConfiguration particleConfiguration, double zombieTurnTime,
                                   int maxZombies, int maxHumans, double spawnHumansEvery, int humansPerSpawn,
                                   Bounds bounds, double humanRadius, double zombieRadius) {
        this.dt = dt;
        this.dtAsBigDecimal = BigDecimal.valueOf(dt);
        this.particleConfiguration = particleConfiguration;
        this.zombieTurnTime = zombieTurnTime;
        this.maxZombies = maxZombies;
        this.maxHumans = maxHumans;
        this.spawnHumansEvery = spawnHumansEvery;
        this.humansPerSpawn = humansPerSpawn;
        this.bounds = bounds;
        this.humanRadius = humanRadius;
        this.zombieRadius = zombieRadius;
    }

    public double getDt() {
        return this.dt;
    }

    public BigDecimal getDtAsBigDecimal() {
        return this.dtAsBigDecimal;
    }

    public ParticleConfiguration getParticleConfiguration() {
        return this.particleConfiguration;
    }

    public double getZombieTurnTime() {
        return this.zombieTurnTime;
    }

    public int getMaxZombies() {
        return this.maxZombies;
    }

    public int getMaxHumans() {
        return this.maxHumans;
    }

    public double getSpawnHumansEvery() {
        return this.spawnHumansEvery;
    }

    public int getHumansPerSpawn() {
        return this.humansPerSpawn;
    }

    public Bounds getBounds() {
        return this.bounds;
    }

    public double getHumanRadius() {
        return this.humanRadius;
    }

    public double getZombieRadius() {
        return this.zombieRadius;
    }
}
