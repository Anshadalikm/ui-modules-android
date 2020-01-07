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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mesibo.api.Mesibo;
import com.mesibo.emojiview.EmojiconEditText;
import com.mesibo.emojiview.EmojiconGridView;
import com.mesibo.emojiview.EmojiconTextView;
import com.mesibo.emojiview.EmojiconsPopup;
import com.mesibo.emojiview.emoji.Emojicon;
import com.mesibo.mediapicker.MediaPicker;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.mesibo.messaging.AllUtils.MessagingAlerts.getProgressDialog;
import static com.mesibo.messaging.MesiboConfiguration.CREATE_GROUP_ERROR_MESSAGE_STRING;
import static com.mesibo.messaging.MesiboConfiguration.CREATE_GROUP_ERROR_TITLE_STRING;
import static com.mesibo.messaging.MesiboConfiguration.CREATE_GROUP_GROUPNAME_ERROR_MESSAGE_STRING;
import static com.mesibo.messaging.MesiboConfiguration.CREATE_GROUP_NOMEMEBER_MESSAGE_STRING;
import static com.mesibo.messaging.MesiboConfiguration.CREATE_GROUP_NOMEMEBER_TITLE_STRING;
import static com.mesibo.messaging.MesiboConfiguration.MAX_GROUP_SUBJECT_LENGTH;
import static com.mesibo.messaging.MesiboConfiguration.MIN_GROUP_SUBJECT_LENGTH;
import static com.mesibo.messaging.MesiboConfiguration.MSG_PERMISON_CAMERA_FAIL;
import static com.mesibo.messaging.MesiboConfiguration.PROGRESS_DIALOG_MESSAGE_STRING;
import static com.mesibo.messaging.MesiboConfiguration.TITLE_PERMISON_CAMERA_FAIL;
import static com.mesibo.messaging.MesiboUserListFragment.MODE_EDITGROUP;
import static com.mesibo.messaging.Utils.saveBitmpToFilePath;
import static com.mesibo.messaging.Utils.showAlert;

public class CreateNewGroupFragment extends Fragment implements MediaPicker.ImageEditorListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ImageView mGroupPicture;
    EmojiconEditText mGroupSubjectEditor;
    ImageView mEmojiButton;
    TextView mCharCounter;
    LinearLayout mCreateGroupBtn;
    RecyclerView mRecyclerView ;
    RecyclerView.Adapter mAdapter;
    String mGroupImagePath = null;
    long mGroupId = 0;
    int  mGroupMode;
    String mGroupStatus = "";
    Mesibo.UIHelperListner mMesiboUIHelperListener = null;
    Bundle mGroupEditBundle = null ;
    ProgressDialog mProgressDialog ;
    Mesibo.UserProfile mModifiedGroupProfile;

    @Override
    public void onImageEdit(int i, String s, String s1, Bitmap bitmap, int i1) {
        mGroupImagePath = Mesibo.getFilePath(Mesibo.FileInfo.TYPE_PROFILEIMAGE) + "grouptemp.jpg";
        if(saveBitmpToFilePath(bitmap, mGroupImagePath))
            setGroupImage(bitmap);
    }

    public static abstract class ResponseHandler extends Handler {

        public void handleMessage(Message msg) {


        }
    }


    public CreateNewGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                MediaPicker.launchPicker(getActivity(), MediaPicker.TYPE_CAMERAIMAGE);

            } else {
                //TBD, show alert that you can't continue
                Utils.showAlert(getActivity(),TITLE_PERMISON_CAMERA_FAIL, MSG_PERMISON_CAMERA_FAIL);

            }
            return;

        }

        // other 'case' lines to check for other
        // permissions this app might request

    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreateNewGroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateNewGroupFragment newInstance(Bundle bundle) {
        CreateNewGroupFragment fragment = new CreateNewGroupFragment();
        Bundle args = new Bundle();
        args.putBundle(ARG_PARAM1, bundle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGroupEditBundle = getArguments().getBundle(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_create_new_group, container, false);
        mGroupSubjectEditor = (EmojiconEditText) v.findViewById(R.id.nugroup_editor);
        mGroupId = 0;

        String groupImagePath = null; //MesiboImages.getDefaultGroupPath();

        //TBD, these all implementation need to be fixed, badly implemented
        if(mGroupEditBundle != null) {
            mGroupId = mGroupEditBundle.getLong(MesiboUI.GROUP_ID);
            if(mGroupId > 0) {
                Mesibo.UserProfile g = Mesibo.getUserProfile(mGroupId);
                if(null != g) {
                    groupImagePath = Mesibo.getUserProfilePicturePath(g, Mesibo.FileInfo.TYPE_AUTO);

                    if(null != g.name)
                        mGroupSubjectEditor.setText(g.name);
                }
            }
        }

        mProgressDialog = getProgressDialog(getActivity(), PROGRESS_DIALOG_MESSAGE_STRING);

        mMesiboUIHelperListener = Mesibo.getUIHelperListner();

        mCreateGroupBtn = (LinearLayout) v.findViewById(R.id.nugroup_create_btn);

        mCreateGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserListFragment.mMemberProfiles.size() ==0 && (mGroupMode==0)) {
                    showAlert(getActivity(), CREATE_GROUP_NOMEMEBER_TITLE_STRING, CREATE_GROUP_NOMEMEBER_MESSAGE_STRING);
                    return;
                }
                if(mGroupSubjectEditor.getText().toString().length() < MIN_GROUP_SUBJECT_LENGTH) {
                    showAlert(getActivity(),null,CREATE_GROUP_GROUPNAME_ERROR_MESSAGE_STRING);
                    return;
                }

                String [] members = new String[ UserListFragment.mMemberProfiles.size()];
                mGroupStatus = "";
                for (int i = 0; i < UserListFragment.mMemberProfiles.size(); i++) {
                    Mesibo.UserProfile mp = UserListFragment.mMemberProfiles.get(i);
                    //mGroupStatus += mp.name + ", ";
                    members[i] = mp.address;
                }


                if(null == mMesiboUIHelperListener)
                    return;

                mProgressDialog.show();
                mMesiboUIHelperListener.Mesibo_onSetGroup(getActivity(), mGroupId, mGroupSubjectEditor.getText().toString(), 0, mGroupStatus, mGroupImagePath, members, new ResponseHandler() {
                    @Override
                    public void handleMessage(Message msg) {
                        Bundle bundle= msg.getData();
                        String s = bundle.getString("result");

                        if(mProgressDialog.isShowing())
                            mProgressDialog.dismiss();

                        if(s.equals("OK")) {

                            boolean newgroup = (mGroupId == 0);

                            mGroupId = bundle.getLong(MesiboUI.GROUP_ID);

                            if(newgroup)
                                MesiboUIManager.launchMessagingActivity(getActivity(), 0, null, mGroupId);

                            Activity a = getActivity();
                            if(null != a)
                                a.finish();

                        }else {
                            showAlert(getActivity(), CREATE_GROUP_ERROR_TITLE_STRING, CREATE_GROUP_ERROR_MESSAGE_STRING);
                        }
                    }
                });
            }
        });

        mGroupPicture = (ImageView) v.findViewById(R.id.nugroup_picture) ;
        setGroupImageFile(groupImagePath);

        mGroupPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MenuBuilder menuBuilder = new MenuBuilder(getActivity());
                MenuInflater inflater = new MenuInflater(getActivity());
                inflater.inflate(R.menu.image_source_menu, menuBuilder);
                MenuPopupHelper optionsMenu = new MenuPopupHelper(getActivity(), menuBuilder, v);
                optionsMenu.setForceShowIcon(true);
                menuBuilder.setCallback(new MenuBuilder.Callback() {
                    @Override
                    public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                        if (item.getItemId() == R.id.popup_camera) {
                            if(Utils.aquireUserPermission(getActivity(), Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE))
                                MediaPicker.launchPicker(getActivity(), MediaPicker.TYPE_CAMERAIMAGE);
                            return true;
                        } else if (item.getItemId() == R.id.popup_gallery) {
                            MediaPicker.launchPicker(getActivity(), MediaPicker.TYPE_FILEIMAGE);
                            return true;
                        } else if(item.getItemId() == R.id.popup_remove) {
                            mMesiboUIHelperListener.Mesibo_onSetGroup(getActivity(), mGroupId, null, 0, null, null, null, null);

                            mGroupImagePath = null;
                            setGroupImage(null);
                            Mesibo.UserProfile u = Mesibo.getUserProfile(mGroupId);
                            u.picturePath = null;
                            Mesibo.setUserProfile(u, false);
                        }
                        return false;

                    }

                    @Override
                    public void onMenuModeChange(MenuBuilder menu) {

                    }
                });
                optionsMenu.show();
            }
        });


        mRecyclerView = (RecyclerView) v.findViewById(R.id.nugroup_members);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mAdapter = new GroupMemeberAdapter(getActivity(), UserListFragment.mMemberProfiles);
        mRecyclerView.setAdapter(mAdapter);


        mCharCounter = (TextView) v.findViewById(R.id.nugroup_counter);
        mCharCounter.setText(String.valueOf(MAX_GROUP_SUBJECT_LENGTH));

        mGroupSubjectEditor.setFilters(new InputFilter[] {new InputFilter.LengthFilter(MAX_GROUP_SUBJECT_LENGTH)});
        mGroupSubjectEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                mCharCounter.setText(String.valueOf(MAX_GROUP_SUBJECT_LENGTH-(mGroupSubjectEditor.getText().length())));

            }
        });


        FrameLayout rootView = (FrameLayout) v.findViewById(R.id.nugroup_root_layout);
        // Give the topmost view of your activity layout hierarchy. This will be used to measure soft keyboard height
        final EmojiconsPopup popup = new EmojiconsPopup(rootView, getActivity());

        //Will automatically set size according to the soft keyboard size
        popup.setSizeForSoftKeyboard();

        mEmojiButton = (ImageView) v.findViewById(R.id.nugroup_smile_btn);
        mEmojiButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                //If popup is not showing => emoji keyboard is not visible, we need to show it
                if (!popup.isShowing()) {
                    //If keyboard is visible, simply show the emoji popup
                    if (popup.isKeyBoardOpen()) {
                        popup.showAtBottom();
                        changeEmojiKeyboardIcon(mEmojiButton, R.drawable.ic_keyboard);
                    }
                    //else, open the text keyboard first and immediately after that show the emoji popup
                    else {
                        mGroupSubjectEditor.setFocusableInTouchMode(true);
                        mGroupSubjectEditor.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(mGroupSubjectEditor, InputMethodManager.SHOW_IMPLICIT);
                        changeEmojiKeyboardIcon(mEmojiButton, R.drawable.ic_keyboard);
                    }
                }
                //If popup is showing, simply dismiss it to show the undelying text keyboard
                else {
                    popup.dismiss();
                }
            }
        });

        //If the emoji popup is dismissed, change emojiButton to smiley icon
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                changeEmojiKeyboardIcon(mEmojiButton, R.drawable.ic_sentiment_satisfied_black_24dp);
                //sendActivity(ACTIVITY_ONLINE);
            }
        });

        //If the text keyboard closes, also dismiss the emoji popup
        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {
                if (popup.isShowing())
                    popup.dismiss();
            }
        });

        //On emoji clicked, add it to edittext
        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (mGroupSubjectEditor == null || emojicon == null) {
                    return;
                }

                // don't need to send here, it will be anyway sent from onTextChanged
                //sendActivity(ACTIVITY_TYPING);
                int start = mGroupSubjectEditor.getSelectionStart();
                int end = mGroupSubjectEditor.getSelectionEnd();
                if (start < 0) {
                    mGroupSubjectEditor.append(emojicon.getEmoji());
                } else {
                    mGroupSubjectEditor.getText().replace(Math.min(start, end),
                            Math.max(start, end), emojicon.getEmoji(), 0,
                            emojicon.getEmoji().length());
                }
            }
        });

        //On backspace clicked, emulate the KEYCODE_DEL key event
        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                mGroupSubjectEditor.dispatchKeyEvent(event);
            }
        });

        return v;
    }

    public void OnBackPressed (){

        Bundle bundle = new Bundle();
        bundle.putString(MesiboUI.GROUP_NAME, mGroupSubjectEditor.getText().toString());
        bundle.putLong(MesiboUI.GROUP_ID, mGroupId);
        bundle.putString(MesiboUI.PICTURE_PATH, mGroupImagePath);
        String memberList = "";
        if(UserListFragment.mMemberProfiles.size() > 0) {
            for (Mesibo.UserProfile u : UserListFragment.mMemberProfiles) {
                memberList += u.address + ",";
            }
        }
        bundle.putString(MesiboUI.MEMBERS, memberList);
        MesiboUIManager.launchContactActivity(getActivity(), 0, MODE_EDITGROUP, 0, false, false, bundle);
        getActivity().finish();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // In fragment class callback
        if(RESULT_OK != resultCode)
            return;

        String fileName = MediaPicker.processOnActivityResult(getActivity(), requestCode, resultCode, data);
        if(null == fileName)
            return;

        MesiboUIManager.launchImageEditor((AppCompatActivity)getActivity(), MediaPicker.TYPE_CAMERAIMAGE, 0, null, fileName, false, false, true, true, 600, this);
    }

    public void setGroupImage(Bitmap bmp) {
        if(null == bmp)
            bmp = MesiboImages.getDefaultGroupBitmap();

        mGroupPicture.setImageDrawable(new RoundImageDrawable(bmp));
    }

    public void setGroupImageFile(String filepath) {
        if(null == filepath || !Mesibo.fileExists(filepath)) {
            setGroupImage(null);
            return;
        }

        Bitmap b = BitmapFactory.decodeFile(filepath);
        setGroupImage(b);
    }

    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }

    public class GroupMemeberAdapter
            extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private int mBackground=0;
        private Context mContext=null;
        private ArrayList<Mesibo.UserProfile> mDataList=null;

        private UserListFragment mHost;




        public GroupMemeberAdapter(Context context,ArrayList<Mesibo.UserProfile> list) {
            this.mContext = context;
            mDataList = list;

        }


        public   class GroupMembersCellsViewHolder extends RecyclerView.ViewHolder  {
            public  View mView=null;
            public  ImageView mContactsProfile=null;
            public  TextView mContactsName=null;
            public  EmojiconTextView mContactsStatus=null;
            public  ImageView mDeleteContact;
            public GroupMembersCellsViewHolder(View view) {
                super(view);
                mView = view;
                mContactsProfile = (ImageView) view.findViewById(R.id.nu_rv_profile);
                mContactsName = (TextView) view.findViewById(R.id.nu_rv_name);
                mContactsStatus = (EmojiconTextView) view.findViewById(R.id.nu_memeber_status);
                mDeleteContact = (ImageView) view.findViewById(R.id.nu_delete_btn);
            }
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.group_memeber_rv_item, parent, false);
            return new GroupMembersCellsViewHolder(view);

        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holderr, final int position) {
            final int pos = position;
            final Mesibo.UserProfile user = mDataList.get(position);
            final GroupMembersCellsViewHolder holder = (GroupMembersCellsViewHolder) holderr;

            if (null == user.other) {
                user.other = new UserData(user);
            }
            final UserData data = (UserData) user.other;
            holder.mContactsName.setText(data.getUserName());
            final Bitmap b = data.getImage();
            String filePath = data.getImagePath();

            if (b != null)
                holder.mContactsProfile.setImageDrawable(new RoundImageDrawable(b));
            else {
                if (filePath != null) {
                    holder.mContactsProfile.setImageDrawable(new RoundImageDrawable(BitmapFactory.decodeFile(filePath)));
                } else
                    holder.mContactsProfile.setImageDrawable(MesiboImages.getDefaultRoundedDrawable());
            }
            if(null != user) {
                if(!TextUtils.isEmpty(user.status))
                    holder.mContactsStatus.setText(user.status);
                else
                    holder.mContactsStatus.setText("");
            }

            holder.mDeleteContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeItem(position);
                }
            });
        }
        @Override
        public int getItemCount() {
            return mDataList.size();
        }

        public void removeItem(int position) {
            mDataList.remove(position);
            notifyItemRemoved(position);
            notifyDataSetChanged();
        }

    }

    static final int CAMERA_PERMISSION_CODE = 102;

}
