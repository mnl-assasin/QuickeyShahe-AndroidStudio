package com.bulsu.quickeyshare.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bulsu.quickeyshare.NetworkHelper;
import com.bulsu.quickeyshare.R;
import com.bulsu.quickeyshare.data.Const;
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

public class ReceiverActivity extends AppCompatActivity {

    String TAG = ReceiverActivity.class.getSimpleName();
    @Bind(R.id.etCode)
    EditText etCode;
    @Bind(R.id.btnConnect)
    Button btnConnect;
    @Bind(R.id.tvTimeLapsed)
    TextView tvTimeLapsed;

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

    }


    @OnClick(R.id.btnConnect)
    public void onClick() {
        String ip = NetworkHelper.getIpAddress();
        String partIP = ip.substring(0, ip.lastIndexOf(".") + 1);
        String code = etCode.getText().toString().trim();

        if (code.equals(""))
            etCode.setError("Enter the code from Sender");
        else {
            Log.d(TAG, "HOST: " + partIP + code);
            ClientThread clientThread = new ClientThread(partIP + code);
            clientThread.start();
        }

    }

    private class ClientThread extends Thread {
        String address;

        public ClientThread(String address) {
            this.address = address;
        }

        @Override
        public void run() {
            super.run();

            Socket socket = null;

            try {
                socket = new Socket(address, NetworkHelper.SERVER_PORT);

//                runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        startProgressDialog();
//                    }
//                });

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
                byte[] buff = new byte[2048];

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
                        startProgressDialog();
                        new DecompressAsyncTask().execute();
                    }

                });

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public class DecompressAsyncTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            String path = Const.DEFAULT_FOLDER_PATH + File.separator;
            if (ZipHelper.decompress(path, Const.COMPRESS_ZIP))
            Log.d(TAG, "Decompress success");
//                Toast.makeText(getApplicationContext(), "Files received. Checked it at QuickeyShare folder", Toast.LENGTH_LONG).show();
            else
            Log.d(TAG, "Decompress fail");
//                Toast.makeText(getApplicationContext(), "Something went wrong! Please try again.", Toast.LENGTH_LONG).show();
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

    private void startProgressDialog() {

        pDialog = new ProgressDialog(ReceiverActivity.this);
        pDialog.setMessage("Decompressing files...");
        pDialog.show();

    }

    private void stopDialog() {
        pDialog.dismiss();
    }


}
