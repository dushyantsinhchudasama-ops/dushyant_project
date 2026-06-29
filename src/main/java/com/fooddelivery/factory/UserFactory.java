package com.fooddelivery.factory;

import com.fooddelivery.model.Admin;
import com.fooddelivery.model.Customer;
import com.fooddelivery.model.DeliveryPerson;
import com.fooddelivery.model.AbstractUser;

public final class UserFactory {
    private UserFactory() {
        // Prevent instantiation
    }

    public static Admin createAdmin(String id, String name, String email, String password) {
        return createAdmin(id, name, email, password, false);
    }

    public static Admin createAdmin(String id, String name, String email, String password, boolean superAdmin) {
        return new Admin(id, name, email, password, superAdmin);
    }

    public static Customer createCustomer(String id, String name, String email, String password, String phoneNumber, String address) {
        return new Customer(id, name, email, password, phoneNumber, address);
    }

    public static DeliveryPerson createDeliveryPerson(String id, String name, String email, String password, String phoneNumber, String vehicleNumber) {
        return new DeliveryPerson(id, name, email, password, phoneNumber, vehicleNumber);
    }
}
