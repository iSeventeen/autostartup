package com.android.autostartup.app;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

import com.android.autostartup.controller.server.LowercaseEnumTypeAdapterFactory;
import com.android.autostartup.serialport.SerialPort;
import com.android.autostartup.serialport.SerialPortFinder;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Application extends android.app.Application {

    private static Gson mGson;
    private static RequestQueue mRequestQueue;

    private ApplicationPreferences mPreferences;

    public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
    private SerialPort mSerialPort = null;

    @Override
    public void onCreate() {
        super.onCreate();

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory());
        mGson = builder.create();

        mRequestQueue = Volley.newRequestQueue(getApplicationContext());

    }

    public static Gson getGson() {
        return mGson;
    }

    public static RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public SerialPort getSerialPort() throws SecurityException, IOException,
            InvalidParameterException {
        if (mSerialPort == null) {
            /* Read serial port parameters */
            mPreferences = new ApplicationPreferences(this);
            String path = mPreferences.getSerialPath();
            int baudrate = mPreferences.getSerialBaundrate();

            /* Check parameters */
            if ((path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }

            /* Open the serial port */
            mSerialPort = new SerialPort(new File(path), baudrate, 0);
        }
        return mSerialPort;
    }

    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }
}
