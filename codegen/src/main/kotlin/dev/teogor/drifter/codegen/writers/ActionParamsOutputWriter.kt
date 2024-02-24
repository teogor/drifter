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
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import dev.teogor.drifter.codegen.commons.fileBuilder
import dev.teogor.drifter.codegen.commons.safe
import dev.teogor.drifter.codegen.commons.writeWith
import dev.teogor.drifter.codegen.facades.CodeOutputStreamMaker
import dev.teogor.drifter.codegen.model.CodeGenConfig
import dev.teogor.drifter.codegen.model.ConverterType
import dev.teogor.drifter.codegen.model.DrifterActionBridgeData
import dev.teogor.drifter.codegen.servicelocator.OutputWriter

class ActionParamsOutputWriter(
  private val codeOutputStreamMaker: CodeOutputStreamMaker,
  codeGenConfig: CodeGenConfig,
) : OutputWriter(codeGenConfig) {

  fun write(actionBridge: DrifterActionBridgeData, converters: List<ConverterType>): TypeName {
    val name = "${actionBridge.baseName}ActionParams"
    val actualType = ClassName(
      packageName = actionBridge.getPackageName(),
      name,
    )
    val jsonObject = ClassName(
      packageName = "org.json",
      "JSONObject",
    )
    fileBuilder(
      packageName = actionBridge.getPackageName(),
      fileName = name,
    ) {
      addType(
        TypeSpec.classBuilder(name)
          .addModifiers(KModifier.DATA)
          .addKdoc("Class to be used as parameter for when invoking unity native methods.")
          .apply {
            val flux = FunSpec.constructorBuilder()
            actionBridge.params.forEach {
              flux.addParameter(
                ParameterSpec.builder(it.name, it.type.copy(nullable = true))
                  .defaultValue("null")
                  .build(),
              )
              addProperty(
                PropertySpec.builder(it.name, it.type.copy(nullable = true))
                  .initializer(it.name)
                  .build(),
              )
            }
            primaryConstructor(flux.build())
          }
          .build(),
      )

      addFunction(
        FunSpec.builder("toJsonObject")
          .receiver(actualType)
          .returns(jsonObject)
          .addStatement("val json = %T()", jsonObject)
          .apply {
            actionBridge.params.forEach {
              val converter = converters.firstOrNull { converterType ->
                converterType.receiverType == it.type.safe
              }
              if (converter != null) {
                val converterType = ClassName(converter.packageName, converter.name)
                addStatement(
                  "${it.name}?.let { json.put(%S, it.%T()) }",
                  it.name,
                  converterType,
                )
              } else {
                addStatement("${it.name}?.let { json.put(%S, it) }", it.name)
              }
            }
          }
          .addStatement("return json")
          .build(),
      )
    }.writeWith(codeOutputStreamMaker)

    return ClassName(
      packageName = actionBridge.getPackageName(),
      name,
    )
  }
}
