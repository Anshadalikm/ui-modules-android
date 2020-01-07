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
// "Therefore those skilled at the unorthodox
// are infinite as heaven and earth,
// inexhaustible as the great rivers.
// When they come to an end,
// they begin again,
// like the days and months;
// they die and are reborn,
// like the four seasons."
//
// - Sun Tsu,
// "The Art of War"

package com.mesibo.mediapicker.cropper;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;

/**
 * Animation to handle smooth cropping image matrix transformation change, specifically for
 * zoom-in/out.
 */
final class CropImageAnimation extends Animation implements Animation.AnimationListener {

  // region: Fields and Consts

  private final ImageView mImageView;

  private final CropOverlayView mCropOverlayView;

  private final float[] mStartBoundPoints = new float[8];

  private final float[] mEndBoundPoints = new float[8];

  private final RectF mStartCropWindowRect = new RectF();

  private final RectF mEndCropWindowRect = new RectF();

  private final float[] mStartImageMatrix = new float[9];

  private final float[] mEndImageMatrix = new float[9];

  private final RectF mAnimRect = new RectF();

  private final float[] mAnimPoints = new float[8];

  private final float[] mAnimMatrix = new float[9];
  // endregion

  public CropImageAnimation(ImageView cropImageView, CropOverlayView cropOverlayView) {
    mImageView = cropImageView;
    mCropOverlayView = cropOverlayView;

    setDuration(300);
    setFillAfter(true);
    setInterpolator(new AccelerateDecelerateInterpolator());
    setAnimationListener(this);
  }

  public void setStartState(float[] boundPoints, Matrix imageMatrix) {
    reset();
    System.arraycopy(boundPoints, 0, mStartBoundPoints, 0, 8);
    mStartCropWindowRect.set(mCropOverlayView.getCropWindowRect());
    imageMatrix.getValues(mStartImageMatrix);
  }

  public void setEndState(float[] boundPoints, Matrix imageMatrix) {
    System.arraycopy(boundPoints, 0, mEndBoundPoints, 0, 8);
    mEndCropWindowRect.set(mCropOverlayView.getCropWindowRect());
    imageMatrix.getValues(mEndImageMatrix);
  }

  @Override
  protected void applyTransformation(float interpolatedTime, Transformation t) {

    mAnimRect.left =
        mStartCropWindowRect.left
            + (mEndCropWindowRect.left - mStartCropWindowRect.left) * interpolatedTime;
    mAnimRect.top =
        mStartCropWindowRect.top
            + (mEndCropWindowRect.top - mStartCropWindowRect.top) * interpolatedTime;
    mAnimRect.right =
        mStartCropWindowRect.right
            + (mEndCropWindowRect.right - mStartCropWindowRect.right) * interpolatedTime;
    mAnimRect.bottom =
        mStartCropWindowRect.bottom
            + (mEndCropWindowRect.bottom - mStartCropWindowRect.bottom) * interpolatedTime;
    mCropOverlayView.setCropWindowRect(mAnimRect);

    for (int i = 0; i < mAnimPoints.length; i++) {
      mAnimPoints[i] =
          mStartBoundPoints[i] + (mEndBoundPoints[i] - mStartBoundPoints[i]) * interpolatedTime;
    }
    mCropOverlayView.setBounds(mAnimPoints, mImageView.getWidth(), mImageView.getHeight());

    for (int i = 0; i < mAnimMatrix.length; i++) {
      mAnimMatrix[i] =
          mStartImageMatrix[i] + (mEndImageMatrix[i] - mStartImageMatrix[i]) * interpolatedTime;
    }
    Matrix m = mImageView.getImageMatrix();
    m.setValues(mAnimMatrix);
    mImageView.setImageMatrix(m);

    mImageView.invalidate();
    mCropOverlayView.invalidate();
  }

  @Override
  public void onAnimationStart(Animation animation) {}

  @Override
  public void onAnimationEnd(Animation animation) {
    mImageView.clearAnimation();
  }

  @Override
  public void onAnimationRepeat(Animation animation) {}
}
