package com.cameraparamter.params;

import android.graphics.ImageFormat;

/**
 * Created by Administrator on 2016/12/1.
 */

public enum PictureFormat {

    NV21(
            1, ImageFormat.NV21, "NV21"),

    RGB_565(
            2, ImageFormat.RGB_565, "RGB_565"),

    JPEG(
            3, ImageFormat.JPEG, "JPEG");

    private int id;
    private int ID;
    private String name;

    PictureFormat(int id, int ID, String name) {
        this.id = id;
        this.ID = ID;
        this.name = name;
    }

    public static PictureFormat getPictureFormatById(int id) {
        for (PictureFormat pictureFormat : values()) {
            if (pictureFormat.id == id) {
                return pictureFormat;
            }
        }
        return PictureFormat.JPEG;
    }

    public static PictureFormat getPictureFormatByID(int ID) {
        for (PictureFormat pictureFormat : values()) {
            if (pictureFormat.ID == ID) {
                return pictureFormat;
            }
        }
        return PictureFormat.JPEG;
    }

    public static PictureFormat getPictureFormatByName(String name) {
        for (PictureFormat pictureFormat : values()) {
            if (pictureFormat.name.compareTo(name) == 0) {
                return pictureFormat;
            }
        }
        return PictureFormat.JPEG;
    }

    public int getID() {
        return ID;
    }

    @Override
    public String toString() {
        return name;
    }
}
