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

package dev.teogor.drifter.codegen.writers

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import dev.teogor.drifter.codegen.commons.fileBuilder
import dev.teogor.drifter.codegen.commons.writeWith
import dev.teogor.drifter.codegen.facades.CodeOutputStreamMaker
import dev.teogor.drifter.codegen.model.CodeGenConfig
import dev.teogor.drifter.codegen.model.DrifterActionBridgeData
import dev.teogor.drifter.codegen.servicelocator.OutputWriter

class KeyConstantsOutputWriter(
  private val codeOutputStreamMaker: CodeOutputStreamMaker,
  codeGenConfig: CodeGenConfig,
) : OutputWriter(codeGenConfig) {

  fun write(actionBridge: DrifterActionBridgeData): TypeName {
    val name = "${actionBridge.baseName}KeyConstants"
    fileBuilder(
      packageName = actionBridge.getPackageName(),
      fileName = name,
    ) {
      addType(
        TypeSpec.objectBuilder(name)
          .addKdoc(
            "Used to change UnityPlayerPrefs. Recommended to use together with [UnityPlayerPrefs]",
          )
          .apply {
            actionBridge.params.forEach {
              addProperty(
                PropertySpec.builder(it.storageKeyName, String::class)
                  .addModifiers(KModifier.PUBLIC)
                  .initializer("%S", it.storageKeyValue)
                  .build(),
              )
            }
          }
          .build(),
      )
    }.writeWith(codeOutputStreamMaker)

    return ClassName(
      packageName = actionBridge.getPackageName(),
      name,
    )
  }
}
