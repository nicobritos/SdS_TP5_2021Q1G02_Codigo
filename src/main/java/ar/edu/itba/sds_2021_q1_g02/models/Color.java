package ar.edu.itba.sds_2021_q1_g02.models;

public class Color {
    private final double red;
    private final double green;
    private final double blue;

    public Color(double red, double green, double blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public double getRed() {
        return this.red;
    }

    public double getGreen() {
        return this.green;
    }

    public double getBlue() {
        return this.blue;
    }
}
