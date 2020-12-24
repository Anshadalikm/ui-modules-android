package com.mesibo.calls.ui.CallLogs;

import android.graphics.Bitmap;

import com.mesibo.api.MesiboUtils;

public class CallLogsItem {

    private String name;
    private String img;
    private long mid;
    private long ts;
    private int status;
    private String peer;
    private int type;
    private int count;
    private int duration;
    private MesiboUtils.TimeStamps timeStamps;
    private boolean isSelected = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
        timeStamps = MesiboUtils.getTimestamps(ts);
    }

    public MesiboUtils.TimeStamps getTimeStamps() {
        return timeStamps;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPeer() {
        return peer;
    }

    public void setPeer(String peer) {
        this.peer = peer;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isAnswered() {
        return  (duration > 0);
    }

    public boolean isBusy() {
        return  (0 == duration && (type&2) > 0);
    }

    public boolean isVideo() {
        return  ((type&1) > 0);
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isSelected() {
        return isSelected;
    }

    public long getMid() {
        return mid;
    }

    public void setMid(long mid) {
        this.mid = mid;
    }
}
