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
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mesibo.api.Mesibo;

import static com.mesibo.messaging.MesiboConfiguration.STATUS_COLOR_OVER_PICTURE;
import static com.mesibo.messaging.MesiboConfiguration.STATUS_COLOR_WITHOUT_PICTURE;

public class MessageViewHolder extends MesiboRecycleViewHolder implements View.OnClickListener,
        View.OnLongClickListener {

    protected TextView otherUserName;
    protected TextView mTime;
    protected View mSelectedOverlay;
    protected ImageView mStatus;
    protected ImageView mFavourite;

    protected MessageView mMview;
    //protected int mType;
    protected MessageData mData = null;
    //private int mPosition = -1;
    //protected com.mesibo.messaging.ThumbnailProgressImageView mPicture;


    protected FrameLayout mBubble;

    private ClickListener listener;

    public MessageViewHolder(int type, View v, ClickListener listener) {

        super(v);
        mData = null;
        //mMessage = (EmojiconTextView ) v.findViewById(R.id.m_message);
        mTime = (TextView) v.findViewById(R.id.m_time);
        mStatus = (ImageView) v.findViewById(R.id.m_status);
        mFavourite = (ImageView) v.findViewById(R.id.m_star);

        mMview = (MessageView) v.findViewById(R.id.mesibo_message_view);

        int bubbleid = R.id.outgoing_layout_bubble;
        if(MesiboRecycleViewHolder.TYPE_INCOMING == type) {
            otherUserName = (TextView) v.findViewById(R.id.m_user_name);
            bubbleid = R.id.incoming_layout_bubble;
        }

        mBubble = (FrameLayout) v.findViewById(bubbleid);
        mSelectedOverlay = (View) v.findViewById(R.id.selected_overlay);

        this.listener = listener;
        v.setOnClickListener(this);
        v.setOnLongClickListener(this);
        //mMview.setOnClickListener(this);
    }

    public void setData(MessageData m, int position, boolean selected) {
        reset();

        setItemPosition(position);
        mData = m;
        mData.setViewHolder(this);

        if(MesiboRecycleViewHolder.TYPE_INCOMING == getType()) {
            if (m.getGroupId() != 0 && m.isShowName()) {
                otherUserName.setVisibility(View.VISIBLE);
                otherUserName.setTextColor(m.getNameColor());
                otherUserName.setText(m.getUsername());
            } else {
                otherUserName.setVisibility(View.GONE);
            }

            setupBackgroundBubble(mBubble, R.drawable.balloon_incoming_normal);
            //mStatus.setVisibility(View.GONE);
        } else {
            setupBackgroundBubble(mBubble, R.drawable.balloon_outgoing_normal);
            setupMessageStatus(mStatus, m.getStatus());
        }

        mTime.setText(m.getTimestamp());


        mFavourite.setVisibility(m.getFavourite()?View.VISIBLE:View.GONE);
        if (m.getMPNG()) {
            setupBackgroundBubble(mBubble, android.R.color.transparent);
        }
        if((null == mData.getTitle() || mData.getTitle().isEmpty()) && (null == mData.getMessage()
                || mData.getMessage().isEmpty())) {
            mTime.setTextColor(Color.parseColor(STATUS_COLOR_OVER_PICTURE));
            mFavourite.setColorFilter(Color.parseColor(STATUS_COLOR_OVER_PICTURE));

        } else {
            mFavourite.setColorFilter(Color.parseColor(STATUS_COLOR_WITHOUT_PICTURE));
            mTime.setTextColor(Color.parseColor(STATUS_COLOR_WITHOUT_PICTURE));
        }
        mMview.setData(mData);

        mSelectedOverlay.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
    }

    public void removeData() {
        if(null == mData)
            return;

        setItemPosition(-1);
        mData = null;
    }

    @Override
    public void reset() {
        if(null == mData)
            return;

        MessageData d = mData;
        mData = null; //so that no loop condition
        setItemPosition(-1);
        d.setViewHolder(null);
    }


    public void updateFileProgress(Mesibo.FileInfo file) {

        if(Mesibo.FileInfo.STATUS_FAILED == file.getStatus() || Mesibo.FileInfo.STATUS_RETRYLATER == file.getStatus()) {

            //TBD, we need to use default image for DOWNLOAD as we do not have message status
            if(Mesibo.FileInfo.MODE_UPLOAD == file.mode) {
                setupMessageStatus(mStatus, Mesibo.MSGSTATUS_FAIL);
                mMview.setProgress(file);
                return;
            }
        }

        // even if download was failed, we set file as transferred in mesibo api so that
        // it is not retried
        if(Mesibo.FileInfo.STATUS_INPROGRESS == file.getStatus() || file.isTransferred())
            mMview.setProgress(file);

    }

    public void setImage(Bitmap image) {
        mMview.setImage(image);
    }


    public void setupBackgroundBubble (FrameLayout fm, int resource)
    {
        int pL1 = fm.getPaddingLeft();
        int pT1 = fm.getPaddingTop();
        int pR1 = fm.getPaddingRight();
        int pB1 = fm.getPaddingBottom();

        fm.setBackgroundResource(resource);
        fm.setPadding(pL1, pT1, pR1, pB1);

    }

    // im will be null on receiver side as we do not have message status
    public void setupMessageStatus(ImageView im, int status) {
        im.setVisibility(mData.isDeleted()?View.GONE:View.VISIBLE);

        if(mData.isDeleted()) {
            //TBD, use a smaller image
            //im.setImageBitmap(MesiboImages.getDeletedMessageImage());
            return;
        }

        im.setImageBitmap(MesiboImages.getStatusImage(status));
    }


    @Override
    public void onClick(View v) {

        if (listener != null) {
            listener.onItemClicked(getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (listener != null) {
            return listener.onItemLongClicked(getAdapterPosition());
        }

        return false;
    }

    public interface ClickListener {
        public void onItemClicked(int position);

        public boolean onItemLongClicked(int position);
    }
}
