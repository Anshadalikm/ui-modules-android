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
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mesibo.api.Mesibo;
import com.mesibo.messaging.AllUtils.MyTrace;

import java.lang.ref.WeakReference;
import java.util.List;

import static com.mesibo.messaging.MesiboRecycleViewHolder.TYPE_CUSTOM;
import static com.mesibo.messaging.MessageData.MESSAGEDATA_TYPE_CUSTOM;
import static com.mesibo.messaging.MessageData.MESSAGEDATA_TYPE_DATE;
import static com.mesibo.messaging.Utils.createRoundDrawable;


public class MessageAdapter extends SelectableAdapter <RecyclerView.ViewHolder> {
    private List<MessageData> mChatList = null;
    private MessagingAdapterListener mListener = null;
    private int mDisplayMsgCnt = 0;
    private int mTotalMessages = 0;
    private int mcellHeight = 0;
    private ProgressBar mProgress = null;
    private ImageView mImageVu = null;
    private String mDateCoin = null;
    int mOriginalId = 0;
    private Context mContext = null;

    private MessageViewHolder.ClickListener clickListener = null;
    private WeakReference<MesiboRecycleViewHolder.Listener> mCustomViewListener = null;

    public interface MessagingAdapterListener {
        boolean isMoreMessage();
        void loadMoreMessages();
        //void showMessgeVisible();
        void showMessageInvisible();
    }

    public MessageAdapter(Context context, MessagingAdapterListener listener, List<MessageData> ChatList, MessageViewHolder.ClickListener cl1, MesiboRecycleViewHolder.Listener customViewListner) {
        mContext = context;
        this.mChatList = ChatList;
        this.mListener = listener;
        this.clickListener = cl1;
        setListener(customViewListner);
        mDisplayMsgCnt = 30;
        mDateCoin = "";
        mTotalMessages = mChatList.size();
        mDisplayMsgCnt = mTotalMessages;
        mcellHeight = 0;
    }

    @Override
    public int getItemViewType(int position) {
        MessageData data = mChatList.get(position);

        if(MESSAGEDATA_TYPE_DATE == data.getType())
            return MesiboRecycleViewHolder.TYPE_DATETIME;

        if(MESSAGEDATA_TYPE_CUSTOM == data.getType())
            return MesiboRecycleViewHolder.TYPE_HEADER;

        if(null != mCustomViewListener) {
            MesiboRecycleViewHolder.Listener l = mCustomViewListener.get();
            if(null != l) {
                int viewType = l.Mesibo_onGetItemViewType(data.getParams(), data.getMessage());
                if(viewType >= TYPE_CUSTOM)
                    return viewType;
            }
        }

        int status = mChatList.get(position).getStatus();
        if(Mesibo.MSGSTATUS_CALLMISSED == status)
            return MesiboRecycleViewHolder.TYPE_MISSEDCALL;

        //if (mChatList.get(position).getTimestamp() == null)
          //  return DATE_TIME;
        if (Mesibo.MSGSTATUS_RECEIVEDNEW == status || Mesibo.MSGSTATUS_RECEIVEDREAD == status)
            return MesiboRecycleViewHolder.TYPE_INCOMING;
        else
            return MesiboRecycleViewHolder.TYPE_OUTGOING;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        MyTrace.start("Messaging-CVH");

        //MessageData cm = mChatList.get(viewType);
        MesiboRecycleViewHolder holder = null;

        if(null != mCustomViewListener) {
            MesiboRecycleViewHolder.Listener l = mCustomViewListener.get();
            if(null != l)
                holder = l.Mesibo_onCreateViewHolder(viewGroup, viewType);

            if(null != holder)
                holder.setCustom(true);
        }

        if (null == holder) {
            if (viewType == MesiboRecycleViewHolder.TYPE_OUTGOING) {

                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.me_user, viewGroup, false);
                holder = new MessageViewHolder(viewType, v, clickListener);

            } else if (viewType == MesiboRecycleViewHolder.TYPE_INCOMING) {

                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.other_user, viewGroup, false);
                holder = new MessageViewHolder(viewType, v, clickListener);
            } else if (viewType == MesiboRecycleViewHolder.TYPE_DATETIME) {

                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_date_view, viewGroup, false);
                holder = new DateViewHolder(v);
            } else if (viewType == MesiboRecycleViewHolder.TYPE_HEADER) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_systemmessage_view, viewGroup, false);
                holder = new SystemMessageViewHolder(v, mContext, 0xffe1d6a6, false);
            } else if (viewType == MesiboRecycleViewHolder.TYPE_MISSEDCALL) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_systemmessage_view, viewGroup, false);
                holder = new SystemMessageViewHolder(v, mContext, 0xffc4dff6, true);
            }else if(viewType >= MesiboRecycleViewHolder.TYPE_CUSTOM) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_systemmessage_view, viewGroup, false);
                holder = new SystemMessageViewHolder(v, mContext, 0xffc4dff6, false);

            }

        }

        if(null != holder)
            holder.setType(viewType);

        MyTrace.stop();
        if(null != holder)
            holder.setAdapter(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {

        MyTrace.start("Messaging-BVH");
        if (i == 0) {
            // if(mDisplayMsgCnt != mTotalMessages)
            if (mListener.isMoreMessage()) {

                // we now don't show button instead we continuously load
                //((MessagingActivity) mContext).showMessgeVisible();
                mListener.loadMoreMessages();
            }

        } else {
            mListener.showMessageInvisible();
        }

        MesiboRecycleViewHolder h = (MesiboRecycleViewHolder) holder;
        int type = h.getType();
        h.setItemPosition(i);

        MessageData cm = mChatList.get(i);

        // it was created by user so let user handle
        if(h.getCustom()) {
            //Mesibo.MesiboMessage
            MesiboRecycleViewHolder.Listener l = mCustomViewListener.get();
            cm.setViewHolder(h);

            if(null != l) {
                l.Mesibo_onBindViewHolder(h, type, isSelected(i), cm.getParams(), cm.getMesiboMessage());
            }
            MyTrace.stop();
            return;
        }

        if(i > 0 && cm.getGroupId() > 0) {
            MessageData prevcm = mChatList.get(i-1);
            cm.checkPreviousData(prevcm);
        }

        if (MesiboRecycleViewHolder.TYPE_DATETIME == type) {

            DateViewHolder dvh = (DateViewHolder) holder;
            dvh.mDate.setText(cm.getPrintDateStamp());

        } else if (MesiboRecycleViewHolder.TYPE_INCOMING == type || MesiboRecycleViewHolder.TYPE_OUTGOING == type) {

            MessageViewHolder mvh = (MessageViewHolder) holder;
            mvh.setData(cm, i, isSelected(i));

        } else if (MesiboRecycleViewHolder.TYPE_HEADER == type) {

        } else if (MesiboRecycleViewHolder.TYPE_MISSEDCALL == type) {
            SystemMessageViewHolder smvh = (SystemMessageViewHolder) holder;
            int messageType = cm.getMessageType();
            if((messageType&1) > 0) {
                smvh.setText("Missed video call at " + cm.getTimestamp());
                smvh.setImage(MesiboImages.getMissedVideoCallImage());
            } else {
                smvh.setText("Missed voice call at " + cm.getTimestamp());
                smvh.setImage(MesiboImages.getMissedVoiceCallImage());
            }
        }


        MyTrace.stop();

    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        MyTrace.start("Messaging-RVH");
        if(null == holder) {
            super.onViewRecycled(holder);
            MyTrace.stop();
            return;
        }

        if(holder instanceof DateViewHolder) {
            super.onViewRecycled(holder);
            MyTrace.stop();
            return;
        }

        if(holder instanceof SystemMessageViewHolder) {
            super.onViewRecycled(holder);
            MyTrace.stop();
            return;
        }

        /* TBD, instead of checking here, MessageViewHolder should implement
           MesiboRecycleViewHolder.Listner and do everything there */
        MesiboRecycleViewHolder h = (MesiboRecycleViewHolder) holder;
                // it was created by user so let user handle
        if(h.getCustom()) {
            MesiboRecycleViewHolder.Listener l = mCustomViewListener.get();
            if(null != l) {
                l.Mesibo_onViewRecycled(h);
            }
            MyTrace.stop();
            return;
        }


        MessageViewHolder mvh = (MessageViewHolder) holder;
        if(null != mvh)
            mvh.reset();

        super.onViewRecycled(holder);
        MyTrace.stop();
    }

    public static class DateViewHolder extends MesiboRecycleViewHolder {

        protected TextView mDate;

        public DateViewHolder(View v) {
            super(v);
            mDate = (TextView) v.findViewById(R.id.chat_date);
        }
    }

    public static class SystemMessageViewHolder extends MesiboRecycleViewHolder {

        protected TextView mText = null;
        protected ImageView mImage  = null;
        private Context mContext;

        public SystemMessageViewHolder(View v, Context context, int color, boolean showImage) {
            super(v);
            mContext = null;
            mText = v.findViewById(R.id.system_msg_text);
            mImage = v.findViewById(R.id.system_msg_icon);
            View layoutView = v.findViewById(R.id.system_msg_layout);
            createRoundDrawable(context, layoutView, color, 9);
        }


        public void setText(String text) {
            mText.setText(text);
        }

        public void setImage(Bitmap bitmap) {
            mImage.setImageBitmap(bitmap);
            mImage.setVisibility(View.VISIBLE);
        }

        public void setImage(int drawable) {
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), drawable);
            setImage(bitmap);
        }
    }

    public void addRow() {
        mDisplayMsgCnt++;
        mTotalMessages = mChatList.size();
    }

    public float getItemHeight() {
        return  mcellHeight;
    }

    public void clearSelections() {
        List<Integer> selection = getSelectedItems();
        clearSelectedItems();
        for (Integer i : selection) {
            notifyItemChanged(i);
        }
    }
    public String copyData() {
        String copiedData="";

        List<Integer> selection = getSelectedItems();
        for (Integer i : selection) {
            int index = i;
            MessageData cm = mChatList.get(index);
            copiedData += cm.getMessage();
            copiedData += "\n";
        }

        return copiedData;
    }


    // if type >= 0, we delete it (for local delete)
    // if type < 0, used for remote delete so that we can send all ids together
    // also if type < 0, we mark message as deleted so that it can show that "this message was deleted"
    public void removeFromChatList(int index, int type){

        MessageData cm = mChatList.get(index);
        if(type >= 0 && cm.getMid() != 0)
            Mesibo.deleteMessage(cm.getMid());

        String currentDS= cm.getDateStamp();
        String prevDS , nextDS;

        cm = mChatList.get(index-1);
        prevDS = cm.getDateStamp();
        String prevTS = cm.getTimestamp();


        if(index==mChatList.size()-1) {
            nextDS = null;
        }else {
            nextDS = mChatList.get(index).getDateStamp();}
        mChatList.remove(index);
        notifyItemRemoved(index);

        if(! currentDS.equals(nextDS)) {

            if(currentDS.equals(prevDS)){
                if(prevTS==null){
                    mChatList.remove(index-1);
                    notifyItemRemoved(index-1);

                }
            }
        }

    }

    public int globalPosition(int position) {

        return (  position);
    }

    public void updateStatus(int index){

        int position = index;
        notifyItemChanged(position);

    }

    @Override
    public int getItemCount() {
          return (null != mChatList ? mChatList.size() : 0);
    }

    public void setListener(MesiboRecycleViewHolder.Listener listener) {
        mCustomViewListener = new WeakReference<MesiboRecycleViewHolder.Listener>(listener);
    }

    public MesiboRecycleViewHolder.Listener getListener() {
        if(null == mCustomViewListener)
            return null;

        return mCustomViewListener.get();
    }

}


