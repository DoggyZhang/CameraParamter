package com.cameraparamter.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cameraparamter.R;
import com.cameraparamter.entry.RecordInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/11/30.
 */

public class RecordRecyclerAdapter extends RecyclerView.Adapter<RecordRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private List<RecordInfo> items;

    private OnItemRemoveListener mOnItemRemoveListener;

    public RecordRecyclerAdapter(Context context) {
        this.mContext = context;
        this.items = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflateView = LayoutInflater.from(mContext).inflate(R.layout.recycler_record_item, parent, false);
        return new ViewHolder(inflateView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        RecordInfo recordInfo = items.get(position);
        Bitmap videoThumbnail = createVideoThumbnail(String.valueOf(Uri.fromFile(new File(recordInfo.getPath()))), 512, 384);
        long duration = recordInfo.getDuration();
        int ss = (int) (duration / 1000);
        int ms = (int) (duration % 1000);
        int s = ss % 60;
        int m = ss / 60 % 60;
        String time = String.format("%02d:%02d.%03d", m, s, ms);

        holder.root.setTag(recordInfo.getPath());
        holder.iv_record.setImageBitmap(videoThumbnail);
        holder.tv_record_name.setText(recordInfo.getName());
        holder.tv_record_duration.setText(time);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void add(RecordInfo item) {
        if (item == null) {
            return;
        }
        this.items.add(item);
        notifyDataSetChanged();
    }

    public void addAll(List items) {
        if (items == null || items.size() == 0) {
            return;
        }
        if (items.get(0) instanceof String) {
            new ConvertToRecordInfoTask(new OnConvertToRecordInfoFinishListener() {
                @Override
                public void onFinish(List<RecordInfo> items) {
                    RecordRecyclerAdapter.this.items.addAll(items);
                    notifyDataSetChanged();
                }
            }).execute(items);
        } else {
            this.items.addAll(items);
            notifyDataSetChanged();
        }
    }

    public void clear() {
        this.items.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View root;
        ImageView iv_record;
        TextView tv_record_duration;
        TextView tv_record_name;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            initView(root);
            initEvent();
        }

        private void initView(View root) {
            iv_record = (ImageView) root.findViewById(R.id.iv_recycler_record_item);
            tv_record_duration = (TextView) root.findViewById(R.id.tv_recycler_record_item_duration);
            tv_record_name = (TextView) root.findViewById(R.id.tv_recycler_record_item_name);
        }

        private void initEvent() {
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String path = (String) root.getTag();
                    intent.setDataAndType(Uri.parse(path), "video/mp4");
                    mContext.startActivity(intent);
                }
            });
            root.setLongClickable(true);
            root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(mContext)
                            .setTitle("是否删除该视频")
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

    private Bitmap createVideoThumbnail(String url, int width, int height) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int kind = MediaStore.Video.Thumbnails.MINI_KIND;
        try {
            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else {
                retriever.setDataSource(url);
            }
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        if (kind == MediaStore.Images.Thumbnails.MICRO_KIND && bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }

    public class ConvertToRecordInfoTask extends AsyncTask<List<String>, Void, List<RecordInfo>> {

        private OnConvertToRecordInfoFinishListener mOnConvertToRecordInfoFinishListener;
        private MediaMetadataRetriever retr;

        public ConvertToRecordInfoTask(OnConvertToRecordInfoFinishListener onConvertToRecordInfoFinishListener) {
            this.mOnConvertToRecordInfoFinishListener = onConvertToRecordInfoFinishListener;
            retr = new MediaMetadataRetriever();
        }

        @Override
        protected List doInBackground(List<String>... params) {
            List<String> paths = params[0];
            List<RecordInfo> recordInfos = new ArrayList<>();
            for (String path : paths) {
                RecordInfo recordInfo = createRecordInfo(path);
                if (recordInfo != null) {
                    recordInfos.add(recordInfo);
                }
            }
            return recordInfos;
        }

        private RecordInfo createRecordInfo(String path) {
            try {
                retr.setDataSource(path);
                retr.setDataSource(mContext, Uri.fromFile(new File(path)));
                RecordInfo recordInfo = new RecordInfo();
                // record name
                String name = path.substring(path.lastIndexOf(File.separatorChar) + 1);
                recordInfo.setName(name);
                // record path
                recordInfo.setPath(path);
                // record duration(ms)
                String duration = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                recordInfo.setDuration(Long.valueOf(duration));
                return recordInfo;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<RecordInfo> list) {
            if (mOnConvertToRecordInfoFinishListener != null) {
                mOnConvertToRecordInfoFinishListener.onFinish(list);
            }
        }
    }

    public interface OnConvertToRecordInfoFinishListener {
        void onFinish(List<RecordInfo> items);
    }

    public interface OnItemRemoveListener {
        void onItemRemove();
    }

    public void setOnItemRemoveListener(OnItemRemoveListener onItemRemoveListener) {
        this.mOnItemRemoveListener = onItemRemoveListener;
    }
}
