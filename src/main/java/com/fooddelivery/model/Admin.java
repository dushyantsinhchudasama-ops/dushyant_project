package com.fooddelivery.model;

import com.fooddelivery.enums.Role;

public class Admin extends AbstractUser {
    public Admin(String id, String name, String email, String password) {
        super(id, name, email, password, Role.ADMIN);
    }
}
