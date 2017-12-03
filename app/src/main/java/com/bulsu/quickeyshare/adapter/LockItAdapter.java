package com.bulsu.quickeyshare.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bulsu.quickeyshare.R;
import com.bulsu.quickeyshare.data.FileHelper;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mykelneds on 27/03/2017.
 */

public class LockItAdapter extends BaseAdapter {

    Context ctx;
    ArrayList<String> items;
    LayoutInflater inflater;

    public LockItAdapter(Context ctx, ArrayList<String> items) {
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
        final ViewHolder holder;

        if (view != null)
            holder = (ViewHolder) view.getTag();
        else {
            view = inflater.inflate(R.layout.item_image, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        String path = items.get(i);

        holder.tvFilename.setText(FileHelper.getFilename(path));
        holder.tvFileSize.setText(FileHelper.getFileSize(path));

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

//        byte[] bytes = EncryptionHelper.decodeFile(ctx, path);
//        if (bytes != null)
//            if (bytes.length > 0) {
//                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
//                holder.ivPreview.setImageBitmap(bmp);
//            }
//        Drawable d = Drawable.createFromResourceStream(new ByteArrayInputStream(bytes), null);

        return view;
    }

    static class ViewHolder {
        @Bind(R.id.ivPreview)
        ImageView ivPreview;
        @Bind(R.id.tvFilename)
        TextView tvFilename;
        @Bind(R.id.tvFileSize)
        TextView tvFileSize;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
