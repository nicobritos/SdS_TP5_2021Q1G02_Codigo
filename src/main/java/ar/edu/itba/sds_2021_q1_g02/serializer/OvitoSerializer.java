package ar.edu.itba.sds_2021_q1_g02.serializer;

import ar.edu.itba.sds_2021_q1_g02.models.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.LinkedList;

public class OvitoSerializer extends Serializer {
    private final StepFormatter stepFormatter;
    private final ParticleFormatter particleFormatter;
    private final FileFormatter fileFormatter;
    private final SimulationConfiguration configuration;

    public OvitoSerializer(StepFormatter stepFormatter, ParticleFormatter particleFormatter, FileFormatter fileFormatter, SimulationConfiguration configuration, double serializeEvery) {
        super(serializeEvery);

        this.stepFormatter = stepFormatter;
        this.particleFormatter = particleFormatter;
        this.fileFormatter = fileFormatter;
        this.configuration = configuration;
    }

    @Override
    public void serialize(Collection<Particle> particles, Step step) {
        if (!this.serialize(step))
            return;

        File file = new File(this.fileFormatter.formatFilename(step.getStepNumber()));
        try {
            if (file.exists())
                Files.delete(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Couldn't delete file: " + file.getName());
        }

        try {
            file.getParentFile().mkdirs();
//            if (!file.mkdirs())
//                throw new RuntimeException("Couldn't create file: " + file.getName());

            FileWriter writer = new FileWriter(file);

            Collection<Particle> allParticles = this.getParticlesWithWall(particles);
            writer.write(this.stepFormatter.format(allParticles, step));
            writer.write("\n");
            for (Particle p : allParticles) {
                writer.write(this.particleFormatter.format(p, step));
                writer.write("\n");
            }

            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Collection<Particle> getParticlesWithWall(Collection<Particle> particles) {
        Collection<Particle> allParticles = new LinkedList<>(particles);
        allParticles.addAll(this.getWallParticles());
        return allParticles;
    }

    private Collection<Particle> getWallParticles() {
        int startId = -1;
        Collection<Particle> particles = new LinkedList<>(this.getDoorParticles(startId));
        startId -= particles.size();
        particles.addAll(this.getCornerParticles(startId));

        return particles;
    }

    private Collection<Particle> getCornerParticles(int startId) {
        Collection<Particle> particles = new LinkedList<>();

        particles.add(this.generatePointParticle(startId--, new Position(0, this.configuration.getBounds().getHeight())));
        particles.add(this.generatePointParticle(startId, new Position(this.configuration.getBounds().getWidth(), 0)));

        return particles;
    }

    private Collection<Particle> getDoorParticles(int startId) {
        double maxY = (this.configuration.getBounds().getHeight() + this.configuration.getBounds().getDoorsSize()) / 2;
        double minY = (this.configuration.getBounds().getHeight() - this.configuration.getBounds().getDoorsSize()) / 2;
        double radius = 0.15;

        Collection<Particle> particles = new LinkedList<>(this.getHumanDoorParticles(startId, minY, maxY, radius));
        startId -= particles.size();
        particles.addAll(this.getZombieDoorParticles(startId, minY, maxY, radius));

        return particles;
    }

    private Collection<Particle> getHumanDoorParticles(int startId, double minY, double maxY, double radius) {
        Collection<Particle> particles = new LinkedList<>();

        double diameter = radius * 2;

        double y = minY;
        while (y < maxY) {
            particles.add(this.getHumanWallParticle(startId--, y, radius));
            y += diameter;
        }
        if (y - diameter < maxY)
            particles.add(this.getHumanWallParticle(startId, y, radius));

        return particles;
    }

    private Collection<Particle> getZombieDoorParticles(int startId, double minY, double maxY, double radius) {
        Collection<Particle> particles = new LinkedList<>();

        double diameter = radius * 2;

        double y = minY;
        while (y < maxY) {
            particles.add(this.getZombieWallParticle(startId--, y, radius));
            y += diameter;
        }
        if (y - diameter < maxY)
            particles.add(this.getZombieWallParticle(startId, y, radius));

        return particles;
    }

    private Particle getHumanWallParticle(int id, double y, double radius) {
        return new Particle(
                id,
                new Radius(radius, radius, radius),
                new Position(0, y),
                Velocity.ZERO,
                Type.HUMAN_DOOR
        );
    }

    private Particle getZombieWallParticle(int id, double y, double radius) {
        return new Particle(
                id,
                new Radius(radius, radius, radius),
                new Position(this.configuration.getBounds().getWidth(), y),
                Velocity.ZERO,
                Type.ZOMBIE_DOOR
        );
    }

    private Particle generatePointParticle(int id, Position position) {
        return new Particle(
                id,
                new Radius(0.01, 0.01, 0.01),
                position,
                Velocity.ZERO,
                Type.HUMAN
        );
    }
}
