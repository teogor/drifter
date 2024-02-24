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
 * Marks a function as a method exposed to the Unity environment.
 *
 * This annotation allows you to declare functions within your Kotlin code that can be
 * accessed and called from Unity scripts. It specifies the name of the exposed method
 * in Unity and the names of its parameters.
 *
 * @property name The name of the exposed method in Unity. Defaults to the function name with
 * first letter uppercase.
 * @property parameters An array of parameter names expected by the exposed method in Unity.
 *
 * **Example Usage:**
 *
 * ```kotlin
 * @DrifterUnityMethod(
 *     name = "SetEditorMode",
 *     parameters = ["isEditorMode", "animated"]
 * )
 * fun setEditorMode()
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
annotation class DrifterUnityMethod(
  val name: String = "",
  val parameters: Array<String> = [],
)
