package com.bulsu.quickeyshare.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bulsu.quickeyshare.NetworkHelper;
import com.bulsu.quickeyshare.R;
import com.bulsu.quickeyshare.builder.DialogBuilder;
import com.bulsu.quickeyshare.data.Const;
import com.bulsu.quickeyshare.data.ZipHelper;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReceiverActivity extends AppCompatActivity {

    String TAG = ReceiverActivity.class.getSimpleName();
    @Bind(R.id.etCode)
    EditText etCode;
    @Bind(R.id.btnConnect)
    Button btnConnect;
    @Bind(R.id.tvTimeLapsed)
    TextView tvTimeLapsed;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.progress)
    ProgressBar progress;
    @Bind(R.id.layoutShareStarted)
    LinearLayout layoutShareStarted;
    @Bind(R.id.activity_sender)
    LinearLayout activitySender;
    @Bind(R.id.etSecret)
    EditText etSecret;

    private Handler customHandler = new Handler();

    long timeInMilliseconds = 0L;
    long updatedTime = 0L;
    long startTime = 0L;
    long timeSwapBuff = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);
        ButterKnife.bind(this);

        initToolbar();
        progress.setVisibility(View.INVISIBLE);

    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.btnConnect)
    public void onClick() {
        String ip = NetworkHelper.getIpAddress();
        String partIP = ip.substring(0, ip.lastIndexOf(".") + 1);
        Log.d(TAG, "IP: " + ip);
        Log.d(TAG, " PART IP: " + partIP);
        String code = etCode.getText().toString().trim();
        String key = etSecret.getText().toString().trim();

//        key = "";

        boolean isValid = true;

        if (code.equals("")) {
            etCode.setError("Enter the code from Sender");
            isValid = false;
        }
        if (key.equals("")) {
            etSecret.setError("Enter Secret key!");
            isValid = false;
        }

        if (isValid) {
            Log.d(TAG, "Part IP: " + partIP);
            Log.d(TAG, "HOST: " + partIP + code);
//            startProgressDialog("Connecting..."); .
            ClientThread clientThread = new ClientThread(partIP + code, key);
//            ClientThread clientThread = new ClientThread(partIP + code, "");
            clientThread.start();
        }
    }

//    private class ClientThread extends Thread {
//        String address;
//        String key;
//
//        public ClientThread(String address, String key) {
//            this.address = address;
//            this.key = key;
//        }
//
//        @Override
//        public void run() {
//            super.run();
//
//            Socket socket;
//
//            try {
//                socket = new Socket(address, NetworkHelper.SERVER_PORT);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        startTimer();
//                    }
//                });
//                File file = new File(Const.DEFAULT_ZIP_PATH);
//                InputStream in = socket.getInputStream();
//                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
//
//                int c = 0;
//                byte[] buff = new byte[4096];
//
//                while ((c = in.read(buff)) > 0) {
//                    bos.write(buff, 0, c);
//                }
//
//                in.close();
//                bos.close();
//                socket.close();
//
//                Log.d("FileTransfer", "FILE TRANSFER FINISHED");
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        pauseTimer();
//                        new DecompressAsyncTask().execute();
//                    }
//
//                });
//
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.d(TAG, "Unknown Host, Add error message");
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        displayDialog("Unknown Host Address");
//                    }
//                });
//            }
//
//        }
//    }

    private class ClientThread extends Thread {
        String address;
        String key;

        public ClientThread(String address, String key) {
            this.address = address;
            this.key = key;
        }

        @Override
        public void run() {
            super.run();

            Socket socket;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;

            try {
                socket = new Socket(address, NetworkHelper.SERVER_PORT);
                dataOutputStream = new DataOutputStream(
                        socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream.writeUTF(key);
                dataOutputStream.flush();

                while (true) {
                    if (dataInputStream.available() > 0) {
                        String message = dataInputStream.readUTF();
                        Log.d(TAG, "Message: " + message);

//                        dataInputStream.close();

                        if (message.equals("KEY CONFIRM")) {
                            Log.d("FileTransfer", "FILE TRANSFER STARTED");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    startTimer();
                                }
                            });

                            File file = new File(Const.DEFAULT_ZIP_PATH);


                            InputStream in = socket.getInputStream();
                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

                            int c = 0;
                            byte[] buff = new byte[4096];

                            while ((c = in.read(buff)) > 0) {
                                bos.write(buff, 0, c);
                            }

                            in.close();
                            bos.close();
                            socket.close();

                            Log.d("FileTransfer", "FILE TRANSFER FINISHED");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pauseTimer();
//                        startProgressDialog("Loading files...");
                                    new DecompressAsyncTask().execute();
                                }

                            });


                            break;
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    displayDialog("Incorrect secret key");
                                }
                            });
                        }
                    }
                }


            } catch (final IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Unknown Host, Add error message");
//

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        displayDialog("Unknown Host Address");
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                });
//                Toast.makeText(getApplicationContext(), "Unknown Host Address", Toast.LENGTH_LONG).show();
            }

        }
    }

    private void displayDialog(String message) {
//        stopProgressDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle("QuicKeyShare");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                etCode.setText("");
                etCode.requestFocus();
                etSecret.setText("");

            }
        }).show();


    }

    /*

                Log.d("FileTransfer", "FILE TRANSFER STARTED");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startTimer();
                    }
                });

                File file = new File(Const.DEFAULT_ZIP_PATH);
//
//                if (!file.exists())
//                    file.mkdirs();
                InputStream in = socket.getInputStream();
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

                int c = 0;
                byte[] buff = new byte[4096];

                while ((c = in.read(buff)) > 0) {
                    bos.write(buff, 0, c);
                }

                in.close();
                bos.close();
                socket.close();

                Log.d("FileTransfer", "FILE TRANSFER FINISHED");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pauseTimer();
//                        startProgressDialog("Loading files...");
                        new DecompressAsyncTask().execute();
                    }

                });
     */

    public class DecompressAsyncTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgressDialog("Loading files...");
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String path = Const.DEFAULT_FOLDER_PATH + File.separator;
            if (ZipHelper.decompress(path, Const.COMPRESS_ZIP)) {
                File file = new File(Const.DEFAULT_ZIP_PATH);
                if (file.exists())
                    if (file.delete())
                        Log.d("FileTransfer", "File deleted");
                    else
                        Log.d("FileTransfer", "Something went wrong");
            } else {
                Log.d("FileTransfer", "Something went wrong");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            stopDialog();

        }
    }

    private void decompress() {

    }


    private void startTimer() {
//        stopProgressDialog();
        progress.setVisibility(View.VISIBLE);
        startTime = SystemClock.uptimeMillis();
        customHandler.post(updateTimerThread);
    }

    private void pauseTimer() {
        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);

    }

    private Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1200);
            int mins = secs / 60;

            secs = secs % 60;
            tvTimeLapsed.setText(String.format("%02d:%02d", mins, secs));
            customHandler.post(updateTimerThread);
        }
    };

    ProgressDialog pDialog;

    private void startProgressDialog(String message) {

        pDialog = new ProgressDialog(ReceiverActivity.this);
        pDialog.setMessage(message);

        pDialog.show();

    }

    private void stopProgressDialog() {
        if (pDialog != null) pDialog.dismiss();
    }

    private void stopDialog() {
        pDialog.dismiss();
        DialogBuilder.createDialog(ReceiverActivity.this, "File Received", "File received successfully!", "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });

    }


}
