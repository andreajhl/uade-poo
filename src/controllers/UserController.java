package controllers;

import models.Role;
import models.User;
import models.enums.Permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class UserController {

    private static UserController instance;
    private HashMap<UUID, User> users;
    private User currentUser;

    private UserController() {
        this.users = new HashMap<>();
        seedDefaultUsers();
    }

    public static UserController getInstance() {
        if (instance == null) instance = new UserController();
        return instance;
    }

    private void seedDefaultUsers() {
        Role supervisor = new Role("Supervisor");
        supervisor.addPermission(Permission.AUTHORIZE_PURCHASE_ORDER);
        supervisor.addPermission(Permission.AUTHORIZE_VOUCHER);
        supervisor.addPermission(Permission.REGISTER_VOUCHER);
        supervisor.addPermission(Permission.ISSUE_PAYMENT_ORDER);

        Role operator = new Role("Operador");
        operator.addPermission(Permission.REGISTER_VOUCHER);
        operator.addPermission(Permission.ISSUE_PAYMENT_ORDER);

        User admin = new User("admin", "Administrador", supervisor);
        User maria = new User("maria", "María González", supervisor);
        User juan = new User("juan", "Juan Pérez", operator);

        users.put(admin.getId(), admin);
        users.put(maria.getId(), maria);
        users.put(juan.getId(), juan);
    }

    public void login(User user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public User create(String username, String fullName, Role role) {
        User user = new User(username, fullName, role);
        users.put(user.getId(), user);
        return user;
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public List<User> findByPermission(Permission permission) {
        List<User> result = new ArrayList<>();
        for (User u : users.values()) {
            if (u.hasPermission(permission)) result.add(u);
        }
        return result;
    }
}
