package com.ignat.Model;



//Model for adding consumption item to the restaurant's Menu

public class MenuItem {

    private String itemName;
    private float itemPrice;
    private String imageURL;

    public MenuItem(String name, float price, String image) {
        this.itemName = name;
        this.itemPrice = price;
        this.imageURL = image;
    }

    public MenuItem() {
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public float getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(float itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj ) {
            return true;
        }
        MenuItem that = (MenuItem) obj;
        if(this.getItemName().equals(that.getItemName())) {
            return true;
        }
        return false;
    }
}
