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

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductTourFragment extends Fragment{
 
    final static String SCREEN_INDEX = "layoutid";
 
    public static ProductTourFragment newInstance(int screenIndex) {
        ProductTourFragment pane = new ProductTourFragment();
        Bundle args = new Bundle();
        args.putInt(SCREEN_INDEX, screenIndex);
        pane.setArguments(args);
        return pane;
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MesiboUiHelperConfig config = MesiboUiHelper.getConfig();
        if(null == config) {
            return null;
        }

        int index = getArguments().getInt(SCREEN_INDEX, -1);
        if(index < 0)
            return null;

        WelcomeScreen screen = config.mScreens.get(index);
        int layoutId = screen.getLayoutId();
        if(layoutId <= 0) {
            layoutId = R.layout.tour_fragment;
        }

        ViewGroup rootView = (ViewGroup) inflater.inflate(layoutId, container, false);

        if(layoutId == R.layout.tour_fragment) {
            View v = rootView.findViewById(R.id.welcome_fragment);
            if(null != v && screen.getBackgroundColor() != 0) {
                v.setBackgroundColor(screen.getBackgroundColor());
            }

            ImageView image = (ImageView) rootView.findViewById(R.id.welcomeImage);
            if(null != image)
                image.setImageResource(screen.getResourceId());
            AutoResizeTextView heading = (AutoResizeTextView) rootView.findViewById(R.id.heading);
            if(null != heading) {
                heading.setMaxLines(1);
                heading.setText(screen.getTitle());
            }
            AutoResizeTextView content = (AutoResizeTextView) rootView.findViewById(R.id.content);
            if(null != content) {
                content.setMaxLines(3);
                content.setText(screen.getDescription());
            }
        }

        if(MesiboUiHelper.mProductTourListener != null) {
            MesiboUiHelper.mProductTourListener.onProductTourViewLoaded(rootView, index, screen);
        }

        return rootView;
    }

}