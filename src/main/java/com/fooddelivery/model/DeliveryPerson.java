package com.fooddelivery.model;

import com.fooddelivery.enums.Role;

public class DeliveryPerson extends AbstractUser {
    private String phoneNumber;
    private String vehicleNumber;
    private boolean available;

    public DeliveryPerson(String id, String name, String email, String password, String phoneNumber, String vehicleNumber) {
        super(id, name, email, password, Role.DELIVERY_PERSON);
        this.phoneNumber = phoneNumber;
        this.vehicleNumber = vehicleNumber;
        this.available = true;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "DeliveryPerson{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", vehicleNumber='" + vehicleNumber + '\'' +
                ", available=" + available +
                '}';
    }
}
