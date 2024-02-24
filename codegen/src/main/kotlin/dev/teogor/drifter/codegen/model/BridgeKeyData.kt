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

package dev.teogor.drifter.codegen.model

import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.UNIT
import dev.teogor.drifter.codegen.commons.toSnakeCase
import dev.teogor.drifter.codegen.commons.toTitleCase

data class BridgeKeyData(
  val name: String,
  val keyName: String,
  val unityNativeMethod: String,
  val type: TypeName,
) {

  val storageKeyName: String
    get() = keyName.ifEmpty {
      name.toSnakeCase()
    }

  val storageKeyValue: String
    get() = keyName.ifEmpty {
      name.toSnakeCase()
    }

  val actualUnityNativeMethod: String
    get() = unityNativeMethod.ifEmpty {
      name.toTitleCase()
    }

  companion object {
    val NOT_PROVIDED = BridgeKeyData(
      name = "",
      keyName = "",
      unityNativeMethod = "",
      type = UNIT,
    )
  }
}
