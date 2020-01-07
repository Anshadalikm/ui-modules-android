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

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.mesibo.api.Mesibo;

public class MesiboRecycleViewHolder extends RecyclerView.ViewHolder {

    public static final int TYPE_NONE = 0;
    public static final int TYPE_INCOMING = 1;
    public static final int TYPE_OUTGOING = 2;
    public static final int TYPE_DATETIME = 3;
    public static final int TYPE_HEADER = 4;
    public static final int TYPE_MISSEDCALL = 5;

    public static final int TYPE_CUSTOM = 100;

    public interface Listener {
        int Mesibo_onGetItemViewType(Mesibo.MessageParams params, String message);

        MesiboRecycleViewHolder Mesibo_onCreateViewHolder(ViewGroup viewGroup, int viewType);
        void Mesibo_onBindViewHolder(MesiboRecycleViewHolder holder, int viewType, boolean selected, Mesibo.MessageParams params, Mesibo.MesiboMessage message);
        void Mesibo_oUpdateViewHolder(MesiboRecycleViewHolder holder, Mesibo.MesiboMessage message);
        void Mesibo_onViewRecycled(MesiboRecycleViewHolder holder);
    }

    public static class MesiboViewData {
        int screenType;
        int viewType;
        boolean selected;
        Mesibo.MessageParams params;
        String message;
        String activityStatus;
    }

    public void reset() {
    }

    private int mType = TYPE_NONE;
    private int mPosition = -1;
    private boolean mCustom = false;
    private MessageAdapter mAdapter = null;

    public MesiboRecycleViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    protected int getItemPosition() {
        return mPosition;
    }

    protected void setItemPosition(int pos) {
        mPosition = pos;
    }

    public int getType() {
        return mType;
    }

    public void refresh() {
        if(null != mAdapter && mPosition > 0)
            mAdapter.notifyItemChanged(mPosition);
    }


    public void delete(int type) {
        if(null != mAdapter && mPosition > 0)
            mAdapter.removeFromChatList(mPosition, type);
    }

    protected void setAdapter(MessageAdapter adapter) {
        mAdapter = adapter;
    }

    protected void setType(int type) {
        mType = type;
    }

    protected void setCustom(boolean custom) {
        mCustom = custom;
    }

    protected boolean getCustom() {
        return mCustom;
    }

}
