package models;

import models.enums.Permission;

import java.util.ArrayList;
import java.util.List;

public class Role {

    private String name;
    private List<Permission> permissions;

    public Role(String name) {
        this.name = name;
        this.permissions = new ArrayList<>();
    }

    public void addPermission(Permission permission) {
        if (!permissions.contains(permission)) permissions.add(permission);
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }

    public String getName() { return name; }

    public List<Permission> getPermissions() { return permissions; }

    @Override
    public String toString() { return name; }
}
