package com.bulsu.quickeyshare.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bulsu.quickeyshare.R;
import com.bulsu.quickeyshare.adapter.BaseFileAdapter;
import com.bulsu.quickeyshare.data.Const;
import com.bulsu.quickeyshare.data.FileHelper;
import com.bulsu.quickeyshare.data.ZipHelper;
import com.bulsu.quickeyshare.model.FileItem;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReviewFilesActivity extends AppCompatActivity {

    String TAG = ReviewFilesActivity.class.getSimpleName();

    ArrayList<String> selectedPath;
    BaseFileAdapter adapter;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.list)
    ListView list;
    @Bind(R.id.btnCancel)
    Button btnCancel;
    @Bind(R.id.btnSend)
    Button btnSend;

    ArrayList<FileItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_files);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setTitle("List of Selected Files");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initList();

    }

    private void initList() {

        items = new ArrayList<>();

        selectedPath = getIntent().getExtras().getStringArrayList("selectedFiles");
        int category = getIntent().getExtras().getInt("category");

        for (int ctr = 0; ctr < selectedPath.size(); ctr++) {
            items.add(new FileItem(selectedPath.get(ctr), category, false));
        }

        adapter = new BaseFileAdapter(this, items);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                removeItem(i);
            }


        });

    }

    private void removeItem(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you really want to remove this file?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedPath.remove(position);
                        items.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    @OnClick({R.id.btnCancel, R.id.btnSend})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCancel:
                showCancelDialog();
                break;
            case R.id.btnSend:

                if (selectedPath.size() > 0) {
                    openSecretKeyDialog();

//                    FileHelper.logList("Selected", selectedPath);
//                    String files[] = selectedPath.toArray(new String[selectedPath.size()]);
//                    new CompressAsyncTask(files, "").execute();

                } else {
                    Toast.makeText(getApplicationContext(), "Select files to share", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void openSecretKeyDialog() {


        final EditText editText = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(editText).setTitle("Add Secret Key");
        builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String key = editText.getText().toString().trim();
                Log.d(TAG, "KEY: " + key);

                if (key.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please add a secret key!", Toast.LENGTH_LONG).show();
                    openSecretKeyDialog();
                } else {
                    FileHelper.logList("Selected", selectedPath);
                    String files[] = selectedPath.toArray(new String[selectedPath.size()]);
                    new CompressAsyncTask(files, key).execute();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showCancelDialog();
        }
        return super.onOptionsItemSelected(item);
    }


    private void showCancelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("").setMessage("Are you sure you want to Cancel send this files?").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                finish();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }


    private class CompressAsyncTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog pDialog;
        String files[];
        String key;

        public CompressAsyncTask(String[] files, String key) {
            this.files = files;
            this.key = key;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ReviewFilesActivity.this);
            pDialog.setMessage("Preparing Files to send...");
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            File fDirectory = new File(Const.DEFAULT_FOLDER_PATH);
            if (!fDirectory.exists()) {
                if (fDirectory.mkdirs())
                    Log.d(TAG, fDirectory.getAbsolutePath() + " folder created");
            }

            String zip = Const.DEFAULT_ZIP_PATH;

            if (ZipHelper.compress(files, zip)) {
                Log.d(TAG, "Compression successful");
//                Toast.makeText(getApplicationContext(), "Compression Successful", Toast.LENGTH_LONG).show();
                startActivityForResult(new Intent(getApplicationContext(), SenderActivity.class).putExtra("secret", key), 300);
            } else {
                Log.d(TAG, "Compression FAILED");
                Toast.makeText(getApplicationContext(), Const.ERROR_MESSAGE, Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 300) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}
