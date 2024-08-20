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

package dev.teogor.drifter.integration.utilities

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import dev.teogor.drifter.integration.utilities.DebugLog.e

/**
 * Stores information about capabilities of Unity player from different Unity versions.
 */
class UnityVersionInfo private constructor(
  unityVersion: String?,
) {

  /**
   * Since Unity 4.0.2, calling `new UnityPlayer()` with non-Activity Context as an argument
   * resulted in an immediate crash. This bug was fixed in Unity 5.3.6.
   *
   * @return
   */
  val isUnityNonActivityConstructorBugFixed: Boolean
  val mHasApi23PermissionRequestSupport: Boolean
  val isUnity540orNewer: Boolean
  val isUnity550orNewer: Boolean

  init {
    isUnityNonActivityConstructorBugFixed = versionCompare(
      unityVersion, "5.3.6",
    ) >= 0
    mHasApi23PermissionRequestSupport = versionCompare(unityVersion, "5.2.4") >= 0
    isUnity540orNewer = versionCompare(unityVersion, "5.4.0") >= 0
    isUnity550orNewer = versionCompare(unityVersion, "5.5.0") >= 0
  }

  /**
   * Since Unity 5.2.4, on API23+ Unity shows a permission request dialog.
   *
   * @return
   */
  fun hasApi23PermissionRequestSupport(): Boolean {
    return mHasApi23PermissionRequestSupport
  }

  companion object {
    var instance: UnityVersionInfo? = null
      private set

    @JvmStatic
    fun updateUnityVersionIfNeeded(context: Context) {
      if (instance != null) return

      // Unity version string is contained in <meta-data> inside <application>.
      val metaDataName = "unity.version"
      val packageManager = context.packageManager
      val applicationInfo: ApplicationInfo = try {
        packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
      } catch (e: PackageManager.NameNotFoundException) {
        e("UnityVersionInfo: Unable to retrieve ApplicationInfo. This should not ever happen.")
        return
      }
      if (applicationInfo.metaData != null && applicationInfo.metaData.containsKey(
          metaDataName,
        )
      ) {
        try {
          val unityVersion = applicationInfo.metaData[metaDataName] as String?
          instance = UnityVersionInfo(unityVersion)
          return
        } catch (th: Throwable) {
          th.printStackTrace()
        }
      }
      val minimalUnityVersion = "5.1.3"
      e(
        "No <meta-data> tag with name 'LiveWallpaper.UnityVersion' found, assuming Unity $minimalUnityVersion",
      )

      // No <meta-data> element was found, fall back to lowest supported Unity version
      instance = UnityVersionInfo(minimalUnityVersion)
    }

    /**
     * Compares two version strings.
     *
     *
     * Use this instead of String.compareTo() for a non-lexicographical
     * comparison that works for version strings. e.g. "1.10".compareTo("1.6").
     *
     * @param str1 a string of ordinal numbers separated by decimal points.
     * @param str2 a string of ordinal numbers separated by decimal points.
     * @return The result is a negative integer if str1 is _numerically_ less than str2.
     * The result is a positive integer if str1 is _numerically_ greater than str2.
     * The result is zero if the strings are _numerically_ equal.
     * @note It does not work if "1.10" is supposed to be equal to "1.10.0".
     */
    private fun versionCompare(str1: String?, str2: String): Int {
      val vals1 = str1!!.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
      val vals2 = str2.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
      var i = 0
      // set index to first non-equal ordinal or length of shortest version string
      while (i < vals1.size && i < vals2.size && vals1[i] == vals2[i]) {
        i++
      }
      // compare first non-equal ordinal number
      if (i < vals1.size && i < vals2.size) {
        val diff = Integer.valueOf(vals1[i]).compareTo(
          Integer.valueOf(
            vals2[i],
          ),
        )
        return Integer.signum(diff)
      }
      // the strings are equal or one string is a substring of the other
      // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
      return Integer.signum(vals1.size - vals2.size)
    }

    /**
     * Sets current instance to the one with new Unity version string.
     *
     * @param unityVersion Unity version string.
     */
    private fun setUnityVersion(unityVersion: String) {
      instance = UnityVersionInfo(unityVersion)
    }
  }
}
