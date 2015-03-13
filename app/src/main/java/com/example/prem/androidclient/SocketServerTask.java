package com.example.prem.androidclient;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by prem on 2/12/2015.
 */
public class SocketServerTask extends AsyncTask<JSONObject, Void, Void> {
    private JSONObject jsonData;
    private boolean success;


    @Override
    protected Void doInBackground(JSONObject... params) {
        Log.v("inside","socketServer");
        Socket socket = null;
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;
        jsonData = params[0];

        try {
            // Create a new Socket instance and connect to host
            socket = new Socket("10.0.2.2",10000);

            dataOutputStream = new DataOutputStream(
                    socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());

            // transfer JSONObject as String to the server
            dataOutputStream.writeUTF(jsonData.toString());
            Log.i("TAG", "waiting for response from host");

            // Thread will wait till server replies
            String response = dataInputStream.readUTF();
            if (response != null && response.equals("Connection Accepted")) {
                success = true;
            } else {
                success = false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        } finally {

            // close socket
            if (socket != null) {
                try {
                    Log.i("TAG", "closing the socket");
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // close input stream
            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // close output stream
            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.v("before","11");
    }
}
