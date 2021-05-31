package ar.edu.itba.sds_2021_q1_g02.serializer;

import ar.edu.itba.sds_2021_q1_g02.models.Particle;
import ar.edu.itba.sds_2021_q1_g02.models.SimulationConfiguration;
import ar.edu.itba.sds_2021_q1_g02.models.Step;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public abstract class Serializable {
    private final List<Serializer> serializers = new LinkedList<>();

    public void addSerializer(Serializer serializer) {
        this.serializers.add(serializer);
    }

    protected void serialize(Collection<Particle> particles, Step step) {
        for (Serializer serializer : this.serializers) {
            serializer.serialize(particles, step);
        }
    }

    protected void serializeSystem(Collection<Particle> particles, SimulationConfiguration configuration) {
        for (Serializer serializer : this.serializers) {
            serializer.serializeSystem(particles, configuration);
        }
    }
}
