package com.android.autostartup.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.autostartup.R;
import com.android.autostartup.controller.StudentController;
import com.android.autostartup.controller.StudentController.StudentUpdateCallback;
import com.android.autostartup.model.Student;

public class TestActivity extends Activity implements StudentUpdateCallback {

    private static final String TAG = TestActivity.class.getSimpleName();

    private TextView mCardIdText;
    private TextView mNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);

        initViews();
        StudentController.addStudentUpdateCallback(this);
        StudentController.getStudentInformation("1234567890");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StudentController.removeStudentUpdateCallback(this);
    }

    private void initViews() {
        mCardIdText = (TextView) findViewById(R.id.card_id);
        mNameText = (TextView) findViewById(R.id.name);
    }

    @Override
    public void updateStudent(Student student) {

        if (null != student) {
            Log.i(TAG, student.toString());
            mCardIdText.setText(String.valueOf(student.cardId));
            mNameText.setText(student.name);
        }
    }
}
