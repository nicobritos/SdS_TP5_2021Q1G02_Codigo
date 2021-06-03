package ar.edu.itba.sds_2021_q1_g02.models;

public class Bounds {
    private final double height;
    private final double width;
    private final double zombieSize;
    private final double doorsSize;

    public Bounds(double height, double width, double zombieSize, double doorsSize) {
        this.height = height;
        this.width = width;
        this.zombieSize = zombieSize;
        this.doorsSize = doorsSize;
    }

    public double getHeight() {
        return this.height;
    }

    public double getWidth() {
        return this.width;
    }

    public double getZombieBoundWidth() {
        return this.zombieSize;
    }

    public double getDoorsSize() {
        return this.doorsSize;
    }
}
