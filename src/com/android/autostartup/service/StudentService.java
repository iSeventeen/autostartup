package com.android.autostartup.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.autostartup.controller.server.Server;
import com.android.autostartup.dao.StudentDao;
import com.android.autostartup.model.Student;

public class StudentService {

    private static final String TAG = StudentService.class.getSimpleName();

    private StudentDao studentDao;

    private Student[] studentsFromServer;
    private List<String> newIds = new ArrayList<String>();
    private List<String> updatedIds = new ArrayList<String>();

    public StudentService(Context context) {
        studentDao = new StudentDao(context);
    }

    public void updateStudentData() {
        Server.requestAllStudentIds(new Server.GetStudentsCallback() {
            @Override
            public void onSuccess(Student[] students) {
                for (Student student : students) {
                    Log.i(TAG, "updateStudentData:" + student);
                }
                studentsFromServer = students;
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
        List<Student> studentFromLocal = studentDao.getAll();
        if (null == studentFromLocal || studentFromLocal.isEmpty()) {
            Log.i(TAG, "save all ids");
            saveAllStudents();
        } else {
            seperateData(studentFromLocal);

            if (!updatedIds.isEmpty()) {
                Log.i(TAG, "update updated ids:" + TextUtils.join(",", updatedIds));
                updateStudents(TextUtils.join(",", updatedIds));
                updatedIds.clear();
            }

            if (!newIds.isEmpty()) {
                Log.i(TAG, "save new ids" + TextUtils.join(",", newIds));
                saveNewStudents(TextUtils.join(",", newIds));
                newIds.clear();
            }

        }
    }

    private void seperateData(List<Student> students) {
        List<Student> localStudentList = students;
        List<Student> serverStudentList = new ArrayList<Student>();
        for (Student student : studentsFromServer) {
            Log.i(TAG, "server>student="+student.toString());
            serverStudentList.add(student);
        }
        for (Student student : localStudentList) {
            Log.i(TAG, "before>" + student.toString());
        }
        
        Log.i(TAG, "ServerStudentList.size="+serverStudentList.size());

        for (int i = serverStudentList.size() - 1; i > -1; i--) {
            for (int j = localStudentList.size() - 1; j > -1; j--) {
                if (serverStudentList.get(i).cardId.equals(localStudentList.get(j).cardId)) {
                    if (serverStudentList.get(i).updatedAt != localStudentList.get(j).updatedAt) {
                        updatedIds.add(serverStudentList.get(i).cardId);
                    }
                    localStudentList.remove(j);
                    serverStudentList.remove(i);
                    Log.i(TAG, "i="+i);
                    Log.i(TAG, "j="+j);
                    break;
                }
            }
        }

        for (Student student : serverStudentList) {
            Log.i(TAG, "after>" + student.toString());
        }

        Log.i(TAG, "serverstudentlist.size="+serverStudentList.size());
        if (!serverStudentList.isEmpty()) {
            for (Student student : serverStudentList) {
                newIds.add(student.cardId);
            }
        }
    }

    private void saveAllStudents() {
        Server.requestAllStudent(new Server.GetStudentsCallback() {
            @Override
            public void onSuccess(Student[] students) {
                for (Student student : students) {
                    Log.i(TAG, student.toString());
                }
                studentDao.save(students);
            }
        }, new Server.ErrorCallback() {

            @Override
            public void onFail(String reason) {
                Log.e(TAG, "get student list failed");
            }
        });
    }

    private void saveNewStudents(String ids) {
        Server.requestStudentsByIds(ids, new Server.GetStudentsCallback() {

            @Override
            public void onSuccess(Student[] students) {
                for (Student student : students) {
                    Log.i(TAG, student.toString());
                }
                studentDao.save(students);

            }
        }, new Server.ErrorCallback() {

            @Override
            public void onFail(String reason) {
                Log.e(TAG, "get student list failed");
            }
        });

    }

    private void updateStudents(String ids) {
        Server.requestStudentsByIds(ids, new Server.GetStudentsCallback() {

            @Override
            public void onSuccess(Student[] students) {
                studentDao.updateByCardId(students);

            }
        }, new Server.ErrorCallback() {

            @Override
            public void onFail(String reason) {
                Log.e(TAG, "get student list failed");
            }
        });
    }

}
