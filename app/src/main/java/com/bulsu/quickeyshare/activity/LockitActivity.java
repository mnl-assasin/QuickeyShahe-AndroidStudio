package com.bulsu.quickeyshare.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.bulsu.quickeyshare.EncryptionHelper;
import com.bulsu.quickeyshare.R;
import com.bulsu.quickeyshare.adapter.LockItAdapter;
import com.bulsu.quickeyshare.data.Const;
import com.bulsu.quickeyshare.data.EZSharedPrefences;
import com.bulsu.quickeyshare.data.FileHelper;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.SecretKey;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LockitActivity extends AppCompatActivity {

    String TAG = LockitActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fabLock)
    FloatingActionButton fabLock;
    @Bind(R.id.activity_lockit)
    RelativeLayout activityLockit;
    @Bind(R.id.list)
    ListView list;

    private String encryptedFileName = "Enc_File.png";
    private static String algorithm = "AES";
    static SecretKey yourKey = null;

    ArrayList<String> path;
    ArrayList<String> selectedPath;

    LockItAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockit);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Locked Files");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private class InitializeTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LockitActivity.this);
            pDialog.setMessage("checking locked files");
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            initData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
        }
    }

    private void initData() {
        if (EZSharedPrefences.getSecret(this).equals(""))
            try {
                EncryptionHelper.generateSecretKey(this);
                Log.d(TAG, "Secret Key created");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        else
            Log.d(TAG, "Secret Key found");

        path = new ArrayList<>();
        selectedPath = new ArrayList<>();

        File fDirectory = new File(Const.DEFAULT_VAULT_PATH);
        File[] dirFiles = null;
        if (fDirectory.exists())
            dirFiles = fDirectory.listFiles();

        if (dirFiles != null) {
            for (int ctr = 0; ctr < dirFiles.length; ctr++) {
                String s = dirFiles[ctr].getAbsolutePath();
                Log.d(TAG, s);
                path.add(dirFiles[ctr].getAbsolutePath());
            }
            Log.d(TAG, "Count: " + dirFiles.length);
        }

        adapter = new LockItAdapter(this, path);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectList(i);
            }
        });


    }

    private void selectList(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Locked Files");
        builder.setMessage("Do you really want to this file from vault?");
        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //
                String filePath = path.get(position);
                String fileName = FileHelper.getFilename(filePath);
                fileName = fileName.substring(0, fileName.length() - 4);
                Log.d(TAG, "File filePath: " + filePath);
                Log.d(TAG, "File name: " + fileName);

                new DecryptTask(filePath, fileName, position).execute();

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    @OnClick(R.id.fabLock)
    public void onClick() {
        startActivity(new Intent(this, LockChooserActivity.class));
    }

    public class DecryptTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog pDialog;
        String filePath;
        String fileName;
        int position;

        public DecryptTask(String filePath, String fileName, int position) {
            this.filePath = filePath;
            this.fileName = fileName;
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LockitActivity.this);
            pDialog.setMessage("Removing file from vault");
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            EncryptionHelper.decryptFile(LockitActivity.this, fileName, filePath);

            File file = new File(filePath);
            try {
                file.getCanonicalFile().delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
            path.remove(position);
            adapter.notifyDataSetChanged();


        }
    }

}
