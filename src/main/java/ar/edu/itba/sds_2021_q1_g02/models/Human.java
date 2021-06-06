package ar.edu.itba.sds_2021_q1_g02.models;

import java.util.HashSet;
import java.util.Set;

public class Human extends Particle {
    private final Set<Zombie> chasedBy;

    public Human(int id, Radius radius, Position position, Velocity velocity) {
        super(id, radius, position, velocity, Type.HUMAN);

        this.chasedBy = new HashSet<>();
    }

    public boolean hasBeenBitten() {
        return this.getType().equals(Type.BITTEN_HUMAN);
    }

    public void bite() {
        this.setType(Type.BITTEN_HUMAN);
        // TODO: Iterate chasing zombies to recalculate new next position
    }

    public Zombie convertToZombie() {
        return new Zombie(this.getId(), this.getRadius(), this.getPosition(), this.getVelocity());
    }

    public void addChasingZombie(Zombie zombie) {
        this.chasedBy.add(zombie);
    }

    public void removeChasingZombie(Zombie zombie) {
        this.chasedBy.remove(zombie);
    }

    @Override
    protected Particle childCopy() {
        Human human = new Human(this.getId(), this.getRadius(), this.getPosition(), this.getVelocity());
        human.setType(Type.BITTEN_HUMAN);
        return human;
    }
}