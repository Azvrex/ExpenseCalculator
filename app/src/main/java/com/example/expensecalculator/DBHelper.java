package com.example.expensecalculator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Budget_database.db";


    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS items (id INTEGER PRIMARY KEY AUTOINCREMENT, item_name TEXT, cost REAL, category TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS items");
        onCreate(db);
    }

    public void removeItem(int itemId){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("items", "id=?", new String[]{String.valueOf(itemId)});
        db.close();
    }

    public void printDatabaseContent() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM items";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String itemName = cursor.getString(cursor.getColumnIndex("item_name"));
                @SuppressLint("Range") double cost = cursor.getDouble(cursor.getColumnIndex("cost"));
                @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category"));
                Log.d("Database", "ID: " + id + ", Item: " + itemName + ", Cost: " + cost + ", Category: " + category);
            }
            cursor.close();
        }

        db.close();
    }

    public float getTotalCostForCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        float totalCost = 0;

        String[] projection = {"cost"};
        String selection = "category=?";
        String[] selectionArgs = {category};

        Cursor cursor = db.query(
                "items",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int costColumnIndex = cursor.getColumnIndexOrThrow("cost");
                float cost = cursor.getFloat(costColumnIndex);
                totalCost += cost;
            }
            cursor.close();
        }

        return totalCost;
    }

    public HashMap getExpenses() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {"item_name", "cost"};
        String selection = "category=?";
        String[] selectionArgs = {"Expenses"};

        Cursor cursor = db.query(
                "items",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        HashMap<String, Double> aggregatedExpenses = null;
        if (cursor != null) {
            aggregatedExpenses = new HashMap<>();

            while (cursor.moveToNext()) {
                // Retrieve the name and cost values from the current row in the cursor
                String name = cursor.getString(cursor.getColumnIndexOrThrow("item_name"));
                double cost = cursor.getDouble(cursor.getColumnIndexOrThrow("cost"));
                // Check if the name already exists in the HashMap
                if (aggregatedExpenses.containsKey(name)) {
                    // If the name already exists, get the current aggregated cost for that name
                    double currentCost = aggregatedExpenses.get(name);
                    // Add the cost from the current row to the existing aggregated cost
                    aggregatedExpenses.put(name, currentCost + cost);
                } else {
                    // If the name doesn't exist in the HashMap, add it with the current cost
                    aggregatedExpenses.put(name, cost);
                }
            }

            cursor.close();
        }

        return aggregatedExpenses;
    }
}
