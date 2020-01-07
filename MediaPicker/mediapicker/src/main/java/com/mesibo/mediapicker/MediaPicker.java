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
package com.mesibo.mediapicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaPicker {

    public static int TYPE_FILEIMAGE      = 10000;
    public static int TYPE_CAMERAIMAGE    = 10001;
    public static int TYPE_FILEVIDEO      = 10002;
    public static int TYPE_CAMERAVIDEO    = 10003;
    public static int TYPE_FACEBOOK       = 10004;
    public static int TYPE_FILE           = 10005;
    public static int TYPE_AUDIO          = 10006;
    public static int TYPE_CUSTOM         = 10007;

    public static int TYPE_CAPTION        = 10015; //maximum value


    public static int BASE_TYPE_VALUE        = 10000;

    //private static Map<String, ImageEditorListener> mEditorListners = new HashMap<String, ImageEditorListener>();\\\

    private static ImageEditorListener mImageEditorListener = null;

    private static String mTempPath = null;
    private static String mAuthority = null;

    public interface ImageEditorListener  {
        void onImageEdit(int type, String caption, String filePath, Bitmap bmp, int result);
    }

    public static void setBaseTypeValue(int val) {

        TYPE_FILEIMAGE = (TYPE_FILEIMAGE-BASE_TYPE_VALUE) + val;
        TYPE_CAMERAIMAGE = (TYPE_CAMERAIMAGE-BASE_TYPE_VALUE) + val;
        TYPE_FILEVIDEO = (TYPE_FILEVIDEO-BASE_TYPE_VALUE) + val;
        TYPE_CAMERAVIDEO = (TYPE_CAMERAVIDEO-BASE_TYPE_VALUE) + val;
        TYPE_FACEBOOK = (TYPE_FACEBOOK-BASE_TYPE_VALUE) + val;
        TYPE_AUDIO = (TYPE_AUDIO-BASE_TYPE_VALUE) + val;
        TYPE_CUSTOM = (TYPE_CUSTOM-BASE_TYPE_VALUE) + val;
        TYPE_CAPTION = (TYPE_CAPTION-BASE_TYPE_VALUE) + val;

        BASE_TYPE_VALUE = val;

    }

    public static int getBaseTypeValue() {
        return BASE_TYPE_VALUE;
    }

    public static int getMaxTypeValue() {
        return TYPE_CAPTION;
    }

    private static int mToolbarColor = 0xff00868b;
    public static void setToolbarColor(int color) {
        mToolbarColor = color;
    }

    public static int getToolbarColor() {
        return mToolbarColor;
    }

    protected static ImageEditorListener getImageEditorListener() {
        return mImageEditorListener;
    }

    public static void launchEditor(Activity context, int type, int drawableid, String title, String filePath, boolean showEditControls, boolean showTitle, boolean showCropOverlay, boolean squareCrop, int maxDimension, ImageEditorListener listener) {
        Intent in = new Intent(context, ImageEditor.class);
        in.putExtra("title", title);
        in.putExtra("filepath", filePath);
        in.putExtra("showEditControls", showEditControls);
        in.putExtra("showTitle", showTitle);
        in.putExtra("showCrop", showCropOverlay);
        in.putExtra("squareCrop", squareCrop);
        in.putExtra("type", type);
        in.putExtra("drawableid", drawableid);
        //in.putExtra("listener", listener);
        mImageEditorListener = listener;
        if(maxDimension > 0)
            in.putExtra("maxDimension", maxDimension);


        if(null == listener)
            context.startActivityForResult(in, TYPE_CAPTION);
        else
            context.startActivity(in);
    }

    public static void launchImageViewer(Activity context, String filePath) {
        Intent intent = new Intent (context, zoomVuPictureActivity.class);
        intent.putExtra("filePath", filePath);
        context.startActivity(intent);
    }

    public static void launchImageViewer(Activity context, ArrayList<String> files, int firstIndex) {
        Intent intent = new Intent (context, zoomVuPictureActivity.class);
        intent.putExtra("position", firstIndex);
        intent.putStringArrayListExtra("stringImageArray", files);
        context.startActivity(intent);
    }

    private static List<AlbumListData> mAlbumList=null;

    public static void launchAlbum(Activity context, List<AlbumListData> albumList) {
        mAlbumList = albumList;
        Intent newIntent = new Intent (context, AlbumStartActivity.class);
        context.startActivity(newIntent);
    }

    public static List<AlbumListData> getAlbumList() {
        return mAlbumList;
    }

    public static void launchPicker(Activity activity, int fileType, String path) {
        ImagePicker.getInstance().pick(activity, fileType, path);
    }

    public static void launchPicker(Activity activity, int fileType) {
        ImagePicker.getInstance().pick(activity, fileType);
    }


    public boolean isActivityStarted(int code) {
        return (code >= getBaseTypeValue() || code <= getMaxTypeValue());
    }

    public static String processOnActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        return ImagePicker.getInstance().processOnActivityResult(activity, requestCode, resultCode, data);
    }

    public static void setPath(String path, String authority) {
        mTempPath = path;
        mAuthority = authority;
    }

    public static void setPath(String path) {
        mTempPath = path;
    }

    public static void setAuthority(String authority) {
        mAuthority = authority;
    }

    public static String getPath() {
        if(TextUtils.isEmpty(mTempPath))
            return android.os.Environment.getExternalStorageDirectory().getPath();

        return mTempPath;
    }

    public static String getAuthority(Context context) {
        if(TextUtils.isEmpty(mAuthority)) {
            return context.getPackageName() + ".provider";
        }
        return mAuthority;
    }

    public static Uri getUri(Context context, File file) {
        return FileProvider.getUriForFile(context, getAuthority(context), file);
    }

    protected static String getTempPath(Context context, String name, String ext, boolean video) {
        //File outputDir = context.getCacheDir(); // context being the Activity pointer
        //File outputFile = File.createTempFile("prefix", "extension", outputDir);

        File storageDir = context.getExternalFilesDir(video?Environment.DIRECTORY_MOVIES:Environment.DIRECTORY_PICTURES);
        try {
            File image = File.createTempFile(
                    name,  /* prefix */
                    ext,         /* suffix */
                    storageDir      /* directory */
            );

            return image.getAbsolutePath();

        } catch (Exception e) {
            return null;
        }
    }
}
