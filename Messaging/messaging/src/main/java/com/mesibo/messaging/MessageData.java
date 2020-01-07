/* By obtaining and/or copying this work, you agree that you have read,
 * understood and will comply with the following terms and conditions.
 *
 * Copyright (c) 2020 Mesibo
 * https://mesibo.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the terms and condition mentioned
 * on https://mesibo.com as well as following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions, the following disclaimer and links to documentation and
 * source code repository.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of Mesibo nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior
 * written permission.
 *
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * Documentation
 * https://mesibo.com/documentation/
 *
 * Source Code Repository
 * https://github.com/mesibo/ui-modules-android

 */
package com.mesibo.messaging;


import android.graphics.Bitmap;
import android.text.TextUtils;

import com.mesibo.api.Mesibo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.mesibo.messaging.MesiboConfiguration.MESSAGE_DELETED_STRING;


public class MessageData {
    private String mUserName = null;
    private String mTimeStamp = null;
    private String mDateStamp = null;
    private String mPrintDateStamp = null;
    private long mTimestampMs = 0;
    private Boolean mPNG = false;
    private long mGroupId = 0;
    private int mType = 0;
    private boolean mFavourite = false;
    private String mPeer = null;

    private boolean mIsReply = false;
    private String mReplyString = null;
    private Bitmap mReplyBitmap = null;
    private String mReplyName = null;
    private boolean mShowName = true;

    public static final int MESSAGEDATA_TYPE_MESSAGE = 1;
    public static final int MESSAGEDATA_TYPE_DATE = 2;
    public static final int MESSAGEDATA_TYPE_CUSTOM = 3;

    private MesiboRecycleViewHolder mViewHolder = null;
    private int mNameColor = 0xff777777;
    private Mesibo.MessageParams mParams = null;
    private Mesibo.MesiboMessage mMsg = null;
    private boolean locationImageRequested = false;
    private boolean mDeleted = false;


    MessageData(Mesibo.MessageParams params, long mid, String peer, String username, String message, long ts, int status, long gid) {


        mMsg = new Mesibo.MesiboMessage();
        mMsg.message = message;
        mMsg.status = status;
        mMsg.mid = mid;
        mMsg.type = params.type;
        mUserName = username;
        mPNG = false;
        mGroupId = gid;
        mPeer = peer;
        mParams = params;
        mDeleted = params.isDeleted();

        mType = MESSAGEDATA_TYPE_MESSAGE;

        if(0 == ts)
            ts = Mesibo.getTimestamp();

        mTimestampMs = ts;
        mMsg.ts = ts;
        setTimestamps();
    }

    MessageData(int type, long ts) {
        mType = type;
        mTimeStamp = null;
        if(0 == ts)
            ts = Mesibo.getTimestamp();
        mTimestampMs = ts;
        setTimestamps();
    }

    void setParams(Mesibo.MessageParams params) {
        mParams = params;
    }

    Mesibo.MessageParams getParams() {
        return mParams;
    }

    private Mesibo.MessageListener mMessageListener = null;
    void setMessageListener(Mesibo.MessageListener listener) {
        mMessageListener = listener;

    }


    void setViewHolder(MesiboRecycleViewHolder vh) {
        // detach data from any existing viewHolder so in case that viewHolder is recycled,
        // it doesn't clean this message
        MesiboRecycleViewHolder pv = mViewHolder; //to avoid race condition in reset
        mViewHolder = null;
        if(null != pv) {
            pv.reset();
        }

        mViewHolder = vh;
        if(null != vh && null != mMsg && null != mMsg.location && null == mMsg.location.image) {
            if(mMessageListener != null && !locationImageRequested) {
                locationImageRequested = true;
                Mesibo.updateLocationImage(mParams, mMsg.location, mMessageListener);
            }
        }
    }

    public Mesibo.MesiboMessage getMesiboMessage() {
        return mMsg;
    }

    public int getPosition() {
        if(null != mViewHolder)
            return mViewHolder.getItemPosition();
        return -1;
    }

    MesiboRecycleViewHolder getViewHolder() {
        return mViewHolder;
    }

    public Mesibo.FileInfo getFile() { return mMsg.file; }

    public void setFile(Mesibo.FileInfo file) {
        mMsg.file = file;
        if(null != mMsg.file)
            mMsg.file.setData(this);
    }

    public void setLocation(Mesibo.Location location) {
        mMsg.location = location;
        if(null != mMsg.location)
            mMsg.location.setData(this);
        //LatLng point = new LatLng(location.lat, location.lon);
    }

    private void setTimestamps() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mTimestampMs);
        Date currtime = (Date) calendar.getTime();

        mTimeStamp = null;
        mDateStamp = null;

        SimpleDateFormat sdf;
        if(MESSAGEDATA_TYPE_MESSAGE == mType) {
            sdf = new SimpleDateFormat("HH:mm");
            mTimeStamp = sdf.format(currtime);
        }

        //date is needed in all nessages to compare against previous date
        sdf = new SimpleDateFormat("dd/MM/yy");
        mDateStamp = sdf.format(currtime);

        mPrintDateStamp = mDateStamp;
        int days = Mesibo.daysElapsed(mTimestampMs);
        if(0 == days)
            mPrintDateStamp = "Today";
        else if(1 == days)
            mPrintDateStamp = "Yesterday";
        else if(days < 7) {
            sdf = new SimpleDateFormat("E, dd MMM");
            mPrintDateStamp = sdf.format(currtime);
        }
    }

    public boolean isImageVideo() {
        if(null == mMsg || null == mMsg.file) return false;


        if(mMsg.file.type == Mesibo.FileInfo.TYPE_IMAGE || mMsg.file.type == Mesibo.FileInfo.TYPE_VIDEO)
            return true;

        return false;
    }

    public boolean isLocation() {
        if(null == mMsg)
            return false;
        return ((null != mMsg.location)?true:false);
    }

    public long getGroupId() {
        return mGroupId;
    }

    public String getPeer() {
        return mPeer;
    }

    public String getUsername() {
        if(TextUtils.isEmpty(mUserName))
            return getPeer();

        return mUserName;
    }
    public long getMid() {
        if(null == mMsg)
            return -1;
        return mMsg.mid;
    }

    public int getMessageType() {
        if(null == mMsg)
            return -1;
        return mMsg.type;
    }

    public Mesibo.Location getLocation() {
        return mMsg.location;
    }


    public String getTitle() {
        if(null == mMsg)
            return null;
        if(null != mMsg.file)
            return mMsg.file.title;
        if(null != mMsg.location)
            return mMsg.location.title;

        return null;
    }

    public String getMessage() {
        if(null == mMsg)
            return null;

        if(isDeleted())
            return MESSAGE_DELETED_STRING;

        return mMsg.message;
    }
    public Boolean getMPNG() {
        return mPNG;
    }

    public String getTimestamp() {
        return mTimeStamp;
    }
    public long getTimestampMs() { return mTimestampMs; }

    public int getStatus() {
        if(null == mMsg)
            return -1;
        return  mMsg.status;
    }

    public boolean isDeleted() {
        return mDeleted;
    }

    public void setDeleted(boolean deleted) {
        mDeleted = deleted;
    }

    public String getUrl() {
        if(null == mMsg || null == mMsg.file) return null;
        return mMsg.file.getUrl();
    }

    public Bitmap getImage() {
        if(null == mMsg) return null;

        if(null != mMsg.file)
            return mMsg.file.image;

        if(null != mMsg.location) {
            if(null != mMsg.location.image)
                return mMsg.location.image;

            return MesiboImages.getDefaultLocationBitmap();
        }

        return null;

    }

    public boolean hasImage() {
        if(null == mMsg)
            return false;

        return (null != mMsg.file || null != mMsg.location);
    }

    public boolean isFileTransferred() {
        if(null == mMsg || null == mMsg.file)
            return false;

        return mMsg.file.isTransferred();
    }

    public String getDateStamp() {
        return mDateStamp;
    }

    public String getPrintDateStamp() {
        return mPrintDateStamp;
    }

    public void setStaus(int status) {
        if(null != mMsg)
            mMsg.status = status;
    }

    public int getType() { return mType; }

    public void setFavourite(Boolean favourite) {
        mFavourite = favourite;

    }
    public Boolean getFavourite() {
        return mFavourite;

    }

    public boolean getReplyStatus() {
        return mIsReply;
    }

    public void setReplyStatus(boolean mIsReply) {
        this.mIsReply = mIsReply;
    }

    public String getReplyString() {
        return mReplyString;
    }

    public void setReplyString(String mReplyString) {
        this.mReplyString = mReplyString;
    }

    public Bitmap getReplyBitmap() {
        return mReplyBitmap;
    }

    public void setReplyBitmap(Bitmap mReplyBitmap) {
        this.mReplyBitmap = mReplyBitmap;
    }

    public String getReplyName() {
        return mReplyName;
    }

    public void setReplyName(String mReplyName) {
        this.mReplyName = mReplyName;
    }

    public void setNameColor(int color) {
        mNameColor = color;
    }

    public int getNameColor() {
        return mNameColor;
    }

    public void checkPreviousData(MessageData pd) {
        if(hasImage() || pd.hasImage()) {
            mShowName = true;
            return;
        }

        String prevPeer = pd.getPeer();
        // TBD, any message after outgoingMessage must have mShowName enabked.
        // check if prevPeer is different for outgoing message
        if(null != prevPeer && null != mPeer && prevPeer.equalsIgnoreCase(mPeer)) {
            mShowName = false;
        }

    }

    public boolean isShowName() {
        return mShowName;
    }
}
