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
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import dev.teogor.drifter.codegen.commons.UnityMessageSender
import dev.teogor.drifter.codegen.commons.fileBuilder
import dev.teogor.drifter.codegen.commons.safe
import dev.teogor.drifter.codegen.commons.toTitleCase
import dev.teogor.drifter.codegen.commons.writeWith
import dev.teogor.drifter.codegen.facades.CodeOutputStreamMaker
import dev.teogor.drifter.codegen.model.CodeGenConfig
import dev.teogor.drifter.codegen.model.DrifterActionBridgeData
import dev.teogor.drifter.codegen.servicelocator.OutputWriter

class UnityMessageSenderOutputWriter(
  private val codeOutputStreamMaker: CodeOutputStreamMaker,
  codeGenConfig: CodeGenConfig,
) : OutputWriter(codeGenConfig) {

  fun write(
    actionBridge: DrifterActionBridgeData,
    actionParams: TypeName,
    actionMappings: TypeName,
  ): TypeName {
    val name = "${actionBridge.baseName}MessageSender"
    fileBuilder(
      packageName = actionBridge.getPackageName(),
      fileName = name,
    ) {
      addType(
        TypeSpec.classBuilder(name)
          .superclass(UnityMessageSender)
          .addSuperclassConstructorParameter(
            "receiver = %S",
            actionBridge.receiverGameObject,
          )
          .apply {
            actionBridge.params.forEach { param ->
              addFunction(
                FunSpec.builder(param.actualUnityNativeMethod.toTitleCase(true))
                  .addCode(
                    CodeBlock.builder()
                      .apply {
                        addStatement("sendMessage(")
                        indent()
                        addStatement("%T.${param.name},", actionMappings)
                        addStatement(
                          "%T(",
                          actionParams,
                        )
                        indent()
                        addStatement("${param.name} = ${param.name},")
                        unindent()
                        addStatement(
                          ").toJsonObject(),",
                        )
                        unindent()
                        addStatement(")")
                      }
                      .build(),
                  )
                  .addParameter(
                    ParameterSpec.builder(param.name, param.type.safe)
                      .build(),
                  )
                  .build(),
              )
            }
            actionBridge.externalMethods?.let { methods ->
              methods.forEach { method ->
                addFunction(
                  FunSpec.builder(method.name.toTitleCase(true))
                    .addCode(
                      CodeBlock.builder()
                        .apply {
                          addStatement("sendMessage(")
                          indent()
                          addStatement("%T.${method.name},", actionMappings)
                          addStatement(
                            "%T(",
                            actionParams,
                          )
                          indent()
                          method.params.forEach { param ->
                            addStatement("$param = $param,")
                          }
                          unindent()
                          addStatement(
                            ").toJsonObject(),",
                          )
                          unindent()
                          addStatement(")")
                        }
                        .build(),
                    )
                    .apply {
                      method.params.forEach { param ->
                        val type = actionBridge.params.firstOrNull {
                          it.name == param
                        } ?: error("Please provide a valid name: $param")
                        addParameter(
                          ParameterSpec.builder(type.name, type.type.safe)
                            .build(),
                        )
                      }
                    }
                    .build(),
                )
              }
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
