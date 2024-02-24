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

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.view.PixelCopy.OnPixelCopyFinishedListener
import android.view.View

// com.unity3d.player.v
internal class UnityPixelCopyView(context: Context?) : View(context), OnPixelCopyFinishedListener {
  @JvmField
  var copiedBitmap: Bitmap? = null

  override fun onPixelCopyFinished(copyResult: Int) {
    if (copyResult == 0) {
      val colorDrawable = ColorDrawable(Color.BLACK)
      val bitmapDrawable = BitmapDrawable(resources, copiedBitmap)
      val layers = arrayOfNulls<Drawable>(2)
      layers[0] = colorDrawable
      layers[1] = bitmapDrawable
      val layerDrawable = LayerDrawable(layers)
      background = layerDrawable
    }
  }
}
