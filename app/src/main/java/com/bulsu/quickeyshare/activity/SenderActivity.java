package com.bulsu.quickeyshare.activity;

import android.content.DialogInterface;
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
import com.bulsu.quickeyshare.builder.DialogBuilder;
import com.bulsu.quickeyshare.data.Const;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SenderActivity extends AppCompatActivity {

    String TAG = "SenderActivity";

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
    @Bind(R.id.tvSecretKey)
    TextView tvSecretKey;


    private Handler customHandler = new Handler();

    long timeInMilliseconds = 0L;
    long updatedTime = 0L;
    long startTime = 0L;
    long timeSwapBuff = 0L;

    ServerSocket serverSocket;
    ServerSocketThread serverSocketThread;

    String key = "";


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
        Log.d(TAG, "IP: " + ip);
        String code = ip.substring(ip.lastIndexOf(".") + 1);
        tvCode.setText(code);

        key = getIntent().getStringExtra("secret");
        tvSecretKey.setText(key);
    }

    private void initSocketServer() {
        serverSocketThread = new ServerSocketThread();
        serverSocketThread.start();
    }

//    public class ServerSocketThread extends Thread {
//
//        @Override
//        public void run() {
//            super.run();
//
//            Socket socket = null;
//            try {
//                serverSocket = new ServerSocket(NetworkHelper.SERVER_PORT);
//
//                socket = serverSocket.accept();
//
//
//                FileTransferThread fileTransferThread = new FileTransferThread(socket);
//                fileTransferThread.start();
//
//
////
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                if (socket != null) {
//                    try {
//                        socket.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//        }
//    }

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

                    DataInputStream dataInputStream = null;
                    DataOutputStream dataOutputStream = null;

                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    String keyClient = dataInputStream.readUTF();
                    Log.d("SenderActivity", "Key CLient: " + keyClient);

                    if (key.equals(keyClient)) {
                        dataOutputStream.writeUTF("KEY CONFIRM");
                        dataOutputStream.flush();

                        FileTransferThread fileTransferThread = new FileTransferThread(socket);
                        fileTransferThread.start();

                    } else {
                        dataOutputStream.writeUTF("WRONG KEY");
                        dataOutputStream.flush();
                    }

//
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

            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {

                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);

                InputStream in = fis;
                OutputStream out = socket.getOutputStream();

                byte[] bytes = new byte[4096];
                int count;

                while ((count = in.read(bytes)) > 0) {
                    out.write(bytes, 0, count);
                }

                out.close();
                in.close();

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
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fis.close();
                    bis.close();
                } catch (IOException e) {
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

        DialogBuilder.createDialog(SenderActivity.this, "File Sent", "File sent successfully!", "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                setResult(RESULT_OK);
                finish();
            }
        });
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

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        DialogBuilder.createDialog(SenderActivity.this, "QuickeyShare", "Are you sure you want to cancel sending this files?", "Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        }, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
    }
}
