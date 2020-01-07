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

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboUtils;

public class UserData {

    private Bitmap mUserImage = null;
    private Bitmap mUserImageThumbnail = null;

    private String mTime;
    private Integer mStatus;

    private String mLastMessage;

    private Integer mUnreadCount;
    private String mCachecPicturePath;
    private long mId;

    private Mesibo.UserProfile mUser;
    private boolean mFixedImage = false;
    private int mUserListPosition = -1;
    private long mTypingTs = 0;
    private Mesibo.UserProfile mTypingProfile = null;
    private boolean mDeleted = false;


    public UserData(Mesibo.UserProfile user){
        mUser = user;
        mUserImage = null;
        mUserImageThumbnail = null;

        mTime="";
        mStatus = Mesibo.MSGSTATUS_RECEIVEDNEW;
        mLastMessage ="";
        mUnreadCount =0;
        mId = 0;
        mCachecPicturePath = null;

    }

    public void setMessage(long mid, String time, Integer status,  boolean deleted, String message){
        this.mTime=time;
        this.mStatus=status;
        this.mLastMessage =message;
        this.mId = mid;
        mDeleted = deleted;
    }

    public void setMessage(String message){
        this.mLastMessage =message;
    }

    public String getPeer() {
        return mUser.address;
    }
    public long getGroupId() { return mUser.groupid; }
    public long getmid() {
        return mId;
    }

    public boolean isDeletedMessage() {
        return mDeleted;
    }

    public void setDeletedMessage(boolean deleted) {
        mDeleted = deleted;
    }
    /*
    public String getUserProfile(){
        return  mUser.profile;
    }*/

    public void setUser(Mesibo.UserProfile user) {
        mUser = user;
        if(mFixedImage)
            return;

        if(null == mUser.picturePath || null == mCachecPicturePath || !mUser.picturePath.equalsIgnoreCase(mCachecPicturePath)) {
            mUserImageThumbnail = null;
            mUserImage = null;
        }
    }

    public void setFixedImage(boolean fixed) {
        mFixedImage = fixed;
    }

    public Integer getUnreadCount() {
        return mUnreadCount;
    }

    public void setUnreadCount(int count) {
        mUnreadCount = count;
    }

    public void clearUnreadCount() {
        mUnreadCount = 0;
    }

    public String getImagePath() {
        return Mesibo.getUserProfilePicturePath(mUser, Mesibo.FileInfo.TYPE_AUTO);
    }

    public void setImageThumbnail(Bitmap b) {
        mUserImageThumbnail = b;
    }

    public Bitmap getImage() {
        return mUserImage;
    }

    public void setImage(Bitmap b) {
        this.mUserImage = b;
    }

    public Bitmap getThumbnail() {
        String path = getImagePath();
        if(null == path || null == mCachecPicturePath || !path.equalsIgnoreCase(mCachecPicturePath)) {
            mUserImageThumbnail = null;
        }

        if(null != mUserImageThumbnail)
            return mUserImageThumbnail;

        if(null == mUser)
            return null;

        mCachecPicturePath = path;
        if(null == mCachecPicturePath)
            return null;

        mUserImageThumbnail = MesiboUtils.loadBitmapFromFile(mCachecPicturePath, 60, true);
        return mUserImageThumbnail;
    }

    public Integer getStatus() {
        return mStatus;
    }

    public void setStatus(Integer mStatus) {
        this.mStatus = mStatus;
    }


    public String getLastMessage() {
        return mLastMessage;
    }

    public String getTime() {
        return mTime;
    }



    public String getUserName() {
        return mUser.name;
    }


    public void clearTyping() {
        mTypingTs = 0;
    }

    public void setTyping(Mesibo.UserProfile profile) {
        mTypingTs = Mesibo.getTimestamp();
        mTypingProfile = profile;
    }

    public long getTypingTimeout() {
        if(0 == mTypingTs)
            return 0;

        long elapsed = Mesibo.getTimestamp() - mTypingTs;
        if(elapsed >= MesiboUI.getConfig().mTypingIndicationTimeMS)
            return 0;

        return MesiboUI.getConfig().mTypingIndicationTimeMS-elapsed;
    }

    public Mesibo.UserProfile getTypingProfile() {
        return mTypingProfile;
    }

    public void setUserListPosition(int position) {
        mUserListPosition = position;
    }

    public int getUserListPosition() {
        return mUserListPosition;
    }

    public static UserData getUserData(Mesibo.MessageParams params) {
        if(null == params || null == params.profile)
            return null;


        return getUserData(params.profile);
    }

    public static UserData getUserData(Mesibo.UserProfile profile) {
        if(null == profile)
            return null;

        UserData d = (UserData) profile.other;
        if(null == d) {
            d = new UserData(profile);
            profile.other = d;
        }

        return d;
    }



}
