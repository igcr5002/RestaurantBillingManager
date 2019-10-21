package com.ignat.Model;


//Model for creating tables

public class Table {

    private String tableName = "Table";
    private int tableNumber;

    public Table(int tableNumber) {
        this.tableNumber = tableNumber;
    }
    public Table() {}

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    @Override
    public String toString() {
        return tableName + " " +  tableNumber;
    }
}
