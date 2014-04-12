package com.android.autostartup.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.android.autostartup.controller.db.SQLiteHelper;
import com.android.autostartup.model.Student;

public class StudentDao {

    // ------------------------student table--------------------------
    public static final String TABLE_STUDENT = "student";
    public static final String FIELD_STUDENT_ID = "id";
    public static final String FIELD_STUDENT_CARD_ID = "card_id";
    public static final String FIELD_STUDENT_NAME = "name";
    public static final String FIELD_STUDENT_AGE = "age";
    public static final String FIELD_STUDENT_GENDER = "gender";
    public static final String FIELD_STUDENT_AVATAR = "avatar";
    public static final String FIELD_STUDENT_CREATED_AT = "created_at";
    public static final String FIELD_STUDENT_UPDATED_AT = "updated_at";

    public static final String SQL_CREATE_STUDENT = "Create table " + TABLE_STUDENT + "("
            + FIELD_STUDENT_ID + " integer primary key autoincrement," + FIELD_STUDENT_CARD_ID
            + " text," + FIELD_STUDENT_NAME + " text," + FIELD_STUDENT_GENDER + " integer,"
            + FIELD_STUDENT_AGE + " integer," + FIELD_STUDENT_AVATAR + " text,"
            + FIELD_STUDENT_CREATED_AT + " integer," + FIELD_STUDENT_UPDATED_AT + " integer" + ");";

    public static final String SQL_DROP_STUDENT = " DROP TABLE IF EXISTS " + TABLE_STUDENT;

    private SQLiteHelper helper;

    public StudentDao(Context context) {
        helper = new SQLiteHelper(context);
    }

    public Student findById(long id) {
        Cursor cursor = helper.queryById(TABLE_STUDENT, id);
        if (cursor.moveToNext()) {
            return buildStudent(cursor);
        }
        return null;
    }

    public Student findByCardId(String cardId) {
        Cursor cursor = helper.queryByCardId(TABLE_STUDENT, cardId);
        if (cursor.moveToNext()) {
            return buildStudent(cursor);
        }
        return null;
    }

    public List<Student> getAll() {
        List<Student> students = new ArrayList<Student>();
        Cursor cursor = helper.query(TABLE_STUDENT);
        while (cursor.moveToNext()) {
            students.add(buildStudent(cursor));
        }
        return students;
    }

    public void updateByCardId(Student[] students) {
        for (Student student : students) {
            updateByCardId(student);
        }
    }

    public void updateByCardId(Student student) {
        helper.update(TABLE_STUDENT, buildValues(student), FIELD_STUDENT_CARD_ID+"=?", student.cardId);
    }

    public void save(Student[] students) {
        for (Student student : students) {
            save(student);
        }
    }

    public void save(Student student) {

        helper.insert(TABLE_STUDENT, buildValues(student));
    }

    public ContentValues buildValues(Student student) {
        ContentValues values = new ContentValues();
        values.put(FIELD_STUDENT_CARD_ID, student.cardId);
        values.put(FIELD_STUDENT_NAME, student.name);
        values.put(FIELD_STUDENT_AGE, student.age);
        values.put(FIELD_STUDENT_GENDER, student.gender);
        values.put(FIELD_STUDENT_AVATAR, student.avatar);
        values.put(FIELD_STUDENT_CREATED_AT, student.createdAt);
        values.put(FIELD_STUDENT_UPDATED_AT, student.updatedAt);
        return values;
    }

    public Student buildStudent(Cursor cursor) {
        return new Student(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                cursor.getInt(3), cursor.getInt(4), cursor.getString(5), cursor.getLong(6),
                cursor.getLong(7));
    }

}
