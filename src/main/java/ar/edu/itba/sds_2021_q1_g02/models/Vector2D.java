package ar.edu.itba.sds_2021_q1_g02.models;

public class Vector2D {
    private double x;
    private double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Vector2D add(Vector2D offset) {
        return new Vector2D(this.x + offset.x, this.y + offset.y);
    }

    public Vector2D multiply(double d) {
        return new Vector2D(this.x * d, this.y * d);
    }

    public double distanceTo(Vector2D other) {
        return Math.sqrt(Math.pow(this.getX() - other.getX(), 2) + Math.pow(this.getY() - other.getY(), 2));
    }
}
