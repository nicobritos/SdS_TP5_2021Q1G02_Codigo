package ar.edu.itba.sds_2021_q1_g02;//package ar.edu.itba.sds_2021_q1_g02;

import ar.edu.itba.sds_2021_q1_g02.models.*;
import ar.edu.itba.sds_2021_q1_g02.serializer.OvitoSerializer;
import ar.edu.itba.sds_2021_q1_g02.serializer.SimulationSerializer;

public class App {
    private static final Bounds BOUNDS = new Bounds(
            20,
            20,
            10,
            3
    );

    private static final double DT = 0.01; // Paper dice 0.05
    private static final double MIN_RADIUS = 0.1; // Paper set of parameters 2
    private static final double MAX_RADIUS = 0.37;
    private static final double BETA = 0.9;
    private static final double ZOMBIES_FOV = 5; // Metros
    private static final double ZOMBIE_TURN_TIME = 7; // Segundos
    private static final int SPAWN_HUMANS_EVERY = 9;
    private static final int HUMANS_PER_SPAWN = 20;
    private static final int MAX_HUMANS = 100;
    private static final double VH = 1.6;
    private static final double HUMAN_RADIUS = 0.1;
    private static final double ZOMBIE_RADIUS = 0.1;

    private static final int[] S_B_ZOMBIES = {10};
//    private static final int[] S_B_ZOMBIES = {2, 5, 10, 15, 20, 25, 30, 35};
    private static final double[] S_C_VZS = {0.4, 0.8, 1.2, 1.6, 2, 2.4};

    private static final double SERIALIZE_EVERY = 0.5;
    private static final SimulationSerializer SIMULATION_SERIALIZER = new SimulationSerializer(
            step -> "output/simulation_1.tsv",
            App.SERIALIZE_EVERY
    );

    public static void main(String[] args) {
        System.out.println("------------- 3.b -------------");
        App.simulationA();
        System.out.println("-------------------------------");
    }

    private static void simulationA() {
        for (int i = 0; i < 6; i++) {
            App.simulate(App.S_B_ZOMBIES[0], App.S_C_VZS[i]);
        }
    }

    private static void simulate(int maxZombies, double vz) {
        SimulationConfiguration configuration = new SimulationConfiguration(
                App.DT,
                new ParticleConfiguration(
                        App.MIN_RADIUS,
                        App.MAX_RADIUS,
                        App.BETA,
                        App.VH,
                        vz,
                        App.ZOMBIES_FOV
                ),
                App.ZOMBIE_TURN_TIME,
                maxZombies,
                App.MAX_HUMANS,
                App.SPAWN_HUMANS_EVERY,
                App.HUMANS_PER_SPAWN,
                App.BOUNDS,
                App.HUMAN_RADIUS,
                App.ZOMBIE_RADIUS

        );

        Simulation simulation = new Simulation(configuration);

        simulation.addSerializer(new OvitoSerializer(
                (systemParticles, step) -> systemParticles.size() + "\n" + "Properties=id:R:1:radius:R:1:pos:R" +
                        ":2:Velocity:R:2:color:R:3",
                (particle, step) -> {
                    Color color = getParticleColor(particle);

                    // id (1), radius (1), pos (2), size (1), color (3, RGB)";
                    return particle.getId() + "\t" +
                            particle.getRadius() + "\t" +
                            particle.getPosition().getX() + "\t" +
                            particle.getPosition().getY() + "\t" +
                            particle.getVelocity().getxSpeed() + "\t" +
                            particle.getVelocity().getySpeed() + "\t" +
                            color.getRed() + "\t" +
                            color.getGreen() + "\t" +
                            color.getBlue();
                },
                step -> "output/simulation_" + maxZombies + "_" + vz + "_" + step + ".xyz",
                configuration,
                App.SERIALIZE_EVERY
        ));

        simulation.simulate();
    }

    private static Color getParticleColor(Particle particle) {
        if (particle.getId() < 0) {
            if (particle.getType().equals(Type.CORNER)) {
                return new Color(0.0001, 0.0001, 0.0001);
            } else if (particle.getType().equals(Type.ZOMBIE_DOOR)) {
                return new Color(1, 0, 1);
            } else if (particle.getType().equals(Type.HUMAN_DOOR)) {
                return new Color(0, 1, 0);
            }
        }

        if (particle.getType().equals(Type.HUMAN)) {
            return new Color(0, 0, 1.0);
        } else if (particle.getType().equals(Type.BITTEN_HUMAN)) {
            return new Color(1.0, 0, 1.0);
        } else {
            return new Color(1.0, 0, 0);
        }
    }
}

//import ar.edu.itba.sds_2021_q1_g02.models.Position;
//import ar.edu.itba.sds_2021_q1_g02.utils.Vector2DUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class App {
//    public static void main(String[] args) {
//        final Position positionA = new Position(0, 0);
//        final Position positionB = new Position(0.35, 0.25);
//        final Position positionC = new Position(0,1);
//        final Position positionD = new Position(0,-1);
//        final Position positionE = new Position(-1,0);
//        final Position positionF = new Position(0.5,0.5);
//        final Position positionG = new Position(0.147,-0.2);
//
//        List<Position> particles = new ArrayList<>();
//        particles.add(positionC);
//        particles.add(positionD);
//        particles.add(positionE);
//        particles.add(positionF);
//        particles.add(positionG);
//
//        List<Position> posi = Vector2DUtils.computeNeighbors(positionA,positionB, particles);
//        System.out.println(posi);
//
//    }
//}