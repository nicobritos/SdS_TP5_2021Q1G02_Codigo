package ar.edu.itba.sds_2021_q1_g02.models;

import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public class Particle {
    private final int id;
    private final Radius radius;
    private Position position;
    private Velocity velocity;
    private Type type;

    public Particle(int id, Radius radius, Position position, Velocity velocity, Type type) {
        this.id = id;
        this.radius = radius;
        this.position = position;
        this.velocity = velocity;
        this.type = type;
    }

    public int getId() {
        return this.id;
    }

    public Radius getRadius() {
        return this.radius;
    }

    public Position getPosition() {
        return this.position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Velocity getVelocity() {
        return this.velocity;
    }

    public void setVelocity(Velocity velocity) {
        this.velocity = velocity;
    }

    public void setType(Type property) {
        this.type = property;
    }

    public Type getType() {
        return this.type;
    }

    public Particle copy() {
        Particle particle = new Particle(this.id, this.radius, this.position, this.velocity, this.type);

        if (this.position != null)
            particle.setPosition(this.position.copy());
        if (this.velocity != null)
            particle.setVelocity(this.velocity.copy());

        return particle;
    }

    public double distanceTo(Particle other) {
        double distance = this.rawDistanceTo(other);
        if (distance <= 0)
            return 0;
        return distance;
    }

    public boolean isInContact(Particle other) {
        return this.rawDistanceTo(other) <= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Particle)) return false;
        Particle particle = (Particle) o;
        return this.getId() == particle.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    private double rawDistanceTo(Particle other) {
        double centerDistance = this.getPosition().distanceTo(other.getPosition());
        if (centerDistance == 0)
            return 0;

        return (centerDistance - this.getRadius().getCurrentRadius()) - other.getRadius().getCurrentRadius();
    }
}