package com.bulsu.quickeyshare.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bulsu.quickeyshare.R;
import com.bulsu.quickeyshare.data.FileHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mykelneds on 18/02/2017.
 */

public class VideoAdapter extends BaseAdapter {

    List<String> items;
    Context ctx;
    LayoutInflater inflater;

    public VideoAdapter(List<String> items, Context ctx) {
        this.items = items;
        this.ctx = ctx;

        inflater = LayoutInflater.from(ctx);

    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final ViewHolder holder;

        if (view != null)
            holder = (ViewHolder) view.getTag();
        else {
            view = inflater.inflate(R.layout.item_video, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        String path = items.get(i);

        Bitmap bmpVidPrev = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
        holder.ivVidPreview.setImageBitmap(bmpVidPrev);
//        holder.tvDuration.setText(getVideoDuration(path));
        holder.tvFilename.setText(FileHelper.getFilename(path));
        holder.tvFileSize.setText(FileHelper.getFileSize(path));
//        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(holder.ivSelected.getVisibility() == View.INVISIBLE)
//                    holder.ivSelected.setVisibility(View.VISIBLE);
//                else
//                    holder.ivSelected.setVisibility(View.INVISIBLE);
//            }
//        });


        return view;
    }


    static class ViewHolder {
        @Bind(R.id.ivVidPreview)
        ImageView ivVidPreview;
        @Bind(R.id.tvDuration)
        TextView tvDuration;
        @Bind(R.id.tvFilename)
        TextView tvFilename;
        @Bind(R.id.tvFileSize)
        TextView tvFileSize;
        @Bind(R.id.ivSelected)
        ImageView ivSelected;
        @Bind(R.id.layoutItem)
        RelativeLayout layoutItem;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }




}
