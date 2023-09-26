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

package dev.teogor.drifter.wallpaper.activities

import android.app.Activity
import android.content.ContextWrapper
import android.os.Bundle
import dev.teogor.drifter.integration.player.UnityPlayerInstanceManager
import dev.teogor.drifter.wallpaper.LiveWallpaperUtility

/**
 * An invisible Activity that opens the wallpaper preview app for current wallpaper,
 * and then closes itself.
 */
class StartWallpaperPreviewActivity : Activity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val unityPlayerInstanceManager = UnityPlayerInstanceManager.instance
    val unityPlayerContext: ContextWrapper = this.application
    unityPlayerInstanceManager.createUnityPlayerDirectly(unityPlayerContext)
    LiveWallpaperUtility.openWallpaperPreview(this)
    finish()
  }
}
