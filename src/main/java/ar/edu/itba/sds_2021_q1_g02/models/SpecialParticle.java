package ar.edu.itba.sds_2021_q1_g02.models;

public class SpecialParticle extends Particle {
    public SpecialParticle(int id, Radius radius, Position position, Velocity velocity, Type type) {
        super(id, radius, position, velocity, type);
    }

    @Override
    protected Particle childCopy() {
        return new SpecialParticle(
                this.getId(),
                this.getRadius(),
                this.getPosition(),
                this.getVelocity(),
                this.getType()
        );
    }
}