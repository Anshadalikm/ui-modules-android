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

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class AlbumListData {

    private String mAlbumName ;
    private String mAlbumPictureUrl;
    private Integer mPhotoCount;
    private Bitmap mAlbumCoverPicture;
    private Boolean mHasImageDownloaded;
    private List<AlbumPhotosData> mPhotosList;


    public AlbumListData(){

        mAlbumName = null;
        mAlbumPictureUrl = null;
        mPhotoCount = 0;
        mAlbumCoverPicture = null;
        mHasImageDownloaded = false;
        mPhotosList = new ArrayList<>();

    }
    public String  getmAlbumName () {

        return mAlbumName;

    }
    public String getmAlbumPictureUrl() {
        return mAlbumPictureUrl;
    }
    public Bitmap getmAlbumCoverPicture(){
        return mAlbumCoverPicture;
    }

    public void setmAlbumName (String name) {
        mAlbumName = name;
    }

    public void setmAlbumPictureUrl(String totalPictures) {
        mAlbumPictureUrl = totalPictures;
    }
    public void setmAlbumCoverPicture (Bitmap albumCover) {
        mAlbumCoverPicture = albumCover;
    }

    public Integer getmPhotoCount() {
        return mPhotoCount;
    }

    public void setmPhotoCount(Integer mPhotoCount) {
        this.mPhotoCount = mPhotoCount;
    }

    public List<AlbumPhotosData> getmPhotosList() {
        return mPhotosList;
    }

    public void setmPhotosList(List<AlbumPhotosData> mPhotosList) {
        this.mPhotosList = mPhotosList;
    }

    public Boolean getmHasImageDownloaded() {
        return mHasImageDownloaded;
    }

    public void setmHasImageDownloaded(Boolean mHasImageDownloaded) {
        this.mHasImageDownloaded = mHasImageDownloaded;
    }
}
