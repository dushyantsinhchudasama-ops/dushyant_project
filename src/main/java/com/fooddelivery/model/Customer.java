package com.fooddelivery.model;

import com.fooddelivery.enums.Role;

public class Customer extends AbstractUser {
    private String phoneNumber;
    private String address;

    public Customer(String id, String name, String email, String password, String phoneNumber, String address) {
        super(id, name, email, password, Role.CUSTOMER);
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
