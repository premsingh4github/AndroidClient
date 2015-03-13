package com.example.prem.androidclient;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class MainActivity extends Activity {

    TextView textResponse;
    EditText editTextAddress, editTextPort,input;
    Button buttonConnect, buttonClear,sendbtn ,closeSocket;
    Socket socket = null;
    DataOutputStream dataOutputStream = null;
    Thread threadRead;
    Thread threadWrite;
    PrintWriter printwriter;
    String inputData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextAddress = (EditText)findViewById(R.id.address);
        editTextPort = (EditText)findViewById(R.id.port);
        input = (EditText) findViewById(R.id.input);
        closeSocket = (Button) findViewById(R.id.closeS);
        setData();
        //setupSocket();
        buttonConnect = (Button)findViewById(R.id.connect);
        buttonClear = (Button)findViewById(R.id.clear);
        textResponse = (TextView)findViewById(R.id.response);
        textResponse.setText("Response :");
        sendbtn = (Button) findViewById(R.id.send);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textResponse.setText("");
            }
        });
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyClientTask myClientTask = new MyClientTask(
                        editTextAddress.getText().toString(),
                        Integer.parseInt(editTextPort.getText().toString()));
                        myClientTask.execute();
               //new Next().execute();
            }
        });
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("clicked:-","1");
                inputData = input.getText().toString();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("from","1");
                    jsonObject.put("to","2");
                    jsonObject.put("message",inputData);
                    Log.v("data:11",inputData);
                    printwriter.println(jsonObject.toString());
                    Log.v("sent","11");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


              /* Next next = new Next(inputData);
                next.execute();*/
            }
        });
        closeSocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printwriter.println("shutdown");
            }
        });
    }


    public class MyClientTask extends AsyncTask<Void, String, Void> {

        String dstAddress;
        int dstPort;
        String response = "";
        PrintWriter writeToHost;

        MyClientTask(String addr, int port){
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(Void... arg0) {



            try {
                socket = new Socket(dstAddress, dstPort);

                ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];

                int bytesRead;
                InputStream inputStream = socket.getInputStream();
                //OutputStreamWriter dataOutputStream = new OutputStreamWriter(socket.getOutputStream());
                printwriter = new PrintWriter(socket.getOutputStream(), true);
                Log.v("status","connected");

    /*
     * notice:
     * inputStream.read() will block if no data return
     */
                while ((bytesRead = inputStream.read(buffer)) != -1){

                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                    Log.v("before writing","I m from app");
                    publishProgress(byteArrayOutputStream.toString("UTF-8"));
                    byteArrayOutputStream.reset();
                    Log.v("write","1");
                    Log.v("data",response);
                }

                Log.v("respose",response);
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
                Log.v("respose",response);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }finally{
                if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            Log.e("asdf",values[0]+"");
            //textResponse.setText(values[0]);
            textResponse.append(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            textResponse.setText(response);
            super.onPostExecute(result);
        }

    }
    public void setData(){
        editTextAddress.setText("192.168.0.20");
        editTextPort.setText("10000");
    }
    public void setupSocket(){

        try {

            threadRead = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.e("inside thread","yes");
                    try {
                        socket = new Socket("192.168.0.20",10000);
                        InputStream in =socket.getInputStream();
                        InputStreamReader is = new InputStreamReader(in);
                        final StringBuilder sb=new StringBuilder();
                        BufferedReader br = new BufferedReader(is);
                        String read = br.readLine();

                        while(read != null) {
                            //System.out.println(read);
                            sb.append(read);
                            read =br.readLine();

                        }


                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textResponse.append(sb.toString());
                            }//public void run() {
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            threadWrite = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.e("inside thread","yes");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    threadRead.start();

    }
    public void writeToSocket(){
        threadWrite.run();
    }
    public void readFromSocket(){
       threadRead.run();
    }
    public class Next extends  AsyncTask<Void,Void,Void>{
        private String inputD;
        Next(String inputD){
            this.inputD = inputD;
            Log.v("input",inputD);

        }

        @Override
        protected Void doInBackground(Void ... para) {
            Toast.makeText(MainActivity.this,"input data",Toast.LENGTH_SHORT).show();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this,"clicked",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(MainActivity.this,"after",Toast.LENGTH_SHORT).show();
        }
    }
}