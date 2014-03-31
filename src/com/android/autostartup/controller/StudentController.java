package com.android.autostartup.controller;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.android.autostartup.controller.server.Server;
import com.android.autostartup.model.Student;

public class StudentController {

    private static final String TAG = StudentController.class.getSimpleName();

    private static Student mStudent;

    public interface StudentUpdateCallback {
        public void updateStudent(Student student);
    }

    private static List<StudentUpdateCallback> callbacks = new ArrayList<StudentController.StudentUpdateCallback>();

    public static void addStudentUpdateCallback(StudentUpdateCallback callback) {
        synchronized (callbacks) {
            callbacks.add(callback);
        }
    }

    public static void removeStudentUpdateCallback(StudentUpdateCallback callback) {
        synchronized (callbacks) {
            callbacks.remove(callback);
        }
    }

    private static void updateStudentInformation() {
        synchronized (callbacks) {
            for (StudentUpdateCallback callback : callbacks) {
                callback.updateStudent(mStudent);
            }
        }
    }

    public static void setStudent(Student student) {
        mStudent = student;
        updateStudentInformation();
    }

    public static void getStudentInformation(String cardId) {
        cardId = "1234567890";
        Server.requestStudent(cardId, new Server.GetStudentCallback() {

            @Override
            public void onSuccess(Student student) {
                setStudent(student);
            }
        }, new Server.ErrorCallback() {

            @Override
            public void onFail(String reason) {
                Log.e(TAG, "get student failed");
            }
        });
    }
}
