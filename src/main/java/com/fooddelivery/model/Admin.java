package com.fooddelivery.model;

import com.fooddelivery.enums.Role;

public class Admin extends AbstractUser {
    public Admin(String id, String name, String email, String password) {
        this(id, name, email, password, false);
    }

    public Admin(String id, String name, String email, String password, boolean superAdmin) {
        super(id, name, email, password, superAdmin ? Role.SUPER_ADMIN : Role.ADMIN);
    }
}
