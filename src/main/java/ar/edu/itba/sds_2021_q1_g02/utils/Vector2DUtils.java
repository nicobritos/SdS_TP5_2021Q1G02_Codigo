package ar.edu.itba.sds_2021_q1_g02.utils;

import ar.edu.itba.sds_2021_q1_g02.models.*;


public class Vector2DUtils {

    public static double calculateAngleByThreePositions(Position vertex, Position positionA, Position positionB) {
        final double dva = vertex.distanceTo(positionA);
        final double dvb = vertex.distanceTo(positionB);
        final double dab = positionA.distanceTo(positionB);

        double div = 2 * dva * dvb;
        if (div == 0)
            div = 1;

        double cosValue = (Math.pow(dva, 2) + Math.pow(dvb, 2) - Math.pow(dab, 2)) / div;
        if (cosValue > 1)
            cosValue = 1;
        else if (cosValue < -1)
            cosValue = -1;

        return Math.acos(cosValue) * 180 / Math.PI;
    }

    public static Vector2D calculateVectorFromTwoPositions(Position positionA, Position positionB) {
        final double x = positionA.getX() - positionB.getX();
        final double y = positionA.getY() - positionB.getY();
        return new Vector2D(x, y);
    }

    public static Vector2D calculateNormalizedVector(Vector2D vector) {
        double module = Math.sqrt(Math.pow(vector.getX(), 2) + Math.pow(vector.getY(), 2));
        module = module == 0 ? 1 : module;

        return new Vector2D(vector.getX() / module, vector.getY() / module);
    }
}
