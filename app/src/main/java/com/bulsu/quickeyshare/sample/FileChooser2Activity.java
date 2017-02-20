package com.bulsu.quickeyshare.sample;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.bulsu.quickeyshare.R;
import com.bulsu.quickeyshare.data.ZipHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bulsu.quickeyshare.readfile.ReadFileActivity.getImagesPath;

public class FileChooser2Activity extends AppCompatActivity {

    String TAG = "FileChooser";
    @Bind(R.id.list)
    ListView list;
    @Bind(R.id.btnProceed)
    Button btnProceed;

    int selected = 0;

    List<String> pathNames;
    List<String> allPath;
    List<String> selectedPath;
    @Bind(R.id.activity_file_chooser)
    LinearLayout activityFileChooser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser2);
        ButterKnife.bind(this);

        initData();
        initListener();
    }

    private void initData() {
        allPath = readAllImage();

        logPath();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(FileChooser2Activity.this, android.R.layout.simple_list_item_1, pathNames);
        list.setAdapter(adapter);

    }

    private void initListener() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Log.d(TAG, "Clicked: " + i);
                selectList(i);
            }
        });
    }

    private void selectList(int i) {
        Log.d(TAG, "position: " + i);
        Log.d(TAG, "Name: " + pathNames.get(i));

//        if (selectedPath.size() == 0)
//            selectedPath.add(allPath.get(i));


        if (!isExist(i))
            selectedPath.add(allPath.get(i));

        int size = selectedPath.size();
        String proceedString = size + " Selected";
        btnProceed.setText(size + " Selected");

    }

    private boolean isExist(int i) {

        for (int ctr = 0; ctr < selectedPath.size(); ctr++) {
            if (selectedPath.get(ctr).equals(allPath.get(i))) {
                selectedPath.remove(ctr);
                return true;
            }
        }
        Log.d(TAG, "Didn't Exist");
        return false;

    }

    private List<String> readAllImage() {


        pathNames = new ArrayList<>();
        selectedPath = new ArrayList<>();

        List<String> imagePath;
        imagePath = getImagesPath(FileChooser2Activity.this);

        for (int ctr = 0; ctr < imagePath.size(); ctr++) {

            String path = imagePath.get(ctr);
            pathNames.add(path.substring(path.lastIndexOf("/") + 1));
        }

        return imagePath;

    }

    private void logPath() {

        for (int ctr = 0; ctr < allPath.size(); ctr++) {

            String path = allPath.get(ctr);
            String name = pathNames.get(ctr);

//            Log.d(TAG, "Name: " + name + " Path: " + path);

        }

    }

    @OnClick(R.id.btnProceed)
    public void onClick() {
        onProceedClick();
    }

    private void onProceedClick() {

        File fDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "QuickeyShare");

        if (!fDirectory.exists()) {
            if (fDirectory.mkdir())
                Log.d(TAG, fDirectory.getAbsolutePath() + " folder created");
        }

        String files[] = selectedPath.toArray(new String[selectedPath.size()]);

        for (int ctr = 0; ctr < files.length; ctr++) {
            Log.d(TAG, "files[" + ctr + "] : " + files[ctr]);
        }

        String zip = fDirectory.getAbsolutePath() + File.separator + "compress.zip";

        if (ZipHelper.compress(files, zip)) {
            Log.d(TAG, "Compression successful");
            Toast.makeText(getApplicationContext(), "Compression Successful", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), Sender2Activity.class));
        } else {
            Log.d(TAG, "Compression failed");
            Toast.makeText(getApplicationContext(), "Compression FAILED", Toast.LENGTH_LONG).show();
        }

    }
}
