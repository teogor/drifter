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

import android.view.SurfaceHolder

/**
 * An object that holds a lock on Unity player. Unity player's main loop is only active when at least
 * one `UnityPlayerHolder` is visible.
 */
class UnityPlayerHolder(
  private val mUnityPlayerPauseResumeManager: UnityPlayerPauseResumeManager,
) {
  var isVisible = false
    private set

  fun onVisibilityChanged(
    isVisible: Boolean,
    newSurfaceHolder: SurfaceHolder?,
  ): Boolean {
    this.isVisible = isVisible
    return mUnityPlayerPauseResumeManager.handleUnityPlayerHolderVisibilityChanged(
      this,
      newSurfaceHolder,
    )
  }

  fun unregister() {
    onVisibilityChanged(false, null)
    mUnityPlayerPauseResumeManager.unregisterUnityPlayerHolder(this)
  }
}
