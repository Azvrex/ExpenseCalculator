package com.example.expensecalculator.Helper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.expensecalculator.Helper.DBHelper;
import com.example.expensecalculator.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemDBHelper {
    private SQLiteDatabase database;

    public ItemDBHelper(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public void addItem(Item item) {
        ContentValues values = new ContentValues();
        values.put("item_name", item.getItemName());
        values.put("cost", item.getCost());
        values.put("category", item.getCategory());

        long newRowId = database.insert("items", null, values);
        item.setId((int) newRowId); // Assign the newly generated ID to the item
    }

    @SuppressLint("Range")
    public List<Item> getAllItems() {
        List<Item> itemList = new ArrayList<>();
        Cursor cursor = database.query("items", null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            Item item = new Item();
            item.setId(cursor.getInt(cursor.getColumnIndex("id")));
            item.setItemName(cursor.getString(cursor.getColumnIndex("item_name")));
            item.setCost((float) cursor.getDouble(cursor.getColumnIndex("cost")));
            item.setCategory(cursor.getString(cursor.getColumnIndex("category")));
            itemList.add(item);
        }

        cursor.close();
        return itemList;
    }




}
