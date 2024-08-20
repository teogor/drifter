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

package dev.teogor.drifter.plugin.unity

/**
 * Data class to represent the Unity version.
 *
 * This class encapsulates the components of a Unity version, which typically includes
 * the year, major version, minor version, and an optional patch version suffix.
 *
 * @param year The year of the Unity version, represented as a 4-digit integer.
 * @param major The major version number, represented as an integer.
 * @param minor The minor version number, represented as an integer.
 * @param patch The patch version with optional suffix, represented as a string.
 */
data class UnityVersion(
  val year: Int,
  val major: Int,
  val minor: Int,
  val patch: String,
) {

  /**
   * Converts the [UnityVersion] object to a string in the format "YYYY.M.mfX".
   *
   * @return The Unity version string representation.
   */
  override fun toString(): String {
    return "v$year.$major.$minor$patch"
  }

  companion object {
    /**
     * Parses a Unity version string into a [UnityVersion] object.
     *
     * This method takes a Unity version string in the format "YYYY.M.mX", where:
     * - `YYYY` is the 4-digit year,
     * - `M` is the major version number,
     * - `m` is the minor version number,
     * - `X` is an optional patch suffix (can be letters and/or numbers).
     *
     * @param versionString The Unity version string to be parsed, e.g., "2022.3.7f1".
     * @return A [UnityVersion] object if the format is valid, or null if parsing fails.
     */
    fun parseString(versionString: String): UnityVersion? {
      // Define a regular expression to match the Unity version format
      val regex = """(\d{4})\.(\d+)\.(\d+)([a-zA-Z0-9]*)""".toRegex()

      return regex.matchEntire(versionString)?.let { matchResult ->
        // Extract components from the match
        val (yearStr, majorStr, minorStr, patchStr) = matchResult.destructured

        // Parse components into integers and strings
        val year = yearStr.toIntOrNull() ?: return null
        val major = majorStr.toIntOrNull() ?: return null
        val minor = minorStr.toIntOrNull() ?: return null

        // Return the UnityVersion object
        UnityVersion(
          year = year,
          major = major,
          minor = minor,
          patch = patchStr,
        )
      } ?: run {
        // Return null if the version string does not match the expected format
        null
      }
    }
  }
}

/**
 * Extension function to convert a string into a [UnityVersion] object.
 *
 * This function is a convenient wrapper around the [UnityVersion.parseString] method
 * and allows for a more fluent API when converting strings to UnityVersion instances.
 *
 * @return A [UnityVersion] object if the string is a valid Unity version format, or null if parsing fails.
 */
fun String.toUnityVersion(): UnityVersion? {
  return UnityVersion.parseString(this)
}
