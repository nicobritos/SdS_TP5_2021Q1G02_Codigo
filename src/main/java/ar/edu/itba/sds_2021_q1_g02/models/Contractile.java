package ar.edu.itba.sds_2021_q1_g02.models;

import ar.edu.itba.sds_2021_q1_g02.utils.Vector2DUtils;
import javafx.geometry.Pos;

import java.util.*;
import java.util.stream.Collectors;

public final class Contractile {
    private Contractile() {
    }

    public static Velocity calculateVelocity(
            Position position,
            Map<Particle, RepulsionVectorConstants> otherParticles,
            Map<Position, RepulsionVectorConstants> wallsPosition,
            Position targetPosition,
            double maxVelocity,
            ParticleZone particleZone,
            double radius,
            double beta,
            boolean isInContactWithParticle,
            boolean isInContactWithWall)
    {
        if (isInContactWithParticle || isInContactWithWall) {
            List<Position> inContactPositions = new ArrayList<>();
            if (isInContactWithWall && isInContactWithParticle)
                return Velocity.ZERO;

            if (isInContactWithParticle) {
                inContactPositions.addAll(otherParticles.keySet().stream().map(Particle::getPosition).collect(Collectors.toList()));
            }
            if (isInContactWithWall) {
                inContactPositions.addAll(wallsPosition.keySet());
            }
            return calculateEscapeVelocity(position, inContactPositions, maxVelocity);
        }

        final double velocityMagnitude = calculateVelocityMagnitude(maxVelocity, particleZone, beta);
        return calculateDesiredVelocity(position, radius, otherParticles, wallsPosition, targetPosition,
                velocityMagnitude);

    }

    public static Position calculatePosition(Position position, Velocity velocity, double dt) {
        final double x = position.getX() + velocity.getxSpeed() * dt;
        final double y = position.getY() + velocity.getySpeed() * dt;
        return new Position(x, y);
    }

    public static double calculateParticleZoneRadius(ParticleZone particleZone, double dt, double tau,
                                                     boolean isInContact) {
        if (isInContact) {
            return particleZone.getMinRadius();
        }
        //tau = 0.5 seg
        double maxRadius = particleZone.getMaxRadius();
        double currentRadius = particleZone.getCurrentRadius();

        return currentRadius < maxRadius ? currentRadius + (maxRadius / (tau / dt)) : maxRadius;
    }

    public static double calculateDiscreteTimeStep(double minRadius, double maxVelocity, double escapeVelocity) {
        return minRadius / (2 * Math.max(maxVelocity, escapeVelocity));
    }

    private static Velocity calculateDesiredVelocity(
            Position position,
            double radius,
            Map<Particle, RepulsionVectorConstants> otherParticles,
            Map<Position, RepulsionVectorConstants> wallsPosition,
            Position targetPosition,
            double velocityMagnitude) {
        final Vector2D avoidanceTargetDirection = calculateAvoidanceTargetDirection(position, radius, otherParticles,
                wallsPosition, targetPosition);
        return new Velocity(velocityMagnitude * avoidanceTargetDirection.getX(),
                velocityMagnitude * avoidanceTargetDirection.getY());
    }

    private static Velocity calculateEscapeVelocity(
            Position position,
            List<Position> othersPosition,
            double velocityMagnitude
    ) {
        Vector2D sumEij = new Vector2D(0, 0);
        for (Position pos : othersPosition) {
            final Vector2D eij = Vector2DUtils.calculateVectorFromTwoPositions(position, pos);
            final Vector2D normalizedEij = Vector2DUtils.calculateNormalizedVector(eij);
            sumEij = sumEij.add(normalizedEij);
        }
        Vector2D sumEijNormalized = Vector2DUtils.calculateNormalizedVector(sumEij);
        return new Velocity(velocityMagnitude * sumEijNormalized.getX(), velocityMagnitude * sumEijNormalized.getY());

    }

    private static Vector2D calculateAvoidanceTargetDirection(
            Position position,
            double radius,
            Map<Particle, RepulsionVectorConstants> otherParticles,
            Map<Position, RepulsionVectorConstants> wallsPosition,
            Position targetPosition) {
        Vector2D avoidanceTarget = Vector2DUtils.calculateVectorFromTwoPositions(targetPosition, position);
        avoidanceTarget = Vector2DUtils.calculateNormalizedVector(avoidanceTarget);

        for (Map.Entry<Particle, RepulsionVectorConstants> entry : otherParticles.entrySet()) {
            final Particle particle = entry.getKey();

            final double angle = Vector2DUtils.calculateAngleByThreePositions(
                    position,
                    targetPosition,
                    particle.getPosition()
            );
            final Vector2D eij = Vector2DUtils.calculateVectorFromTwoPositions(position, particle.getPosition());
            final Vector2D normalizedEij = Vector2DUtils.calculateNormalizedVector(eij);
            final double dij = position.distanceTo(particle.getPosition()) - radius - particle.getRadius();
            final Vector2D repVec = calculateRepulsionVector(
                    normalizedEij,
                    dij,
                    angle,
                    entry.getValue().getAp(),
                    entry.getValue().getBp()
            );

            avoidanceTarget = avoidanceTarget.add(repVec);
        }

        for (Map.Entry<Position, RepulsionVectorConstants> entry : wallsPosition.entrySet()) {
            final Position wall = entry.getKey();

            final double angle = Vector2DUtils.calculateAngleByThreePositions(position, targetPosition, wall);
            final Vector2D eij = Vector2DUtils.calculateVectorFromTwoPositions(position, wall);
            final Vector2D normalizedEij = Vector2DUtils.calculateNormalizedVector(eij);
            final double dij = position.distanceTo(wall) - radius;
            final Vector2D repVec = calculateRepulsionVector(
                    normalizedEij,
                    dij,
                    angle,
                    entry.getValue().getAp(),
                    entry.getValue().getBp()
            );
            avoidanceTarget = avoidanceTarget.add(repVec);
        }
        return Vector2DUtils.calculateNormalizedVector(avoidanceTarget);
    }

    private static double calculateVelocityMagnitude(double maxVelocity, ParticleZone particleZone, double beta) {
        // beta = 1
        double currentRadius = particleZone.getCurrentRadius();
        double minRadius = particleZone.getMinRadius();
        double maxRadius = particleZone.getMaxRadius();

        double nom = currentRadius - minRadius;
        double den = maxRadius - minRadius;
        double radiusDivision = nom == den ? 1 : (nom) / (den);
        return maxVelocity * Math.pow(radiusDivision, beta);
    }

    public static Vector2D calculateRepulsionVector(Vector2D eij, double distance, double angle, double ap,
                                                    double bp) {
        final double r_x = eij.getX() * ap * Math.pow(Math.E, (-1 * distance) / bp) * Math.cos(Math.toRadians(angle));
        final double r_y = eij.getY() * ap * Math.pow(Math.E, (-1 * distance) / bp) * Math.cos(Math.toRadians(angle));

        return new Vector2D(r_x, r_y);
    }


}
