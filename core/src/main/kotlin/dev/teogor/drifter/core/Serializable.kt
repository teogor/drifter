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

package dev.teogor.drifter.core

/**
 * A class that encapsulates serialization and deserialization logic for a specific type.
 *
 * This class provides a convenient way to store and retrieve objects in a serialized format.
 * It hides the implementation details of serialization and deserialization behind private
 * methods, while offering public methods for basic usage.
 *
 * @param T The type of object this [Serializable] instance handles.
 * @param encodeToString A function that converts an object of type [T] to a string
 * representation.
 * @param decodeFromString A function that converts a string representation back to an object
 * of type [T].
 */
class Serializable<T>(
  private val encodeToString: (T) -> String,
  private val decodeFromString: (String) -> T,
) {

  /**
   * Internal implementation of the [encodeToString] function.
   *
   * This method is not intended for direct use, but rather used internally by other parts
   * of the class.
   *
   * @param value The object to serialize.
   * @return The serialized string representation of the object.
   */
  internal fun encodeToStringImpl(value: T): String = encodeToString(value)

  /**
   * Internal implementation of the [decodeFromString] function.
   *
   * This method is not intended for direct use, but rather used internally by other parts
   * of the class.
   *
   * @param string The serialized string representation of the object.
   * @return The deserialized object of type [T].
   */
  internal fun decodeFromStringImpl(string: String): T = decodeFromString(string)
}

/**
 * Creates a new [Serializable] instance for the given type with the provided serialization
 * and deserialization functions.
 *
 * This inline function avoids unnecessary function call overhead and offers a concise way
 * to create [Serializable] instances.
 *
 * @param T The type of object this [Serializable] instance handles.
 * @param encodeToString A function that converts an object of type [T] to a string
 * representation.
 * @param decodeFromString A function that converts a string representation back to an object
 * of type [T].
 * @return A new [Serializable] instance for the given type.
 */
inline fun <reified T> createSerializable(
  noinline encodeToString: (T) -> String,
  noinline decodeFromString: (String) -> T,
): Serializable<T> = Serializable(encodeToString, decodeFromString)
