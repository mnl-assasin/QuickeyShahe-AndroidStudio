package com.bulsu.quickeyshare.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bulsu.quickeyshare.R;
import com.bulsu.quickeyshare.activity.SenderActivity;
import com.bulsu.quickeyshare.adapter.FileAdapter;
import com.bulsu.quickeyshare.data.Const;
import com.bulsu.quickeyshare.data.FileHelper;
import com.bulsu.quickeyshare.data.ZipHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class FileChooserFragment extends Fragment {


    @Bind(R.id.list)
    ListView list;
    @Bind(R.id.btnProceed)
    Button btnProceed;

    List<String> allPath;
    List<String> selectedPath;
    FileAdapter adapter;

    public FileChooserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_file_chooser, container, false);
        ButterKnife.bind(this, view);

        initList();
        return view;
    }

    private void initList() {
        selectedPath = new ArrayList<>();
        allPath = filterPath(FileHelper.getFilesPath(getActivity()));
        adapter = new FileAdapter(allPath, getActivity());
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ImageView ivSelected = (ImageView) view.findViewById(R.id.ivSelected);
                if (ivSelected.getVisibility() == View.INVISIBLE)
                    ivSelected.setVisibility(View.VISIBLE);
                else
                    ivSelected.setVisibility(View.INVISIBLE);
                selectList(i);
            }
        });
    }

    private List<String> filterPath(List<String> path) {
        List list = new ArrayList();
        String[] endsWith = {"docx", "zip", "doc", "txt"};
        for (int ctr = 0; ctr < path.size(); ctr++) {
            for (String anEndsWith : endsWith) {
                if (path.get(ctr).endsWith(anEndsWith)) {
                    list.add(path.get(ctr));
                    break;
                }
            }
        }
        return list;
    }

    private void selectList(int i) {
        Log.d(TAG, "Position: " + i + " / " + "Path: " + allPath.get(i));

        if (!isExist(i))
            selectedPath.add(allPath.get(i));

        btnProceed.setText(selectedPath.size() + " Selected");
    }

    private boolean isExist(int i) {

        for (int ctr = 0; ctr < selectedPath.size(); ctr++) {
            if (selectedPath.get(ctr).equals(allPath.get(i))) {
                selectedPath.remove(ctr);
                Log.d(TAG, "Path didn't exists");
                return true;
            }
        }
        Log.d(TAG, "Path exists");
        return false;

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btnProceed)
    public void onClick() {

        if (selectedPath.size() > 0) {
            FileHelper.logList("Selected", selectedPath);
            String files[] = selectedPath.toArray(new String[selectedPath.size()]);
            new CompressAsyncTask(files).execute();
        } else {
            Toast.makeText(getActivity(), "Select files to share", Toast.LENGTH_LONG).show();
        }

    }

    private class CompressAsyncTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog pDialog;

        String files[];

        public CompressAsyncTask(String[] files) {
            this.files = files;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
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
                startActivity(new Intent(getActivity(), SenderActivity.class));
            } else {
                Log.d(TAG, "Compression FAILED");
                Toast.makeText(getActivity(), Const.ERROR_MESSAGE, Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
        }
    }
}
