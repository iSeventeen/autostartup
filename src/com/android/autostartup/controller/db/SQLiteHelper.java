package com.android.autostartup.controller.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.android.autostartup.dao.ParentDao;
import com.android.autostartup.dao.StudentDao;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "garten.local.db";
    private static final int DATABASE_VERSION = 1;

    private static final String[] createTableList = { StudentDao.SQL_CREATE_STUDENT,
            ParentDao.SQL_CREATE_PARENT };
    private static final String[] dropTableList = { StudentDao.SQL_DROP_STUDENT,
            ParentDao.SQL_DROP_PARENT };

    private SQLiteDatabase db;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        Log.i(TAG, "onCreate SQLite db");

        execSQL(createTableList);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Onupgrade SQL");
        this.db = db;
        execSQL(dropTableList);
        execSQL(createTableList);
    }

    private void execSQL(String[] sqls) {
        for (String sql : sqls) {
            db.execSQL(sql);
        }
    }

    public void createDB() {
        db = this.getWritableDatabase();
        execSQL(createTableList);
    }

    public Cursor query(String tablename) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query(tablename, null, null, null, null, null, null);
        return c;
    }

    public Cursor queryById(String tablename, long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select * from " + tablename + " where id=?",
                new String[] { String.valueOf(id) });
        return c;
    }

    public Cursor queryByCardId(String tablename, String cardId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select * from " + tablename + " where card_id=?",
                new String[] { cardId });
        return c;
    }

    public void insert(String tablename, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(tablename, null, values);
        db.close();
    }

    public void update(String tablename, ContentValues values, String whereClause, String cardId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(tablename, values, whereClause, new String[] { cardId });
        db.close();
    }

    public void delete(String talbename, String id) {
        if (db == null) {
            db = this.getWritableDatabase();
            db.delete(talbename, "_id=?", new String[] { String.valueOf(id) });
        }
    }

    public void deleteAll(String tablename) {
        if (db == null) {
            db = this.getWritableDatabase();
            db.delete(tablename, null, null);
        }
    }

    public void DropTable(String tablename) {
        db = this.getWritableDatabase();
        execSQL(dropTableList);
    }

    public void DropDB() {
        db = this.getWritableDatabase();
        // db.execSQL("drop database jshopmactive");
        onUpgrade(db, 1, 2);
    }

    public void close() {
        if (db != null) {
            db.close();
        }
    }

}
