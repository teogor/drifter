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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.view.View

// com.unity3d.palyer.C
@SuppressLint("DiscouragedApi", "ViewConstructor")
class UnitySplashView(
  context: Context?,
  private val splashMode: UnitySplashMode,
) : View(context) {
  private val staticSplashDrawableId: Int
  private var backgroundBitmap: Bitmap? = null
  private var scaledBitmap: Bitmap? = null
  private val layerDrawable: LayerDrawable

  enum class UnitySplashMode {
    Fit,
    Fill,
  }

  init {
    val resources = resources
    val packageName = getContext().packageName
    staticSplashDrawableId = resources.getIdentifier(
      "unity_static_splash",
      "drawable",
      packageName,
    )
    val backgroundColor = ColorDrawable(BACKGROUND_COLOR)
    val bitmapDrawable = BitmapDrawable(getResources(), scaledBitmap)
    bitmapDrawable.gravity = BITMAP_GRAVITY
    layerDrawable = LayerDrawable(arrayOf(backgroundColor, bitmapDrawable))
    if (staticSplashDrawableId != 0) {
      forceLayout()
    }
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    if (staticSplashDrawableId != 0) {
      if (backgroundBitmap == null) {
        val options = BitmapFactory.Options()
        options.inScaled = false
        backgroundBitmap =
          BitmapFactory.decodeResource(resources, staticSplashDrawableId, options)
      }
      val bitmapWidth = backgroundBitmap!!.width
      val bitmapHeight = backgroundBitmap!!.height
      val viewWidth = width
      val viewHeight = height
      val aspectRatio = bitmapWidth.toFloat() / bitmapHeight.toFloat()
      val fitToWidth = viewWidth / viewHeight.toFloat() <= aspectRatio

      val newWidth: Int
      val newHeight: Int
      if (viewWidth < bitmapWidth) {
        newHeight = (viewWidth / aspectRatio).toInt()
        newWidth = viewWidth
      } else {
        newWidth = bitmapWidth
        newHeight = bitmapHeight
      }
      if (scaledBitmap == null || scaledBitmap!!.width != newWidth || scaledBitmap!!.height != newHeight) {
        if (scaledBitmap != null && scaledBitmap != backgroundBitmap) {
          scaledBitmap!!.recycle()
          scaledBitmap = null
        }
        scaledBitmap =
          Bitmap.createScaledBitmap(backgroundBitmap!!, newWidth, newHeight, true)
        scaledBitmap!!.density = resources.displayMetrics.densityDpi
      }
      background = layerDrawable
    }
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    if (backgroundBitmap != null) {
      backgroundBitmap!!.recycle()
      backgroundBitmap = null
    }
    if (scaledBitmap != null) {
      scaledBitmap!!.recycle()
      scaledBitmap = null
    }
  }

  fun getSplashMode() = splashMode

  companion object {
    private const val BACKGROUND_COLOR = -16777216
    private const val BITMAP_GRAVITY = 17
  }
}
