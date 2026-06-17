package com.example.subscriptionmanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.subscriptionmanager.models.Subscription;
import com.example.subscriptionmanager.models.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "subscriptions.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";

    private static final String TABLE_SUBSCRIPTIONS = "subscriptions";
    private static final String COL_SUB_ID = "id";
    private static final String COL_SUB_NAME = "name";
    private static final String COL_SUB_PRICE = "price";
    private static final String COL_SUB_CATEGORY = "category";
    private static final String COL_SUB_START_DATE = "start_date";
    private static final String COL_SUB_END_DATE = "end_date";
    private static final String COL_SUB_USER_ID = "user_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_USERNAME + " TEXT UNIQUE,"
                + COL_PASSWORD + " TEXT" + ")";
        db.execSQL(createUsersTable);

        String createSubscriptionsTable = "CREATE TABLE " + TABLE_SUBSCRIPTIONS + "("
                + COL_SUB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_SUB_NAME + " TEXT,"
                + COL_SUB_PRICE + " REAL,"
                + COL_SUB_CATEGORY + " TEXT,"
                + COL_SUB_START_DATE + " TEXT,"
                + COL_SUB_END_DATE + " TEXT,"
                + COL_SUB_USER_ID + " INTEGER,"
                + "FOREIGN KEY(" + COL_SUB_USER_ID + ") REFERENCES "
                + TABLE_USERS + "(" + COL_USER_ID + ")" + ")";
        db.execSQL(createSubscriptionsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBSCRIPTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public boolean registerUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, user.getUsername());
        values.put(COL_PASSWORD, user.getPassword());

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public User loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS
                + " WHERE " + COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{username, password});

        if (cursor.moveToFirst()) {
            User user = new User();
            user.setId(cursor.getInt(0));
            user.setUsername(cursor.getString(1));
            user.setPassword(cursor.getString(2));
            cursor.close();
            return user;
        }
        cursor.close();
        return null;
    }

    public boolean addSubscription(Subscription subscription) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SUB_NAME, subscription.getName());
        values.put(COL_SUB_PRICE, subscription.getPrice());
        values.put(COL_SUB_CATEGORY, subscription.getCategory());
        values.put(COL_SUB_START_DATE, subscription.getStartDate());
        values.put(COL_SUB_END_DATE, subscription.getEndDate());
        values.put(COL_SUB_USER_ID, subscription.getUserId());

        long result = db.insert(TABLE_SUBSCRIPTIONS, null, values);
        return result != -1;
    }

    public List<Subscription> getSubscriptions(int userId) {
        List<Subscription> subscriptions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_SUBSCRIPTIONS
                + " WHERE " + COL_SUB_USER_ID + " = ? ORDER BY " + COL_SUB_END_DATE + " ASC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Subscription subscription = new Subscription();
                subscription.setId(cursor.getInt(0));
                subscription.setName(cursor.getString(1));
                subscription.setPrice(cursor.getDouble(2));
                subscription.setCategory(cursor.getString(3));
                subscription.setStartDate(cursor.getString(4));
                subscription.setEndDate(cursor.getString(5));
                subscription.setUserId(cursor.getInt(6));
                subscriptions.add(subscription);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return subscriptions;
    }

    public boolean deleteSubscription(int subscriptionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_SUBSCRIPTIONS, COL_SUB_ID + " = ?",
                new String[]{String.valueOf(subscriptionId)});
        return result > 0;
    }

    public boolean updateSubscription(Subscription subscription) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SUB_NAME, subscription.getName());
        values.put(COL_SUB_PRICE, subscription.getPrice());
        values.put(COL_SUB_CATEGORY, subscription.getCategory());
        values.put(COL_SUB_START_DATE, subscription.getStartDate());
        values.put(COL_SUB_END_DATE, subscription.getEndDate());

        int result = db.update(TABLE_SUBSCRIPTIONS, values, COL_SUB_ID + " = ?",
                new String[]{String.valueOf(subscription.getId())});
        return result > 0;
    }
}