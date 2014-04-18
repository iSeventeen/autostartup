package com.android.autostartup.activity;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.android.autostartup.R;
import com.android.autostartup.app.Application;
import com.android.autostartup.serialport.SerialPort;

public class OpenSerialPort {
    
    private Context context;
    
    private Application mApplication;
    private SerialPort mSerialPort = null;
    // private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    
    public OpenSerialPort(Context context, Application application){
        this.context = context;
        this.mApplication = application;
        init();
    }
    
    private void init() {
        try {
            mSerialPort = mApplication.getSerialPort();
            // mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

            mReadThread = new ReadThread();
            mReadThread.start();

        } catch (InvalidParameterException e) {
            DisplayError(R.string.error_configuration);
        } catch (SecurityException e) {
            DisplayError(R.string.error_security);
        } catch (IOException e) {
            DisplayError(R.string.error_unknown);
        }
    }
    
    public void start(){
        
    }
    
    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[64];
                    if (mInputStream == null)
                        return;
                    size = mInputStream.read(buffer);
                    if (size > 0) {
//                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
    


    private void DisplayError(int resourceId) {
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        b.setTitle("Error");
        b.setMessage(resourceId);
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // MainActivity.this.finish();
            }
        });
        b.show();
    }

}
