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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.Date;
import java.util.List;


public class ImagePicker {

    private static List<AlbumListData> mAlbumList=null;
    ImageView im;


    private static ImagePicker instance = null;
    private static Activity parentActivity = null;


    String TAG = "FacebookLogin";
    TextView mUserNameTextView;
    Boolean login = false;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Intent FirstIntent;


    File cameraFile;

    private ImagePicker() {
        // Exists only to defeat instantiation.
    }

    public static ImagePicker getInstance(Activity mContext) {
        if(instance == null) {
            instance = new ImagePicker();
        }

        parentActivity =(Activity) mContext;
        return instance;
    }


    public static ImagePicker getInstance( ) {
        if(instance == null) {
            instance = new ImagePicker();
        }

        return instance;
    }



    private void LoadImageOptions(ImagePicker im){

        final CharSequence[] items = {"Take Photo", "From Gallery","From Facebook",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
        builder.setTitle("Change Profile Picture?");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {

                    imageCapture(parentActivity, null);/*
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraFile = new File(android.os.Environment
                            .getExternalStorageDirectory(), "temp"+new Date().getTime()+".jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
                    parentActivity.startActivityForResult(intent, REQUEST_CAMERA);*/
                } else if (items[item].equals("From Gallery")) {
                    imageFromGallery(parentActivity);/*
                    Intent intent = new Intent(
                            Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");

                    parentActivity.startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_PICTURE);*/

                } else if (items[item].equals("From Facebook")) {
                    //initilizeAlbumList();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void imageCapture(Activity activity, String tempPath) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(null == tempPath) {
            tempPath = MediaPicker.getPath();
        }



        // Since we are storing camera output to a file, we need file:// permission from android
        // N onward else we will get FileUriExposedException
        // https://stackoverflow.com/questions/39242026/fileuriexposedexception-in-android-n-with-camera (we copied from here)
        // https://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed
        // https://inthecheesefactory.com/blog/how-to-share-access-to-file-with-fileprovider-on-android-nougat/en

        cameraFile = new File(tempPath + File.separator + "camera-"+new Date().getTime()+".jpg");

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
        } else {
            //File file = new File(Uri.fromFile(cameraFile).getPath());
            //Uri photoUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", cameraFile);
            Uri photoUri = MediaPicker.getUri(activity.getApplicationContext(), cameraFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        activity.startActivityForResult(intent, MediaPicker.TYPE_CAMERAIMAGE);

    }

    private void videoCapture(Activity activity, String tempPath) {

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if(null == tempPath) {
            tempPath = MediaPicker.getPath();
        }

        cameraFile = new File(tempPath + File.separator + "video-"+new Date().getTime()+".mp4");

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
        } else {
            //File file = new File(Uri.fromFile(cameraFile).getPath());
            Uri photoUri = MediaPicker.getUri(activity.getApplicationContext(), cameraFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        activity.startActivityForResult(intent, MediaPicker.TYPE_CAMERAVIDEO);

    }

    private void imageFromGallery(Activity activity) {
        Intent intent = new Intent(
                Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");

        activity.startActivityForResult(
                Intent.createChooser(intent, "Select an Image"),
                MediaPicker.TYPE_FILEIMAGE);

    }

    private void videoFromGallery(Activity activity){
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        activity.startActivityForResult(intent, MediaPicker.TYPE_FILEVIDEO);
    }



    public void selectFiles(Activity activity, int fileType, String filter) {
        Intent intent_upload = new Intent();
        intent_upload.setType(filter);
        intent_upload.addCategory(Intent.CATEGORY_OPENABLE); // this will exclude contacts
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent_upload, fileType);
    }

    public void pick(Activity activity, int fileType, String tempPath, String filter) {

        if(MediaPicker.TYPE_CAMERAIMAGE == fileType) {
            imageCapture(activity, tempPath);
            return;
        }

        if(MediaPicker.TYPE_FILEIMAGE == fileType) {
            imageFromGallery(activity);
            return;
        }

        if(MediaPicker.TYPE_CAMERAVIDEO == fileType) {
            videoCapture(activity, tempPath);
            return;
        }

        if(MediaPicker.TYPE_FILEVIDEO == fileType) {
            videoFromGallery(activity);
            return;
        }

        if(MediaPicker.TYPE_AUDIO == fileType) {
            selectFiles(activity, MediaPicker.TYPE_AUDIO, "audio/*");
            return;
        }

        if(MediaPicker.TYPE_FILE == fileType) {
            selectFiles(activity, MediaPicker.TYPE_FILE, "*/*");
            return;
        }

        if(MediaPicker.TYPE_CUSTOM == fileType) {
            if(null != filter)
                selectFiles(activity, MediaPicker.TYPE_FILE, filter);
            return;
        }

    }

    public void pick(Activity activity, int fileType, String tempPath) {
        pick(activity, fileType, tempPath, null);
    }

    public void pick(Activity activity, int fileType) {
        pick(activity, fileType, null);
    }


    public String getAbsolutePath(Activity activity, Uri uri) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public Bitmap decodeFile(String path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 200;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }




    public String processOnActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return null;
        }

        String actualFilePAth =null;

        if (requestCode == MediaPicker.TYPE_FILEIMAGE) {

            Uri imgUri = data.getData();//SocialUtilities.getImageUri(parentActivity,bitmap);

            Cursor cursor = null; //parentActivity.getContentResolver().query(imgUri, new String[]{ MediaStore.Images.ImageColumns.DATA},null,null,null);

            if(null == cursor) {
                actualFilePAth = FileUtils.getPath(activity, imgUri);

            } else {
                cursor.moveToFirst();
                actualFilePAth = cursor.getString(0);
            }

        } else if (requestCode == MediaPicker.TYPE_CAMERAIMAGE) {

            actualFilePAth = cameraFile.getPath();

        }else  if (requestCode == MediaPicker.TYPE_FACEBOOK){

            actualFilePAth =data.getStringExtra("PATH");

        }
        else if (requestCode== MediaPicker.TYPE_CAMERAVIDEO){

            if(null == data) {
                actualFilePAth = cameraFile.getPath();
            } else {

                //actualFilePAth =  getVideoPath(activity, data.getData());
                actualFilePAth = FileUtils.getPath(activity, data.getData());
                //Bitmap bitmap1 = createThumbnailAtTime(actualFilePAth,0);
                //String imagePath = SocialUtilities.bitmapToFilepath(parentActivity.getApplicationContext(),bitmap1);
            }


        }else if (requestCode== MediaPicker.TYPE_FILEVIDEO) {

            actualFilePAth = FileUtils.getPath(activity, data.getData());
            //actualFilePAth =  getVideoPath(activity, data.getData());
            //Bitmap bitmap1 = createThumbnailAtTime(actualFilePAth,0);
            //String imagePath = SocialUtilities.bitmapToFilepath(parentActivity.getApplicationContext(),bitmap1);

        } else if(requestCode == MediaPicker.TYPE_FILE || requestCode == MediaPicker.TYPE_AUDIO || requestCode == MediaPicker.TYPE_CUSTOM) {

            Uri uri = data.getData();
            String auth = uri.getAuthority();
            //ContentResolver resolver = activity.getContentResolver();
            //actualFilePAth = getFilePathFromContentUri(uri, resolver);
            //actualFilePAth = getFileNameByUri(activity, uri);
            actualFilePAth = FileUtils.getPath(activity, uri);
        }
        else {

        }

        return actualFilePAth;

    }



    /*
    private Bitmap createThumbnailAtTime(String filePath, int timeInSeconds){
        MediaMetadataRetriever mMMR = new MediaMetadataRetriever();
        mMMR.setDataSource(filePath);
        //api time unit is microseconds
        return mMMR.getFrameAtTime(timeInSeconds*1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
    } */


    public String getVideoPath(Activity activity, Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        if(cursor!=null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        else return null;
    }

}
