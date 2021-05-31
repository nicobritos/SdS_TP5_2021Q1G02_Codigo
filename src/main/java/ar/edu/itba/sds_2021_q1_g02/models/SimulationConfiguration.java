package ar.edu.itba.sds_2021_q1_g02.models;

import java.math.BigDecimal;

public class SimulationConfiguration {
    private final double dt;
    private final BigDecimal dtAsBigDecimal;
    private final ParticleConfiguration particleConfiguration;
    private final double zombieTurnTime;
    private final int maxZombies;
    private final int maxHumans;
    private final int spawnHumansEvery;
    private final int humansPerSpawn;
    private final Bounds bounds;

    public SimulationConfiguration(double dt, ParticleConfiguration particleConfiguration, double zombieTurnTime, int maxZombies, int maxHumans, int spawnHumansEvery, int humansPerSpawn, Bounds bounds) {
        this.dt = dt;
        this.dtAsBigDecimal = BigDecimal.valueOf(dt);
        this.particleConfiguration = particleConfiguration;
        this.zombieTurnTime = zombieTurnTime;
        this.maxZombies = maxZombies;
        this.maxHumans = maxHumans;
        this.spawnHumansEvery = spawnHumansEvery;
        this.humansPerSpawn = humansPerSpawn;
        this.bounds = bounds;
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

    public int getSpawnHumansEvery() {
        return this.spawnHumansEvery;
    }

    public int getHumansPerSpawn() {
        return this.humansPerSpawn;
    }

    public Bounds getBounds() {
        return this.bounds;
    }
}
