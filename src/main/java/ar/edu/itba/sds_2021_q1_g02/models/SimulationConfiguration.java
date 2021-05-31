package ar.edu.itba.sds_2021_q1_g02.models;

public class SimulationConfiguration {
    private final double dt;
    private final double minRadius;
    private final double maxRadius;
    private final double beta;
    private final double zombieTurnTime;
    private final double vh;
    private final double vz;
    private final double zombieFOV;
    private final int maxZombies;
    private final int maxHumans;
    private final int spawnHumansEvery;
    private final int humansPerSpawn;

    public SimulationConfiguration(double dt, double minRadius, double maxRadius, double beta, double zombieTurnTime, double vh, double vz, double zombieFOV, int maxZombies, int maxHumans, int spawnHumansEvery, int humansPerSpawn) {
        this.dt = dt;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        this.beta = beta;
        this.zombieTurnTime = zombieTurnTime;
        this.vh = vh;
        this.vz = vz;
        this.zombieFOV = zombieFOV;
        this.maxZombies = maxZombies;
        this.maxHumans = maxHumans;
        this.spawnHumansEvery = spawnHumansEvery;
        this.humansPerSpawn = humansPerSpawn;
    }

    public double getDt() {
        return this.dt;
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

    public double getZombieTurnTime() {
        return this.zombieTurnTime;
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
}
