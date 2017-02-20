package com.bulsu.quickeyshare.adapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
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

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

/**
 * Created by mykelneds on 18/02/2017.
 */

public class MusicAdapter extends BaseAdapter {

    List<String> items;
    Context ctx;
    LayoutInflater inflater;

    public MusicAdapter(List<String> items, Context ctx) {
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
            view = inflater.inflate(R.layout.item_music, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }


        String path = items.get(i);
        Log.d(TAG, "PATH: " + path);
        holder.tvFilename.setText(FileHelper.getFilename(path));
        holder.tvFileSize.setText(FileHelper.getFileSize(path));
        holder.tvDuration.setText(getAudioDuration(path));
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

    public String getAudioDuration(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(ctx, Uri.parse(path));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long millis = Long.parseLong(time);

        return String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

    }

}
