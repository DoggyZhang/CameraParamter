package com.cameraparamter.utils;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/11/29.
 */

public class FilePathUtils {
    public static final String photoDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();

    public static final String recordDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath();

    public static final int FILENAME_PHOTO = 0x01;
    public static final int FILENAME_RECORD = 0x02;

    public static String createFileName(int fileType) {
        String fileName = null;
        switch (fileType) {
            case FILENAME_PHOTO:
                fileName = "IMG_" +
                        new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
                        + ".jpeg";
                break;
            case FILENAME_RECORD:
                fileName = "VID_" +
                        new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
                        + ".mp4";
                break;
        }
        return fileName;
    }

    public static String createFilePath(int fileType) {
        String path = null;
        switch (fileType) {
            case FILENAME_PHOTO:
                path = photoDir + File.separator + createFileName(fileType);
                break;
            case FILENAME_RECORD:
                path = recordDir + File.separator + createFileName(fileType);
                break;
        }
        return path;
    }

    public static File createFile(int fileType) {
        File file = new File(createFilePath(fileType));
        if (file.exists()) {
            file.delete();
        }
        return file;
    }

}
