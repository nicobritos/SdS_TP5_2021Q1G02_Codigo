package ar.edu.itba.sds_2021_q1_g02.serializer;

import ar.edu.itba.sds_2021_q1_g02.models.Particle;
import ar.edu.itba.sds_2021_q1_g02.models.Position;
import ar.edu.itba.sds_2021_q1_g02.models.SimulationConfiguration;
import ar.edu.itba.sds_2021_q1_g02.models.Step;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SimulationSerializer extends Serializer {
    private final FileFormatter fileFormatter;
    private TreeMap<Double, Map<SimulationConfiguration, Position>> results;

    public SimulationSerializer(FileFormatter fileFormatter, double serializeEvery) {
        super(serializeEvery);

        this.fileFormatter = fileFormatter;
        this.reset();
    }

//    public void finish() {
//        File file = new File(this.fileFormatter.formatFilename(0));
//        if (file.exists() && !file.delete())
//            throw new RuntimeException("Couldn't delete file: " + file.getName());
//
//        try {
//            file.getParentFile().mkdirs();
//            if (!file.createNewFile())
//                throw new RuntimeException("Couldn't create file: " + file.getName());
//
//            FileWriter writer = new FileWriter(file);
//
//            // "abs sist1 sist2 sist3 sist4";
//            StringBuilder builder = new StringBuilder();
//            builder.append("abs");
//            for (IntegrationAlgorithm integrationAlgorithm : this.integrationAlgorithms) {
//                builder.append("\t\"");
//                builder.append(integrationAlgorithm.getName());
//                builder.append("\"");
//            }
//            writer.write(builder + "\n");
//
//            // "abs sist1 sist2 sist3 sist4";
//            for (Map.Entry<Double, Map<IntegrationAlgorithm, Position>> entry : this.results.entrySet()) {
//                builder = new StringBuilder();
//                builder.append(entry.getKey());
//                for (IntegrationAlgorithm integrationAlgorithm : this.integrationAlgorithms) {
//                    builder.append("\t");
//                    builder.append(entry.getValue().get(integrationAlgorithm).getX());
//                }
//
//                writer.write(builder + "\n");
//            }
//
//            writer.close();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        this.reset();
//    }

    @Override
    public void serialize(Collection<Particle> particles, Step step) {
//        if (step.getStep() == 0) {
//            this.restartCount();
//            this.integrationAlgorithms.add(step.getIntegrationAlgorithm());
//        } else if (!this.serialize(step)) {
//            return;
//        }
//
//        Particle particle = particles.stream().findFirst().get();
//        this.results.computeIfAbsent(step.getAbsoluteTime().doubleValue(), abs -> new HashMap<>()).put(step.getIntegrationAlgorithm(), particle.getPosition());
    }

    public void reset() {
//        this.integrationAlgorithms = new LinkedList<>();
//        this.results = new TreeMap<>();
//        this.restartCount();
    }
}
