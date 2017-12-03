package com.bulsu.quickeyshare.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bulsu.quickeyshare.R;
import com.bulsu.quickeyshare.adapter.ReceiveFileAdapter;
import com.bulsu.quickeyshare.data.Const;
import com.bulsu.quickeyshare.data.FileHelper;
import com.bulsu.quickeyshare.model.FileItem;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReceiveFilesActivity extends AppCompatActivity {

    String TAG = "deb1234";

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.list)
    ListView list;


    ArrayList<FileItem> items;
    ReceiveFileAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_files);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setTitle("Received Files");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initData();
        initListener();
    }

    private void initListener() {

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openAction(i);
            }
        });
    }

    private void openAction(int i) {
        String path = items.get(i).getPath();
        String extension = FileHelper.getFileExtension(path);

        if (extension.equalsIgnoreCase("mp4")) {

            // VIDEO PATH;
            Log.d(TAG, "Video CLICKED: " + path);
            openVideo(path);

        } else if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") ||
                extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("gif")) {

            //IMAGE PATH;
            Log.d(TAG, path);
            openImage(path);

        } else if (extension.equalsIgnoreCase("mp3")) {

            // MUSIC PATH
            Log.d(TAG, "Music CLICKED: " + path);
            openMusic(path);
        }

    }

    private void openMusic(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
        intent.setDataAndType(Uri.parse(path), "audio/*");
        startActivity(intent);
    }

    private void openImage(String path) {
        Log.d(TAG, "Image path: " + path);
        Intent intent = new Intent(this, ImagePreviewActivity.class).putExtra("path", path);
        startActivity(intent);

    }

    private void openVideo(String path) {

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
            intent.setDataAndType(Uri.parse(String.valueOf(path)), "video/*");
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No Application found to open this file", Toast.LENGTH_LONG).show();
        }

    }

    private void initData() {

        items = new ArrayList<>();
        adapter = new ReceiveFileAdapter(this, items);
        list.setAdapter(adapter);

        File fileDirectory = new File(Const.DEFAULT_FOLDER_PATH);
        File[] dirFiles = null;

        if (fileDirectory != null)
            if (fileDirectory.exists())
                dirFiles = fileDirectory.listFiles();

        if (dirFiles != null)
            if (dirFiles.length > 0) {
                for (int ctr = 0; ctr < dirFiles.length; ctr++) {

                    String path = dirFiles[ctr].getAbsolutePath();
                    String extension = FileHelper.getFileExtension(path);

                    if (extension.equalsIgnoreCase("mp4")) {

                        // VIDEO PATH;
                        Log.d(TAG, "Video PATH: " + path);
                        items.add(new FileItem(path, Const.CAT_VIDEO, false));

                    } else if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") ||
                            extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("gif")) {

                        //IMAGE PATH;
                        Log.d(TAG, "Image PATH: " + path);
                        items.add(new FileItem(path, Const.CAT_IMAGE, false));

                    } else if (extension.equalsIgnoreCase("mp3")) {

                        // MUSIC PATH
                        Log.d(TAG, "Music PATH: " + path);
                        items.add(new FileItem(path, Const.CAT_AUDIO, false));

                    }
                }
            }

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
