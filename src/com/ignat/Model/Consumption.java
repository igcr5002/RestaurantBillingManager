package com.ignat.Model;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;


//Model for adding/removing consumptions to a table

public class Consumption {

    private SimpleStringProperty name;
    private SimpleIntegerProperty quantity;
    private SimpleIntegerProperty tableId;
    private SimpleFloatProperty pricePerItem;

    public Consumption() {
        name = new SimpleStringProperty();
        quantity = new SimpleIntegerProperty();
        tableId = new SimpleIntegerProperty();
        pricePerItem = new SimpleFloatProperty();
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
          this.name.set(name);
    }

    public int getQuantity() {
        return quantity.get();
    }

    public SimpleIntegerProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public int getTableId() {
        return tableId.get();
    }

    public SimpleIntegerProperty tableIdProperty() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId.set(tableId);
    }

    public float getPricePerItem() {
        return pricePerItem.get();
    }

    public SimpleFloatProperty pricePerItemProperty() {
        return pricePerItem;
    }

    public void setPricePerItem(float pricePerItem) {
        this.pricePerItem.set(pricePerItem);
    }
}
