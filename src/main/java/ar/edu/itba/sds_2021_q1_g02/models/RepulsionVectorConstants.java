package ar.edu.itba.sds_2021_q1_g02.models;

public class RepulsionVectorConstants {
    private final double ap;
    private final double bp;

    public RepulsionVectorConstants(double ap, double bp) {
        if (ap == 0)
            throw new IllegalArgumentException("ap can't be zero");
        if (bp <= 0)
            throw new IllegalArgumentException("bp must be a positive number");

        this.ap = ap;
        this.bp = bp;
    }

    public double getAp() {
        return this.ap;
    }

    public double getBp() {
        return this.bp;
    }

    public RepulsionVectorConstants multiply(double f) {
        return this.multiply(f, false);
    }

    public RepulsionVectorConstants multiply(double f, boolean bp) {
        if (f == 0)
            throw new IllegalArgumentException("factor cannot be zero");

        return new RepulsionVectorConstants(this.ap * f, bp ? this.bp * f : this.bp);
    }
}
