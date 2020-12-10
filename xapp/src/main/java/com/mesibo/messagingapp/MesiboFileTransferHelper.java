package com.mesibo.messagingapp;

import android.os.Bundle;

import com.mesibo.api.Mesibo;


public class MesiboFileTransferHelper implements Mesibo.FileTransferHandler {

    MesiboFileTransferHelper() {
        Mesibo.addListener(this);
    }

    public boolean Mesibo_onStartUpload(long id, String s, Mesibo.FileInfo file) {

        //if(!file.userInteraction)
          //  return false;

        Bundle b = new Bundle();
        b.putString("op", "uploadtest");
        b.putLong("mid", id);

        final long mid = id;

        Mesibo.upload_deprecated(file, file.getPath(),
                "https://api.tringme.com/webapi2/webapi.php", b, "fileid", 0,
                new Mesibo.HTTPUtilsProgress() {
                    @Override
                    public boolean on_http_progress(Object cbdata, int percent, String result) {
                        Mesibo.FileInfo f = (Mesibo.FileInfo)cbdata;
                        if(100 == percent) {
                            f.setUrl(result);
                        }

                        int status = Mesibo.FileInfo.STATUS_INPROGRESS;
                        if(percent < 0)
                            status = Mesibo.FileInfo.STATUS_RETRYLATER;

                        Mesibo.updateFileTransferProgress(f, percent, status);
                        //Log.d(TAG, "http upload progress: " + percent + " :" + result);
                        return true;
                    }
                }
        );

        return true;

    }

    public boolean Mesibo_onStartDownload(long id, String peer, Mesibo.FileInfo file) {

        if(false && !file.userInteraction)
            return false;

        final long mid = id;
        String url = "https://mesibo.com/samplefiles/" + file.getUrl();
        Mesibo.download_deprecated(file, url, file.getPath(), 10, new Mesibo.HTTPUtilsProgress() {


            @Override
            public boolean on_http_progress(Object cbdata, int percent, String result) {
                Mesibo.FileInfo f = (Mesibo.FileInfo)cbdata;
                Mesibo.updateFileTransferProgress((Mesibo.FileInfo)cbdata , percent, Mesibo.FileInfo.STATUS_INPROGRESS);
                //Log.d(TAG, "http download progress: " + percent + " :" + result);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean Mesibo_onStartFileTransfer(Mesibo.FileInfo file) {
        if(Mesibo.FileInfo.MODE_DOWNLOAD == file.mode)
            return Mesibo_onStartDownload(file.mid, file.getParams().peer, file);

        return Mesibo_onStartUpload(file.mid, file.getParams().peer, file);
    }

    @Override
    public boolean Mesibo_onStopFileTransfer(Mesibo.FileInfo fileInfo) {
        return false;
    }
}
