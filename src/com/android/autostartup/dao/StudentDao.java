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
import com.android.autostartup.model.Student;
import com.android.autostartup.utils.FileUtils;

public class StudentDao {

    private static final String TAG = Student.class.getSimpleName();

    // ------------------------student table--------------------------
    public static final String TABLE_STUDENT = "student";
    public static final String FIELD_STUDENT_ID = "id";
    public static final String FIELD_STUDENT_CARD_ID = "card_id";
    public static final String FIELD_STUDENT_NAME = "name";
    public static final String FIELD_STUDENT_GRADE = "grade";
    public static final String FIELD_STUDENT_GENDER = "gender";
    public static final String FIELD_STUDENT_ADDRESS = "address";
    public static final String FIELD_STUDENT_AVATAR = "avatar";
    public static final String FIELD_STUDENT_NOTES = "notes";
    public static final String FIELD_STUDENT_CREATED_AT = "created_at";
    public static final String FIELD_STUDENT_UPDATED_AT = "updated_at";

    public static final String SQL_CREATE_STUDENT = "Create table " + TABLE_STUDENT + "("
            + FIELD_STUDENT_ID + " integer primary key," + FIELD_STUDENT_CARD_ID + " text,"
            + FIELD_STUDENT_NAME + " text," + FIELD_STUDENT_GRADE + " integer,"
            + FIELD_STUDENT_GENDER + " integer," + FIELD_STUDENT_ADDRESS + " text,"
            + FIELD_STUDENT_AVATAR + " text," + FIELD_STUDENT_NOTES + " text,"
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

    public void updateById(Student[] students) {
        for (Student student : students) {
            updateById(student);
        }
    }

    public void updateById(Student student) {
        helper.update(TABLE_STUDENT, buildValues(student), FIELD_STUDENT_ID + "=?",
                String.valueOf(student.id));
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
        values.put(FIELD_STUDENT_ID, student.id);
        values.put(FIELD_STUDENT_CARD_ID, student.cardId);
        values.put(FIELD_STUDENT_NAME, student.name);
        values.put(FIELD_STUDENT_GRADE, student.grade);
        values.put(FIELD_STUDENT_GENDER, student.gender);
        values.put(FIELD_STUDENT_ADDRESS, student.address);
        values.put(FIELD_STUDENT_AVATAR, student.avatar);
        values.put(FIELD_STUDENT_NOTES, student.notes);
        values.put(FIELD_STUDENT_CREATED_AT, student.createdAt);
        values.put(FIELD_STUDENT_UPDATED_AT, student.updatedAt);
        return values;
    }

    public Student buildStudent(Cursor cursor) {
        return new Student(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                cursor.getInt(3), cursor.getInt(4), cursor.getString(5), cursor.getString(6),
                cursor.getString(7), cursor.getLong(8), cursor.getLong(9));
    }

    public void loadAndSavePics(Student[] students) {
        new DownloadPicsTask().execute(students);
    }

    private class DownloadPicsTask extends AsyncTask<Student[], Void, Void> {
        @Override
        protected Void doInBackground(Student[]... params) {
            try {

                for (Student student : params[0]) {
                    FileUtils.loadAndSavePic(Server.PICTURE_BASE_URL + student.avatar);
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
