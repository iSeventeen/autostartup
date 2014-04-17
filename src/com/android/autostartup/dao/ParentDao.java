package com.android.autostartup.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.android.autostartup.controller.db.SQLiteHelper;
import com.android.autostartup.controller.server.Server;
import com.android.autostartup.model.Parent;
import com.android.autostartup.utils.FileUtils;

public class ParentDao {

    private static final String TAG = ParentDao.class.getSimpleName();

    // ------------------------parent table-----------------------------
    public static final String TABLE_PARENT = "parent";
    public static final String FIELD_PARENT_ID = "id";
    public static final String FIELD_PARENT_CARD_ID = "card_id";
    public static final String FIELD_PARENT_NAME = "name";
    public static final String FIELD_PARENT_ROLE = "role";
    public static final String FIELD_PARENT_AVATAR = "avatar";
    public static final String FIELD_PARENT_PHONE = "phone";
    public static final String FIELD_PARENT_CREATED_AT = "created_at";
    public static final String FIELD_PARENT_UPDATED_AT = "updated_at";
    public static final String FIELD_PARENT_STUDENT = "student";

    public static final String SQL_CREATE_PARENT = "Create table " + TABLE_PARENT + "("
            + FIELD_PARENT_ID + " integer primary key," + FIELD_PARENT_CARD_ID + " text,"
            + FIELD_PARENT_NAME + " text," + FIELD_PARENT_ROLE + " text," + FIELD_PARENT_AVATAR
            + " text," + FIELD_PARENT_PHONE + " text," + FIELD_PARENT_CREATED_AT + " integer,"
            + FIELD_PARENT_UPDATED_AT + " integer," + FIELD_PARENT_STUDENT + " integer" + ");";

    public static final String SQL_DROP_PARENT = " DROP TABLE IF EXISTS " + TABLE_PARENT;

    private SQLiteHelper helper;

    public ParentDao(Context context) {
        helper = new SQLiteHelper(context);
    }

    public Parent findById(long id) {
        Cursor cursor = helper.queryById(TABLE_PARENT, id);
        if (cursor.moveToNext()) {
            return buildParent(cursor);
        }
        return null;
    }

    public List<Parent> getAll() {
        List<Parent> parents = new ArrayList<Parent>();
        Cursor cursor = helper.query(TABLE_PARENT);
        while (cursor.moveToNext()) {
            parents.add(buildParent(cursor));
        }
        return parents;
    }

    public List<Parent> getByStudentId(long studentId) {
        List<Parent> parents = new ArrayList<Parent>();
        Cursor cursor = helper.query(TABLE_PARENT, FIELD_PARENT_STUDENT, String.valueOf(studentId));
        while (cursor.moveToNext()) {
            parents.add(buildParent(cursor));
        }
        return parents;
    }

    public Parent getByCardId(String cardId) {
        Cursor cursor = helper.query(TABLE_PARENT, FIELD_PARENT_CARD_ID, cardId);
        if (cursor.moveToNext()) {
            return buildParent(cursor);
        }
        return null;
    }

    public void updateById(Parent[] parents) {
        for (Parent parent : parents) {
            updateById(parent);
        }
    }

    public void updateById(Parent parent) {
        helper.update(TABLE_PARENT, buildValues(parent), FIELD_PARENT_ID + "=?",
                String.valueOf(parent.id));
    }

    public void save(Parent[] parents) {
        for (Parent parent : parents) {
            save(parent);
        }
    }

    public void save(Parent parent) {

        helper.insert(TABLE_PARENT, buildValues(parent));
    }

    private ContentValues buildValues(Parent parent) {
        ContentValues values = new ContentValues();
        values.put(FIELD_PARENT_ID, parent.id);
        values.put(FIELD_PARENT_CARD_ID, parent.cardId);
        values.put(FIELD_PARENT_NAME, parent.name);
        values.put(FIELD_PARENT_ROLE, parent.role);
        values.put(FIELD_PARENT_AVATAR, parent.avatar);
        values.put(FIELD_PARENT_PHONE, parent.phone);
        values.put(FIELD_PARENT_CREATED_AT, parent.createdAt);
        values.put(FIELD_PARENT_UPDATED_AT, parent.updatedAt);
        values.put(FIELD_PARENT_STUDENT, parent.student);

        return values;
    }

    private Parent buildParent(Cursor cursor) {
        return new Parent(cursor.getLong(0), cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getLong(6),
                cursor.getLong(7), cursor.getLong(8));
    }

    public void loadAndSavePics(Parent[] parents) {
        new DownloadPicsTask().execute(parents);
    }

    private class DownloadPicsTask extends AsyncTask<Parent[], Void, Void> {
        @Override
        protected Void doInBackground(Parent[]... params) {
            try {

                for (Parent parent : params[0]) {
                    FileUtils.loadAndSavePic(Server.PICTURE_BASE_URL + parent.avatar);
                }

            } catch (IOException e) {
                // TODO
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
        }
    }
}
