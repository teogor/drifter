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

package dev.teogor.drifter.integration.player

import android.content.ContextWrapper
import android.view.SurfaceHolder
import dev.teogor.drifter.integration.core.IUnityPlayerInstanceCreatedListener
import dev.teogor.drifter.integration.utilities.DebugLog.d
import dev.teogor.drifter.integration.utilities.DebugLog.v

/**
 * Creates the Unity player instance and fires callbacks at important points of this process.
 */
abstract class UnityPlayerWrapperInstantiator(
  private val mContextWrapper: ContextWrapper,
) {

  private lateinit var mUnityPlayerHolder: UnityPlayerHolder
  private lateinit var mUnityPlayerWrapper: UnityPlayerWrapper

  fun instantiate() {
    val unityPlayerInstanceManager = UnityPlayerInstanceManager.instance
    mUnityPlayerHolder = unityPlayerInstanceManager.createUnityPlayerHolder()
    onSetUnityPlayerHolder(mUnityPlayerHolder)
    if (unityPlayerInstanceManager.unityPlayerWrapperInstance == null) {
      // If null, register a UnityPlayerWrapper creation listener
      // and submit a request to create UnityPlayerWrapper
      // FIXME: possible situation when multiple engines are created before UnityPlayer is created?
      d("UnityPlayerWrapper == null, trying to create it")
      unityPlayerInstanceManager.registerUnityPlayerInstanceCreatedListener(
        object :
          IUnityPlayerInstanceCreatedListener {
          override fun onUnityPlayerInstanceCreated() {
            v("UnityPlayerWrapper creation event received")

            // Cache references
            mUnityPlayerWrapper = unityPlayerInstanceManager.unityPlayerWrapperInstance!!
            onSetUnityPlayerWrapper(mUnityPlayerWrapper)
            this@UnityPlayerWrapperInstantiator.onUnityPlayerInstanceCreated()
            unityPlayerInstanceManager.activeUnityPlayerHolder = mUnityPlayerHolder
          }
        },
      )

      // Attempt to create the UnityPlayer
      unityPlayerInstanceManager.requestCreateUnityPlayer(mContextWrapper)
    } else {
      mUnityPlayerWrapper = unityPlayerInstanceManager.unityPlayerWrapperInstance!!
      onSetUnityPlayerWrapper(mUnityPlayerWrapper)
      onAlreadyInstantiated()
      d("mUnityPlayerWrapper != null, starting right away")
    }
  }

  protected abstract fun isVisible(): Boolean
  protected abstract fun getSurfaceHolder(): SurfaceHolder?
  protected abstract fun onAlreadyInstantiated()
  protected abstract fun onUnityPlayerInstanceCreated()
  protected abstract fun onSetUnityPlayerWrapper(unityPlayerWrapper: UnityPlayerWrapper)
  protected abstract fun onSetUnityPlayerHolder(unityPlayerHolder: UnityPlayerHolder)
}
