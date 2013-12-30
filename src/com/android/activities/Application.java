package com.android.activities;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

import com.android.serialport.SerialPort;
import com.android.serialport.SerialPortFinder;
import com.android.utils.ApplicationPreferences;

public class Application extends android.app.Application {

    private ApplicationPreferences mPreferences;

    public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
    private SerialPort mSerialPort = null;

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
