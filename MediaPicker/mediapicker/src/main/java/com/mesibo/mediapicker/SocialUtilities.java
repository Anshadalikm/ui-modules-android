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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Date;
import java.util.UUID;

public final class SocialUtilities {

    public static  String createImageFromBitmap(Context ctx, Bitmap bitmap) {
         String fileName = "myImage";//no .png or .jpg needed
        File f = new File(android.os.Environment
                .getExternalStorageDirectory(), "temp"+new Date().getTime()+".jpg");
        //String fileName = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/view.png";
        try {

            FileOutputStream fo = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fo);

            fo.flush();
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return f.getAbsolutePath();
    }

    public static Bitmap getMyProfilePictureBitmap(Context ctx ) {
        Bitmap bitmap =null;
        String filename = "myImage";
        //String filename = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/view.png";

        try {

            bitmap = BitmapFactory.decodeStream(ctx.openFileInput(filename));
        }catch (Exception e) {
            e.printStackTrace();
            filename = null;
        }

        return(bitmap);
    }
    public static  String saveBitmpToFile(Context ctx, Bitmap bitmap, String fileName) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = ctx.openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }

    public static Bitmap getBitmapFromfile(Context ctx,String filename ) {
        Bitmap bitmap =null;
        try {

            bitmap = BitmapFactory.decodeStream(ctx.openFileInput(filename));
        }catch (Exception e) {
            e.printStackTrace();
            filename = null;
        }

        return(bitmap);
    }

    public static Bitmap getBitmapFromImagefile(String filename ) {
        Bitmap bitmap =null;
        try {

            bitmap = BitmapFactory.decodeFile(filename);
        }catch (Exception e) {
            e.printStackTrace();
            filename = null;
        }

        return(bitmap);
    }

    public static  Bitmap createSqrCropppedProfile (Bitmap bitmap) {
        Bitmap dstBmp = null;
        if (bitmap.getWidth() >= bitmap.getHeight()){

            dstBmp = Bitmap.createBitmap(
                    bitmap,
                    bitmap.getWidth()/2 - bitmap.getHeight()/2,
                    0,
                    bitmap.getHeight(),
                    bitmap.getHeight()
            );

        }else{

            dstBmp = Bitmap.createBitmap(
                    bitmap,
                    0,
                    bitmap.getHeight()/2 - bitmap.getWidth()/2,
                    bitmap.getWidth(),
                    bitmap.getWidth()
            );
        }


        return dstBmp;
    }

    public  static String bitmapToFilepath(Context mContext, Bitmap icon)

    {


        String file_path = MediaPicker.getPath() +
                "/profiles";
        File dir = new File(file_path);
        if (!dir.exists())
            dir.mkdirs();

        String uuid = UUID.randomUUID().toString();
        File file = new File(dir, uuid + ".png");
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        icon.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }
    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }


    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }
    public  static Bitmap createThumbnailAtTime(String filePath, int timeInSeconds){
        MediaMetadataRetriever mMMR = new MediaMetadataRetriever();
        mMMR.setDataSource(filePath);
        //api time unit is microseconds
        return mMMR.getFrameAtTime(timeInSeconds*1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static int getExifRotation(String filePath) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int rotationInDegrees = 0;

        if (rotation == ExifInterface.ORIENTATION_ROTATE_90) {
            rotationInDegrees =  90;
        }
        else if (rotation == ExifInterface.ORIENTATION_ROTATE_180) {
            rotationInDegrees =  180;
        }
        else if (rotation == ExifInterface.ORIENTATION_ROTATE_270) {
            rotationInDegrees =  270;
        }

        return rotationInDegrees;
    }

    public static ExifInterface getExif(String filePath) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return exif;
    }

    public static int getDeviceRotation(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int deviceRotation = display.getRotation();
        if(Surface.ROTATION_0 == deviceRotation)
            return 0;
        if(Surface.ROTATION_90 == deviceRotation)
            return 90;
        if(Surface.ROTATION_180 == deviceRotation)
            return 180;
        if(Surface.ROTATION_270 == deviceRotation)
            return 270;
        return 0;
    }

}
