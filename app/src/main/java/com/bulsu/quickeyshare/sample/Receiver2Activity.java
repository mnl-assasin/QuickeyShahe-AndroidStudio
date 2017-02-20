package com.bulsu.quickeyshare.sample;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bulsu.quickeyshare.R;
import com.bulsu.quickeyshare.data.ZipHelper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Receiver2Activity extends AppCompatActivity {

    static final int SocketServerPORT = 8080;

    @Bind(R.id.address)
    EditText editTextAddress;
    @Bind(R.id.port)
    TextView textPort;
    @Bind(R.id.connect)
    Button buttonConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver2);
        ButterKnife.bind(this);

        editTextAddress = (EditText) findViewById(R.id.address);
        textPort = (TextView) findViewById(R.id.port);
        textPort.setText("port: " + SocketServerPORT);
        buttonConnect = (Button) findViewById(R.id.connect);

    }

    @OnClick(R.id.connect)
    public void onClick() {

        ClientRxThread clientRxThread =
                new ClientRxThread(
                        editTextAddress.getText().toString(),
                        SocketServerPORT);

        clientRxThread.start();

    }

    private class ClientRxThread extends Thread {
        String dstAddress;
        int dstPort;

        ClientRxThread(String address, int port) {
            dstAddress = address;
            dstPort = port;
        }

        @Override
        public void run() {
            Socket socket = null;

            try {
                socket = new Socket(dstAddress, dstPort);


//                Socket s = socket.accept();

                Receiver2Activity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        startProgressDialog();
                    }
                });

                File file = new File(
                        Environment.getExternalStorageDirectory() + File.separator + "QuickeyShare" + File.separator, "compress.zip");

//                file.getParentFile().createNewFile();

                InputStream in = socket.getInputStream();
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

                int c = 0;
                byte[] buff = new byte[2048];

                while ((c = in.read(buff)) > 0) { // read something from inputstream into buffer
                    // if something was read
                    bos.write(buff, 0, c);
                }

                in.close();
                bos.close();
                socket.close();

//                File file = new File(
//                        Environment.getExternalStorageDirectory() + File.separator + "QuickeyShare" + File.separator, "compress.zip");
//
//                byte[] bytes = new byte[1024];
//                InputStream is = socket.getInputStream();
//                FileOutputStream fos = new FileOutputStream(file);
//                BufferedOutputStream bos = new BufferedOutputStream(fos);
//                int bytesRead = is.read(bytes, 0, bytes.length);
//                bos.write(bytes, 0, bytesRead);
//                bos.close();
//                socket.close();
//
                Receiver2Activity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(Receiver2Activity.this,
                                "Finished",
                                Toast.LENGTH_LONG).show();
                        stopDialog();
                        decompress();
                    }
                });

            } catch (IOException e) {

                e.printStackTrace();

                final String eMsg = "Something wrong: " + e.getMessage();
                Receiver2Activity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(Receiver2Activity.this,
                                eMsg,
                                Toast.LENGTH_LONG).show();
                    }
                });

            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    String TAG = "Receiver";

    private void decompress() {
        File fDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "QuickeyShare");
        String path = fDirectory.getAbsolutePath() + File.separator;
        if (ZipHelper.decompress(path, "compress.zip"))
//            Log.d(TAG, "Decompress success");
            Toast.makeText(getApplicationContext(), "FILES RECEIVED", Toast.LENGTH_LONG).show();
        else
//            Log.d(TAG, "Decompress fail");
            Toast.makeText(getApplicationContext(), "Something went wrong! Please try again.", Toast.LENGTH_LONG).show();
    }

    ProgressDialog pDialog;

    private void startProgressDialog() {

        pDialog = new ProgressDialog(Receiver2Activity.this);
        pDialog.setMessage("Incoming files...");
        pDialog.show();

    }

    private void stopDialog() {
        pDialog.dismiss();
    }

}
