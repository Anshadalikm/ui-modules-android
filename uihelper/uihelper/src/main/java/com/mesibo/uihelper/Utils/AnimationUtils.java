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
package com.mesibo.uihelper.Utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

/**
 * Created by root on 7/26/15.
 */
public class AnimationUtils {
    /**
     * @param view         View to animate
     * @param toVisibility Visibility at the end of animation
     * @param toAlpha      Alpha at the end of animation
     * @param duration     Animation duration in ms
     */
    /*
    public static void animateView(final View view, final int toVisibility, float toAlpha, int duration) {
        boolean show = (toVisibility == View.VISIBLE);
        if (show) {
            view.setAlpha(0);
        }
        view.setVisibility(View.VISIBLE);
        view.animate()
                .setDuration(duration)
                .alpha(show ? toAlpha : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(toVisibility);
                    }
                });
    }
    */

    public static void animateView(final View view, final boolean show, int duration) {
        view.clearAnimation();
        float maxAlpha = 0.6f;
        float startalpha = maxAlpha;
        float endAlpha = 0.0f;
        if(show) {
            startalpha = 0.0f;
            endAlpha = maxAlpha;
        }
        AlphaAnimation animation = new AlphaAnimation(startalpha, endAlpha);
        animation.setDuration(duration);
        //animation.setStartOffset(5000);
        animation.setFillAfter(true);

        view.setVisibility(View.VISIBLE);
        view.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                //view.clearAnimation();
                // start animation1 when animation2 ends (repeat)
                view.setVisibility(show?View.VISIBLE:View.GONE);

                if(!show) {
                    //http://stackoverflow.com/questions/8690029/why-doesnt-setvisibility-work-after-a-view-is-animated
                    view.clearAnimation();
                    view.setVisibility(View.GONE);
                    //view.setClickable(false);
                }


            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub

            }
        });
    }



	private void animateImage(ImageView iv) {
		int vy = iv.getLayoutParams().height;

		int x = iv.getDrawable().getBounds().width()/2;
		int y = iv.getDrawable().getBounds().height()/2;
		float animation_range = -100f;
		int duration = 8000; // time to cover the range, more the value, slower the animation
        TranslateAnimation _translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, animation_range, TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f);
		_translateAnimation.setDuration(duration);
		_translateAnimation.setRepeatCount(Animation.INFINITE); // -1
		_translateAnimation.setRepeatMode(Animation.REVERSE); // REVERSE
		_translateAnimation.setInterpolator(new LinearInterpolator());
        iv.startAnimation(_translateAnimation);
	}

}
