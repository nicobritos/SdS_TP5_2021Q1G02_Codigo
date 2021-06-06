package ar.edu.itba.sds_2021_q1_g02.models;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Human extends Particle {
    private final Set<Zombie> chasedBy;
    private boolean zombie;

    public Human(int id, ParticleZone particleZone, double radius, Position position, Velocity velocity) {
        super(id, particleZone, radius, position, velocity, Type.HUMAN);

        this.chasedBy = new HashSet<>();
        this.zombie = false;
    }

    public boolean hasBeenBitten() {
        return this.getType().equals(Type.BITTEN_HUMAN) && !this.zombie;
    }

    public void bite() {
        this.setType(Type.BITTEN_HUMAN);
    }

    public Zombie toZombie(ParticleZone particleZone, double radius) {
        this.zombie = true;
        return new Zombie(this.getId(), particleZone, radius, this.getPosition(), this.getVelocity());
    }

    public void addChasingZombie(Zombie zombie) {
        this.chasedBy.add(zombie);
    }

    public void removeChasingZombie(Zombie zombie) {
        this.chasedBy.remove(zombie);
    }

    public Set<Zombie> getChasingZombies() {
        return new HashSet<>(this.chasedBy);
    }

    @Override
    protected Particle childCopy() {
        Human human = new Human(this.getId(), this.getParticleZone(), this.getRadius(), this.getPosition(),
                this.getVelocity());
        human.setType(Type.BITTEN_HUMAN);
        return human;
    }
}