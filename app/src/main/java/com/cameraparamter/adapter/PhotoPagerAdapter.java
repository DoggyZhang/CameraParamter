package com.cameraparamter.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Administrator on 2016/11/8 0008.
 */

public class PhotoPagerAdapter extends PagerAdapter {

    //    private List<View> items;\
    private Context context;
    private List<String> items;
    private OnRemovePhotoListener onRemovePhotoListener;
    private final BitmapFactory.Options bitmapOptions;

    public PhotoPagerAdapter(Context context) {
        this.context = context;
        items = new ArrayList<>();
        bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inScaled = true;
        bitmapOptions.inMutable = true;
        bitmapOptions.inSampleSize = 5;
        bitmapOptions.inDensity = 1;
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public String getItem(int index) {
        return items.get(index);
    }

    public void addItem(String item) {
        this.items.add(item);
        notifyDataSetChanged();
    }

    public void addItems(Collection items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void clear() {
        this.items.clear();
        notifyDataSetChanged();
    }

    public void remove(int index) {
        this.items.remove(index);
        notifyDataSetChanged();
    }

    public int findItem(String item) {
        return items.indexOf(item);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        String itemPath = items.get(position);
//        Bitmap bitmap = BitmapUtils.createImageThumbnail(itemPath);
        Bitmap bitmap = BitmapFactory.decodeFile(itemPath, bitmapOptions);

        ImageView item = new ImageView(context);
        item.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        item.setAdjustViewBounds(true);
        item.setImageBitmap(bitmap);
        item.setOnClickListener(new View.OnClickListener() {
            private long preTime = 0;
            private int clickCount = 0;

            @Override
            public void onClick(View v) {
                long time = System.currentTimeMillis();
                if (preTime == 0 && clickCount == 0) {
                    clickCount = 1;
                    preTime = time;
                } else if (time - preTime < 500 && clickCount == 1) {
                    clickCount = 2;
                    preTime = time;
                } else if (time - preTime < 500 && clickCount == 2) {
                    clickCount = 0;
                    preTime = 0;
                    new AlertDialog.Builder(context)
                            .setTitle("删除该图片")
                            .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (onRemovePhotoListener != null) {
                                        onRemovePhotoListener.onRemovePhotoListener(position);
                                    }
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setCancelable(true)
                            .show();
                }
            }
        });
        container.addView(item);
        return item;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public interface OnRemovePhotoListener {
        void onRemovePhotoListener(int position);
    }

    public void setOnRemovePhotoListener(OnRemovePhotoListener onRemovePhotoListener) {
        this.onRemovePhotoListener = onRemovePhotoListener;
    }

    @Override
    public int getItemPosition(Object object) {
        return items.indexOf(object);
    }


}
