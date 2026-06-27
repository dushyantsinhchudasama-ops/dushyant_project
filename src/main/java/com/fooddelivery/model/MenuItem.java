package com.fooddelivery.model;

public class MenuItem {
    private final int id;
    private final int categoryId;
    private String name;
    private double price;
    private boolean available;

    public MenuItem(int id, int categoryId, String name, double price, boolean available) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.price = price;
        this.available = available;
    }

    public int getId() {
        return id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", available=" + available +
                '}';
    }
}
