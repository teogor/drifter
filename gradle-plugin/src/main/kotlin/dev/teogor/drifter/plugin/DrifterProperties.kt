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

@file:OptIn(InternalDrifterApi::class)

package dev.teogor.drifter.plugin

import org.gradle.api.Project

/**
 * Retrieves a property from gradle.properties and converts it to the specified type.
 *
 * @param key The property key to retrieve.
 * @param defaultValue The default value to return if the property is not found or has
 * an incompatible type.
 * @throws IllegalArgumentException if the property is not found or has an incompatible
 * type.
 */
inline fun <reified T> Project.getProperty(key: String, defaultValue: T? = null): T? {
  val value = findProperty(key)?.toString()
  return when {
    value != null && T::class.java.isAssignableFrom(value.javaClass) -> value as T
    defaultValue != null -> defaultValue
    else -> throw IllegalArgumentException("Property '$key' not found or has incompatible type")
  }
}

/**
 * Getter for the `drifter.unity.path.ndk` property from Gradle properties.
 */
@InternalDrifterApi
val Project.drifterUnityPathNdk: String?
  get() = getProperty("drifter.unity.path.ndk")

/**
 * Retrieves the Drifter Unity NDK path from Gradle properties.
 *
 * @return The Drifter Unity NDK path.
 */
@OptIn(InternalDrifterApi::class)
fun Project.getSafeDrifterUnityPathNdk(): String {
  return drifterUnityPathNdk ?: error(
    "Please provide the required property 'drifter.unity.path.ndk' in gradle.properties",
  )
}

/**
 * Getter for the `drifter.unity.path.export` property from Gradle properties.
 */
@InternalDrifterApi
val Project.drifterUnityPathExport: String?
  get() = getProperty("drifter.unity.path.export")

/**
 * Retrieves the Drifter Unity export path from Gradle properties.
 *
 * @return The Drifter Unity export path.
 */
@OptIn(InternalDrifterApi::class)
fun Project.getSafeDrifterUnityPathExport(): String {
  return drifterUnityPathExport ?: error(
    "Please provide the required property 'drifter.unity.path.export' in gradle.properties",
  )
}
