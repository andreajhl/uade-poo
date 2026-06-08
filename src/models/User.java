package models;

import models.enums.Permission;

import java.util.UUID;

public class User {

    private UUID id;
    private String username;
    private String fullName;
    private Role role;

    public User(String username, String fullName, Role role) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public boolean hasPermission(Permission permission) {
        return role != null && role.hasPermission(permission);
    }

    public UUID getId() { return id; }

    public String getUsername() { return username; }

    public String getFullName() { return fullName; }

    public Role getRole() { return role; }

    public void setUsername(String username) { this.username = username; }

    public void setFullName(String fullName) { this.fullName = fullName; }

    public void setRole(Role role) { this.role = role; }

    @Override
    public String toString() { return fullName + " (" + role + ")"; }
}
