package com.bulsu.quickeyshare.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bulsu.quickeyshare.R;
import com.bulsu.quickeyshare.data.FileHelper;
import com.bulsu.quickeyshare.model.FileItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bulsu.quickeyshare.data.FileHelper.populateList;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment {


    @Bind(R.id.ivSelectAll)
    ImageView ivSelectAll;
    @Bind(R.id.layoutSelectAll)
    LinearLayout layoutSelectAll;
    @Bind(R.id.list)
    ListView list;
    @Bind(R.id.btnProceed)
    Button btnProceed;

    List<FileItem> items;
    List<String> selectedPath;
    List<String> allPath;

    public VideoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_chooser, container, false);
        ButterKnife.bind(this, view);

        initList();
        return view;
    }

    private void initList() {

        selectedPath = new ArrayList<>();
        items = populateList(FileHelper.getVideoPath(getActivity()));


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
                break;
            case R.id.btnProceed:
                break;
        }
    }
}
