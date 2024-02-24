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

import kotlin.reflect.KClass

/**
 * Marks a class as a Drifter module, defining its functionality and integration points.
 *
 * This annotation associates a class with a specific Unity GameObject and exposes its methods
 * for communication and data exchange with the Unity environment.
 *
 * @property name The name of the module, used for identification and logging.
 * @property receiver The name of the Unity GameObject this module interacts with.
 * @property methods The Kotlin class representing the interface containing exposed methods.
 * Defaults to [Unit] if no methods are explicitly defined.
 *
 * **Example Usage:**
 *
 * ```kotlin
 * @DrifterModule(
 *     name = "Aquarium",
 *     receiver = "BridgeController",
 *     methods = AquariumMethods::class
 * )
 * data class AquariumConfig(
 *     val isEditorMode: Boolean? = null,
 *     val waterColor: Color? = null,
 *     // ... other properties
 * )
 *
 * interface AquariumMethods {
 *     @DrifterUnityMethod
 *     fun setEditorMode()
 *     // ... other methods
 * }
 * ```
 */
@Target(AnnotationTarget.CLASS)
annotation class DrifterModule(
  val name: String = "",
  val receiver: String = "",
  val methods: KClass<*> = Unit::class,
)
