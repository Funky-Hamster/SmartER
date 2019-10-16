package edu.monash.smarter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBManager {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "smarter.db";
    private final Context context;
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBStructure.tableEntry.TABLE_NAME + " (" +
                    DBStructure.tableEntry._ID + " INTEGER PRIMARY KEY," + DBStructure.tableEntry.COLUMN_RESID + TEXT_TYPE + COMMA_SEP + DBStructure.tableEntry.COLUMN_USAGE_DATE + TEXT_TYPE + COMMA_SEP
                    + DBStructure.tableEntry.COLUMN_USAGE_HOUR + TEXT_TYPE + COMMA_SEP +
                    DBStructure.tableEntry.COLUMN_FRIDGE_USAGE + TEXT_TYPE + COMMA_SEP +
                    DBStructure.tableEntry.COLUMN_AIR_CONDITIONER_USAGE + TEXT_TYPE + COMMA_SEP +
                    DBStructure.tableEntry.COLUMN_WASHING_MACHINE_USAGE + TEXT_TYPE + COMMA_SEP +
                    DBStructure.tableEntry.COLUMN_TEMPERATURE + TEXT_TYPE +
                    " );";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DBStructure.tableEntry.TABLE_NAME;

    private static class MySQLiteOpenHelper extends SQLiteOpenHelper {
        public MySQLiteOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            //db.execSQL(SQL_DELETE_ENTRIES);
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
    }

    private MySQLiteOpenHelper myDBHelper;
    private SQLiteDatabase db;

    public DBManager(Context ctx) {
        this.context = ctx;
        myDBHelper = new MySQLiteOpenHelper(context);
    }

    public DBManager open() throws SQLException {
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        myDBHelper.close();
    }

    public long insertUsage(String resid, String usageDate, String usageHour, String fridgeUsage, String airConditionerUsage, String washingMachineUsage, String temperature) {

        ContentValues values = new ContentValues();
        values.put(DBStructure.tableEntry.COLUMN_RESID, resid);
        values.put(DBStructure.tableEntry.COLUMN_USAGE_DATE, usageDate);
        values.put(DBStructure.tableEntry.COLUMN_USAGE_HOUR, usageHour);
        values.put(DBStructure.tableEntry.COLUMN_FRIDGE_USAGE, fridgeUsage);
        values.put(DBStructure.tableEntry.COLUMN_AIR_CONDITIONER_USAGE, airConditionerUsage);
        values.put(DBStructure.tableEntry.COLUMN_WASHING_MACHINE_USAGE, washingMachineUsage);
        values.put(DBStructure.tableEntry.COLUMN_TEMPERATURE, temperature);
        return db.insert(DBStructure.tableEntry.TABLE_NAME, null, values);
    }

    public Cursor getAllUsages() {
        return db.query(DBStructure.tableEntry.TABLE_NAME, columns, null, null, null, null, null);
    }

    private String[] columns = {DBStructure.tableEntry.COLUMN_RESID, DBStructure.tableEntry.COLUMN_USAGE_DATE, DBStructure.tableEntry.COLUMN_USAGE_HOUR,
            DBStructure.tableEntry.COLUMN_FRIDGE_USAGE, DBStructure.tableEntry.COLUMN_AIR_CONDITIONER_USAGE, DBStructure.tableEntry.COLUMN_WASHING_MACHINE_USAGE,
            DBStructure.tableEntry.COLUMN_TEMPERATURE};

   public void deleteAll() {
        db.delete(DBStructure.tableEntry.TABLE_NAME, null, null);
    }

    public Cursor getUsageByResid(String resid) throws SQLException {
        String selection = DBStructure.tableEntry.COLUMN_RESID + " = ?";
        String[] selectionArgs = {String.valueOf(resid)};
        Cursor cursor = db.query(true, DBStructure.tableEntry.TABLE_NAME, columns,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getUsageByUsageHour(String usageHour) throws SQLException {
        String selection = DBStructure.tableEntry.COLUMN_USAGE_HOUR + " = ?";
        String[] selectionArgs = {String.valueOf((usageHour))};
        Cursor cursor = db.query(true, DBStructure.tableEntry.TABLE_NAME, columns,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getAllData(){
        return db.query(DBStructure.tableEntry.TABLE_NAME, columns, null, null, null, null, null);
    }

}

