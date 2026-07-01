package com.fooddelivery.model;

import com.fooddelivery.enums.OrderStatus;
import com.fooddelivery.enums.PaymentType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private final String id;
    private final String customerId;
    private String deliveryPersonId;
    private final List<OrderItem> items = new ArrayList<>();
    private final double originalAmount;
    private final double discountPercentage;
    private final double discountAmount;
    private final double finalAmount;
    private final PaymentType paymentType;
    private final String paymentDetails;
    private final String houseNO;
    private final String mainAddress;
    private final String pincode;
    private OrderStatus status;
    private final LocalDateTime orderDate;

    public Order(String id,
                 String customerId,
                 String deliveryPersonId,
                 List<OrderItem> items,
                 double originalAmount,
                 double discountPercentage,
                 double discountAmount,
                 double finalAmount,
                 PaymentType paymentType,
                 String paymentDetails,
                 String houseNO,
                 String mainAddress,
                 String pincode,
                 OrderStatus status,
                 LocalDateTime orderDate) {
        this.id = id;
        this.customerId = customerId;
        this.deliveryPersonId = deliveryPersonId;
        if (items != null) {
            this.items.addAll(items);
        }
        this.originalAmount = originalAmount;
        this.discountPercentage = discountPercentage;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.paymentType = paymentType;
        this.paymentDetails = paymentDetails;
        this.houseNO = houseNO;
        this.mainAddress = mainAddress;
        this.pincode = pincode;
        this.status = status;
        this.orderDate = orderDate;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getDeliveryPersonId() {
        return deliveryPersonId;
    }

    public void setDeliveryPersonId(String deliveryPersonId) {
        this.deliveryPersonId = deliveryPersonId;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public double getOriginalAmount() {
        return originalAmount;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public String getPaymentDetails() {
        return paymentDetails;
    }

    public String getPaymentMethodName() {
        return paymentType == null ? "cash" : paymentType.name().toLowerCase();
    }

    public String getHouseNO() {
        return houseNO;
    }

    public String getPincode() {
        return pincode;
    }

    public String getMainAddress() {
        return mainAddress;
    }

    public String getDeliveryAddress() {
        StringBuilder address = new StringBuilder();
        if (houseNO != null && !houseNO.isBlank()) {
            address.append(houseNO);
        }
        if (mainAddress != null && !mainAddress.isBlank()) {
            if (!address.isEmpty()) {
                address.append(", ");
            }
            address.append(mainAddress);
        }
        if (pincode != null && !pincode.isBlank()) {
            if (!address.isEmpty()) {
                address.append(", ");
            }
            address.append(pincode);
        }
        return address.toString();
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", customerId='" + customerId + '\'' +
                ", deliveryPersonId='" + deliveryPersonId + '\'' +
                ", items=" + items +
                ", originalAmount=" + originalAmount +
                ", discountPercentage=" + discountPercentage +
                ", discountAmount=" + discountAmount +
                ", finalAmount=" + finalAmount +
                ", paymentType=" + paymentType +
                ", paymentDetails='" + paymentDetails + '\'' +
                ", deliveryAddress='" + getDeliveryAddress() + '\'' +
                ", status=" + status +
                ", orderDate=" + orderDate +
                '}';
    }
}
