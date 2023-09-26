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

package com.unity3d.player.callbacks

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.PixelCopy.OnPixelCopyFinishedListener
import android.view.SurfaceHolder
import com.unity3d.player.components.UnityFrameLayout
import com.unity3d.player.utils.PlatformSupport

// com.unity3d.player.H
class UnitySurfaceHolderCallback(private val unityFrameLayout: UnityFrameLayout) :
  SurfaceHolder.Callback {
  override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
    val unityPlayer = unityFrameLayout.unityPlayer
    unityPlayer.updateGLDisplay(0, surfaceHolder.surface)
    val nativeLayout = unityFrameLayout.nativeLayout
    val unityPixelCopyView = nativeLayout.pixelCopyView
    if (unityPixelCopyView != null && unityPixelCopyView.parent == null) {
      unityPlayer.addView(nativeLayout.pixelCopyView)
      unityPlayer.bringChildToFront(nativeLayout.pixelCopyView)
    }
  }

  override fun surfaceChanged(
    surfaceHolder: SurfaceHolder,
    format: Int,
    width: Int,
    height: Int,
  ) {
    unityFrameLayout.unityPlayer.updateGLDisplay(0, surfaceHolder.surface)
    unityFrameLayout.unityPlayer.sendSurfaceChangedEvent()
  }

  override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
    val surfaceView = unityFrameLayout.unitySurfaceView
    val nativeLayout = unityFrameLayout.nativeLayout
    if (PlatformSupport.NOUGAT_SUPPORT && nativeLayout.pixelCopyView != null) {
      val pixelCopyListener = nativeLayout.pixelCopyView
      var copyBitmap: Bitmap
      synchronized(pixelCopyListener!!) {
        copyBitmap = Bitmap.createBitmap(
          surfaceView.width,
          surfaceView.height,
          Bitmap.Config.ARGB_8888,
        )
        pixelCopyListener.copiedBitmap = copyBitmap
      }
      val handler = Handler(Looper.getMainLooper())
      PixelCopy.request(
        surfaceView,
        copyBitmap,
        (pixelCopyListener as OnPixelCopyFinishedListener?)!!,
        handler,
      )
    }
    unityFrameLayout.unityPlayer.updateGLDisplay(0, null)
  }
}
