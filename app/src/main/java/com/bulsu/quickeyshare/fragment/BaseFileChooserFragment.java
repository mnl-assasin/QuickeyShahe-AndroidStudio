package com.bulsu.quickeyshare.fragment;


import android.app.Activity;
import android.content.Intent;
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

import com.bulsu.quickeyshare.R;
import com.bulsu.quickeyshare.activity.ReviewFilesActivity;
import com.bulsu.quickeyshare.adapter.BaseFileAdapter;
import com.bulsu.quickeyshare.data.Const;
import com.bulsu.quickeyshare.data.FileHelper;
import com.bulsu.quickeyshare.model.FileItem;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bulsu.quickeyshare.data.FileHelper.populateList;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFileChooserFragment extends Fragment {

    String TAG = BaseFileChooserFragment.class.getSimpleName();

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

    public BaseFileChooserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_chooser, container, false);
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
            case Const.CAT_FILE:
                filePath = getFiles(FileHelper.getFilesPath(getActivity()));
                break;
            case Const.CAT_VIDEO:
                filePath = FileHelper.getVideoPath(getActivity());
                break;
            case Const.CAT_IMAGE:
                filePath = FileHelper.getImagesPath(getActivity());
                break;
            case Const.CAT_AUDIO:
                filePath = FileHelper.getAudioPath(getActivity());
                break;
        }
        items = populateList(filePath, category);
        adapter = new BaseFileAdapter(getActivity(), items);
        list.setAdapter(adapter);
    }

    private ArrayList<String> getFiles(ArrayList<String> tempPath) {
        ArrayList<String> path = new ArrayList<>();

        for (int ctr = 0; ctr < tempPath.size(); ctr++) {
            String ext = FileHelper.getFileExtension(tempPath.get(ctr));
            Log.d("Extension", "Ext: " + ext);

            if (ext.equalsIgnoreCase("docx") || ext.equalsIgnoreCase("doc") || ext.equalsIgnoreCase("txt") ||
                    ext.equalsIgnoreCase("pdf") || ext.equalsIgnoreCase("text")) {
                path.add(tempPath.get(ctr));
            }
        }

        return path;
    }


    private void initListener() {

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "Position: " + i);
                selectList(i);
                adapter.notifyDataSetChanged();
            }
        });

    }

    double maxSize = 512 * 1024;

    private void selectList(int i) {

        boolean isBelowLimit = FileHelper.isBelowLimit(selectedPath);


//        if (!isExist(i) && isBelowLimit) {
//            items.get(i).setSelected(true);
//            selectedPath.add(items.get(i).getPath());
//        } else if (!isBelowLimit)
//            Toast.makeText(getActivity(), "500 MB Limit Reached", Toast.LENGTH_LONG).show();
//        else {
//            items.get(i).setSelected(false);
//        }


        if (!isExist(i)) {

            double totalSize = FileHelper.getAllSize(selectedPath);
            double size = FileHelper.getSize(items.get(i).getPath());

//            if ((totalSize + size) < maxSize) {
            items.get(i).setSelected(true);
            selectedPath.add(items.get(i).getPath());
//            } else {
//                Toast.makeText(getActivity(), "500 MB Limit Reached", Toast.LENGTH_LONG).show();
//            }

        } else {
            items.get(i).setSelected(false);
        }

        btnProceed.setText(selectedPath.size() + " Selected (" + FileHelper.getFileSize(selectedPath) + ")");
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

    @OnClick({R.id.layoutSelectAll, R.id.btnProceed})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layoutSelectAll:
                onSelectAllClick();
                break;
            case R.id.btnProceed:

                if (selectedPath.size() > 0) {
                    Bundle extras = new Bundle();
                    extras.putStringArrayList("selectedFiles", selectedPath);
                    extras.putInt("category", category);
                    startActivityForResult(new Intent(getActivity(), ReviewFilesActivity.class).putExtras(extras), 1000);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000)
            if (resultCode == Activity.RESULT_OK) {
                (getActivity()).finish();
            }

    }

    ArrayList<String> tempPath;

    private void onSelectAllClick() {
        if (selectAll) {
            selectAll = false;
            selectedPath.clear();

            for (int ctr = 0; ctr < items.size(); ctr++) {
                items.get(ctr).setSelected(false);
                ivSelectAll.setImageResource(R.drawable.item_unselected);
            }
        } else {

//            for (int ctr = 0; ctr < items.size(); ctr++) {
//                selectedPath.add(items.get(ctr).getPath());
//
//            }
//
//            if (FileHelper.isBelowLimit(selectedPath)) {
//                selectAll = true;
//                for (int ctr = 0; ctr < items.size(); ctr++) {
//                    items.get(ctr).setSelected(true);
//                    ivSelectAll.setImageResource(R.drawable.item_selected);
//
//                }
//            } else {
//                Toast.makeText(getActivity(), "500 MB Limit Reached", Toast.LENGTH_LONG).show();
//            }

            tempPath = new ArrayList<>();
            for (int ctr = 0; ctr < items.size(); ctr++)
                tempPath.add(items.get(ctr).getPath());

//            if (FileHelper.getAllSize(tempPath) < maxSize) {
            selectAll = true;
            selectedPath = tempPath;
            for (int ctr = 0; ctr < items.size(); ctr++)
                items.get(ctr).setSelected(true);

            ivSelectAll.setImageResource(R.drawable.item_selected);
//            } else
//                Toast.makeText(getActivity(), "500 MB Limit Reached", Toast.LENGTH_LONG).show();

        }
        btnProceed.setText(selectedPath.size() + " Selected (" + FileHelper.getFileSize(selectedPath) + ")");
        adapter.notifyDataSetChanged();
    }
}
