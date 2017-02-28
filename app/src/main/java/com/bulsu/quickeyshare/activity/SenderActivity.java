package com.bulsu.quickeyshare.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bulsu.quickeyshare.NetworkHelper;
import com.bulsu.quickeyshare.R;
import com.bulsu.quickeyshare.data.Const;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SenderActivity extends AppCompatActivity {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tvCode)
    TextView tvCode;
    @Bind(R.id.tvTimeLapsed)
    TextView tvTimeLapsed;
    @Bind(R.id.layoutShareStarted)
    LinearLayout layoutShareStarted;
    @Bind(R.id.activity_sender)
    LinearLayout activitySender;


    private Handler customHandler = new Handler();

    long timeInMilliseconds = 0L;
    long updatedTime = 0L;
    long startTime = 0L;
    long timeSwapBuff = 0L;

    ServerSocket serverSocket;
    ServerSocketThread serverSocketThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);
        ButterKnife.bind(this);

        initToolbar();
        initCode();
        initSocketServer();

//        startTimer();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initCode() {
        String ip = NetworkHelper.getIpAddress();
        String code = ip.substring(ip.lastIndexOf(".") + 1);
        tvCode.setText(code);
    }

    private void initSocketServer() {
        serverSocketThread = new ServerSocketThread();
        serverSocketThread.start();
    }

    public class ServerSocketThread extends Thread {

        @Override
        public void run() {
            super.run();

            Socket socket = null;
            try {
                serverSocket = new ServerSocket(NetworkHelper.SERVER_PORT);

                while (true) {
                    // start of timer;
                    socket = serverSocket.accept();
                    FileTransferThread fileTransferThread = new FileTransferThread(socket);
                    fileTransferThread.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    public class FileTransferThread extends Thread {
        Socket socket;


        FileTransferThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();

            Log.d("FileTransfer", "FILE TRANSFER STARTED");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startTimer();
                }
            });

            File file = new File(Const.DEFAULT_ZIP_PATH);

            byte[] bytes = new byte[(int) file.length()];
            BufferedInputStream bis;
            try {
                bis = new BufferedInputStream(new FileInputStream(file));
                bis.read(bytes, 0, bytes.length);
                OutputStream os = socket.getOutputStream();
                os.write(bytes, 0, bytes.length);
                os.flush();
                socket.close();

                final String sentMsg = "File sent to: " + socket.getInetAddress();
                Log.d("FileTransfer", "FILE TRANSFER FINISHED");
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        pauseTimer();
                    }
                });

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private void startTimer() {
        startTime = SystemClock.uptimeMillis();
        customHandler.post(updateTimerThread);


    }

    private void pauseTimer() {
        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);
        File file = new File(Const.DEFAULT_ZIP_PATH);
        if (file.exists())
            if (file.delete())
                Log.d("FileTransfer", "File deleted");
            else
                Log.d("FileTransfer", "Something went wrong");
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


}
