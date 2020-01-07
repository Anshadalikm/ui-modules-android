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
package com.mesibo.uihelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.mesibo.uihelper.Utils.Alert;

import java.lang.ref.WeakReference;

/**
 * Created by root on 1/14/17.
 */

public class MesiboUiHelper {
    public static MesiboUiHelperConfig mMesiboUiHelperConfig = new MesiboUiHelperConfig();
    public static WeakReference<ILoginInterface> mLoginInterface = null;
    public static IProductTourListener mProductTourListener = null;


    public static void launchTour(Context context, boolean newTask, IProductTourListener listener) {

        mProductTourListener = listener;
        Intent intent = new Intent(context, ProductTourActivity.class);
        if(newTask)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        context.startActivity(intent);
    }

    public static void launchWelcome(Context context, boolean newTask, ILoginInterface iLogin) {

        mLoginInterface = new WeakReference<ILoginInterface>(iLogin);
        launch(context, newTask, 0);
    }

    public static void launchLogin(Context context, boolean newTask, int type, ILoginInterface iLogin) {
        mLoginInterface = new WeakReference<ILoginInterface>(iLogin);

        launch(context, newTask, type);
    }

    //type 0 - welcome and login
    // type 1 - old login
    // type 2 - new login
    private static void launch(Context context, boolean newTask, int type) {
        Intent intent = new Intent(context, LoginActivity.class);
        if(newTask)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        intent.putExtras(bundle);

        context.startActivity(intent);
    }

    public static void launchAccountKit(Context context, boolean newTask, ILoginInterface iLogin, Bundle bundle) {
        mLoginInterface = new WeakReference<ILoginInterface>(iLogin);
        Intent intent = new Intent(context, AccountKitLauncherActivity.class);
        if(newTask)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        if(bundle != null)
            intent.putExtras(bundle);

        context.startActivity(intent);
    }

    public static void setConfig(MesiboUiHelperConfig mesiboUiHelperConfig) {
        mMesiboUiHelperConfig = mesiboUiHelperConfig;
    }

    public static MesiboUiHelperConfig getConfig() {
        return mMesiboUiHelperConfig;
    }

    public static ILoginInterface getLoginInterface() {
        if(null == mLoginInterface)
            return null;

        return mLoginInterface.get();
    }

    public static void showChoicesDialog(final Activity context, String title, String[] items) {
        Alert.showChoicesDialog(context, title, items);
    }
}
