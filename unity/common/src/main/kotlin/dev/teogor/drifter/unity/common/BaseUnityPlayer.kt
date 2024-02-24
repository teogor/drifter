/*
 * Copyright 2024 teogor (Teodor Grigor)
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

package dev.teogor.drifter.unity.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.view.SurfaceView
import android.widget.FrameLayout
import java.lang.reflect.Field

/**
 * Base class for Unity Player implementations.
 *
 * This class provides a common foundation for integrating Unity Players into the application.
 * It extends [FrameLayout] to serve as a container for the Unity content and implements
 * the [IUnityPlayer] interface to expose essential Unity Player functionality.
 *
 * @param context The Android context.
 */
abstract class BaseUnityPlayer(
  context: Context,
) : FrameLayout(context), IUnityPlayer

fun BaseUnityPlayer.applyOptions(
  options: UnityOptions,
  contextWrapper: ContextWrapper,
) {
  if (options.detachFromActivity) {
    setupUnityPlayerInnerState(
      unityPlayer = this,
      contextWrapper = contextWrapper,
      onContextSetter = options.contextFieldConfigurator,
    )
  }
}

private fun setupUnityPlayerInnerState(
  unityPlayer: BaseUnityPlayer,
  contextWrapper: ContextWrapper,
  onContextSetter: (Field) -> Unit = {},
) {
  // Completely detach Unity from Activity
  val playerFields = unityPlayer::class.java.declaredFields
  for (field in playerFields) {
    // Unregister SurfaceView callbacks
    when (field.type) {
      SurfaceView::class.java -> {
        try {
          field.isAccessible = true
          val surfaceView = field[unityPlayer]
          val callbacksField = surfaceView.javaClass.getDeclaredField("mCallbacks")
          callbacksField.isAccessible = true
          val callbacks = callbacksField[surfaceView] as MutableList<*>
          callbacks.clear()
        } catch (e: Throwable) {
          e.printStackTrace()
        }
      }

      BroadcastReceiver::class.java -> {
        try {
          field.isAccessible = true
          val shutdownReceiver = field[unityPlayer] as? BroadcastReceiver
          contextWrapper.unregisterReceiver(shutdownReceiver)
          field[unityPlayer] = null
        } catch (e: IllegalArgumentException) {
          if (e.message != null) {
            if (!e.message!!.contains("Receiver not registered")) {
              e.printStackTrace()
            }
          }
        } catch (e: Throwable) {
          e.printStackTrace()
        }
      }

      ContextWrapper::class.java -> {
        field.isAccessible = true
        onContextSetter(field)
      }
    }
  }
}
