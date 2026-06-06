package models;

import java.util.UUID;

public class Category {

    private UUID id;
    private String name;

    public Category(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    public UUID getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    @Override
    public String toString() { return name; }
}
