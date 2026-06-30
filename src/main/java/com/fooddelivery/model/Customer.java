package com.fooddelivery.model;

import com.fooddelivery.enums.Role;

public class Customer extends AbstractUser {
    private String phoneNumber;
    private String houseNo;
    private String mainAddress;
    private String pincode;

    public Customer(String id, String name, String email, String password, String phoneNumber, String houseNo, String mainAddress, String pincode) {
        super(id, name, email, password, Role.CUSTOMER);
        this.phoneNumber = phoneNumber;
        this.houseNo = houseNo;
        this.mainAddress  = mainAddress;
        this.pincode = pincode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getHouseNo() {
        return houseNo;
    }
    public String getMainAddress() {return mainAddress;}
    public String getPincode() {return pincode;}

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }
    public void setMainAddress(String mainAddress) {this.mainAddress = mainAddress;}
    public void setPincode(String pincode) {this.pincode = pincode;}

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + houseNo + '\'' +
                ", address='" + mainAddress + '\'' +
                ", address='" + pincode + '\'' +
                '}';
    }
}
