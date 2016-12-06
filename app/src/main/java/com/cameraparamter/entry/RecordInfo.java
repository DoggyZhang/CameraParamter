package com.cameraparamter.entry;

/**
 * Created by Administrator on 2016/12/2.
 */

public class RecordInfo {

    public String name;
    public String path;
    public long duration;

    public RecordInfo() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
