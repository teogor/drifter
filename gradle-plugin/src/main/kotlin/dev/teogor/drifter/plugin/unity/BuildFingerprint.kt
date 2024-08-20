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

import java.io.File
import java.io.IOException

/**
 * Data class to represent the build fingerprint information.
 *
 * @param unityVersion The Unity version used.
 * @param scriptingBackend The scripting backend used (e.g., IL2CPP).
 * @param buildType The build configuration (e.g., Release).
 * @param stripEngineCode Indicates if engine code is stripped.
 * @param optimizedFramePacing Indicates if frame pacing is optimized.
 */
data class BuildFingerprint(
  val unityVersion: UnityVersion,
  val scriptingBackend: String,
  val buildType: String,
  val stripEngineCode: Int,
  val optimizedFramePacing: Int,
)

/**
 * Reads and parses the build fingerprint information from a file.
 *
 * @param dirPath The directory path where the file is located.
 * @param filePath The path within the directory where the file is located.
 * @param fileName The name of the file to read.
 * @return A [BuildFingerprint] object containing the parsed data, or null if an error occurs.
 */
fun readBuildFingerprint(
  dirPath: String,
  filePath: String = "src/main/resources/META-INF",
  fileName: String = "com.android.games.engine.build_fingerprint",
): BuildFingerprint? {
  // Construct the full file path
  val fullFilePath = "$dirPath/$filePath/$fileName"

  // Attempt to access the file
  val file = runCatching { File(fullFilePath) }
    .getOrElse {
      return null
    }

  return try {
    // Read the file content
    val content = file.readText().trim()

    // Split the content by semicolons
    val parts = content.split(';')
      .dropLastWhile { it.isEmpty() }

    // Ensure we have the expected number of parts
    if (parts.size != 5) {
      println("Unexpected file format. Expected 5 parts but found ${parts.size}.")
      println(parts)
      return null
    }

    // Extract and validate each part
    val unityVersion = parts[0].takeIf { it.isNotBlank() }?.toUnityVersion() ?: run {
      println("Invalid Unity Version.")
      return null
    }

    val scriptingBackend = parts[1].takeIf { it.isNotBlank() } ?: run {
      println("Invalid Scripting Backend.")
      return null
    }

    val buildType = parts[2].takeIf { it.isNotBlank() } ?: run {
      println("Invalid Build Type.")
      return null
    }

    val stripEngineCode = parts[3].removePrefix("StripEngineCode:").toIntOrNull() ?: run {
      println("Invalid Strip Engine Code.")
      return null
    }

    val optimizedFramePacing = parts[4].removePrefix("OptimizedFramePacing:").toIntOrNull() ?: run {
      println("Invalid Optimized Frame Pacing.")
      return null
    }

    // Create and return the BuildFingerprint object
    BuildFingerprint(
      unityVersion = unityVersion,
      scriptingBackend = scriptingBackend,
      buildType = buildType,
      stripEngineCode = stripEngineCode,
      optimizedFramePacing = optimizedFramePacing,
    )
  } catch (e: IOException) {
    println("Error reading the build fingerprint file: ${e.message}")
    null
  } catch (e: NumberFormatException) {
    println("Error parsing the build fingerprint values: ${e.message}")
    null
  }
}
