package ar.edu.itba.sds_2021_q1_g02.models;


import java.util.Objects;

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
        return new Position(this.x + offset.x, this.y + offset.y);
    }

    public Position add(Vector2D offset) {
        return new Position(this.x + offset.getX(), this.y + offset.getY());
    }

    public Position multiply(double d) {
        return new Position(this.x * d, this.y * d);
    }

    public double distanceTo(Position other) {
        return Math.sqrt(Math.pow(this.getX() - other.getX(), 2) + Math.pow(this.getY() - other.getY(), 2));
    }

    public Position copy() {
        return new Position(this.x, this.y);
    }

    @Override
    public String toString() {
        return String.format("(%.5f, %.5f)", this.x, this.y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position position = (Position) o;
        return Double.compare(position.getX(), this.getX()) == 0 && Double.compare(position.getY(), this.getY()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getX(), this.getY());
    }
}