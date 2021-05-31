package ar.edu.itba.sds_2021_q1_g02.models;

import java.math.BigDecimal;
import java.util.Map;

public class Step {
    private BigDecimal deltaTime;
    private BigDecimal absoluteTime;
    private final int stepNumber;
    private boolean isLastStep = false;

    public Step(BigDecimal deltaTime, BigDecimal absoluteTime, int stepNumber) {
        this.deltaTime = deltaTime;
        this.absoluteTime = absoluteTime;
        this.stepNumber = stepNumber;
    }

    public BigDecimal getRelativeTime() {
        return this.deltaTime;
    }

    public BigDecimal getAbsoluteTime() {
        return this.absoluteTime;
    }

    public int getStepNumber() {
        return this.stepNumber;
    }

    public Step copy() {
        return new Step(
                this.deltaTime,
                this.absoluteTime,
                this.stepNumber
        );
    }

    public void setAbsoluteTime(BigDecimal absoluteTime) {
        this.absoluteTime = absoluteTime;
    }

    public void setRelativeTime(BigDecimal relativeTime) {
        this.deltaTime = relativeTime;
    }

    public boolean isLastStep() {
        return this.isLastStep;
    }

    public void setLastStep(boolean lastStep) {
        this.isLastStep = lastStep;
    }
}
