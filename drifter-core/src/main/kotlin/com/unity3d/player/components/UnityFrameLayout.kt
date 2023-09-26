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
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import com.unity3d.player.UnityPlayer
import com.unity3d.player.callbacks.UnityActivityLifecycleCallback
import com.unity3d.player.callbacks.UnitySurfaceHolderCallback

// com.unity3d.player.I
@SuppressLint("ViewConstructor")
class UnityFrameLayout constructor(
  context: Context,
  @JvmField var unityPlayer: UnityPlayer,
) : FrameLayout(context) {
  @JvmField
  var unitySurfaceView: UnitySurfaceView

  @JvmField
  internal var nativeLayout: UnityActivityLifecycleCallback

  init {
    nativeLayout = UnityActivityLifecycleCallback(context)
    val unitySurfaceView = UnitySurfaceView(context, null, 0, 0, unityPlayer)
    this.unitySurfaceView = unitySurfaceView
    unitySurfaceView.id = context.resources.getIdentifier(
      "unitySurfaceView",
      "id",
      context.packageName,
    )
    if (shouldSetSurfaceViewFormat()) {
      this.unitySurfaceView.holder.setFormat(-3)
      this.unitySurfaceView.setZOrderOnTop(true)
    } else {
      this.unitySurfaceView.holder.setFormat(-1)
    }
    this.unitySurfaceView.holder.addCallback(UnitySurfaceHolderCallback(this))
    this.unitySurfaceView.isFocusable = true
    this.unitySurfaceView.isFocusableInTouchMode = true
    this.unitySurfaceView.contentDescription = getContentDescription(context)
    setBackgroundColor(Color.BLACK)
    addView(
      this.unitySurfaceView,
      LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT,
        Gravity.CENTER,
      ),
    )
  }

  private fun shouldSetSurfaceViewFormat(): Boolean {
    val activity = UnityPlayer.currentActivity ?: return false
    val obtainStyledAttributes = activity.theme.obtainStyledAttributes(intArrayOf(16842840))
    val z = obtainStyledAttributes.getBoolean(0, false)
    obtainStyledAttributes.recycle()
    return z
  }

  private fun getContentDescription(context: Context): String {
    return context.resources.getString(
      context.resources
        .getIdentifier("game_view_content_description", "string", context.packageName),
    )
  }

  fun setAspectRatio(ratio: Float) {
    unitySurfaceView.setAspectRatio(ratio)
  }

  val isSurfaceViewValid: Boolean
    get() {
      val aVar = unitySurfaceView
      return aVar.hasAspectRatio()
    }

  fun removeNativeView() {
    val wVar = nativeLayout
    val unityPlayer = unityPlayer
    val vVar = wVar.pixelCopyView
    if (!(vVar == null || vVar.parent == null)) {
      unityPlayer.removeView(wVar.pixelCopyView)
    }
    nativeLayout.pixelCopyView = null
  }
}
