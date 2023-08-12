package com.example.expensecalculator;

public class Item {
    private int id;
    private String itemName;
    private float cost;
    private String category;

    public Item() {
    }

    public Item(String itemName, float cost) {
        this.itemName = itemName;
        this.cost = cost;
    }

    // Add constructors as needed
    // For example, if you want to include an id parameter in the constructor:
    public Item(int id, String itemName, float cost) {
        this.id = id;
        this.itemName = itemName;
        this.cost = cost;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }
    public void setCategory(String category){
        this.category = category;
    }
    public String getCategory(){
        return category;
    }

}
