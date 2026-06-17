package com.example.subscriptionmanager.models;

public class Subscription {
    private int id;
    private String name;
    private double price;
    private String category;
    private String startDate;
    private String endDate;
    private int userId;

    public Subscription() {}

    public Subscription(String name, double price, String category, String startDate, String endDate, int userId) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.userId = userId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}