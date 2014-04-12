package com.android.autostartup.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.android.autostartup.controller.server.Server;
import com.android.autostartup.dao.StudentDao;
import com.android.autostartup.model.Student;

public class SyncStudentsToServer {

    private static final String TAG = SyncStudentsToServer.class.getSimpleName();

    private StudentDao studentDao;
    private List<Student> studentsFromServer = new ArrayList<Student>();

    private List<Student> newStudents = new ArrayList<Student>();
    private List<Student> updateStudents = new ArrayList<Student>();

    public SyncStudentsToServer(Context context) {
        studentDao = new StudentDao(context);
    }

    public void syncData() {
        Server.requestAllStudentIds(new Server.GetStudentsCallback() {
            @Override
            public void onSuccess(Student[] students) {
                for (Student student : students) {
                    studentsFromServer.add(student);
                }

                updateData();
            }
        }, new Server.ErrorCallback() {

            @Override
            public void onFail(String reason) {
                Log.e(TAG, "get student ids list failed");
            }
        });
    }

    private void updateData() {
        List<Student> localStudents = studentDao.getAll();
        if (null == localStudents || localStudents.isEmpty()) {
            return;
        } else {
            seperateData(localStudents);

            saveStudents();
            updateStudents();
        }
    }

    private void seperateData(List<Student> localStudents) {
        List<Student> serverStudents = studentsFromServer;
        for (int i = localStudents.size() - 1; i > -1; i--) {
            Student localStudent = localStudents.get(i);
            String localCardId = localStudent.cardId;
            for (int j = serverStudents.size() - 1; j > -1; j--) {
                Student serverStudent = serverStudents.get(j);
                if (localCardId.equals(serverStudent.cardId)) {
                    if (localStudent.updatedAt != serverStudent.updatedAt) {
                        updateStudents.add(localStudent);
                    }
                    localStudents.remove(i);
                    serverStudents.remove(j);
                    break;
                }
            }
        }

        newStudents = localStudents;
    }

    private void saveStudents() {
        for (Student student : newStudents) {
            doSave(student);
        }
        newStudents.clear();
    }

    private void updateStudents() {
        for (Student student : updateStudents) {
            doUpdate(student);
        }
        updateStudents.clear();
    }

    private void doSave(Student student) {
        Server.saveStudent(student, new Server.CommonCallback() {
            @Override
            public void onSuccess(String status) {
                // TODO do something?
            }
        }, new Server.ErrorCallback() {
            @Override
            public void onFail(String reason) {
                Log.e(TAG, "save student failed");
            }
        });
    }

    private void doUpdate(Student student) {
        Server.updateStudent(student, new Server.CommonCallback() {
            @Override
            public void onSuccess(String status) {
                // TODO do something??
            }
        }, new Server.ErrorCallback() {
            @Override
            public void onFail(String reason) {
                Log.e(TAG, "update student failed");
            }
        });
    }

}
