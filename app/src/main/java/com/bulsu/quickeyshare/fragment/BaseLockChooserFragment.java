package com.bulsu.quickeyshare.fragment;


import android.app.ProgressDialog;
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
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bulsu.quickeyshare.EncryptionHelper;
import com.bulsu.quickeyshare.R;
import com.bulsu.quickeyshare.adapter.BaseFileAdapter;
import com.bulsu.quickeyshare.data.Const;
import com.bulsu.quickeyshare.data.FileHelper;
import com.bulsu.quickeyshare.model.FileItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bulsu.quickeyshare.data.FileHelper.populateList;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseLockChooserFragment extends Fragment {

    String TAG = BaseLockChooserFragment.class.getSimpleName();
    String TAG2 = "Experiment";

    @Bind(R.id.ivSelectAll)
    ImageView ivSelectAll;
    @Bind(R.id.layoutSelectAll)
    LinearLayout layoutSelectAll;
    @Bind(R.id.list)
    ListView list;
    @Bind(R.id.btnProceed)
    Button btnProceed;

    ArrayList<FileItem> items;
    ArrayList<String> filePath;
    ArrayList<String> selectedPath;
    BaseFileAdapter adapter;

    boolean selectAll = false;
    int category;

    public BaseLockChooserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_base_lock_chooser, container, false);
        ButterKnife.bind(this, view);

        category = getArguments().getInt("category", 0);

        initList(category);
        initListener();

        return view;
    }

    private void initList(int category) {
        selectedPath = new ArrayList<>();
        filePath = new ArrayList<>();
        switch (category) {
            case Const.CAT_VIDEO:
                filePath = FileHelper.getVideoPath(getActivity());
                break;
            case Const.CAT_IMAGE:
                filePath = FileHelper.getImagesPath(getActivity());
                break;
        }
        items = populateList(filePath, category);
        adapter = new BaseFileAdapter(getActivity(), items);
        list.setAdapter(adapter);
    }

    private void initListener() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectList(i);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void selectList(int i) {

        if (!isExist(i)) {
            items.get(i).setSelected(true);
            selectedPath.add(items.get(i).getPath());
        } else {
            items.get(i).setSelected(false);
        }

        btnProceed.setText(selectedPath.size() + " Selected");


    }

    private boolean isExist(int i) {
        Log.d(TAG, items.get(i).getPath());
        for (int ctr = 0; ctr < selectedPath.size(); ctr++) {
            if (selectedPath.get(ctr).equals(items.get(i).getPath())) {
                selectedPath.remove(ctr);
                Log.d(TAG, "Path exists");

                return true;
            }
        }
        Log.d(TAG, "Path didn't exists");
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btnProceed)
    public void onClick() {
        new EncryptTask().execute();
    }

    private class EncryptTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Encrypting files...");
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int ctr = 0; ctr < selectedPath.size(); ctr++) {
                String origPath = selectedPath.get(ctr);
                String fileLocation = FileHelper.getFileLocation(origPath);
                String fileName = FileHelper.getFilename(origPath);
                String fileExtension = FileHelper.getFileExtension(origPath);
                Log.d(TAG2, "Old path: " + origPath);
                Log.d(TAG2, "File Location: " + fileLocation);
                Log.d(TAG2, "File name: " + fileName);
                Log.d(TAG2, "Extension: " + fileExtension);

                EncryptionHelper.encryptFile(getActivity(), fileName + ".qks", origPath);
                File file = new File(origPath);
                try {
                    file.getCanonicalFile().delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
            getActivity().finish();
        }
    }
}