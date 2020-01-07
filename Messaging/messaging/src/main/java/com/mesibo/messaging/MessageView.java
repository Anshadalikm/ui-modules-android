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

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mesibo.api.Mesibo;
import com.mesibo.emojiview.EmojiconTextView;

import static com.mesibo.messaging.MesiboConfiguration.DELETEDTOPIC_TEXT_COLOR_WITHOUT_PICTURE;
import static com.mesibo.messaging.MesiboConfiguration.FAVORITED_INCOMING_MESSAGE_DATE_SPACE;
import static com.mesibo.messaging.MesiboConfiguration.FAVORITED_OUTGOING_MESSAGE_DATE_SPACE;
import static com.mesibo.messaging.MesiboConfiguration.NORMAL_INCOMING_MESSAGE_DATE_SPACE;
import static com.mesibo.messaging.MesiboConfiguration.NORMAL_OUTGOING_MESSAGE_DATE_SPACE;
import static com.mesibo.messaging.MesiboConfiguration.TOPIC_TEXT_COLOR_WITHOUT_PICTURE;
import static com.mesibo.messaging.MesiboConfiguration.TOPIC_TEXT_COLOR_WITH_PICTURE;

public class MessageView extends RelativeLayout {

    LayoutInflater mInflater = null;
    TextView mTitleView = null;
    EmojiconTextView mTopicView=null;
    ThumbnailProgressView mPictureThumbnail=null;
    FrameLayout mPicLayout=null;
    FrameLayout mReplayContainer = null;
    RelativeLayout mPTTlayout=null;

    RelativeLayout mReplyLayout = null;
    TextView mReplyUserName;
    TextView mReplyMessage;
    ImageView mReplyImage;
    ImageView mAudioVideoLayer;
    View mMessageView = null;

    private static int mThumbailWidth = 0;
    private MessageData mData = null;
    private boolean hasImage = false;

    public MessageView(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);
        init();

    }

    public MessageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mInflater = LayoutInflater.from(context);
        init();
    }

    public MessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = LayoutInflater.from(context);
        init();
    }

    public void init() {

        View v = mInflater.inflate(R.layout.message_view, this, true);
        mMessageView = v;
        mPicLayout = (FrameLayout)  v.findViewById(R.id.m_piclayout);
        //mPictureThumbnail = (ThumbnailProgressView) v.findViewById(R.id.m_picture);
        mTitleView = (TextView) v.findViewById(R.id.m_ptitle);
        mTopicView = (EmojiconTextView) v.findViewById(R.id.m_pmessage);
        mPTTlayout = (RelativeLayout) v.findViewById(R.id.message_layout);
        mReplayContainer = (FrameLayout)  v.findViewById(R.id.reply_container);

        if(false) {
            mReplyLayout = (RelativeLayout) v.findViewById(R.id.reply_layout);
            mReplyImage = (ImageView) v.findViewById(R.id.reply_image);
            mReplyUserName = (TextView) v.findViewById(R.id.reply_name);
            mReplyMessage = (TextView) v.findViewById(R.id.reply_text);
        }


        //mAudioVideoLayer = (ImageView) v.findViewById(R.id.audio_video_layer);
    }

    public void loadImageView() {
        if(null != mPictureThumbnail)
            return;

        View v = mInflater.inflate(R.layout.thumbnail_progress_view_layout, mPicLayout, true);
        mPictureThumbnail = (ThumbnailProgressView) v.findViewById(R.id.m_picture);
        mAudioVideoLayer = (ImageView) mPictureThumbnail.findViewById(R.id.audio_video_layer);
        mAudioVideoLayer.setVisibility(GONE);
    }

    public void loadReplyView() {
        if(null != mReplyLayout)
            return;

        View v = mInflater.inflate(R.layout.reply_layout, mReplayContainer, true);
        mReplyLayout = (RelativeLayout) v.findViewById(R.id.reply_layout);
        mReplyImage = (ImageView) v.findViewById(R.id.reply_image);
        mReplyUserName = (TextView) v.findViewById(R.id.reply_name);
        mReplyMessage = (TextView) v.findViewById(R.id.reply_text);
    }

    public void setData(MessageData data) {
        mData = data;

        ViewGroup.LayoutParams PTTParams = getLayoutParams();

        String title = mData.getTitle();
        String message = mData.getMessage();
        Bitmap thumbnail = mData.getImage();
        //int type = mData.isLocation();


        if(null != thumbnail || null != mData.getFile() || null != mData.getLocation()) {
            loadImageView();
        } else {
            if(null != mPictureThumbnail)
                mPictureThumbnail.setVisibility(GONE);
            if(null != mAudioVideoLayer)
                mAudioVideoLayer.setVisibility(GONE);
            mPicLayout.setVisibility(GONE);
        }

        if(null != mAudioVideoLayer)
            mAudioVideoLayer.setVisibility(GONE);

        Mesibo.FileInfo file = mData.getFile();
        if(null != file ) {
            if(Mesibo.FileInfo.TYPE_AUDIO == file.type || Mesibo.FileInfo.TYPE_VIDEO == file.type) {
                if(file.isTransferred())
                    mAudioVideoLayer.setVisibility(VISIBLE);
            }
        }

        if(mData.getReplyStatus()) {
            loadReplyView();
            mReplayContainer.setVisibility(VISIBLE);
            mReplyLayout.setVisibility(VISIBLE);
            if(null != mData.getReplyString()) {
                mReplyMessage.setText(mData.getReplyString());
            } else {
                mReplyMessage.setText("");
            }
            mReplyUserName.setText(mData.getReplyName());
            if(null != mData.getReplyBitmap()) {
                mReplyImage.setVisibility(VISIBLE);
                mReplyImage.setImageBitmap(mData.getReplyBitmap());
            }else {
                mReplyImage.setVisibility(GONE);
            }

        }else {
            if(null != mReplyLayout) {
                mReplyLayout.setVisibility(GONE);
                mReplayContainer.setVisibility(GONE);
            }


        }
        if(null != thumbnail) {
            //PTTParams.width = width*(3/4);

            if(0 == mThumbailWidth) {
                //mThumbailWidth = ((width * 3)/4);
                mThumbailWidth = Mesibo.getMessageWidthInPixel();
            }

            PTTParams.width = mThumbailWidth;
            RelativeLayout.LayoutParams picLayoutParam = (RelativeLayout.LayoutParams)mPicLayout.getLayoutParams();
            FrameLayout.LayoutParams thumbnailParams = (FrameLayout.LayoutParams)mPictureThumbnail.getLayoutParams();

            thumbnailParams.width = mThumbailWidth;

            if(mData.isLocation()) {
                thumbnailParams.height = (mThumbailWidth*2)/3;
            }else {
                thumbnailParams.height = mThumbailWidth;
            }

            picLayoutParam.height = thumbnailParams.height;
            picLayoutParam.width = thumbnailParams.width;
            mPicLayout.setLayoutParams(picLayoutParam);
            mPicLayout.requestLayout();
            mPictureThumbnail.setLayoutParams(thumbnailParams);
            mPictureThumbnail.requestLayout();

            mPictureThumbnail.setData(mData);
            mPicLayout.setVisibility(VISIBLE);
            mPictureThumbnail.setVisibility(VISIBLE);
            mTopicView.setTextColor(TOPIC_TEXT_COLOR_WITH_PICTURE);

            hasImage = true;
            //PTTParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        }else {
            if(mData.isDeleted())
                mTopicView.setTextColor(DELETEDTOPIC_TEXT_COLOR_WITHOUT_PICTURE);
            else
                mTopicView.setTextColor(TOPIC_TEXT_COLOR_WITHOUT_PICTURE);
            // PTTParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            //thumbnailParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            if(hasImage) {
                //mMessageView.requestLayout(); //not required
            }

            hasImage = false;

        }

        if(!TextUtils.isEmpty(title)) {
            mTitleView.setVisibility(VISIBLE);
            mTitleView.setText(title);
        }else {
            mTitleView.setVisibility(GONE);

        }


        if(!TextUtils.isEmpty(message)) {
            mTopicView.setVisibility(VISIBLE);
            boolean incoming = (data.getStatus() == Mesibo.MSGSTATUS_RECEIVEDREAD || data.getStatus() == Mesibo.MSGSTATUS_RECEIVEDNEW);
            if(data.getFavourite()) {
                if(incoming)
                    mTopicView.setText(message + " " + FAVORITED_INCOMING_MESSAGE_DATE_SPACE);
                else
                    mTopicView.setText(message + " " + FAVORITED_OUTGOING_MESSAGE_DATE_SPACE);

            } else {
                if (incoming)
                    mTopicView.setText(message + " " + NORMAL_INCOMING_MESSAGE_DATE_SPACE);
                else
                    mTopicView.setText(message + " " + NORMAL_OUTGOING_MESSAGE_DATE_SPACE);

            }
        }else {
            mTopicView.setVisibility(GONE);

        }

        if(false && null != thumbnail) {
            mTitleView.setVisibility(GONE);
            mTopicView.setVisibility(GONE);
        }

        if(null == thumbnail) {
            PTTParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        setLayoutParams(PTTParams);

        PTTParams = mTopicView.getLayoutParams();
        if(null != thumbnail) {
            PTTParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            PTTParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        mTopicView.setLayoutParams(PTTParams);

        if(false && null != thumbnail) {
            mTitleView.setVisibility(VISIBLE);
            mTopicView.setVisibility(VISIBLE);
           // mPictureThumbnail.setImage(thumbanil);

        }
    }

    public void setImage(Bitmap image) {
        loadImageView();
        mPictureThumbnail.setImage(image);
    }

    public void setProgress(Mesibo.FileInfo file) {
        loadImageView();
        mPictureThumbnail.setProgress(file);
    }

}
