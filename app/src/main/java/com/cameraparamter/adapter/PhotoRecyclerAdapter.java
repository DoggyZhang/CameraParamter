package com.cameraparamter.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cameraparamter.R;
import com.cameraparamter.fragment.PhotoDetailActivity;
import com.cameraparamter.utils.BitmapUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Administrator on 2016/11/17.
 */

public class PhotoRecyclerAdapter extends RecyclerView.Adapter<PhotoRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mItems;

    private OnItemRemoveListener mOnItemRemoveListener;

    public PhotoRecyclerAdapter(Context context) {
        this.mContext = context;
        mItems = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflateView = LayoutInflater.from(mContext).inflate(R.layout.recycler_photo_item, parent, false);
        return new ViewHolder(inflateView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String photoPath = mItems.get(position);
        holder.root.setTag(photoPath);
        Bitmap bitmap = BitmapUtils.createImageThumbnail(photoPath);
        holder.iv_photolist_item.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void add(String item) {
        this.mItems.add(item);
        notifyDataSetChanged();
    }

    public void addAll(Collection items) {
        this.mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void clear() {
        this.mItems.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View root;
        ImageView iv_photolist_item;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            iv_photolist_item = (ImageView) itemView.findViewById(R.id.iv_recycler_photo_item);
            initEvent();
        }

        private void initEvent() {
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PhotoDetailActivity.class);
                    intent.putExtra(PhotoDetailActivity.SELECTPATH, (String) root.getTag());
                    mContext.startActivity(intent);
                }
            });

            root.setLongClickable(true);
            root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(mContext)
                            .setTitle("是否删除该照片")
                            .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String itemPath = (String) root.getTag();
                                    File file = new File(itemPath);
                                    if (file.exists()) {
                                        file.delete();
                                    }
                                    if (mOnItemRemoveListener != null) {
                                        mOnItemRemoveListener.onItemRemove();
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
                    return true;
                }
            });
        }
    }

    public interface OnItemRemoveListener {
        void onItemRemove();
    }

    public void setOnItemRemoveListener(OnItemRemoveListener onItemRemoveListener) {
        this.mOnItemRemoveListener = onItemRemoveListener;
    }
}
