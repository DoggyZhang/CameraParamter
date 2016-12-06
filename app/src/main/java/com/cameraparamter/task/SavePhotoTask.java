package com.cameraparamter.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;

import com.cameraparamter.utils.FilePathUtils;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/11/30.
 */

public class SavePhotoTask extends AsyncTask<byte[], Void, Boolean> {

    private OnSavePhotoTaskListener mOnSavePhotoTaskListener;

    public SavePhotoTask(OnSavePhotoTaskListener onSavePhotoTaskListener) {
        this.mOnSavePhotoTaskListener = onSavePhotoTaskListener;
    }

    @Override
    protected Boolean doInBackground(byte[]... params) {
        byte[] data = params[0];
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        Matrix matrix = new Matrix();
        matrix.setRotate(90, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        FileOutputStream fos = null;
        try {
            String filePath = FilePathUtils.createFilePath(FilePathUtils.FILENAME_PHOTO);
            fos = new FileOutputStream(filePath);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            fos = null;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if (mOnSavePhotoTaskListener != null) {
            mOnSavePhotoTaskListener.result(success);
        }
    }

    public interface OnSavePhotoTaskListener {
        void result(boolean success);
    }
}