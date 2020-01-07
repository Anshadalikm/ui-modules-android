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

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.mesibo.api.Mesibo;
import com.mesibo.emojiview.EmojiconEditText;
import com.mesibo.emojiview.EmojiconGridView.OnEmojiconClickedListener;
import com.mesibo.emojiview.EmojiconTextView;
import com.mesibo.emojiview.EmojiconsPopup;
import com.mesibo.emojiview.EmojiconsPopup.OnEmojiconBackspaceClickedListener;
import com.mesibo.emojiview.EmojiconsPopup.OnSoftKeyboardOpenCloseListener;
import com.mesibo.emojiview.emoji.Emojicon;
import com.mesibo.mediapicker.MediaPicker;
import com.mesibo.messaging.AllUtils.LetterTileProvider;
import com.mesibo.messaging.AllUtils.TextToEmoji;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.mesibo.api.Mesibo.FLAG_DELIVERYRECEIPT;
import static com.mesibo.api.Mesibo.MESIBO_DELETE_LOCAL;
import static com.mesibo.api.Mesibo.MESIBO_DELETE_RECALL;
import static com.mesibo.api.Mesibo.MSGSTATUS_CALLINCOMING;
import static com.mesibo.api.Mesibo.MSGSTATUS_CALLOUTGOING;
import static com.mesibo.api.Mesibo.MSGSTATUS_FAIL;
import static com.mesibo.api.Mesibo.MSGSTATUS_OUTBOX;
import static com.mesibo.api.Mesibo.ORIGIN_REALTIME;
import static com.mesibo.api.Mesibo.RESULT_OK;
import static com.mesibo.api.Mesibo.startFileTranser;
import static com.mesibo.messaging.MesiboConfiguration.COPY_STRING;
import static com.mesibo.messaging.MesiboConfiguration.EMOJI_ICON;
import static com.mesibo.messaging.MesiboConfiguration.FILE_NOT_AVAILABLE_MSG;
import static com.mesibo.messaging.MesiboConfiguration.FILE_NOT_AVAILABLE_TITLE;
import static com.mesibo.messaging.MesiboConfiguration.FROM_GALLERY_STRING;
import static com.mesibo.messaging.MesiboConfiguration.KEYBOARD_ICON;
import static com.mesibo.messaging.MesiboConfiguration.MESIBO_INTITIAL_READ_MESSAGEVIEW;
import static com.mesibo.messaging.MesiboConfiguration.MSG_INVALID_GROUP;
import static com.mesibo.messaging.MesiboConfiguration.MSG_PERMISON_FAIL;
import static com.mesibo.messaging.MesiboConfiguration.TITLE_INVALID_GROUP;
import static com.mesibo.messaging.MesiboConfiguration.TITLE_PERMISON_FAIL;
import static com.mesibo.messaging.MesiboConfiguration.VIDEO_RECORDER_STRING;
import static com.mesibo.messaging.MesiboConfiguration.VIDEO_TITLE_STRING;
import static com.mesibo.messaging.MesiboConfiguration.YOU_STRING_IN_REPLYVIEW;
import static com.mesibo.messaging.MesiboMessagingFragment.ERROR_FILE;
import static com.mesibo.messaging.MesiboMessagingFragment.ERROR_INVALIDGROUP;
import static com.mesibo.messaging.MesiboMessagingFragment.ERROR_PERMISSION;
import static com.mesibo.messaging.MesiboUserListFragment.MODE_SELECTCONTACT_FORWARD;

public class MessagingFragment extends BaseFragment implements Mesibo.MessageListener,
        Mesibo.ConnectionListener, Mesibo.FileTransferListener, OnClickListener,
        MessageViewHolder.ClickListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, MediaPicker.ImageEditorListener, Mesibo.UserProfileUpdateListener,
        MessageAdapter.MessagingAdapterListener,
        MessagingActivityListener
{

    private static final String TAG = "MessagingFragment";

    private ArrayList<MessageData> mMessageList = new ArrayList<>();
    private RecyclerView mRecyclerView=null;
    private MessageAdapter mAdapter=null;
    private LinearLayoutManager mLayoutManager=null;
    private LinearLayout showMessage=null;
    private HashMap<Long, MessageData> mMessageMap = new HashMap<Long, MessageData>();
    boolean hidden = true;


    ImageButton ib_gallery=null, ib_contacts=null, ib_location=null;
    ImageButton ib_video=null, ib_audio=null, ib_upload=null, ib_send = null;
    ImageButton ib_cam = null, ib_showattach = null, ib_closeattach = null;
    boolean mPressed = false;


    private WeakReference<MesiboMessagingFragment.FragmentListener> mListener = null;
    private String mName = null;
    private String mPeer = null;
    private long mGroupId = 0;
    private long mForwardId = 0;
    private Mesibo.UserProfile mUser=null;
    private String mGroupStatus = null;

    private boolean mReadOnly = false;

    private LetterTileProvider mLetterTitler = null;

    //in seconds
    private static int ONLINE_TIME=60000;

    //in ms
    private static long ACTIVITY_DISPLAY_DURATION = 10000;

    private TimerTask mTimerTask = null;
    private Timer mTimer = null;

    private boolean read_flag = false;

    private String mImagePath =null;
    private Mesibo.MessageParams mParameter=null;
    private String filename=null;


    private boolean showLoadMore = true;
    private int mLastReadCount = 0;
    private int mLastMessageCount = 0;
    private int mLastMessageStatus = -1;


    private ImageView mEmojiButton =null;

    private Map<String, String> mEmojiMap;
    private Toolbar toolbar=null;

    private Mesibo.MessageFilter mMessageFilter = Mesibo.getMessageFilter();
    private boolean mMediaHandled = true;

    private UserData mUserData =null;
    private LocationRequest mLocationRequest=null;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private String mPrevDateStamp = "";
    private long mlastDbTimestamp = 0;
    private boolean mFirstDBMessage = true;
    private boolean mFirstRTMessage = true;

    private long mLastMessageId = 0;

    private View mEditLayout, mAttachLayout, mBottomLayout;


    private TextView mReplyName;
    private EmojiconTextView mReplyText;
    private ImageView mReplyImage;
    private ImageView mReplyCancel;
    private RelativeLayout mReplyLayout;
    private EmojiconEditText mEmojiEditText;

    private Bitmap mReplyUserBitmap = null;
    private String mReplyUserName = null;
    private String mReplyUserMessage = null;
    private Boolean mReplyEnabled = false;

    private Mesibo.UIHelperListner mMesiboUIHelperlistener = null;
    private MesiboUI.Config mMesiboUIOptions = null ;

    private RelativeLayout mMessageViewBackgroundImage ;
    private boolean mPlayServiceAvailable = false;

    private int mNonDeliveredCount = 0;

    private boolean mSelectionMode = false;
    private boolean mShowMissedCalls = true;


    public static final int MESIBO_MESSAGECONTEXTACTION_FORWARD = 1;
    public static final int MESIBO_MESSAGECONTEXTACTION_REPLY = 1;
    public static final int MESIBO_MESSAGECONTEXTACTION_RESEND = 1;
    public static final int MESIBO_MESSAGECONTEXTACTION_DELETE = 1;
    public static final int MESIBO_MESSAGECONTEXTACTION_COPY = 1;
    public static final int MESIBO_MESSAGECONTEXTACTION_FAVORITE = 1;

    private MesiboRecycleViewHolder.Listener mCustomViewListener = null;
    private MesiboRecycleViewHolder mHeaderView = null;

    //TBD, make it local variable so it will be recycled
    private Mesibo.ReadDbSession mReadSession = null;
    private FrameLayout mCustomLayout = null;
    private View mParentView = null;

    public int getLayout() {
        return R.layout.fragment_messaging;
    }

    public void onCreateCustomView(View parent, FrameLayout frame, String message) {
        if(parent != null) {

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //View view = super.onCreateView(inflater, container, savedInstanceState);
        if(null == container)
            return null;

        setListener(); //TBD. we are now using myActivity

        if(null == getListener()) {
            finish();
            return null;
        }

        if(this instanceof MesiboRecycleViewHolder.Listener) {
            mCustomViewListener = (MesiboRecycleViewHolder.Listener) this;
        }

        Bundle args = getArguments();

        String peer = args.getString(MesiboUI.PEER);
        long gid = args.getLong(MesiboUI.GROUP_ID, 0);

        // this should not happen but extra precaution if something changes in android
        // mainly for viewpager as onCreateView is getting called multiple times
        if(null == mMessageList ||
                (null != peer && null != mPeer && 0 != mPeer.compareToIgnoreCase(peer)) ||
                (gid > 0 && mGroupId > 0 && gid != mGroupId)) {
            read_flag = false;

            // if null or having existing data - reset it
            if(null == mMessageList || mMessageList.size() > 0)
                mMessageList = new ArrayList<>();
        }

        mPeer = peer;
        mGroupId = gid;

        long forwardid = args.getLong(MesiboMessagingFragment.MESSAGE_ID, 0);
        mReadOnly = args.getBoolean(MesiboMessagingFragment.READONLY, false);
        boolean createProfile = args.getBoolean(MesiboMessagingFragment.CREATE_PROFILE, false);
        boolean hideReply = args.getBoolean(MesiboMessagingFragment.HIDE_REPLY, false);
        mShowMissedCalls = args.getBoolean(MesiboMessagingFragment.SHOWMISSEDCALLS, true);


        if(mGroupId > 0) {
            mUser = Mesibo.getUserProfile(mGroupId);
            mLetterTitler = new LetterTileProvider(getActivity(), 60, null);
            if(null != mUser) {
                mGroupStatus = mUser.status;
            }

        } else
            mUser = Mesibo.getUserProfile(mPeer);

        View view = inflater.inflate(getLayout(), container,false);
        mParentView = view;

        if(null == mUser && createProfile) {
            mUser = new Mesibo.UserProfile();
            mUser.groupid = mGroupId;
            if(0 == mUser.groupid)
                mUser.address = mPeer;

            Mesibo.setUserProfile(mUser, false);
        }

        //TBD, this must be fixed
        if(!Mesibo.isReady() || null == mUser) {
            finish();
            return view;
        }

        // if read-only not set by fragment arguments, check here
        if(!mReadOnly)
            mReadOnly = mUser.isReadOnlyUi();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.chat_list_view);

        mCustomLayout = view.findViewById(R.id.customLayout);
        onCreateCustomView(view, mCustomLayout, null);

        mLayoutManager = new LinearLayoutManager(getActivity());
        //mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        //mLayoutManager.setAutoMeasureEnabled(false);

        mRecyclerView.setLayoutManager(mLayoutManager);

        // Note that when fragment is used in adapter, it may call onCreateView again
        // There are two approaches, either we init array here or we init array one time in constructor

        if(false) {
            read_flag = false;
            mMessageList = new ArrayList<>();
        }

        mAdapter = new MessageAdapter(getActivity(), this, mMessageList, this, mCustomViewListener);
        mRecyclerView.setAdapter(mAdapter);

        ib_cam = (ImageButton) view.findViewById(R.id.cameraButton);

        ib_cam.setOnClickListener(this);

        showMessage = (LinearLayout) view.findViewById(R.id.messageLayout);
        if (mAdapter.getItemCount() != 0) {
            if (mLayoutManager.findLastCompletelyVisibleItemPosition() == mAdapter.getItemCount() - 1)

                showMessgeVisible();
        }

        showMessage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessage.setVisibility(View.INVISIBLE);
                loadFromDB(MESIBO_INTITIAL_READ_MESSAGEVIEW);

            }
        });

        if(hideReply) {
            View replyLayout = view.findViewById(R.id.reply_layout_id);
            if(null != replyLayout)
                replyLayout.setVisibility(View.GONE);
        }

        mEmojiEditText = (EmojiconEditText) view.findViewById(R.id.chat_edit_text1);
        setupTextWatcher(mEmojiEditText);

        ib_send = (ImageButton) view.findViewById(R.id.sendmessage);

        ib_send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onSend();
            }
        });

        //mRevealView = (LinearLayout) findViewById(R.id.reveal_items);
        ib_audio = (ImageButton) view.findViewById(R.id.audio);
        ib_upload = (ImageButton) view.findViewById(R.id.document_btn);
        //ib_contacts = (ImageButton) findViewById(R.id.contacts);
        ib_gallery = (ImageButton) view.findViewById(R.id.gallery);
        ib_location = (ImageButton) view.findViewById(R.id.location);
        ib_video = (ImageButton) view.findViewById(R.id.video);
        ib_audio.setOnClickListener(this);
        ib_upload.setOnClickListener(this);
        //ib_contacts.setOnClickListener(this);
        ib_gallery.setOnClickListener(this);
        ib_location.setOnClickListener(this);
        ib_video.setOnClickListener(this);

        ib_showattach = (ImageButton) view.findViewById(R.id.showAttachment);

        ib_showattach.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditLayout.setVisibility(View.GONE);
                mAttachLayout.setVisibility(View.VISIBLE);
            }
        });

        ib_showattach.setVisibility(mMediaHandled?View.VISIBLE:View.GONE);

        //setSupportActionBar(toolbar);
        //mRevealView.setVisibility(View.INVISIBLE);
        showLoadMore = false;

        if(!mMediaHandled){
            ib_cam.setClickable(false);
            ib_cam.setVisibility(View.GONE);
            ib_send.setClickable(true);
            ib_send.setVisibility(View.VISIBLE);
        }

        if (null == mUser.other) {
            mUser.other = new UserData(mUser);
        }

        mUserData = (UserData) mUser.other;

        mMediaHandled = Mesibo.isFileTransferEnabled();

        MesiboImages.init(getActivity());
        mFirstRTMessage = true;

        mEmojiMap = TextToEmoji.getEmojimap();

        mName = mUserData.getUserName();

        Mesibo.addListener(this);
        mMesiboUIHelperlistener = Mesibo.getUIHelperListner();
        mMesiboUIOptions = MesiboUI.getConfig();

        mBottomLayout = view.findViewById(R.id.bottomlayout);

        mMessageViewBackgroundImage = (RelativeLayout) view.findViewById(R.id.chat_layout);
        if(null != mMesiboUIOptions.messagingBackground) {
            Drawable drawable = new BitmapDrawable(getResources(),mMesiboUIOptions.messagingBackground);
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                mMessageViewBackgroundImage.setBackground(drawable);
            } else {
                mMessageViewBackgroundImage.setBackgroundDrawable(drawable);
            }
        }

        mReplyLayout = (RelativeLayout) view.findViewById(R.id.reply_layout);

        mReplyCancel = (ImageView) view.findViewById(R.id.reply_cancel);
        mReplyCancel.setVisibility(View.VISIBLE);
        mReplyCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mReplyLayout.setVisibility(View.GONE);
                mReplyEnabled = false;
            }
        });


        mReplyLayout.setVisibility(View.GONE);

        mReplyImage = (ImageView) view.findViewById(R.id.reply_image);

        mReplyName = (TextView) view.findViewById(R.id.reply_name);
        mReplyText = (EmojiconTextView) view.findViewById(R.id.reply_text);


        final View activityRootView = view.findViewById(R.id.chat_root_layout);

        /* TBD crashing after moving from activity to fragment
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();

                if (heightDiff > 100) {
                    //sendActivity(ACTIVITY_TYPING);
                } else {
                    //sendActivity(ACTIVITY_ONLINE);
                }
            }
        });
        */

        mParameter = new Mesibo.MessageParams(mUserData.getPeer(), mUserData.getGroupId(), Mesibo.FLAG_DEFAULT, 0);

        // forward before read so that we get it back on read()
        if(forwardid > 0) {
            long mId = getMessageId();
            Mesibo.forwardMessage(mParameter, mId, forwardid);
            forwardid = 0;
        }

        filename = mUserData.getImagePath();

        //mPosition = getIntent().getIntExtra("position", 0);

        //imgHndlr.setCallback(this);


        RelativeLayout rootView = (RelativeLayout) view.findViewById(R.id.chat_layout);

        mEditLayout = view.findViewById(R.id.edit_layout);
        mAttachLayout = view.findViewById(R.id.attachLayout);


        // Give the topmost view of your activity layout hierarchy. This will be used to measure soft keyboard height
        final EmojiconsPopup popup = new EmojiconsPopup(rootView, getActivity());


        //Will automatically set size according to the soft keyboard size
        popup.setSizeForSoftKeyboard();

        mEmojiButton = (ImageView) view.findViewById(R.id.mojiButton);

        mEmojiButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                sendActivity(Mesibo.ACTIVITY_TYPING);

                //If popup is not showing => emoji keyboard is not visible, we need to show it
                if (!popup.isShowing()) {


                    //If keyboard is visible, simply show the emoji popup
                    if (popup.isKeyBoardOpen()) {
                        popup.showAtBottom();
                        changeEmojiKeyboardIcon(mEmojiButton, KEYBOARD_ICON);
                    }
                    //else, open the text keyboard first and immediately after that show the emoji popup
                    else {
                        mEmojiEditText.setFocusableInTouchMode(true);
                        mEmojiEditText.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) myActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(mEmojiEditText, InputMethodManager.SHOW_IMPLICIT);
                        changeEmojiKeyboardIcon(mEmojiButton, KEYBOARD_ICON);
                    }
                }
                //If popup is showing, simply dismiss it to show the undelying text keyboard
                else {
                    popup.dismiss();
                }
            }
        });

        //If the emoji popup is dismissed, change emojiButton to smiley icon
        popup.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                changeEmojiKeyboardIcon(mEmojiButton, EMOJI_ICON);
                //sendActivity(ACTIVITY_ONLINE);
            }
        });

        //If the text keyboard closes, also dismiss the emoji popup
        popup.setOnSoftKeyboardOpenCloseListener(new OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {
                sendActivity(Mesibo.ACTIVITY_TYPING);
            }

            @Override
            public void onKeyboardClose() {
                if (popup.isShowing())
                    popup.dismiss();
            }
        });

        //On emoji clicked, add it to edittext
        popup.setOnEmojiconClickedListener(new OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (mEmojiEditText == null || emojicon == null) {
                    return;
                }

                // don't need to send here, it will be anyway sent from onTextChanged
                //sendActivity(ACTIVITY_TYPING);
                int start = mEmojiEditText.getSelectionStart();
                int end = mEmojiEditText.getSelectionEnd();
                if (start < 0) {
                    mEmojiEditText.append(emojicon.getEmoji());
                } else {
                    mEmojiEditText.getText().replace(Math.min(start, end),
                            Math.max(start, end), emojicon.getEmoji(), 0,
                            emojicon.getEmoji().length());
                }
            }
        });

        //On backspace clicked, emulate the KEYCODE_DEL key event
        popup.setOnEmojiconBackspaceClickedListener(new OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                mEmojiEditText.dispatchKeyEvent(event);
            }
        });

        // To toggle between text keyboard and emoji keyboard keyboard(Popup)


        enableLocationServices();

        Mesibo_onConnectionStatus(Mesibo.getConnectionStatus());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {

        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        //Mesibo.removeListener(this);
        if(null != mUserData && null != mReadSession) {
            mReadSession.stop();
            //Mesibo.endReadingSession(mUserData.getPeer(), mUserData.getGroupId());
        }


        //if(null != mMessageList)
          //  mMessageList.clear();

        super.onStop();

    }

    public void setListener() {
        mListener = new WeakReference<MesiboMessagingFragment.FragmentListener>((MesiboMessagingFragment.FragmentListener)getActivity());
    }

    public MesiboMessagingFragment.FragmentListener getListener() {
        if(null == mListener)
            return null;

        return mListener.get();
    }

    @Override
    public void onResume() {
        super.onResume();

        setListener();

        mNonDeliveredCount = 0;


        if(mReadOnly || (mUser.groupid > 0 && (mUser.flag & Mesibo.UserProfile.FLAG_DELETED) > 0 && null != mBottomLayout)) {
            mBottomLayout.setVisibility(View.GONE);
        }

        if(!Mesibo.setAppInForeground(myActivity(), 1, true)) {
            finish();
            return;
        }

        Mesibo.startUserProfilePictureTransfer(mUser, null);
        setProfilePicture();

        Mesibo.addListener(this);

        // a crash was reported here
        if(null == mUser) {
            finish();
            return;
        }

        if(!TextUtils.isEmpty(mUser.draft)){
            mEmojiEditText.setText(mUser.draft);
        }

        if(null == mUserData)
            return;

        mReadSession = new Mesibo.ReadDbSession(mUserData.getPeer(), mUserData.getGroupId(), null, this);
        mReadSession.enableReadReceipt(true);
        mReadSession.enableMissedCalls(mShowMissedCalls);

        //Mesibo.setReadingSession(Mesibo.READFLAG_READRECEIPT | Mesibo.READFLAG_SENDEOR, null);

        if(!read_flag) {
            if(null != mCustomViewListener) {
                //mHeaderView = mCustomViewListener.Mesibo_onCreateViewHolder(mRecyclerView, MesiboRecycleViewHolder.TYPE_HEADER);
                //mCustomViewListener.Mesibo_onBindViewHolder();
            }
            read_flag = true;
            loadFromDB(MESIBO_INTITIAL_READ_MESSAGEVIEW);
            int sizeNext = mMessageList.size();
            mAdapter.notifyItemRangeInserted(0, sizeNext);

            // add header if size 0
            if(0 == mLastReadCount) {
                addHeaderMessage();
            }
        } else {
            mAdapter.notifyDataSetChanged();
            //mAdapter.notifyItemRangeChanged();
        }

        if(0 == mGroupId)
            sendActivity(Mesibo.ACTIVITY_ONLINE);

        if(mUser.groupid > 0) {
            mGroupStatus = mUser.status;
        }

        if(!TextUtils.isEmpty(mGroupStatus))
            updateUserStatus(mGroupStatus, 0);
        else if(isOnline())
            updateUserActivity(null, Mesibo.ACTIVITY_ONLINE);

        //Mesibo.sendReadReceipt()
    }

    @Override
    public void onPause() {
        super.onPause();
        mUser.draft = mEmojiEditText.getText().toString();

        //Mesibo.endReadingSession();
        Mesibo.sendActivity(mParameter, 0, 0, Mesibo.ACTIVITY_LEFT, 0);
        //Mesibo.setAppInForeground(myActivity(), 1, false);
        //sendActivity(ACTIVITY_OFFLINE);
    }

    private void finish() {
        FragmentActivity a = getActivity();
        if(null != a)
            a.finish();
    }

    public void onMediaButtonClicked(int buttonId) {

        if (buttonId == R.id.cameraButton) {
            MediaPicker.launchPicker(myActivity(), MediaPicker.TYPE_CAMERAIMAGE, Mesibo.getTempFilesPath());
        } else if (buttonId == R.id.audio) {
            MediaPicker.launchPicker(myActivity(), MediaPicker.TYPE_AUDIO);
        } else if (buttonId == R.id.document_btn) {
            MediaPicker.launchPicker(myActivity(), MediaPicker.TYPE_FILE);
        } else if (buttonId == R.id.location) {
                try {
                    displayPlacePicker();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                }

        }/*else if (v.getId() == R.id.contacts) {

            //imagePicker.initilizeAlbumList();

        } */ else if (buttonId == R.id.video) {


            CharSequence Options[] = new CharSequence[] {VIDEO_RECORDER_STRING, FROM_GALLERY_STRING};

            AlertDialog.Builder builder = new AlertDialog.Builder(myActivity());
            builder.setTitle(VIDEO_TITLE_STRING);
            builder.setItems(Options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // the user clicked on colors[which]
                    if(which==0)
                    {
                        MediaPicker.launchPicker(myActivity(), MediaPicker.TYPE_CAMERAVIDEO, Mesibo.getTempFilesPath());;
                    }else if (which==1) {
                        MediaPicker.launchPicker(myActivity(), MediaPicker.TYPE_FILEVIDEO);;
                    }
                }
            });
            builder.show();

        } else if (buttonId == R.id.gallery) {

            MediaPicker.launchPicker(myActivity(), MediaPicker.TYPE_FILEIMAGE);

        }

    }

    private int mMediaButtonClicked = -1;
    @Override
    public void onClick(View v) {
        showAttachments(false);
        mPressed = false;
        hidden = true;

        mMediaButtonClicked = v.getId();

        List<String> permissions = new ArrayList<>();
        if (mMediaButtonClicked != R.id.location) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (mMediaButtonClicked == R.id.cameraButton || mMediaButtonClicked == R.id.video) {
            permissions.add(Manifest.permission.CAMERA);
        } if (mMediaButtonClicked == R.id.location) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }


        if(Utils.aquireUserPermissions(getActivity(), permissions, MESSAGING_PERMISSION_CODE)) {
            onMediaButtonClicked(mMediaButtonClicked);
            mMediaButtonClicked = -1;
        }

    }

    @Override
    public boolean Mesibo_onBackPressed() {

        if (mAttachLayout.getVisibility() == View.VISIBLE) {
            showAttachments(false);
            mPressed = false;
            hidden = true;
            return true;
        } else {
            //super.onBackPressed(); // allows standard use of backbutton for page 1
            return false;
        }

    }

    private void setProfilePicture() {
        Bitmap thumbnail = mUserData.getThumbnail();
        if(null == thumbnail) {
            thumbnail = BitmapFactory.decodeFile(mUserData.getImagePath());
            if( null == thumbnail) {
                if(mMesiboUIOptions.useLetterTitleImage){
                    thumbnail = new LetterTileProvider(myActivity(), 60, mMesiboUIOptions.mLetterTitleColors).getLetterTile(mUser.name, false);
                }else if (mUser.groupid > 0) {
                    thumbnail = MesiboImages.getDefaultGroupBitmap();
                }else if (mUser.groupid == 0) {
                    thumbnail = MesiboImages.getDefaultUserBitmap();
                }

            }
        }

        MesiboMessagingFragment.FragmentListener l = getListener();
        if(null == l)
            return;

        l.Mesibo_onUpdateUserPicture(mUser, thumbnail, mUserData.getImagePath());
        //mProfileImage.setImageDrawable(new RoundImageDrawable(thumbnail));
    }
    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }



    private GoogleApiClient mGoogleApiClient = null;
    private static int  PLACE_PICKER_REQUEST=199;

    /* https://inthecheesefactory.com/blog/how-to-fix-nested-fragment-onactivityresult-issue/en

    Although we could call startActivityForResult directly from Fragment but actually mechanic behind
    are all handled by Activity. Once you call startActivityForResult from a Fragment, requestCode will
    be changed to attach Fragment's identity to the code. That will let Activity be able to track back
    that who send this request once result is received.
     */
    private void displayPlacePicker() throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {

        enableLocationServices();

        if( mGoogleApiClient == null || !mGoogleApiClient.isConnected() )
            return;

        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            Intent intent = builder.build(myActivity());
            //MesiboUIManager.launchPlacePicker(this, intent, PLACE_PICKER_REQUEST);
            //Note, read above comment
            myActivity().startActivityForResult(intent, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil
                    .getErrorDialog(e.getConnectionStatusCode(), myActivity(), 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(myActivity(), "Google Play Services is not available.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(myActivity(), "Google Play Services exception.", Toast.LENGTH_LONG).show();
        }
        //start--ActivityForResult( intent, PLACE_PICKER_REQUEST );
    }

    private boolean isOnline() {
        if(null == mUser)
            return false;

        if(mUser.groupid > 0)
            return true;

        //TBD., test and implement
        //if(mNonDeliveredCount > 3)
        //   return false;

        long lastOnline = Mesibo.getTimestamp() - mUser.lastActiveTime; //in ms
        return (lastOnline <= ONLINE_TIME);
    }

    private int updateUserStatus(String status, long duration) {
        if(null != mTimer) {
            mTimer.cancel();
            mTimer = null;
        }

        if(null == status) {

            if(TextUtils.isEmpty(mGroupStatus)) {
                getListener().Mesibo_onUpdateUserOnlineStatus(mUser, null);
            }
            else {
                //mUserStatus.setVisibility(View.VISIBLE);
                //mUserStatus.setText(mGroupStatus);
                getListener().Mesibo_onUpdateUserOnlineStatus(mUser, mGroupStatus);
            }

            return 0;
        }

        getListener().Mesibo_onUpdateUserOnlineStatus(mUser, status);
        //mUserStatus.setText(status);
        //mUserStatus.setVisibility(View.VISIBLE);
        if(duration > 0) {
            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    Activity a = myActivity();
                    if(null == a)
                        return;
                    a.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateUserActivity(null, Mesibo.ACTIVITY_NONE);
                        }
                    });
                }
            };
            mTimer.schedule(mTimerTask, duration);
        }

        return 0;
    }

    private int updateUserActivity(Mesibo.MessageParams params, int activity) {

        int connectionStatus = Mesibo.getConnectionStatus();
        if(Mesibo.STATUS_CONNECTING == connectionStatus) {
            return updateUserStatus(mMesiboUIOptions.connectingIndicationTitle, 0);
        }

        if(Mesibo.STATUS_NONETWORK == connectionStatus) {
            return updateUserStatus(mMesiboUIOptions.noNetworkIndicationTitle, 0);
        }

        if(Mesibo.STATUS_CONNECTFAILURE == connectionStatus) {
            return updateUserStatus(mMesiboUIOptions.offlineIndicationTitle, 0);
        }

        if(Mesibo.STATUS_ONLINE != connectionStatus) {
            return updateUserStatus(mMesiboUIOptions.offlineIndicationTitle, 0);
        }

        String status = null;
        long duration = MesiboUI.getConfig().mTypingIndicationTimeMS;

        if ( Mesibo.ACTIVITY_TYPING== activity) {
            mUserData.setTyping(null);
            status = "";
            if(null != params && params.groupid > 0 && params.profile != null) {
                status = params.profile.name + " is ";
            }
            status += MesiboConfiguration.USER_STATUS_TYPING;
        }

        // if user back to user list, should again send typing
        if(Mesibo.ACTIVITY_LEFT == activity) {
            mLastActivityTimeStamp = 0;
        }

        if (status == null) {
            if(!isOnline() || mGroupId > 0) {
                return updateUserStatus(null, 0);
            }

            status = mMesiboUIOptions.userOnlineIndicationTitle;
            // for next timer event to check if user is still online
            duration = (ONLINE_TIME + 1000) - (Mesibo.getTimestamp() - mUser.lastActiveTime);
            if(duration <= 0)
                return 0;
        }

        return updateUserStatus(status, duration);
    }

    public boolean isMoreMessage() {
        return showLoadMore;
    }

    private boolean isForMe(Mesibo.MessageParams params) {
        if(ORIGIN_REALTIME == params.origin)
            updateUserActivity(params, Mesibo.ACTIVITY_NONE); //TBD, this will fire twice in case of activity

        if(mGroupId > 0)
            return (params.groupid == mGroupId);

        // if sender sent to group, it should have mateched above. Since we are here, sender should have sent to
        // address and not to group (when send to group, both peer and groupid is set)
        if(params.groupid == 0)
            return mParameter.peer.equalsIgnoreCase(params.peer);

        return false;
    }

    private void loadFromDB(int count) {
        //get messagelist size before load so that we can later calculate total number of messages including date
        // messages
        mLastMessageCount = mMessageList.size();

        showLoadMore = false;
        mFirstDBMessage = true;
        mLastReadCount = mReadSession.read(count);
        if(mLastReadCount == count) {
            showLoadMore = true;
        } else {
            /* we should do this here but we can't because messages are posted in UI thread
               which will take time and if we do it here, it will inserted before DB messages
               and will show at bottom. We should change this design now.

            if(true || null != mCustomViewListener) {
                mMessageList.add(0, new MessageData(MessageData.MESSAGEDATA_TYPE_CUSTOM, 0));
                mAdapter.notifyItemRangeInserted(0, mMessageList.size() - 1);
            }
            */
        }
    }

    private void addTimestamp(MessageData m, boolean realtime) {
        // if we are loading from db, remove oldest timestamp
        if(mFirstDBMessage && !realtime && mMessageList.size() > 0) {
            //remove oldest if it's a date type
            MessageData oldest = mMessageList.get(0);
            if (null != oldest && oldest.getType() == MessageData.MESSAGEDATA_TYPE_DATE) {
                mMessageList.remove(0);
            }

            mFirstDBMessage = false;
        }

        String ts = m.getDateStamp();

        // if empty, only add it for realtime message but record date stamp for both the cases
        if(0 == mMessageList.size()) {
            mPrevDateStamp = ts;
            mlastDbTimestamp = m.getTimestampMs();
            if(realtime)
                mMessageList.add(new MessageData(MessageData.MESSAGEDATA_TYPE_DATE, m.getTimestampMs()));

            return;
        }

        if(mPrevDateStamp.equalsIgnoreCase(ts)) {
            return;
        }

        // if realtime message, add current ts, else add ts of previous day
        if(realtime){
            if(mFirstRTMessage) {
                mFirstRTMessage = false;
                MessageData fistDbMessage = mMessageList.get(mMessageList.size()-1);
                String fistDbMessageDateStamp = fistDbMessage.getDateStamp();
                if(!fistDbMessageDateStamp.equalsIgnoreCase(ts)) {
                    mMessageList.add(new MessageData(MessageData.MESSAGEDATA_TYPE_DATE, m.getTimestampMs()));
                }
            }  else
                mMessageList.add(new MessageData(MessageData.MESSAGEDATA_TYPE_DATE, m.getTimestampMs()));

        }
        else
            mMessageList.add(0, new MessageData(MessageData.MESSAGEDATA_TYPE_DATE, mlastDbTimestamp));

        mPrevDateStamp = ts;

    }

    private void addMessage(Mesibo.MessageParams params, MessageData m) {


        mMessageMap.put(Long.valueOf(m.getMid()), m);

        if (Mesibo.ORIGIN_DBMESSAGE == params.origin) {

            //This will add previous TS if this one is older
            addTimestamp(m, false);
            mMessageList.add(0, m);
            mlastDbTimestamp = m.getTimestampMs();

            //temporarily disabled to check EOR
            if(false) {
                int sizeNext = mMessageList.size();
                mAdapter.notifyItemRangeInserted(0, 1);
            }

        } else {
            addTimestamp(m, true);
            mMessageList.add(m);

            //cam.setVisibility(View.VISIBLE);
            mAdapter.addRow();
            //mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);

            mAdapter.notifyItemInserted(mMessageList.size());
            //float height = mAdapter.getItemHeight();
            //mRecyclerView.smoothScrollBy(0,73);//Height of single cell ****hardcoding *****************
            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
        }

    }

    MessageData findMessage(long id) {
        return mMessageMap.get(Long.valueOf(id));
    }

    private long getMessageId() {
        mLastMessageId = Mesibo.random();
        return mLastMessageId;
    }

    @Override
    public void Mesibo_onActivity(Mesibo.MessageParams params, int activity) {
        if(!isForMe(params)) return;

        updateUserActivity(params, activity);

    }

    @Override
    public void Mesibo_onLocation(Mesibo.MessageParams params, Mesibo.Location location) {

        if(!isForMe(params)) return;

        Log.d("test", "test on messge");
        if(location.update) {
            MessageData m = (MessageData) location.getData();
            if(null == m)
                return;

            MesiboRecycleViewHolder rvh = m.getViewHolder();
            if(null == rvh)
                return;

            if(rvh.getCustom()) {
                //MesiboRecycleViewHolder.Listener l = mCustomViewListener.get();
                if(null != mCustomViewListener) {
                    mCustomViewListener.Mesibo_oUpdateViewHolder(rvh, m.getMesiboMessage());
                }
                return;
            }

            MessageViewHolder vh = (MessageViewHolder) rvh;
            vh.setImage(location.image);
            return;
        }

        //String URL = "http://maps.google.com/maps/api/staticmap?center=" + location.lat + "," + location.lon + "&zoom=15&size=200x200&sensor=false";

        MessageData cm = new MessageData(params, location.update?location.mid:params.mid, params.peer, params.profile.name, location.message, params.ts,  params.getStatus() , mGroupId);
        if(null != mLetterTitler)
            cm.setNameColor(mLetterTitler.pickColor(params.profile.name));

        cm.setParams(mParameter);
        cm.setLocation(location);
        cm.setMessageListener(this);
        addMessage(params, cm);

        // This should be taken care by Mesibo
        if(null == location.image) {
            //location.tnImage = MesiboImages.getDefaultLocationBitmap();
            //Mesibo.updateLocationImage(params, location);
        }
        return ;
    }

    @Override
    public void Mesibo_onFile(Mesibo.MessageParams params, Mesibo.FileInfo fileInfo) {

        if(!isForMe(params)) return;
        String message = fileInfo.message;

        if(null == fileInfo.image) {
            //TBD, need to optimize this
            //TBD, this may overwrite file image being downloaded
            int drawable = MesiboImages.getFileDrawable(fileInfo.getPath());
            fileInfo.image = BitmapFactory.decodeResource(myActivity().getApplicationContext().getResources(), drawable);
        }

        MessageData cm = new MessageData(params, params.mid, params.peer, params.profile.name, message, params.ts,  params.getStatus(), mGroupId);
        if(null != mLetterTitler)
            cm.setNameColor(mLetterTitler.pickColor(params.profile.name));
        cm.setFile(fileInfo);
        fileInfo.setListener(this);
        addMessage(params, cm);
        return ;
    }

    @Override
    public boolean Mesibo_onFileTransferProgress(Mesibo.FileInfo file) {

        MessageData m = (MessageData) file.getData();
        if(null == m)
            return true;

        MesiboRecycleViewHolder rvh = m.getViewHolder();
        if(null == rvh)
            return true;

        if(rvh.getCustom()) {
            //MesiboRecycleViewHolder.Listener l = mCustomViewListener.get();
            if(null != mCustomViewListener)
                mCustomViewListener.Mesibo_oUpdateViewHolder(rvh, m.getMesiboMessage());
            return false;
        }

        MessageViewHolder vh = (MessageViewHolder) rvh;
        vh.updateFileProgress(file);
        vh.updateFileProgress(file); //?? why twice TBD
        return false;
    }

    public void addHeaderMessage() {
        mMessageList.add(0, new MessageData(MessageData.MESSAGEDATA_TYPE_CUSTOM, 0));
        mAdapter.notifyItemRangeInserted(0, 1);
    }

    public void updateUiIfLastMessage(Mesibo.MessageParams params) {
        if(!params.isLastMessage()) return;

        if(!params.isDbMessage()) return;

            if(mMessageList.size() > 0) {
                MessageData oldest = mMessageList.get(0);
                if (null != oldest && oldest.getType() != MessageData.MESSAGEDATA_TYPE_DATE) {
                    mPrevDateStamp = oldest.getDateStamp();
                    mMessageList.add(0, new MessageData(MessageData.MESSAGEDATA_TYPE_DATE, mlastDbTimestamp));
                }
            }

            if(!isMoreMessage() && (true || null != mCustomViewListener)) {
                //TBD. in notifyItemRangeInserted do we need to adjust for this one message?
                addHeaderMessage();
            }

            // since we will add messages as well as date stamp, we must keep track of
            // all the newly added message
            mAdapter.notifyItemRangeInserted(0, mMessageList.size()-mLastMessageCount);
            return;

    }

    @Override
    public boolean Mesibo_onMessage(Mesibo.MessageParams params, byte[] data) {
        Log.d(TAG, "Mesibo_onMessage");
        //this may come in real-time

        //TBD. maybe custom UI needs it so we should not restrict
        if(MSGSTATUS_CALLINCOMING == params.getStatus() || MSGSTATUS_CALLOUTGOING == params.getStatus()) {
            updateUiIfLastMessage(params); //needed ???
            return true;
        }

        if(!isForMe(params)) return true;

        String str = "";
        try {
            str = new String(data, "UTF-8");
        } catch (Exception e) {
            return false;
        }

        MessageData cm = new MessageData(params, params.mid, params.peer, params.profile.name, str, params.ts,  params.getStatus() , mGroupId);
        if(null != mLetterTitler)
            cm.setNameColor(mLetterTitler.pickColor(params.profile.name));

        addMessage(params, cm);

        if(params.isRealtimeMessage())
            onCreateCustomView(mParentView, mCustomLayout, str);

        updateUiIfLastMessage(params);
        return false;
    }

    public void onFailedMessage(long mid) {
        Mesibo.MessageParams params = new Mesibo.MessageParams();
        params.setStatus(MSGSTATUS_FAIL);
        params.mid = mid;

        Mesibo_onMessageStatus(params);
    }

    @Override
    public void Mesibo_onMessageStatus(Mesibo.MessageParams params) {
        Log.d("test", "test message status");
        if(null == params || 0 == params.mid) {
            return;
        }

        if(!isForMe(params)) return;

        if(params.isDeleted()) {
            MessageData m = findMessage(params.mid);
            if(null != m) {
                m.setDeleted(true);
                int position = m.getPosition();
                if(position >= 0)
                    mAdapter.updateStatus(position);
            }
            return;
        }

        mLastMessageStatus = params.getStatus();

        mNonDeliveredCount++;
        //TBD, this should be only for read and delivered and only if mid matched
        if(Mesibo.MSGSTATUS_READ == params.getStatus() || Mesibo.MSGSTATUS_DELIVERED == params.getStatus()) {
            updateUserActivity(null, Mesibo.ACTIVITY_NONE);
            mNonDeliveredCount = 0;
        }

        if(params.mid == mOnlineActivityMid) {
            return;
        }

        //TBD, must be fixed once we support read receipt for groups
        if(mUser.groupid > 0 && Mesibo.MSGSTATUS_READ == params.getStatus())
            return;

        if(0 == mMessageList.size())
            return;

        if (params.isMessageStatusFailed()) {

        }
        if(mUser.groupid > 0 && Mesibo.MSGSTATUS_INVALIDDEST == params.getStatus()) {
            getListener().Mesibo_onError(ERROR_INVALIDGROUP, TITLE_INVALID_GROUP, MSG_INVALID_GROUP);
        }

        //Note, read receipt will only be sent from C API if user in summary page or reading that user
        //TBD, maintain hash
        if(params.getStatus() == Mesibo.MSGSTATUS_READ) {

            int i = mMessageList.size();
            //TBD, we need to lock else it may crash if message was deleted etc
            while(i > 0) {
                MessageData cm = mMessageList.get(i-1);

                // This MUST not happen but it has crashed once so possible condition while screen is closing
                if(null == cm) return;

                if(cm.getStatus() == Mesibo.MSGSTATUS_READ) {
                    break; //all previous are already marked read, break
                }

                if (cm.getStatus() == Mesibo.MSGSTATUS_DELIVERED || cm.getStatus() == Mesibo.MSGSTATUS_SENT) {
                    cm.setStaus(params.getStatus());
                }
                i--;
            }

            mAdapter.notifyDataSetChanged();
            return;
        }

        MessageData m = findMessage(params.mid);
        if(null != m) {
            m.setStaus(params.getStatus());
            int position = m.getPosition();
            if(position >= 0) {
                mAdapter.updateStatus(position);
            }
        }

        if(true)
            return;

        int i = mMessageList.size();
        while(i > 0) {
            MessageData cm = mMessageList.get(i-1);
            if(cm.getMid() != 0 && cm.getMid() == params.mid) {
                cm.setStaus(params.getStatus());
                mAdapter.updateStatus(i-1);
                break; //all previous are already marked read, break
            }

            i--;
        }


    }

    @Override
    public void Mesibo_onConnectionStatus(int status) {
        if(status == Mesibo.STATUS_SHUTDOWN) {
            finish();
            return;
        }

        updateUserActivity(null, Mesibo.ACTIVITY_NONE);
    }

    @Override
    public void Mesibo_onUserProfileUpdated(Mesibo.UserProfile userProfile, int i, boolean refresh) {
        if(!refresh)
            return;

        if(userProfile != mUser)
            return;

        if(Mesibo.isUiThread()) {
            setProfilePicture();
            return;
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                setProfilePicture();
            }
        });

    }

    private boolean canSendActivity() {
        if(isOnline())
            return true;

        if(Mesibo.MSGSTATUS_READ == mLastMessageStatus || Mesibo.MSGSTATUS_DELIVERED == mLastMessageStatus) {
            return true;
        }

        if(Mesibo.MSGSTATUS_RECEIVEDNEW == mLastMessageStatus) {
            return true;
        }

        return false;
    }


    // This was getting called from messageadaper
    // we no longer use this which was used to make "show more messages" visible. Instead we
    // now directly load messages
    public void showMessgeVisible() {
        showMessage.setVisibility(View.VISIBLE);
    }

    public void loadMoreMessages() {
        loadFromDB(MESIBO_INTITIAL_READ_MESSAGEVIEW);
    }

    public void showMessageInvisible() {
        if (showMessage.getVisibility() == View.VISIBLE) {
            // slideToConceal(showMessage);
            showMessage.setVisibility(View.GONE);
        }

    }

    // To animate view slide out from top to bottom
    private void showAttachments(boolean show) {
        boolean isVisible = (mAttachLayout.getVisibility() == View.VISIBLE);

        if(show == isVisible ) {
            return;
        }

        mEditLayout.setVisibility(show?View.GONE:View.VISIBLE);
        mAttachLayout.setVisibility(show?View.VISIBLE:View.GONE);

        /*
        TranslateAnimation animate = new TranslateAnimation(0, 0, -view.getHeight(), 0);
        animate.setDuration(500);
        //animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.VISIBLE);
        */

    }

    @Override
    public void onImageEdit(int type, String caption, String filePath, Bitmap bmp, int result) {
        //Log.d(TAG, "onImageCaption");
        if(0 != result)
            return;

        sendFile(type, caption, filePath, bmp, result);
    }

    private void sendFile(int type, String caption, String filePath, Bitmap bmp, int result) {
        Log.d(TAG, "onImageCaption");
        if(0 != result)
            return;

        int temp = mMessageList.size();
        long mId = getMessageId();

        MessageData m = new MessageData(mParameter, mId, null, mName, caption, Mesibo.getTimestamp(), MSGSTATUS_OUTBOX, mGroupId);
        addMessage(mParameter, m);

        // TBD, convert image type to mesibo image type
        int filetype = Mesibo.FileInfo.TYPE_AUTO;
        if(type == MediaPicker.TYPE_CAMERAIMAGE || type == MediaPicker.TYPE_FILEIMAGE)
            filetype = Mesibo.FileInfo.TYPE_IMAGE;
        else if(type == MediaPicker.TYPE_CAMERAVIDEO || type == MediaPicker.TYPE_FILEVIDEO)
            filetype = Mesibo.FileInfo.TYPE_VIDEO;
        else if(type == MediaPicker.TYPE_AUDIO)
            filetype = Mesibo.FileInfo.TYPE_AUDIO;

        //TBD, if we send two people same file with differetnt crops, it may send the same.
        Mesibo.FileInfo file = Mesibo.getFileInstance(mParameter, mId, Mesibo.FileInfo.MODE_UPLOAD, filetype, Mesibo.FileInfo.SOURCE_MESSAGE, filePath, null, this);
        //File file1 = new File(filePath);
        file.message = caption;
        file.image = bmp;
        file.title = null;
        file.userInteraction = true;

        m.setFile(file);

        int sendFileresult  = Mesibo.sendFile(mParameter, mId, file);
        if(RESULT_OK != sendFileresult) {
            onFailedMessage(mId);
        }

        int position = m.getPosition();
        if(position < 0)
            position = mAdapter.getItemCount() - 1;

        mAdapter.notifyItemInserted(position);

        mLastActivityTimeStamp = 0; //so that new typing indication can be sent

        // if camera image, we can delete as Mesibo makes it's own resized copy
        if(type == MediaPicker.TYPE_CAMERAIMAGE) {
            boolean deleted = new File(filePath).delete();
            Log.d(TAG, "Deleted: " + deleted);

        }

        //if(bmp != null)
        //  bmp.recycle();

        if(false) {
            mAdapter.addRow();
            mAdapter.notifyItemInserted(temp);
            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
        }
    }


    int mLastActivity = -1;
    long mLastActivityTimeStamp = 0;
    private long mOnlineActivityMid = 0;
    private void sendActivity(int activity) {

        //only send if remote user is online or first time sending
        // check lastOnlineTs
        // also update lastOnlineTs is message status is READ and id is lastmsg we sent
        long mid = 0;
        int flag = 0;

        // we send ONLINE activity with message id so that we get delivery report
        if(Mesibo.ACTIVITY_ONLINE == activity) {
            mid = getMessageId();
            flag = FLAG_DELIVERYRECEIPT; // we just need delivery receipt to see if user is online
            mOnlineActivityMid = mid;
            mLastActivityTimeStamp = 0;
        }
        else {
            if(!canSendActivity())
                return;
        }

        int interval = (int)(ACTIVITY_DISPLAY_DURATION-2000);
        if(mLastActivity == activity && (Mesibo.getTimestamp() - mLastActivityTimeStamp) < interval)
            return;

        mLastActivityTimeStamp = Mesibo.getTimestamp();
        mLastActivity = activity;

        //pass interval 0 as we are already doing validation above and also we are resetting
        //mLastActivityTimeStamp on sending message
        Mesibo.sendActivity(mParameter, flag, mid, activity, 0);
    }

    @Override
    public void onItemClicked(int position) {
        if (mSelectionMode) {
            toggleSelection(position);
            return;
        }

        MessageData m = mMessageList.get(position);

        if(m.isLocation()) {
            Mesibo.launchLocation(myActivity(), m.getLocation());
            return;
        }

        Mesibo.FileInfo file = m.getFile();
        if(null == file)
            return;

        file.userInteraction = true;
        if(!file.isTransferred()) {

            if(Mesibo.FileInfo.STATUS_INPROGRESS == file.getStatus()) {
                Mesibo.stopFileTransfer(file);
                // stop transfer
                return;
            }

            if(Mesibo.FileInfo.STATUS_FAILED == file.getStatus()) {

            }

            if(true || Mesibo.FileInfo.STATUS_IDLE == file.getStatus() || Mesibo.FileInfo.STATUS_RETRYLATER == file.getStatus()) {
                // start trnasfer

                file.userInteraction = true;
                //TBD, workaround till it gets fixed in API
                if(file.getParams().getExpiry() == 0)
                    file.getParams().setExpiry(-1);

                Mesibo.startFileTranser(file);
                return;
            }



            //Fall through if filename is present (fail case)
            //TBD, check URl in location case
            if(TextUtils.isEmpty(file.getPath()))
                return;
            //fall through
        }

        // if file download was failed, we mark it as transferreda  with no filePath set
        if(TextUtils.isEmpty(file.getPath())) {
            getListener().Mesibo_onError(ERROR_FILE, FILE_NOT_AVAILABLE_TITLE, FILE_NOT_AVAILABLE_MSG);
            return;
        }

        if(Mesibo.FileInfo.TYPE_IMAGE == file.type) {
            MesiboUIManager.launchPictureActivity(myActivity(), mUser.name, file.getPath());
            return;
        }

        Mesibo.launchFile(myActivity(), file.getPath());
        return;
    }

    @Override
    public boolean onItemLongClicked(int position) {
        if (!mSelectionMode) {
            getListener().Mesibo_onShowInContextUserInterface();
            mSelectionMode = true;
        }

        toggleSelection(position);

        return true;
    }

    /**
     * Toggle the selection state of an item.
     * <p/>
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private void toggleSelection(int position) {

        int gPosition = mAdapter.globalPosition(position);
        mAdapter.toggleSelection(gPosition);
        mAdapter.notifyItemChanged(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            getListener().Mesibo_onHideInContextUserInterface();
        } else {
            getListener().Mesibo_onContextUserInterfaceCount(count);
        }
    }

    public void sendTextMessage(String newText) {
        if(0 == newText.length())
            return;

        int temp = mMessageList.size() + 1;

        long mId = getMessageId();
        MessageData messageData = new MessageData(mParameter, mId, null, mName, newText , Mesibo.getTimestamp(), MSGSTATUS_OUTBOX, mGroupId);
        if(mReplyEnabled) {
            messageData.setReplyStatus(true);
            messageData.setReplyBitmap(mReplyUserBitmap);
            messageData.setReplyName(mReplyUserName);
            messageData.setReplyString(mReplyUserMessage);
            mReplyEnabled = false;
            mReplyLayout.setVisibility(View.GONE);
        }
        addMessage(mParameter,messageData );
        //long b = Mesibo.sendMessage(mParameter, mId, SEND_TO_USER, newText);

        if(RESULT_OK != Mesibo.sendMessage(mParameter, mId, newText)) {
            //long[] data = {mId};
            onFailedMessage(mId);
        }

        mLastActivityTimeStamp = 0; //so that new typing indication can be sent

        //updateUserStatus("Online");
        mEmojiEditText.getText().clear();
        //et.clearFocus();


        if (temp < mMessageList.size()) {
            mAdapter.addRow();
            mAdapter.notifyItemInserted(temp);
        }

        //cam.setVisibility(View.VISIBLE);
        mAdapter.addRow();
        mAdapter.notifyItemInserted(mMessageList.size());
        mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
    }

    private void onSend() {

        String newStr = mEmojiEditText.getText().toString();
        String newText = newStr.trim();

        mEmojiEditText.getText().clear();
        mEmojiEditText.setText("");

        //=newText = new String(Character.toChars(0x1F60A));
        if (MesiboUI.getConfig().mConvertSmilyToEmoji) {
            Iterator<Map.Entry<String, String>> iitr = mEmojiMap.entrySet().iterator();
            while (iitr.hasNext()) {
                Map.Entry<String, String> entry = iitr.next();
                newText = newText.replace(entry.getKey(), entry.getValue());
            }
        }

        sendTextMessage(newText);
        //MesiboHelper.sendMessage(0, SEND_TO_USER, "Got your message - my dummy replay");
    }

    private void setupTextWatcher(EmojiconEditText et) {
        //same as emojiconEditText
        et.setRawInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);


        et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                sendActivity(Mesibo.ACTIVITY_TYPING);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mMediaHandled) {
                    if (s.length() == 0) {
                        //cam.setVisibility(View.VISIBLE);
                        if (ib_cam.getVisibility() == View.GONE) {
                            //slideToReveal(cam);
                            TranslateAnimation animate = new TranslateAnimation(15, 0, 0, 0);
                            animate.setDuration(100);
                            ib_cam.startAnimation(animate);
                            ib_send.startAnimation(animate);
                            if(mMediaHandled) {
                                ib_cam.setVisibility(View.VISIBLE);
                                ib_send.setVisibility(View.GONE);
                            }

                        }

                    } else
                        //cam.setVisibility(View.GONE);
                        if (ib_cam.getVisibility() == View.VISIBLE) {
                            //slideToConceal(cam);/*
                            TranslateAnimation animate = new TranslateAnimation(0, 15, 0, 0);
                            animate.setDuration(100);
                            ib_cam.startAnimation(animate);
                            ib_send.startAnimation(animate);
                            ib_cam.setVisibility(View.GONE);
                            ib_send.setVisibility(View.VISIBLE);
                        }

                }}
        });
    }

    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }


    private boolean mGoogleApiClientChecked = false;
    private void enableLocationServices() {
        if(null != mGoogleApiClient || mGoogleApiClientChecked)
            return;

        mGoogleApiClientChecked = true;
        // First we need to check availability of play services
        if (Utils.checkPlayServices(myActivity(), PLAY_SERVICES_RESOLUTION_REQUEST)) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
            mPlayServiceAvailable = true;
        }
    }
    /**
     * Creating google api client object
     * */

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    static final int MESSAGING_PERMISSION_CODE = 101;

    @Override
    public void Mesibo_onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if(MESSAGING_PERMISSION_CODE != requestCode)
            return;

        for(int i=0; i < grantResults.length; i++) {

            if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                getListener().Mesibo_onError(ERROR_PERMISSION, TITLE_PERMISON_FAIL, MSG_PERMISON_FAIL);
                return;
            }
        }

        onMediaButtonClicked(mMediaButtonClicked);
        mMediaButtonClicked = -1;
        return;

    }

    @Override
    public boolean Mesibo_onActivityResult(int requestCode, int resultCode, Intent data) {

        if( !(requestCode == PLACE_PICKER_REQUEST ||
                (requestCode >= MediaPicker.getBaseTypeValue() || requestCode <= MediaPicker.getMaxTypeValue()))) {
            return false;

        }

        if(Activity.RESULT_OK != resultCode)
            return true;

        if( requestCode == PLACE_PICKER_REQUEST) {
            Place place = PlacePicker.getPlace( data, myActivity() ) ;
            if( place == null )
                return true;

            LatLng newPoint = place.getLatLng();
            //String URL = "http://maps.google.com/maps/api/staticmap?center=" + newPoint.latitude + "," + newPoint.longitude + "&zoom=15&size=200x200&sensor=false";

            String nameOfLocation = "";
            CharSequence c = place.getName();
            if(null != c)
                nameOfLocation = c.toString();

            String addressOfLocation = "";
            c = place.getAddress();
            if(null != c)
                addressOfLocation = c.toString();

            Geocoder geocoder;
            List<Address> addresss = null;
            geocoder = new Geocoder(myActivity(), Locale.getDefault());
            try {
                addresss = geocoder.getFromLocation(newPoint.latitude,newPoint.longitude,1);
            } catch (IOException e) {
                e.printStackTrace();
            }


            long mId = getMessageId();
            int temp = mMessageList.size();
            Mesibo.Location location  = new Mesibo.Location();
            location.mid = mId;
            location.title = nameOfLocation;
            location.message = addressOfLocation;
            location.lat = (float)newPoint.latitude;
            location.lon = (float)newPoint.longitude;
            location.image = null; //we initiate transfer of image from bindview

            MessageData cm = new MessageData(mParameter, mId, null, mName, addressOfLocation, Mesibo.getTimestamp(), MSGSTATUS_OUTBOX, mGroupId);
            cm.setLocation(location);
            addMessage(mParameter, cm);
            cm.setParams(mParameter);

            if(RESULT_OK != Mesibo.sendLocation(mParameter, mId,  location)) {
                onFailedMessage(mId);
            }

            mLastActivityTimeStamp = 0; //so that new typing indication can be sent

            mAdapter.addRow();
            mAdapter.notifyItemInserted(temp);
            mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);

            return true;

        }

        String filePath = MediaPicker.processOnActivityResult(myActivity(), requestCode, resultCode, data);

        if(null == filePath)
            return true;

        int drawableid = -1;
        if(MediaPicker.TYPE_AUDIO == requestCode) {
            drawableid = R.drawable.file_audio;
        } else if(MediaPicker.TYPE_FILE == requestCode) {
            drawableid = MesiboImages.getFileDrawable(filePath);
        }

        MesiboUIManager.launchImageEditor(myActivity(), requestCode, drawableid, null, filePath, true, true, false, false, 1280, this);
        return true;
    }


    @Override
    public int Mesibo_onGetEnabledActionItems() {

            int enabled = MESIBO_MESSAGECONTEXTACTION_FORWARD | MESIBO_MESSAGECONTEXTACTION_DELETE | MESIBO_MESSAGECONTEXTACTION_FAVORITE;

            List<Integer> selection = mAdapter.getSelectedItems();

            boolean hideResend = false;

            for (Integer i : selection) {
                MessageData cm = mMessageList.get(i);
                if ((cm.getStatus() >= 1 & cm.getStatus() < 4) || cm.getStatus()==Mesibo.MSGSTATUS_RECEIVEDREAD || cm.getStatus()==Mesibo.MSGSTATUS_RECEIVEDNEW) {

                    hideResend = true;
                }
            }

            if (!hideResend) {
                enabled |= MESIBO_MESSAGECONTEXTACTION_RESEND;
            }

            //menu.findItem(R.id.menu_forward).setVisible(selection.size() == 1);
            if(selection.size() == 1) {
                enabled |= MESIBO_MESSAGECONTEXTACTION_COPY | MESIBO_MESSAGECONTEXTACTION_REPLY;
            }

            return enabled;

    }
    @Override
    public boolean Mesibo_onActionItemClicked(int item) {
        if (MESIBO_MESSAGECONTEXTACTION_DELETE == item) {
            List<Integer> selection = mAdapter.getSelectedItems();
            mAdapter.clearSelections();
            Collections.reverse(selection);
            int maxDeleteInterval = Mesibo.deletePolicy(-1, -1);

            boolean deleteRemote = true;

            //TBD, we don't need to check all, only the one but we need to sure that selection is
            // in order
            for (Integer i : selection) {
                MessageData m = mMessageList.get(i);
                if(m.getStatus() > Mesibo.MSGSTATUS_READ || ((Mesibo.getTimestamp() - m.getTimestampMs())/1000) > maxDeleteInterval) {
                    deleteRemote = false;
                    break;
                }
            }

            if(deleteRemote)
                promptAndDeleteMessage(selection);
            else
                deleteSelectedMessages(selection, MESIBO_DELETE_LOCAL);

            return true;
        }

        if (MESIBO_MESSAGECONTEXTACTION_COPY == item) {

            String st = mAdapter.copyData();
            if(TextUtils.isEmpty(st))
                return true;

            st = st.trim();

            ClipboardManager clipboard = (ClipboardManager) myActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(COPY_STRING, st);
            clipboard.setPrimaryClip(clip);
            return true;
        }

        if (MESIBO_MESSAGECONTEXTACTION_RESEND == item) {

            List<Integer> selection = mAdapter.getSelectedItems();
            mAdapter.clearSelections();

            for (Integer i : selection) {
                int index = i;
                MessageData cm = mMessageList.get(i);
                if((cm.getStatus()& MSGSTATUS_FAIL) > 0) {   //
                    if(cm.getFile() == null || cm.getFile().isTransferred()) {
                        Mesibo.resend(cm.getMid());
                    } else if(cm.getFile() != null) {
                        cm.getFile().userInteraction = true;

                        //TBD, workaround till it gets fixed in API
                        if(cm.getFile().getParams().getExpiry() == 0)
                            cm.getFile().getParams().setExpiry(-1);

                        startFileTranser(cm.getFile());
                    }
                }
            }

            return true;
        }

        if (MESIBO_MESSAGECONTEXTACTION_FORWARD == item) {

            List<Integer> selection = mAdapter.getSelectedItems();
            mAdapter.clearSelections();
            int j = 0;
            long[] mids = new long[selection.size()];
            for (Integer i : selection) {
                int index = i;
                MessageData cm = mMessageList.get(i);
                // don't forward if upload is in progress
                if(cm.getFile() == null || cm.getFile().mode == Mesibo.FileInfo.MODE_DOWNLOAD || cm.getFile().isTransferred())
                    mids[j++] = cm.getMid();
            }

            MesiboUIManager.launchContactActivity(myActivity(), MODE_SELECTCONTACT_FORWARD, mids);
            return true;
        }

        if (MESIBO_MESSAGECONTEXTACTION_FAVORITE == item) {

            List<Integer> selection = mAdapter.getSelectedItems();
            mAdapter.clearSelections();

            Boolean setFlag = false;

            for (Integer i : selection) {
                MessageData cm = mMessageList.get(i);
                setFlag = cm.getFavourite();
                if(setFlag)
                    break;
            }

            setFlag = !setFlag;

            for (Integer i : selection) {
                MessageData cm = mMessageList.get(i);
                if(cm.getFavourite() != setFlag) {
                    cm.setFavourite(setFlag);
                    mAdapter.updateStatus(i);
                }
            }

            return true;
        }

        if (MESIBO_MESSAGECONTEXTACTION_REPLY == item) {

            List<Integer> selection = mAdapter.getSelectedItems();
            mAdapter.clearSelections();
            mReplyEnabled = true;
            mReplyUserBitmap = null;
            mReplyUserName = null;
            mReplyUserMessage = null;

            for (Integer i : selection) {
                int index = i;
                MessageData cm = mMessageList.get(i);
                if(cm.getStatus() == Mesibo.MSGSTATUS_RECEIVEDREAD
                        || cm.getStatus() == Mesibo.MSGSTATUS_RECEIVEDREAD)
                    mReplyUserName = cm.getUsername();
                else
                    mReplyUserName = YOU_STRING_IN_REPLYVIEW;


                mReplyUserMessage = cm.getMessage();

                mReplyName.setTextColor(cm.getNameColor());
                mReplyName.setText(mReplyUserName);

                if(cm.getMessage() != null)
                    mReplyText.setText(cm.getMessage());
                else
                    mReplyText.setText("");

                mReplyImage.setVisibility(View.GONE);
                //if(cm.getType()==1 || cm.getType()== 2) {
                Mesibo.FileInfo fileInfo = cm.getFile();
                if(null != fileInfo) {
                    mReplyImage.setVisibility(View.VISIBLE);
                    mReplyImage.setImageBitmap(fileInfo.image);
                    mReplyUserBitmap = fileInfo.image;
                }
                //}

                if(null != cm.getLocation()) {
                    mReplyImage.setVisibility(View.VISIBLE);
                    mReplyImage.setImageBitmap(cm.getLocation().image);
                    mReplyUserBitmap = cm.getLocation().image;
                }
                mReplyLayout.setVisibility(View.VISIBLE);
                break;
            }

            return true;
        }

        return false;
    }

    public void deleteSelectedMessages(List<Integer> selection, int type) {
        List<Long> mids = new ArrayList<Long>();
        for (Integer i : selection) {
            int index = i;

            MessageData md = mMessageList.get(i);
            if(md != null)
                md.setDeleted(true);

            //only last element will affect mPrevdateStamp nothing else and 2 elements befor will give decide
            // datestamp because even single element previous stamp will be only datestmp and no meaningful data

            if(i == mMessageList.size()-1) {
                if(i>=2) {
                    if (!mMessageList.get(i - 2).getDateStamp().equalsIgnoreCase(mPrevDateStamp))
                        mPrevDateStamp = mMessageList.get(i - 2).getDateStamp();
                }else
                    mPrevDateStamp = null;
            }

            mids.add(mMessageList.get(i).getMid());
            if(type == MESIBO_DELETE_LOCAL)
                mAdapter.removeFromChatList(i, -1);
            else {
                mAdapter.notifyItemChanged(i);
            }
        }
        //   Log.d(TAG, "menu_remove");
        long[] m = new long[mids.size()];
        for(int i=0; i < mids.size(); i++) {
            m[i] = mids.get(i);
        }

        Mesibo.deleteMessages(m, type);
    }

    public void promptAndDeleteMessage(final List<Integer> selection) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Delete Messages?");

        String[] items = {"DELETE FOR EVERYONE", "DELETE FOR ME", "CANCEL"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(2 == which) {

                    return;
                }

                int type = MESIBO_DELETE_LOCAL;
                if(0 == which)
                    type = MESIBO_DELETE_RECALL;

                deleteSelectedMessages(selection, type);
            }
        });

        builder.show();
    }

    @Override
    public void Mesibo_onInContextUserInterfaceClosed() {
        mAdapter.clearSelections();
        mSelectionMode = false;
    }

}
