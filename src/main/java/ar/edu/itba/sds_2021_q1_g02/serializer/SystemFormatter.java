package ar.edu.itba.sds_2021_q1_g02.serializer;

import ar.edu.itba.sds_2021_q1_g02.models.Particle;
import ar.edu.itba.sds_2021_q1_g02.models.SimulationConfiguration;

import java.util.Collection;

@FunctionalInterface
public interface SystemFormatter {
    String format(Collection<Particle> systemParticles, SimulationConfiguration configuration);
}
