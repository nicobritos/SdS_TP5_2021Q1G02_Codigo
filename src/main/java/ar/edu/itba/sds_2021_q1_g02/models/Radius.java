package ar.edu.itba.sds_2021_q1_g02.models;

public class Radius {
    private final double minRadius;
    private final double maxRadius;
    private double currentRadius;

    public Radius(double minRadius, double maxRadius, double currentRadius) {
        this.maxRadius = maxRadius;
        this.minRadius = minRadius;
        this.currentRadius = currentRadius;
    }

    public double getCurrentRadius() {
        return currentRadius;
    }

    public void setCurrentRadius(double currentRadius) {
        this.currentRadius = currentRadius;
    }

    public double getMinRadius() {
        return minRadius;
    }

    public double getMaxRadius() {
        return maxRadius;
    }
}
