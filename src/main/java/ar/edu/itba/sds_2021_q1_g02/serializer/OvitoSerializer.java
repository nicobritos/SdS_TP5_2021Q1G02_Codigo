package ar.edu.itba.sds_2021_q1_g02.serializer;

import ar.edu.itba.sds_2021_q1_g02.models.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

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

            writer.write(this.stepFormatter.format(particles, step));
            writer.write("\n");
            writer.write(this.particleFormatter.format(
                    this.generatePointParticle(new Position(0, this.configuration.getBounds().getHeight())),
                    step
            ));
            writer.write("\n");
            writer.write(this.particleFormatter.format(
                    this.generatePointParticle(new Position(this.configuration.getBounds().getWidth(), 0)),
                    step
            ));
            writer.write("\n");
            for (Particle p : particles) {
                writer.write(this.particleFormatter.format(p, step));
                writer.write("\n");
            }

            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Particle generatePointParticle(Position position) {
        return new Particle(
                -2,
                new Radius(0.01, 0.01, 0.01),
                position,
                new Velocity(0, 0),
                State.HUMAN
        );
    }
}
