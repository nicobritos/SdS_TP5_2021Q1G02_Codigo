package ar.edu.itba.sds_2021_q1_g02;

import ar.edu.itba.sds_2021_q1_g02.models.Radius;

public class TemporalEvolution {
    public static double calculatePosition(double position, double velocity, double dt) {
        return position + velocity * dt;
    }

    public static double calculateVelocity(double maxVelocity, Radius radius, double beta, boolean isInContact) {
        // beta = 1
        double currentRadius = radius.getCurrentRadius();
        double minRadius = radius.getMinRadius();
        double maxRadius = radius.getMaxRadius();

        return isInContact ? maxVelocity : maxVelocity * ((currentRadius - minRadius) / (maxRadius - minRadius)) * beta;
    }

    public static double calculateRadius(Radius radius, double dt, double tau, boolean isInContact) {
        //tau = 0.5 seg
        double currentRadius = radius.getCurrentRadius();
        double minRadius = radius.getMinRadius();
        double maxRadius = radius.getMaxRadius();

        return isInContact ? minRadius : currentRadius < maxRadius ? currentRadius + (maxRadius / (tau / dt)) :
                currentRadius;
    }
}
