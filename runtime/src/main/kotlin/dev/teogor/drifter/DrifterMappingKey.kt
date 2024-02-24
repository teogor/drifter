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

package dev.teogor.drifter

/**
 * Specifies a key for mapping a property or parameter to a Unity method.
 *
 * @property storageKey The key used for storage and retrieval. If empty, defaults to the
 * property/parameter name in snake_case uppercase (e.g., "waterColor" -> "WATER_COLOR").
 * @property exposedMethod The name of the Unity method associated with this property/parameter.
 * **Example Usage:**
 *
 * ```kotlin
 * @DrifterMappingKey(
 *     storageKey = "WATER_COLOR",
 *     exposedMethod = "SetEditorColor"
 * )
 * val waterColor: Color? = null
 * ```
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
annotation class DrifterMappingKey(
  val storageKey: String = "",
  val exposedMethod: String = "",
)
