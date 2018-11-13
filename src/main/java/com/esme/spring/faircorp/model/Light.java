package com.esme.spring.faircorp.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * @author devmind
 */
@Entity
public class Light {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private Integer level;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    private Room room;

    public Light() {
    }

    // ...
    public Light(Room room, Integer level, Status status) {
        this.level = level;
        this.status = status;
        this.room = room;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Light light = (Light) o;
        return Objects.equals(level, light.level) &&
                status == light.status &&
                Objects.equals(room, light.room);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, status, room);
    }

    @Override
    public String toString() {
        return "Light{" +
                "id=" + id +
                ", level=" + level +
                ", status=" + status +
                ", room=" + room +
                '}';
    }
}
