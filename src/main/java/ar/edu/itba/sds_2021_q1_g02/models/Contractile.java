package ar.edu.itba.sds_2021_q1_g02.models;

import ar.edu.itba.sds_2021_q1_g02.utils.Vector2DUtils;
import javafx.geometry.Pos;

import java.util.List;
import java.util.stream.Collectors;

public final class Contractile {
    private Contractile() {
    }

    public static Velocity calculateVelocity(Position position, List<Position> othersPosition,
                                             Position targetPosition, double maxVelocity, Radius radius, double beta
            , boolean isInContact) {
        if (isInContact) {
            return calculateEscapeVelocity(position, othersPosition, maxVelocity);
        }

        final double velocityMagnitude = calculateVelocityMagnitude(maxVelocity, radius, beta);
        return calculateDesiredVelocity(position, othersPosition, targetPosition, velocityMagnitude);
    }

    public static Position calculatePosition(Position position, Velocity velocity, double dt) {
        final double x = position.getX() + velocity.getxSpeed() * dt;
        final double y = position.getY() + velocity.getySpeed() * dt;
        return new Position(x, y);
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

    private static Velocity calculateDesiredVelocity(Position position, List<Position> othersPosition,
                                                     Position targetPosition, double velocityMagnitude) {
        final Vector2D avoidanceTargetDirection = calculateAvoidanceTargetDirection(position, othersPosition,
                targetPosition);
        return new Velocity(velocityMagnitude * avoidanceTargetDirection.getX(),
                velocityMagnitude * avoidanceTargetDirection.getY());
    }

    private static Velocity calculateEscapeVelocity(Position position, List<Position> othersPosition,
                                                    double velocityMagnitude) {
        Vector2D sumEij = new Vector2D(0, 0);
        for (Position otherPosition : othersPosition) {
            final Vector2D eij = Vector2DUtils.calculateVectorFromTwoPositions(position, otherPosition);
            final Vector2D normalizedEij = Vector2DUtils.calculateNormalizedVector(eij);
            sumEij.add(normalizedEij);
        }
        Vector2D sumEijNormalized = Vector2DUtils.calculateNormalizedVector(sumEij);
        return new Velocity(velocityMagnitude * sumEijNormalized.getX(), velocityMagnitude * sumEijNormalized.getY());

    }

    private static Vector2D calculateAvoidanceTargetDirection(Position position, List<Position> othersPosition,
                                                              Position targetPosition) {
        Vector2D avoidanceTarget = Vector2DUtils.calculateVectorFromTwoPositions(targetPosition, position);
        avoidanceTarget = Vector2DUtils.calculateNormalizedVector(avoidanceTarget);
        for (Position otherPosition : othersPosition) {
            final double angle = Vector2DUtils.calculateAngleByThreePositions(position, targetPosition, otherPosition);
            final Vector2D eij = Vector2DUtils.calculateVectorFromTwoPositions(position, otherPosition);
            final Vector2D normalizedEij = Vector2DUtils.calculateNormalizedVector(eij);
            final double dij = position.distanceTo(otherPosition);
            final Vector2D repVec = calculateRepulsionVector(normalizedEij, dij, angle, 1, 1);
            avoidanceTarget = avoidanceTarget.add(repVec);
        }
        return Vector2DUtils.calculateNormalizedVector(avoidanceTarget);
    }

    private static double calculateVelocityMagnitude(double maxVelocity, Radius radius, double beta) {
        // beta = 1
        double currentRadius = radius.getCurrentRadius();
        double minRadius = radius.getMinRadius();
        double maxRadius = radius.getMaxRadius();

        double radiusDivision = (currentRadius - minRadius) / (maxRadius - minRadius);
        return maxVelocity * Math.pow(radiusDivision, beta);
    }

    public static Vector2D calculateRepulsionVector(Vector2D eij, double distance, double angle, double ap,
                                                    double bp) {
        final double r_x = eij.getX() * ap * Math.pow(Math.E, (-1 * distance) / bp) * Math.cos(angle);
        final double r_y = eij.getY() * ap * Math.pow(Math.E, (-1 * distance) / bp) * Math.cos(angle);

        return new Vector2D(r_x, r_y);
    }


}
