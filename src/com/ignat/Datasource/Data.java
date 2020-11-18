package com.ignat.Datasource;

import com.ignat.Model.Consumption;
import com.ignat.Model.MenuItem;
import com.ignat.Model.Table;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;



//Class that handles all exchanges between SQL database and the UI

public class Data {

    //SQL Database name
    public static final String DB_NAME = "consumptions.db";

    //Path to database. This must be changed, accordingly
    public static final String CONNECTION = "jdbc:sqlite:D:\\Documents\\JavaPrograms\\RestaurantBillingManager\\" + DB_NAME;


    public static final String CONSUMPTION_TABLE = "items";
    public static final String TABLES_TABLE = "tables";
    public static final String MENU_TABLE = "menu";

    public static final String COLUMN_TABLE_ID = "_id";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_QUANTITY = "Quantity";
    public static final String COLUMN_PRICE = "Price";

    public static final String COLUMN_TABLE_NUMBER = "Table_number";

    public static final String COLUMN_ITEM_NAME = "Item_Name";
    public static final String COLUMN_ITEM_PRICE = "Item_Price";
    public static final String COLUMN_ITEM_IMAGE = "Item_Image";


    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + CONSUMPTION_TABLE + " (" +
            COLUMN_NAME + " TEXT, " + COLUMN_PRICE + " FLOAT, " + COLUMN_QUANTITY + " INTEGER, " +
            COLUMN_TABLE_ID + " INTEGER)";
    public static final String CREATE_TABLES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLES_TABLE + " (" +
            COLUMN_TABLE_NUMBER + " INTEGER)";
    public static final String CREATE_ITEMS_TABLE = "CREATE TABLE IF NOT EXISTS " + MENU_TABLE + " (" +
            COLUMN_ITEM_NAME + " TEXT, " + COLUMN_ITEM_PRICE + " FLOAT, " + COLUMN_ITEM_IMAGE + " TEXT)";


    public static final String QUERY_ITEMS = "SELECT * FROM " + CONSUMPTION_TABLE;
    public static final String QUERY_TABLES = "SELECT * FROM " + TABLES_TABLE;
    public static final String QUERY_MENU = "SELECT * FROM " + MENU_TABLE;

    public static final String QUERY_TABLE_CONSUMPTION = "SELECT * FROM " + CONSUMPTION_TABLE + " WHERE " + COLUMN_TABLE_ID + " = ?";
    public static final String INSERT_ITEM = "INSERT INTO " + CONSUMPTION_TABLE + " VALUES(?, ?, ?, ?)";
    public static final String QUERY_ITEM_QUANTITY = "SELECT " + COLUMN_QUANTITY + " FROM " + CONSUMPTION_TABLE + " WHERE " +
            COLUMN_NAME + "=? AND " + COLUMN_TABLE_ID + "=?";
    public static final String UPDATE_QUANTITY = "UPDATE " + CONSUMPTION_TABLE + " SET " + COLUMN_QUANTITY + "=? WHERE " +
            COLUMN_NAME + "=? AND " + COLUMN_TABLE_ID + "=?";
    public static final String DELETE_ITEM = "DELETE FROM " + CONSUMPTION_TABLE + " WHERE " + COLUMN_NAME + " = ?";

    public static final String INSERT_TABLE = "INSERT INTO " + TABLES_TABLE + " VALUES ( ? )";
    public static final String DELETE_TABLE = "DELETE FROM " + TABLES_TABLE + " WHERE " + COLUMN_TABLE_NUMBER + " = ?";

    public static final String INSERT_MENU_ITEM = "INSERT INTO " + MENU_TABLE + " VALUES ( ?,?,?)";
    public static final String DELETE_MENU_ITEM = "DELETE FROM " + MENU_TABLE + " WHERE " + COLUMN_ITEM_NAME + " = ?";
    public static final String UPDATE_MENU_ITEM = "UPDATE " + MENU_TABLE + " SET " + COLUMN_ITEM_NAME + " = ?, " +
            COLUMN_ITEM_PRICE + " =?, " + COLUMN_ITEM_IMAGE + " =? WHERE " + COLUMN_ITEM_NAME + " =?";


    private static Data dataInstance = new Data();
    private ObservableList<Consumption> consumptions;
    private ObservableList<Table> tables;
    private static int tableIndex;
    private List<MenuItem> items;

    private Connection conn;
    private Statement createTableStatement;
    private Statement loadDataStatement;
    private Statement loadTablesStatement;
    private Statement loadMenuStatement;

    private PreparedStatement queryTableConsumption;
    private PreparedStatement insertConsumption;
    private PreparedStatement queryItemQuantity;
    private PreparedStatement updateQuantity;
    private PreparedStatement deleteItem;
    private PreparedStatement insertTable;
    private PreparedStatement deleteTable;
    private PreparedStatement insertMenuItem;
    private PreparedStatement deleteMenuItem;
    private PreparedStatement updateMenuItem;


    public Data() {
        consumptions = FXCollections.observableArrayList();
        tables = FXCollections.observableArrayList();
        items = new ArrayList<>();
    }

    public static Data getDataInstance() {
        return dataInstance;
    }

    public static int getTableIndex() {
        return tableIndex;
    }

    public ObservableList<Consumption> getConsumptions() {
        return consumptions;
    }

    public void setConsumptions(ObservableList<Consumption> consumptions) {
        this.consumptions = consumptions;
    }

    public void addConsumption(Consumption newConsumption) {
        consumptions.add(newConsumption);
    }

    //Method to load tables in UI from database
    public ObservableList<Table> loadTables() {
        tableIndex = 0;
        try (ResultSet results = loadTablesStatement.executeQuery(QUERY_TABLES)) {
            while (results.next()) {
                Table table = new Table(results.getInt(COLUMN_TABLE_NUMBER));
                tables.add(table);
                tableIndex++;
            }
        } catch (SQLException e) {
            System.out.println("Error loading tables " + e.getMessage());
        }
        return tables;
    }

    //Creates the connection to database and prepares the SQL statements. Also creates, if not created already the tables in DB
    public boolean open() {
        try {
            conn = DriverManager.getConnection(CONNECTION);
            createTableStatement = conn.createStatement();
            createTableStatement.execute(CREATE_TABLE);
            createTableStatement.execute(CREATE_TABLES_TABLE);
            createTableStatement.execute(CREATE_ITEMS_TABLE);
            loadDataStatement = conn.createStatement();
            loadTablesStatement = conn.createStatement();
            loadMenuStatement = conn.createStatement();
            queryTableConsumption = conn.prepareStatement(QUERY_TABLE_CONSUMPTION);
            insertConsumption = conn.prepareStatement(INSERT_ITEM);
            queryItemQuantity = conn.prepareStatement(QUERY_ITEM_QUANTITY);
            updateQuantity = conn.prepareStatement(UPDATE_QUANTITY);
            deleteItem = conn.prepareStatement(DELETE_ITEM);
            insertTable = conn.prepareStatement(INSERT_TABLE);
            deleteTable = conn.prepareStatement(DELETE_TABLE);
            insertMenuItem = conn.prepareStatement(INSERT_MENU_ITEM);
            deleteMenuItem = conn.prepareStatement(DELETE_MENU_ITEM);
            updateMenuItem = conn.prepareStatement(UPDATE_MENU_ITEM);
            return true;
        } catch (SQLException e) {
            System.out.println("Could not connect to database" + e.getMessage());
            return false;
        }
    }


    //Closes connection and statements
    public void close() {
        try {
            if (createTableStatement != null) {
                createTableStatement.close();
            }
            if (loadDataStatement != null) {
                loadDataStatement.close();
            }
            if(loadTablesStatement != null) {
                loadTablesStatement.close();
            }
            if (queryTableConsumption != null) {
                queryTableConsumption.close();
            }
            if (insertConsumption != null) {
                insertConsumption.close();
            }
            if (queryItemQuantity != null) {
                queryItemQuantity.close();
            }
            if(updateQuantity!=null) {
                updateQuantity.close();
            }
            if(deleteItem!=null) {
                deleteItem.close();
            }
            if(insertTable!=null) {
                insertTable.close();
            }
            if(deleteTable!=null) {
                deleteTable.close();
            }
            if(insertMenuItem!=null) {
                insertMenuItem.close();
            }
            if(deleteMenuItem!=null) {
                deleteMenuItem.close();
            }
            if(updateMenuItem!=null) {
                updateMenuItem.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Cannot close connection to database" + e.getMessage());
        }
    }

    //Loads all the existing consumtions to tables. This is useful if application was closed without checking out all tables first.
    public void loadData() {

        try (ResultSet results = loadDataStatement.executeQuery(QUERY_ITEMS)) {
            while (results.next()) {
                Consumption consumption = new Consumption();
                consumption.setName(results.getString(COLUMN_NAME));
                consumption.setQuantity(results.getInt(COLUMN_QUANTITY));
                consumption.setTableId(results.getInt(COLUMN_TABLE_ID));
                consumption.setPricePerItem(results.getFloat(COLUMN_PRICE));
                consumptions.add(consumption);
            }

        } catch (SQLException e) {
            System.out.println("Error querying database for consumptions" + e.getMessage());
        }

    }


    //Filters the consumptions for a particular table
    public ObservableList<Consumption> getTableConsumption(int tableNumber) {

        try {
            queryTableConsumption.setInt(1, tableNumber);
            ResultSet results = queryTableConsumption.executeQuery();
            ObservableList<Consumption> newConsumptions = FXCollections.observableArrayList();

            while (results.next()) {
                Consumption newConsumption = new Consumption();
                newConsumption.setName(results.getString(COLUMN_NAME));
                newConsumption.setQuantity(results.getInt(COLUMN_QUANTITY));
                newConsumption.setTableId(results.getInt(COLUMN_TABLE_ID));
                newConsumption.setPricePerItem(results.getFloat(COLUMN_PRICE));

                newConsumptions.add(newConsumption);

            }
            return newConsumptions;
        } catch (SQLException e) {
            System.out.println("Error loading consumption for Table " + tableNumber);
            System.out.println(e.getMessage());
            return null;
        }

    }

    //Gets the final price for a particular table
    public float getTotal(List<Consumption> consumedItems) {
        float totalPrice = 0;
        for (Consumption consumption : consumedItems) {
            totalPrice += consumption.getPricePerItem() * consumption.getQuantity();
        }
        return totalPrice;
    }

    //Adds consumption to a particular table
    public void addConsumptionToTable(String item, float price, int tableId) {
        try {
//            System.out.println("AM ajuns aici");
            queryItemQuantity.setString(1, item);
            queryItemQuantity.setInt(2, tableId);
            ResultSet results = queryItemQuantity.executeQuery();
            int quantity = 1;
            if (results.next()) {
                quantity = results.getInt(1) + 1;
                updateQuantity.setInt(1, quantity);
                updateQuantity.setString(2, item);
                updateQuantity.setInt(3, tableId);
                updateQuantity.executeUpdate();
            } else {
                insertConsumption.setString(1, item);
                insertConsumption.setFloat(2, price);
                insertConsumption.setInt(3, quantity);
                insertConsumption.setInt(4, tableId);
                insertConsumption.execute();
            }

            loadData();


        } catch (SQLException e) {
            System.out.println("Error inserting item " + e.getMessage());
        }
    }

    //Removes 1 unit of a particular item from a table. If amount drops to 0, it removes the consumption from that table.
    public void removeConsumption(Consumption consumption) {
        try {
            if(consumption.getQuantity()-1>0) {
                updateQuantity.setInt(1, consumption.getQuantity() - 1);
                updateQuantity.setString(2, consumption.getName());
                updateQuantity.setInt(3, consumption.getTableId());
                updateQuantity.executeUpdate();
            } else {
                deleteItem.setString(1, consumption.getName());
                deleteItem.execute();
            }
        } catch (SQLException e) {
            System.out.println("Error removing item "+ e.getMessage());
        }

        loadData();
    }

    //Removes a consumption from a table. Method used to clear a table after check out is done.
    public void deleteConsumption (Consumption consumption) {
        try {
            deleteItem.setString(1, consumption.getName());
            deleteItem.execute();
        } catch (SQLException e) {
            System.out.println("Error deleting item " + e.getMessage());
        }

        loadData();
    }

    //Adds table to restaurant
    public void addTable() {
        try {
            insertTable.setInt(1,++tableIndex);
            insertTable.execute();
        } catch (SQLException e) {
            System.out.println("Error adding new table " + e.getMessage());
        }

        tables.add(new Table(tableIndex));
    }

    //Removes table from restaurant
    public void removeTable() {
        try{
            deleteTable.setInt(1,tableIndex);
            deleteTable.execute();
        } catch (SQLException e) {
            System.out.println("Error removing table " + e.getMessage());
        }
        tables.remove((tableIndex--)-1);
    }

    //Adds consumption to menu
    public void addItemToMenu(MenuItem menuItem) {
        items.add(menuItem);
        insertItemToMenu(menuItem);
    }

    public List<MenuItem> getMenuItems() {
        return items;
    }

    //Load menu to UI
    public void loadMenu() {
        try(ResultSet results = loadMenuStatement.executeQuery(QUERY_MENU)) {
            while (results.next()) {
                MenuItem menuItem = new MenuItem();
                menuItem.setItemName(results.getString(COLUMN_ITEM_NAME));
                menuItem.setItemPrice(results.getFloat(COLUMN_ITEM_PRICE));
                menuItem.setImageURL(results.getString(COLUMN_ITEM_IMAGE));
                items.add(menuItem);
            }

        } catch (SQLException e) {
            System.out.println("Error loading menu " + e.getMessage());
        }
    }

    // Saves the new consumption, added to menu, to the database, to be used in the future, even if the application is closed
    public void insertItemToMenu(MenuItem menuItem) {

        try {
            insertMenuItem.setString(1, menuItem.getItemName());
            insertMenuItem.setFloat(2, menuItem.getItemPrice());
            insertMenuItem.setString(3, menuItem.getImageURL());
            insertMenuItem.execute();
        } catch (SQLException e) {
            System.out.println("Error inserting item to menu " + e.getMessage());
        }
    }


    public void deleteItemFromMenu(MenuItem menuItem) {

        items.remove(menuItem);

        try {
            deleteMenuItem.setString(1,menuItem.getItemName());
            deleteMenuItem.execute();
        } catch (SQLException e) {
            System.out.println("Error while removing item from menu " + e.getMessage());
        }
    }


    public void updateItemFromMenu(MenuItem editedMenuItem, MenuItem newMenuItem) {

        items.remove(editedMenuItem);
        items.add(newMenuItem);

        try {
            updateMenuItem.setString(1,newMenuItem.getItemName());
            updateMenuItem.setFloat(2,newMenuItem.getItemPrice());
            updateMenuItem.setString(3,newMenuItem.getImageURL());
            updateMenuItem.setString(4,editedMenuItem.getItemName());
            updateMenuItem.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating item in menu " + e.getMessage());
        }
    }
}
