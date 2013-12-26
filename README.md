autostartup
===========

### This is an Android App:
  - Auto run when start the phone;
  - Play video and audio;
  - Read the data from serial port.

### platform:
  - android sdk 4.0;
  - android ndk.
 

### Config SerialPort:
  - Download SerialPort-api demo: https://code.google.com/p/android-serialport-api/wiki/Building_the_project;
  - Extract the demo;
  - Create a pacakge to put serial port files, like: com.android.serialport;
  - Copy 'SerialPort.java' and 'SerialPortFinder.java' to 'com.android.serialport';
  - Copy 'jni' fold from demo to your project;
  - Open the 'SerialPort.c' and 'SerialPort.h' files on 'jni', and change the SeriportPort route to map to your SerialPort.java file.
  - Open Terminal and place to 'jni';
  - Input: $ your_android_ndk_path/ndk-build, press Enter;
  - After finish, 'libserialport.so' file will be added on libs/../;

