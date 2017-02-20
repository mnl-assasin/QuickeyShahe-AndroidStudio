package com.bulsu.quickeyshare.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bulsu.quickeyshare.R;
import com.bulsu.quickeyshare.data.FileHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

/**
 * Created by mykelneds on 18/02/2017.
 */

public class ImageAdapter extends BaseAdapter {

    List<String> items;
    Context ctx;
    LayoutInflater inflater;

    public ImageAdapter(List<String> items, Context ctx) {
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
            view = inflater.inflate(R.layout.item_image, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }


        String path = items.get(i);
        Log.d(TAG, "PATH: " + path);
        Picasso.with(ctx).load(new File(path)).into(holder.ivPreview);
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
        @Bind(R.id.ivPreview)
        ImageView ivPreview;
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
