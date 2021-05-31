package ar.edu.itba.sds_2021_q1_g02.models;


public class Velocity {
    private final double xSpeed;
    private final double ySpeed;

    public Velocity(double xSpeed, double ySpeed) {
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    public double getxSpeed() {
        return this.xSpeed;
    }

    public double getySpeed() {
        return this.ySpeed;
    }

    public Velocity copy() {
        return new Velocity(this.xSpeed, this.ySpeed);
    }

    public boolean isZero(double epsilon) {
        return Math.abs(this.xSpeed) <= epsilon && Math.abs(this.ySpeed) <= epsilon;
    }

    public double magnitude() {
        return Math.sqrt(Math.pow(this.xSpeed, 2) + Math.pow(this.ySpeed, 2));
    }
    
    @Override
    public String toString() {
        return String.format("(%.5f, %.5f)", this.xSpeed, this.ySpeed);
    }
}