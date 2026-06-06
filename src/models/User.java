package models;

import java.util.UUID;

public class User {

    private UUID id;
    private String name;
    private boolean supervisor;

    public User(String name, boolean supervisor) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.supervisor = supervisor;
    }

    public UUID getId() { return id; }

    public String getName() { return name; }

    public boolean isSupervisor() { return supervisor; }

    public void setName(String name) { this.name = name; }

    public void setSupervisor(boolean supervisor) { this.supervisor = supervisor; }

    @Override
    public String toString() { return name; }
}
