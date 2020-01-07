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
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mesibo.api.Mesibo;
import com.mesibo.messaging.AllUtils.MyTrace;

import static com.mesibo.messaging.MesiboConfiguration.PROGRESSVIEW_DOWNLOAD_SYMBOL;
import static com.mesibo.messaging.MesiboConfiguration.PROGRESSVIEW_UPLOAD_SYMBOL;


public class ThumbnailProgressView extends FrameLayout  {


    private int mCurrentState=-1;
    LayoutInflater mInflater=null;
    FrameLayout mFrameLayout=null;
    ImageView mPictureView=null;
    ProgressBar mProgressBar=null;
    Button mTransferButton = null;
    private MessageData mData = null;
    private Mesibo.FileInfo mFile = null;
    private String mFileSize = null;

    // Default, In this state, we just need to display thumbnail, button and progress gone
    public final static int STATE_DISPLAY   = 0;
    // In this state, we teed to display thumbnail and progress, button gone
    public final static int STATE_INPROGRESS   = 1;
    // In this state, we teed to display thumbnail and progress and button
    public final static int STATE_PROMPT   = 2;

    public ThumbnailProgressView(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);
        init();

    }
    public ThumbnailProgressView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        mInflater = LayoutInflater.from(context);
        init();
    }
    public ThumbnailProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = LayoutInflater.from(context);
        init();
    }

    public void init() {

        MyTrace.start("TPV-Inflate");
        mFrameLayout = (FrameLayout) mInflater.inflate(R.layout.thumbnail_progress_view, this, true);
        mPictureView = (ImageView) mFrameLayout.findViewById(R.id.imageView);
        mProgressBar = (ProgressBar) mFrameLayout.findViewById(R.id.progressBar);
        MyTrace.stop();

        MyTrace.start("TPV-ProgressBarFilter");
        mProgressBar.getIndeterminateDrawable().setColorFilter(MesiboUI.getConfig().mToolbarColor, android.graphics.PorterDuff.Mode.MULTIPLY);
        MyTrace.stop();


        // Note that button is made non-clickable in resources so that it deliveres click even to parent. This
        // we are doing so that we can capture click anywhere to start file transfer
        mTransferButton = (Button) mFrameLayout.findViewById(R.id.transferButton);
        setState(STATE_DISPLAY);

    }


    public void setData(MessageData data) {
        mData = data;

        Bitmap image =  mData.getImage();
        if(null == image) {
            return;
        }

        mPictureView.setImageBitmap(image);

        mFile = data.getFile();
        if(null == mFile) {
            setState(STATE_DISPLAY);  //Location or sticker
            return;
        }

        mFileSize = Utils.getFileSizeString(mFile.size);

        Drawable img = null;
        //TBD, need to cache
        if(Mesibo.FileInfo.MODE_DOWNLOAD == mFile.mode)
            img = getContext().getResources().getDrawable(PROGRESSVIEW_DOWNLOAD_SYMBOL);
        else
            img = getContext().getResources().getDrawable(PROGRESSVIEW_UPLOAD_SYMBOL);

        mTransferButton.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        mTransferButton.setText(mFileSize);

        if(mFile.isTransferred()) {
            setState(STATE_DISPLAY);
        } else {
            if(mFile.getStatus() == Mesibo.FileInfo.STATUS_INPROGRESS) {
                setState(STATE_INPROGRESS);
            }
            else
                setState(STATE_PROMPT); // for idle or retrylater stage
        }

    }

    public void setState(int state) {
        if(state == mCurrentState)
            return;

        mCurrentState = state;
        if(STATE_DISPLAY == state) {
            mTransferButton.setVisibility(GONE);
            mProgressBar.setVisibility(GONE);
        }
        else  if(STATE_INPROGRESS == state) {
            if(mCurrentState != state) {
                mProgressBar.setIndeterminate(true); //so that we can show spinning at start
                mProgressBar.setMax(100);
            }
            mProgressBar.setVisibility(VISIBLE);
            mTransferButton.setVisibility(GONE);
        } else {
            mTransferButton.setVisibility(VISIBLE);
            mProgressBar.setVisibility(GONE);
        }

    }

    public void setProgress(Mesibo.FileInfo file) {

        if(file.isTransferred() || Mesibo.FileInfo.STATUS_FAILED == file.getStatus()) {
            setState(STATE_DISPLAY);

            //update thumbnail
            if(file.isTransferred())
                mPictureView.setImageBitmap(file.image);

            return;
        }

        if(Mesibo.FileInfo.STATUS_INPROGRESS == file.getStatus()) {
            setState(STATE_INPROGRESS);
            mProgressBar.setProgress(file.getProgress());
            return;
        }

        setState(STATE_PROMPT); // for idle and retry later
    }

    public void setImage(Bitmap bmp) {
        mPictureView.setImageBitmap(bmp);
    }
}