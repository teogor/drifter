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
 * Marks a function as a custom encoder for a specific type during JSON serialization.
 *
 * This annotation allows you to define custom logic for converting objects of a specific
 * type into a format suitable for JSON. This is useful when the default serialization
 * behavior doesn't meet your needs or requires specific transformations.
 *
 * The annotated function takes an object of the specified type as input and returns a
 * JSON-compatible value.
 *
 * **Example Usage:**
 *
 * ```kotlin
 * @DrifterEncoder
 * fun Color.encodeFromColor(): Int = toArgb()
 *
 * // ... in a serialization method
 *
 * val json = JSONObject()
 * waterColor?.let { json.put("waterColor", it.encodeFromColor()) }
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
annotation class DrifterEncoder
