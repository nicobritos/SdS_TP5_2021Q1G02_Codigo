package ar.edu.itba.sds_2021_q1_g02.models;


public class Position {
    private double x;
    private double y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public int getRoundedX() {
        return (int) Math.round(this.x);
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public int getRoundedY() {
        return (int) Math.round(this.y);
    }

    public void setY(double y) {
        this.y = y;
    }

    public Position add(Position offset) {
        return new Position(
                this.x + offset.x,
                this.y + offset.y
        );
    }

    public Position copy() {
        return new Position(this.x, this.y);
    }

    public double distanceTo(Position other) {
        return Math.sqrt(
                Math.pow(this.getX() - other.getX(), 2)
                        + Math.pow(this.getY() - other.getY(), 2)
        );
    }

    @Override
    public String toString() {
        return String.format("(%.5f, %.5f)", this.x, this.y);
    }

    public Position multiply(double d) {
        return new Position(
                this.x * d,
                this.y * d
        );
    }
}