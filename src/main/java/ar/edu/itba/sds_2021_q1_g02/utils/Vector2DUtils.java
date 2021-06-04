package ar.edu.itba.sds_2021_q1_g02.utils;

import ar.edu.itba.sds_2021_q1_g02.models.*;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.List;


public class Vector2DUtils {

    public static double calculateAngleByThreePositions(Position vertex, Position positionA, Position positionB) {
        final double dva = vertex.distanceTo(positionA);
        final double dvb = vertex.distanceTo(positionB);
        final double dab = positionA.distanceTo(positionB);

        final double angle = Math.acos((Math.pow(dva, 2) + Math.pow(dvb, 2) - Math.pow(dab, 2)) / (2 * dva * dvb));
        return angle * 180 / Math.PI;
    }

    public static Vector2D calculateVectorFromTwoPositions(Position positionA, Position positionB) {
        final double x = positionA.getX() - positionB.getX();
        final double y = positionA.getY() - positionB.getY();
        return new Vector2D(x / Math.abs(x), y / Math.abs(y));
    }

    public static Vector2D calculateNormalizedVector(Vector2D vector) {
        double module = Math.sqrt(Math.pow(vector.getX(), 2) + Math.pow(vector.getY(), 2));
        module = module == 0 ? 1 : module;

        return new Vector2D(vector.getX() / module, vector.getY() / module);
    }

    public static List<Particle> getValidParticlesByTwoPositions(Position positionA, Position positionB, List<Particle> particles) {
        final double m = (positionB.getY() - positionA.getY()) / (positionB.getX() - positionA.getX());
        final double p_m = m == 0 ? 1 : -1 / m;
        final double b = positionA.getY() - p_m * positionA.getX();
        List<Particle> validParticles = new ArrayList<>();
        for (Particle particle : particles) {
            final Position pos = particle.getPosition();
            final double y = p_m * pos.getX() + b;
            if (pos.getY() >= y) validParticles.add(particle);
        }
        return validParticles;
    }

    public static Particle getParticleToFollowByPositionAndRange(Position position, double range, List<Particle> particles) {
        Particle particleToFollow = null;
        double distanceToParticleToFollow = 0;
        for (Particle particle : particles) {
            final double x = particle.getPosition().getX();
            final double y = particle.getPosition().getY();
            if ((x >= position.getX() - 5 && x <= position.getX() + 5) && (y >= position.getY() - 5 && y <= position.getY() + 5)) {
                final double distance = position.distanceTo(particle.getPosition());
                if(particleToFollow == null || distance < distanceToParticleToFollow) {
                    particleToFollow = particle;
                    distanceToParticleToFollow = distance;
                }
            }
        }
        return particleToFollow;
    }
}
