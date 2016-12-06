package com.cameraparamter.params;

/**
 * Created by Administrator on 2016/12/1.
 */

public enum FaceDetect {
    ON(
            1, "On"
    ),
    OFF(
            2, "Off"
    );

    private int id;
    private String name;

    FaceDetect(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public static FaceDetect getFocusModeById(int id) {
        for (FaceDetect faceDetect : values()) {
            if (faceDetect.id == id) {
                return faceDetect;
            }
        }
        return FaceDetect.OFF;
    }

    public static FaceDetect getFocusModeByName(String name) {
        for (FaceDetect faceDetect : values()) {
            if (faceDetect.name.compareTo(name) == 0) {
                return faceDetect;
            }
        }
        return FaceDetect.OFF;
    }

    @Override
    public String toString() {
        return name;
    }
}
