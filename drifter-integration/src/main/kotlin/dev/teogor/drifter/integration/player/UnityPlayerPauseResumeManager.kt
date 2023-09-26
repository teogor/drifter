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
import dev.teogor.drifter.integration.utilities.DebugLog.v

/**
 * Stores list of `UnityPlayerHolder ` and pauses/resumes the Unity player.
 * Unity player's main loop is only active when at least one `UnityPlayerHolder` is visible.
 */
class UnityPlayerPauseResumeManager {
  /**
   * Contains `UnityPlayerHolder ` instances.
   */
  private val mUnityPlayerHolders: MutableSet<UnityPlayerHolder> = HashSet()

  /**
   * Managed `UnityPlayerWrapper`
   */
  private var mUnityPlayerWrapper: UnityPlayerWrapper? = null

  /**
   * Number of visible `UnityPlayerHolder` at the end of last `handleUnityPlayerHolderVisibilityChanged` call.
   */
  private var mVisibleHoldersCountPrev = 0
  /**
   * @return Whether Unity Player is currently paused.
   */
  /**
   * Whether Unity Player is currently paused.
   */
  var isUnityPlayerPaused = false
    private set

  fun setUnityPlayerWrapper(unityPlayerWrapper: UnityPlayerWrapper?) {
    mUnityPlayerWrapper = unityPlayerWrapper
  }

  /**
   * Must be called when `UnityPlayerHolder` changes its visibility state.
   *
   * @param unityPlayerHolder `UnityPlayerHolder` that has changed its state
   * @param newSurfaceHolder  Can be null.
   * @return Whether the Unity player has actually changed .
   */
  fun handleUnityPlayerHolderVisibilityChanged(
    unityPlayerHolder: UnityPlayerHolder,
    newSurfaceHolder: SurfaceHolder?,
  ): Boolean {
    // Add the {@code UnityPlayerHolder} to the list
    mUnityPlayerHolders.add(unityPlayerHolder)
    val visibleHoldersCount = visibleUnityPlayerHoldersCount
    if (mUnityPlayerWrapper == null) {
      mVisibleHoldersCountPrev = visibleHoldersCount
      return false
    }
    v("UnityPlayerPauseResumeManager: visibleHoldersCount: $visibleHoldersCount")
    var isChangesApplied = false

    // TODO: improve case when surface updated while Unity has not yet initialized the context
    // if (surfaceHolder != null && mSurfaceHolder != surfaceHolder) {
    if (newSurfaceHolder != null) {
      mUnityPlayerWrapper!!.setPlayerSurface(newSurfaceHolder)
    }

    // Only change the actual state when needed
    if (visibleHoldersCount != mVisibleHoldersCountPrev) {
      isChangesApplied = true
      isUnityPlayerPaused = visibleHoldersCount <= 0
      if (!isUnityPlayerPaused) {
        mUnityPlayerWrapper!!.resumePlayer()
      } else {
        mUnityPlayerWrapper!!.pausePlayer()
      }
    }
    mVisibleHoldersCountPrev = visibleHoldersCount
    return isChangesApplied
  }

  /**
   * Must be called when wallpaper engine is destroyed to correctly handle its destruction.
   *
   * @param unityPlayerHolder
   */
  fun unregisterUnityPlayerHolder(unityPlayerHolder: UnityPlayerHolder) {
    mUnityPlayerHolders.remove(unityPlayerHolder)
  }

  val visibleUnityPlayerHoldersCount: Int
    /**
     * @return Number of currently visible `UnityPlayerHolder`.
     */
    get() {
      var count = 0
      for (entry in mUnityPlayerHolders) {
        if (entry.isVisible) count++
      }
      return count
    }
  val unityPlayerHoldersCount: Int
    /**
     * @return Number of currently registered `UnityPlayerHolder`.
     */
    get() = mUnityPlayerHolders.size
}
