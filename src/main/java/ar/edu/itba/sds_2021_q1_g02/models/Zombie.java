package ar.edu.itba.sds_2021_q1_g02.models;

public class Zombie extends Particle {
    private Human chasing;

    public Zombie(int id, ParticleZone particleZone, double radius, Position position, Velocity velocity) {
        super(id, particleZone, radius, position, velocity, Type.ZOMBIE);
    }

    public void setChasing(Human human) {
        this.chasing = human;
    }

    public Human getChasing() {
        return this.chasing;
    }

    @Override
    protected Particle childCopy() {
        return new Zombie(this.getId(), this.getParticleZone(), this.getRadius(), this.getPosition(),
                this.getVelocity());
    }
}