package ar.edu.itba.sds_2021_q1_g02.serializer;

import ar.edu.itba.sds_2021_q1_g02.models.Particle;
import ar.edu.itba.sds_2021_q1_g02.models.SimulationConfiguration;
import ar.edu.itba.sds_2021_q1_g02.models.Step;

import java.util.Collection;

public class ConsoleSerializer extends Serializer {
    private static final int CONSOLE_SERIALIZER_LIMIT = 50;

    private final SystemFormatter systemFormatter;
    private final StepFormatter stepFormatter;
    private final ParticleFormatter particleFormatter;

    public ConsoleSerializer(SystemFormatter systemFormatter, StepFormatter stepFormatter, ParticleFormatter particleFormatter, double serializeEvery) {
        super(serializeEvery);

        this.systemFormatter = systemFormatter;
        this.stepFormatter = stepFormatter;
        this.particleFormatter = particleFormatter;
    }

    @Override
    public void serializeSystem(Collection<Particle> particles, SimulationConfiguration configuration) {
        System.out.println(this.systemFormatter.format(particles, configuration));
    }

    @Override
    public void serialize(Collection<Particle> particles, Step step) {
        if (!this.serialize(step))
            return;

        System.out.println(this.stepFormatter.format(particles, step));

        if (particles.size() < CONSOLE_SERIALIZER_LIMIT) {
            for (Particle p : particles) {
                System.out.println(this.particleFormatter.format(p, step));
            }
        }

        System.out.println("----------------------");
    }
}
