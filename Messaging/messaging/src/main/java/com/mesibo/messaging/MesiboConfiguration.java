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

public class MesiboConfiguration {




    public  static final String CREATE_GROUP_NOMEMEBER_TITLE_STRING = "No Members";
    public  static final String CREATE_GROUP_NOMEMEBER_MESSAGE_STRING = "Add two or more members to create a group.";

    public  static final String CREATE_GROUP_GROUPNAME_ERROR_MESSAGE_STRING = "Group name should be at least 2 characters";

    public  static final String CREATE_GROUP_ERROR_TITLE_STRING = "Group Update Failed !";
    public  static final String CREATE_GROUP_ERROR_MESSAGE_STRING = "Please check internet connection and try again later";

    public static final int MAX_GROUP_SUBJECT_LENGTH = 50;
    public static final int  MIN_GROUP_SUBJECT_LENGTH = 2;


    public  static final String CREATE_NEW_GROUP_MESSAGE_STRING = "Add group members from the next screen.";

    public  static final String PROGRESS_DIALOG_MESSAGE_STRING = "Please wait. . .";

    public  static final int DEFAULT_GROUP_MODE = 0;


    public  static final String ALL_USERS_STRING = "All Users";
    public  static final String FREQUENT_USERS_STRING = "Recent Users";
    public  static final String GROUP_MEMBERS_STRING = "Group Members";

    public static final int STATUS_TIMER = R.drawable.ic_av_timer_black_18dp ;
    public static final int STATUS_SEND = R.drawable.ic_done_black_18dp ;
    public static final int STATUS_NOTIFIED= R.drawable.ic_check_circle_black_18dp ;
    public static final int STATUS_READ = R.drawable.ic_check_circle_black_18dp;
    public static final int STATUS_ERROR = R.drawable.ic_error_black_18dp;
    public static final int DELETED_DRAWABLE = R.drawable.ic_action_cancel_black_18dp;

    public static final int MISSED_VOICECALL_DRAWABLE = R.drawable.baseline_call_missed_black_18;
    public static final int MISSED_VIDEOCALL_DRAWABLE = R.drawable.baseline_missed_video_call_black_18;
    public static final int MISSED_CALL__TINT_COLOR = 0xCC0000;

    public static final int NORMAL_TINT_COLOR = 0xAAAAAA;
    public static final int READ_TINT_COLOR = 0x23b1ef;
    public static final int ERROR_TINT_COLOR = 0xCC0000;
    public static final int DELETED_TINT_COLOR = 0xBBBBBB;

    public static final int DEFAULT_PROFILE_PICTURE = R.drawable.default_user_image;
    public static final int DEFAULT_GROUP_PICTURE = R.drawable.default_group_image;

    public static final String FAVORITED_INCOMING_MESSAGE_DATE_SPACE = "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0";
    public static final String FAVORITED_OUTGOING_MESSAGE_DATE_SPACE = "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0";
    public static final String NORMAL_INCOMING_MESSAGE_DATE_SPACE = "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0";
    public static final String NORMAL_OUTGOING_MESSAGE_DATE_SPACE = "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0";

    public static final int TOPIC_TEXT_COLOR_WITH_PICTURE = 0xffa6abad;
    public static final int TOPIC_TEXT_COLOR_WITHOUT_PICTURE = 0xff000000;
    public static final int DELETEDTOPIC_TEXT_COLOR_WITHOUT_PICTURE = 0x77000000;

    public static final String STATUS_COLOR_WITHOUT_PICTURE = "#a6abad";
    public static final String STATUS_COLOR_OVER_PICTURE = "#ffffffff";

    public static final int PROGRESSVIEW_DOWNLOAD_SYMBOL =  R.drawable.ic_file_download_white_24dp;
    public static final int PROGRESSVIEW_UPLOAD_SYMBOL = R.drawable.ic_file_upload_white_24dp;


    public static final String MESSAGE_STRING_USERLIST_SEARCH = "Messages";
    public static final String USERS_STRING_USERLIST_SEARCH = "Users";
    //public static final String CREATE_NEW_GROUP_STRING = "Create New Group";

    public static final String ATTACHMENT_STRING = "Attachment";
    public static final String LOCATION_STRING = "Location";
    public static final String VIDEO_STRING = "Video";
    public static final String AUDIO_STRING = "Audio";
    public static final String IMAGE_STRING = "Image";
    public static final String MISSED_VIDEO_CALL = "Missed video call";
    public static final String MISSED_VOICE_CALL = "Missed voice call";
    public static final String MESSAGE_DELETED_STRING = "This message was deleted";

    //http://romannurik.github.io/AndroidAssetStudio/icons-generic.html
    //#7f7f7f, 1dp
    public static final int ATTACHMENT_ICON = R.drawable.ic_attachment_grey_18dp ;
    public static final int VIDEO_ICON = R.drawable.ic_video_on_grey_18dp ;
    public static final int IMAGE_ICON = R.drawable.ic_insert_photo_grey_500_18dp ;
    public static final int LOCATION_ICON = R.drawable.ic_location_on_grey_18dp ;
    public static final String EMPTY_MESSAGE_LIST ="You do not have any messages";
    public static final String EMPTY_USER_LIST ="You do not have any contacts";
    public static final String EMPTY_SEARCH_LIST ="Your search returned no results";

    public static final int LETTER_TITLAR_SIZE = 60 ;

    public static final int MESIBO_INTITIAL_READ_USERLIST = 100 ;
    public static final int MESIBO_SUBSEQUENT_READ_USERLIST = 100 ;
    public static final int MESIBO_SEARCH_READ_USERLIST = 100 ;


    public static final int MESIBO_INTITIAL_READ_MESSAGEVIEW = 50 ;
    public static final int MESIBO_SUBSEQUENT_READ_MESSAGEVIEW = 50 ;

    public static final int EXPIRY_PARAMS_MESSAGEVIEW = 24*30*3600 ;

    public static final int KEYBOARD_ICON = R.drawable.ic_action_keyboard;
    public static final int EMOJI_ICON = R.drawable.input_emoji;
    public static final int DEFAULT_LOCATION_IMAGE = R.drawable.bmap;
    public static final int DEFAULT_FILE_IMAGE = R.drawable.file_file;


    public static final String YOU_STRING_IN_REPLYVIEW = "You";

    public static final String VIDEO_RECORDER_STRING = "Video Recorder";
    public static final String FROM_GALLERY_STRING = "From Gallery";
    public static final String VIDEO_TITLE_STRING = "Select your video from?";

    public static final String COPY_STRING = "Copy";
    public static final String GOOGLE_PLAYSERVICE_STRING = "Please Download Google play service from Google Play store.";

    public static final String TITLE_PERMISON_FAIL = "Permission Denied";
    public static final String MSG_PERMISON_FAIL = "One or more required permission was denied by you! Change the permission from settings and try again";


    public static final String TITLE_PERMISON_LOCATION_FAIL = "Permission Denied";
    public static final String MSG_PERMISON_LOCATION_FAIL = "Location permission was denied by you! Change the permission from settings menu location service";

    public static final String TITLE_PERMISON_CAMERA_FAIL = "Permission Denied";
    public static final String MSG_PERMISON_CAMERA_FAIL = "Camera permission was denied by you! Change the permission from settings menu";


    public static final String TITLE_INVALID_GROUP = "Invalid group";
    public static final String MSG_INVALID_GROUP = "You are not a member of this group or not allowed to send message to this group";

    public static final int TOOLBAR_COLOR = MesiboUI.getConfig().mToolbarColor ;
    public static final int TOOLBAR_TEXT_COLOR = MesiboUI.getConfig().mToolbarTextColor ;
    public static final int TOOLBAR_STATUSBAR_COLOR = MesiboUI.getConfig().mStatusbarColor ;

    public static final String FILE_NOT_AVAILABLE_TITLE = "File not available";
    public static final String FILE_NOT_AVAILABLE_MSG = "Sorry, this File is no longer available on the server.";

    public static final String USER_STATUS_TYPING = "typing...";
}
