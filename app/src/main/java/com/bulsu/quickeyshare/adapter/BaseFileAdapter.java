package com.bulsu.quickeyshare.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bulsu.quickeyshare.R;
import com.bulsu.quickeyshare.data.Const;
import com.bulsu.quickeyshare.data.FileHelper;
import com.bulsu.quickeyshare.model.FileItem;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mykelneds on 23/03/2017.
 */

public class BaseFileAdapter extends BaseAdapter {

    Context ctx;
    ArrayList<FileItem> items;
    LayoutInflater inflater;

    public BaseFileAdapter(Context ctx, ArrayList<FileItem> items) {
        this.ctx = ctx;
        this.items = items;

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

        FileItem item = items.get(i);
        String path = item.getPath();
        switch (item.getCategory()) {
            case Const.CAT_FILE:

                final FileHolder fHolder;

                if (view != null)
                    fHolder = (FileHolder) view.getTag();
                else {
                    view = inflater.inflate(R.layout.item_files, viewGroup, false);
                    fHolder = new FileHolder(view);
                    view.setTag(fHolder);
                }

                fHolder.tvFilename.setText(FileHelper.getFilename(path));
                fHolder.tvFileSize.setText(FileHelper.getFileSize(path));


                if (item.isSelected())
                    fHolder.ivSelected.setVisibility(View.VISIBLE);
                else
                    fHolder.ivSelected.setVisibility(View.INVISIBLE);

                break;

            case Const.CAT_VIDEO:

                final VideoHolder vHolder;

                if (view != null)
                    vHolder = (VideoHolder) view.getTag();
                else {
                    view = inflater.inflate(R.layout.item_video, viewGroup, false);
                    vHolder = new VideoHolder(view);
                    view.setTag(vHolder);
                }


                Bitmap bmpVidPrev = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                vHolder.ivVidPreview.setImageBitmap(bmpVidPrev);
                vHolder.tvDuration.setText(FileHelper.getVideoDuration(ctx, path));
                vHolder.tvFilename.setText(FileHelper.getFilename(path));
                vHolder.tvFileSize.setText(FileHelper.getFileSize(path));

                if (item.isSelected())
                    vHolder.ivSelected.setVisibility(View.VISIBLE);
                else
                    vHolder.ivSelected.setVisibility(View.INVISIBLE);

                break;

            case Const.CAT_IMAGE:

                final ImageHolder iHolder;

                if (view != null)
                    iHolder = (ImageHolder) view.getTag();
                else {
                    view = inflater.inflate(R.layout.item_image, viewGroup, false);
                    iHolder = new ImageHolder(view);
                    view.setTag(iHolder);
                }

                Picasso.with(ctx).load(new File(path)).into(iHolder.ivPreview);
                iHolder.tvFilename.setText(FileHelper.getFilename(path));
                iHolder.tvFileSize.setText(FileHelper.getFileSize(path));

                if (item.isSelected())
                    iHolder.ivSelected.setVisibility(View.VISIBLE);
                else
                    iHolder.ivSelected.setVisibility(View.INVISIBLE);

                break;

            case Const.CAT_AUDIO:

                final MusicHolder mHolder;

                if (view != null)
                    mHolder = (MusicHolder) view.getTag();
                else {
                    view = inflater.inflate(R.layout.item_music, viewGroup, false);
                    mHolder = new MusicHolder(view);
                    view.setTag(mHolder);
                }
                mHolder.tvFilename.setText(FileHelper.getFilename(path));
                mHolder.tvFileSize.setText(FileHelper.getFileSize(path));
                mHolder.tvDuration.setText(getAudioDuration(path));

                if (item.isSelected())
                    mHolder.ivSelected.setVisibility(View.VISIBLE);
                else
                    mHolder.ivSelected.setVisibility(View.INVISIBLE);

                break;
        }


        return view;
    }

    static class FileHolder {
        @Bind(R.id.tvFilename)
        TextView tvFilename;
        @Bind(R.id.tvFileSize)
        TextView tvFileSize;
        @Bind(R.id.ivSelected)
        ImageView ivSelected;
        @Bind(R.id.layoutItem)
        RelativeLayout layoutItem;

        FileHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class VideoHolder {
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

        VideoHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class ImageHolder {
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

        ImageHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class MusicHolder {
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

        MusicHolder(View view) {
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
