/*
 * Copyright 2023 teogor (Teodor Grigor)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.unity3d.player.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.ViewGroup
import com.unity3d.player.UnityPlayer

// com.unity3d.player.C0005a
@SuppressLint("ViewConstructor")
class UnitySurfaceView(
  context: Context? = null,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0,
  private val unityPlayer: UnityPlayer,
) : SurfaceView(context, attrs, defStyleAttr, defStyleRes) {
  private var aspectRatio = 0f

  fun setAspectRatio(aspectRatio: Float) {
    this.aspectRatio = aspectRatio
    val layoutParams = layoutParams
    if (aspectRatio <= 0.0f) {
      layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
      layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
    } else {
      layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
      layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
    }
    setLayoutParams(layoutParams)
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    if (aspectRatio <= 0.0f) {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec)
      return
    }
    var width = MeasureSpec.getSize(widthMeasureSpec)
    var height = MeasureSpec.getSize(heightMeasureSpec)
    if (width <= 0 || height <= 0 || MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.AT_MOST || MeasureSpec.getMode(
        heightMeasureSpec,
      ) != MeasureSpec.AT_MOST
    ) {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec)
      return
    }
    val viewAspectRatio = width.toFloat() / height.toFloat()
    if (viewAspectRatio < aspectRatio) {
      height = (width / aspectRatio).toInt()
    } else {
      width = (height * aspectRatio).toInt()
    }
    setMeasuredDimension(width, height)
  }

  fun hasAspectRatio(): Boolean {
    return aspectRatio > 0.0f
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
    return if (hasAspectRatio()) {
      unityPlayer.injectEvent(motionEvent)
    } else {
      false
    }
  }

  override fun onGenericMotionEvent(motionEvent: MotionEvent): Boolean {
    return if (hasAspectRatio()) {
      unityPlayer.injectEvent(motionEvent)
    } else {
      false
    }
  }
}
