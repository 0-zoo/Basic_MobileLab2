package com.example.demo01;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;



public class ConnectedThread extends Thread{

    private final BluetoothSocket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final Handler handler;

    public ConnectedThread(BluetoothSocket socket, Handler handler) {
        this.socket = socket;
        this.handler = handler;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        inputStream = tmpIn;
        outputStream = tmpOut;
    }


    public void run(){
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = inputStream.available();
                if(bytes != 0) {
                    buffer = new byte[1024];
                    SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                    bytes = inputStream.available(); // how many bytes are ready to be read?
                    bytes = inputStream.read(buffer, 0, bytes); // record how many bytes we actually read
                    handler.obtainMessage(SignalActivity.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget(); // Send the obtained bytes to the UI activity
                }
            } catch (IOException e) {
                e.printStackTrace();

                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(int number) {
        try {
            outputStream.write(number);

            // Share the sent message with the UI activity.
            Message writtenMsg = handler.obtainMessage(
                    SignalActivity.MESSAGE_WRITE, -1, -1, number);
            writtenMsg.sendToTarget();
        } catch (IOException e) {

//            Log.e(TAG, "Error occurred when sending data", e);
//
//            // Send a failure message back to the activity.
//            Message writeErrorMsg =
//                    handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
//            Bundle bundle = new Bundle();
//            bundle.putString("toast",
//                    "Couldn't send data to the other device");
//            writeErrorMsg.setData(bundle);
//            handler.sendMessage(writeErrorMsg);
        }
    }


    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) { }
    }

}
