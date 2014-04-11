package com.android.autostartup.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.android.autostartup.controller.db.SQLiteHelper;
import com.android.autostartup.model.Parent;

public class ParentDao {

    // ------------------------parent table-----------------------------
    public static final String TABLE_PARENT = "parent";
    public static final String FIELD_PARENT_ID = "id";
    public static final String FIELD_PARENT_NAME = "name";
    public static final String FIELD_PARENT_ROLE = "role";
    public static final String FIELD_PARENT_AVATAR = "avatar";
    public static final String FIELD_PARENT_STUDENT = "student";

    public static final String SQL_CREATE_PARENT = String
            .format("CREATE TABLE %1$s(%2$s integer primary key autoincrement,%3$s text,%4$s text,%5$s text,%6$s text);",
                    TABLE_PARENT, FIELD_PARENT_ID, FIELD_PARENT_NAME, FIELD_PARENT_ROLE,
                    FIELD_PARENT_AVATAR, FIELD_PARENT_STUDENT);

    public static final String SQL_DROP_PARENT = " DROP TABLE IF EXISTS " + TABLE_PARENT;

    private SQLiteHelper helper;

    public ParentDao(Context context) {
        helper = new SQLiteHelper(context);
    }

    public void save(Parent parent) {
        ContentValues values = new ContentValues();
        values.put(FIELD_PARENT_NAME, parent.name);
        values.put(FIELD_PARENT_ROLE, parent.role);
        values.put(FIELD_PARENT_AVATAR, parent.avatar);
        values.put(FIELD_PARENT_STUDENT, parent.student);
        helper.insert(TABLE_PARENT, values);
    }

    public Parent findById(long id) {
        Cursor cursor = helper.queryById(TABLE_PARENT, id);
        if (cursor.moveToNext()) {
            return createParent(cursor);
        }
        return null;
    }

    public List<Parent> getAllByStudent() {
        List<Parent> parents = new ArrayList<Parent>();
        Cursor cursor = helper.query(TABLE_PARENT);
        while (cursor.moveToNext()) {
            parents.add(createParent(cursor));
        }
        return parents;
    }

    private Parent createParent(Cursor cursor) {
        return new Parent(cursor.getLong(0), cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getInt(4));
    }
}
