package com.example.docapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "DocAppDB";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";

    private static final String TABLE_APPOINTMENTS = "appointments";
    private static final String COL_APPOINTMENT_ID = "id";
    private static final String COL_DOCTOR_ID = "doctor_id";
    private static final String COL_DATE = "date";
    private static final String COL_TIME = "time";
    private static final String COL_STATUS = "status";

    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_USERNAME + " TEXT,"
                + COL_PASSWORD + " TEXT)";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_APPOINTMENTS_TABLE = "CREATE TABLE " + TABLE_APPOINTMENTS + "("
                + COL_APPOINTMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_DOCTOR_ID + " INTEGER,"
                + COL_DATE + " TEXT,"
                + COL_TIME + " TEXT,"
                + COL_STATUS + " INTEGER DEFAULT 0)";
        db.execSQL(CREATE_APPOINTMENTS_TABLE);

        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, "user");
        values.put(COL_PASSWORD, "pass");
        db.insert(TABLE_USERS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
        onCreate(db);
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_USER_ID};
        String selection = COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public void bookAppointment(int doctorId, String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DOCTOR_ID, doctorId);
        values.put(COL_DATE, date);
        values.put(COL_TIME, time);
        values.put(COL_STATUS, 1);
        db.insert(TABLE_APPOINTMENTS, null, values);
    }

    public void cancelAppointment(int doctorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_APPOINTMENTS, COL_DOCTOR_ID + " = ?", new String[]{String.valueOf(doctorId)});
    }

    public boolean hasActiveAppointment(int doctorId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_APPOINTMENTS +
                " WHERE " + COL_DOCTOR_ID + " = " + doctorId +
                " AND " + COL_STATUS + " = 1";
        Cursor cursor = db.rawQuery(query, null);
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    public boolean isTimeSlotAvailable(String date, String time) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_APPOINTMENTS +
                " WHERE " + COL_DATE + " = ? AND " + COL_TIME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{date, time});
        boolean isAvailable = cursor.getCount() == 0;
        cursor.close();
        return isAvailable;
    }

    public String getAppointmentInfo(int doctorId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_DATE + ", " + COL_TIME +
                " FROM " + TABLE_APPOINTMENTS +
                " WHERE " + COL_DOCTOR_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(doctorId)});

        if (cursor.moveToFirst()) {
            String date = cursor.getString(0);
            String time = cursor.getString(1);
            cursor.close();
            return "Запись на " + date + " в " + time;
        }
        cursor.close();
        return "Нет активной записи";
    }
}