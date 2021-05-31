package ar.edu.itba.sds_2021_q1_g02.models;

import java.util.List;
import java.util.stream.Collectors;

public final class Contractile {
    private Contractile() {}

    public static Position calculatePosition(Position position, Velocity velocity, double dt) {
        return new Position(
                Contractile.calculatePosition(position.getX(), velocity.getxSpeed(), dt),
                Contractile.calculatePosition(position.getY(), velocity.getySpeed(), dt)
        );
    }

    public static Velocity calculateVelocity(Position position, List<Position> targetPositions, double maxVelocity,
                                            Radius radius, double beta, boolean isInContact) {
        return new Velocity(
                Contractile.calculateVelocity(
                        position.getX(),
                        targetPositions.stream().map(Position::getX).collect(Collectors.toList()),
                        maxVelocity,
                        radius,
                        beta,
                        isInContact
                ),
                Contractile.calculateVelocity(
                        position.getY(),
                        targetPositions.stream().map(Position::getY).collect(Collectors.toList()),
                        maxVelocity,
                        radius,
                        beta,
                        isInContact
                )
        );
    }

    public static double calculateRadius(Radius radius, double dt, double tau, boolean isInContact) {
        if (isInContact) {
            return radius.getMinRadius();
        }

        //tau = 0.5 seg
        double maxRadius = radius.getMaxRadius();
        double currentRadius = radius.getCurrentRadius();

        return currentRadius < maxRadius ? currentRadius + (maxRadius / (tau / dt)) : maxRadius;
    }

    public static double calculateDiscreteTimeStep(double minRadius, double maxVelocity, double escapeVelocity) {
        return minRadius / (2 * Math.max(maxVelocity, escapeVelocity));
    }

    private static double calculateVelocity(double position, List<Double> targetPositions, double maxVelocity,
                                            Radius radius, double beta, boolean isInContact) {
        if (isInContact) {
            return calculateEscapeVelocity(position, targetPositions, maxVelocity);
        }

        final double velocityMagnitude = calculateVelocityMagnitude(maxVelocity, radius, beta);
        return calculateDesiredVelocity(position, targetPositions.get(0), velocityMagnitude);
    }

    public static double calculatePosition(double position, double velocity, double dt) {
        return position + velocity * dt;
    }

    private static double calculateDesiredVelocity(double position, double targetPosition, double velocityMagnitude) {
        double target = calculateTarget(position, targetPosition);
        return velocityMagnitude * target;
    }

    private static double calculateEscapeVelocity(double position, List<Double> targetPositions,
                                                  double velocityMagnitude) {
        double targetSum = targetPositions.stream().mapToDouble(targetPosition -> calculateTarget(position,
                targetPosition)).sum();
        return velocityMagnitude * (targetSum / Math.abs(targetSum));
    }

    private static double calculateTarget(double position, double targetPosition) {
        return (targetPosition - position) / Math.abs(targetPosition - position);
    }

    private static double calculateVelocityMagnitude(double maxVelocity, Radius radius, double beta) {
        // beta = 1
        double currentRadius = radius.getCurrentRadius();
        double minRadius = radius.getMinRadius();
        double maxRadius = radius.getMaxRadius();

        double radiusDivision = (currentRadius - minRadius) / (maxRadius - minRadius);
        return maxVelocity * Math.pow(radiusDivision, beta);
    }
}
