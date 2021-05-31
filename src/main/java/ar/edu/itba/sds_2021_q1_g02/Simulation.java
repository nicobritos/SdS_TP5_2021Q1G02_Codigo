package ar.edu.itba.sds_2021_q1_g02;

import ar.edu.itba.sds_2021_q1_g02.models.*;
import ar.edu.itba.sds_2021_q1_g02.serializer.Serializable;
import javafx.util.Pair;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class Simulation extends Serializable {
    private final Collection<Particle> particles;
    private final SimulationConfiguration configuration;

    public Simulation(SimulationConfiguration configuration) {
        this.particles = new LinkedList<>();
        this.configuration = configuration;
    }

    public void simulate() {
//        this.serializeSystem(this.particles, this.configuration);
//        Step step = this.calculateFirstStep();
//        this.serialize(this.particles, step);

//        while (step.getAbsoluteTime().doubleValue() < this.maxTime) {
//            step = this.simulateStep(step);
//            if (step.getAbsoluteTime().doubleValue() >= this.maxTime)
//                step.setLastStep(true);
//
//            this.serialize(this.particles, step);
//        }
    }

//    private Step simulateStep(Step previousStep) {
//        Step newStep = new Step(
//                Collections.singletonMap(this.particle, this.particle.getPosition()),
//                Collections.singletonMap(this.particle, this.particle.getVelocity()),
//                this.dt,
//                previousStep.getAbsoluteTime().add(this.dt),
//                previousStep.getStep() + 1,
//                this.integrationAlgorithm
//        );
//
//        Pair<Position, Velocity> newVelocityPositions = this.integrationAlgorithm.perform(this.particle, previousStep);
//
//        this.particle.setPosition(newVelocityPositions.getKey());
//        this.particle.setVelocity(newVelocityPositions.getValue());
//
//        return newStep;
//    }

//    private Step calculateFirstStep() {
//        return new Step(
//                Collections.emptyMap(),
//                Collections.emptyMap(),
//                this.dt,
//                BigDecimal.ZERO,
//                0,
//                this.integrationAlgorithm
//        );
//    }
}
