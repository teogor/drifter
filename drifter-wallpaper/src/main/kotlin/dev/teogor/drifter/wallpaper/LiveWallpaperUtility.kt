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

package dev.teogor.drifter.wallpaper

import android.app.WallpaperManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import dev.teogor.drifter.integration.utilities.DebugLog.d
import dev.teogor.drifter.integration.utilities.DebugLog.e

/**
 * Various useful live wallpaper related methods.
 */
object LiveWallpaperUtility {
  /**
   * Opens the wallpaper preview screen, if possible.
   *
   * @param context
   * @return true on success, false on error.
   */
  fun openWallpaperPreview(context: Context): Boolean {
    val componentName = getWallpaperServiceComponentName(context)
    if (componentName == null) {
      e("No wallpaper service found")
      return false
    }
    d("Starting live wallpaper preview screen for " + componentName.className)
    return openWallpaperPreview(context, componentName)
  }

  /**
   * Opens the wallpaper preview screen, if possible.
   *
   * @param context
   * @param componentName The live wallpaper service component name.
   * @return true on success, false on error.
   */
  fun openWallpaperPreview(context: Context, componentName: ComponentName?): Boolean {
    var intent: Intent
    try {
      intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, componentName)
      context.startActivity(intent)
      return true
    } catch (ignored: ActivityNotFoundException) {
      d("Unable to open live wallpaper preview using Jelly Bean method, using fallback")
      /*
      Happens at least on Samsung Galaxy S7:
      java.lang.RuntimeException: Unable to start activity ComponentInfo{...}: android.content.ActivityNotFoundException: No Activity found to handle Intent
      { act=android.service.wallpaper.CHANGE_LIVE_WALLPAPER ...
       */
    }
    try {
      // Generic Android wallpaper chooser
      intent = Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)
      context.startActivity(intent)
      return true
    } catch (ignored: ActivityNotFoundException) {
      d("Unable to open wallpaper preview using generic live wallpaper chooser, using fallback")
    }
    return false
  }

  /**
   * Searches for the first live wallpaper service and returns its `ServiceInfo`.
   *
   * @param context
   * @return `ServiceInfo` of wallpaper service on success, null on error.
   */
  fun getWallpaperServiceInfo(context: Context): ServiceInfo? {
    try {
      val packageInfo = context
        .packageManager
        .getPackageInfo(
          context.packageName,
          PackageManager.GET_SERVICES or PackageManager.GET_META_DATA or PackageManager.GET_INTENT_FILTERS,
        )
      val services = packageInfo.services
      for (service in services) {
        if (service.metaData != null && service.metaData.containsKey("android.service.wallpaper")) return service
      }
    } catch (ignored: PackageManager.NameNotFoundException) {
    }
    return null
  }

  private fun getWallpaperServiceComponentName(context: Context): ComponentName? {
    val wallpaperService = getWallpaperServiceInfo(context) ?: return null
    return ComponentName(wallpaperService.packageName, wallpaperService.name)
  }
}
