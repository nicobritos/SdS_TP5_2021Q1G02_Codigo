package ar.edu.itba.sds_2021_q1_g02.models;

public class Zombie extends Particle {
    private Human chasing;

    public Zombie(int id, Radius radius, Position position, Velocity velocity) {
        super(id, radius, position, velocity, Type.ZOMBIE);
    }

    public void setChasing(Human human) {
        this.chasing = human;
    }

    public Human getChasing() {
        return this.chasing;
    }

    @Override
    protected Particle childCopy() {
        return new Zombie(this.getId(), this.getRadius(), this.getPosition(), this.getVelocity());
    }
}