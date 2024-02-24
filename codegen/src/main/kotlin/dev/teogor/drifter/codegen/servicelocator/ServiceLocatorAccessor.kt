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

package dev.teogor.drifter.codegen.servicelocator

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import dev.teogor.drifter.codegen.facades.CodeOutputStreamMaker
import dev.teogor.drifter.codegen.model.CodeGenConfig
import dev.teogor.drifter.codegen.model.DrifterActionBridgeData
import dev.teogor.drifter.codegen.writers.ActionMappingsOutputWriter
import dev.teogor.drifter.codegen.writers.ActionParamsOutputWriter
import dev.teogor.drifter.codegen.writers.KeyConstantsOutputWriter
import dev.teogor.drifter.codegen.writers.UnityMessageSenderOutputWriter

internal interface ServiceLocatorAccessor {
  val codeOutputStreamMaker: CodeOutputStreamMaker
  val codeGenConfig: CodeGenConfig
}

abstract class OutputWriter(
  private val codeGenConfig: CodeGenConfig,
) {

  fun DrifterActionBridgeData.getPackageName() = codeGenConfig.generatedPackageName ?: packageName

  fun FunSpec.Builder.addDocumentation(
    format: String,
    vararg args: Any,
  ) = this.apply {
    if (codeGenConfig.addDocumentation) {
      addKdoc(format, args)
    }
  }

  fun FunSpec.Builder.addDocumentation(
    block: CodeBlock,
  ) = this.apply {
    if (codeGenConfig.addDocumentation) {
      addKdoc(block)
    }
  }

  fun TypeSpec.Builder.addDocumentation(
    format: String,
    vararg args: Any,
  ) = this.apply {
    if (codeGenConfig.addDocumentation) {
      addKdoc(format, args)
    }
  }

  fun TypeSpec.Builder.addDocumentation(
    block: CodeBlock,
  ) = this.apply {
    if (codeGenConfig.addDocumentation) {
      addKdoc(block)
    }
  }
}

internal val ServiceLocatorAccessor.keyConstantsOutputWriter
  get() = KeyConstantsOutputWriter(
    codeOutputStreamMaker,
    codeGenConfig,
  )

internal val ServiceLocatorAccessor.actionMappingsOutputWriter
  get() = ActionMappingsOutputWriter(
    codeOutputStreamMaker,
    codeGenConfig,
  )

internal val ServiceLocatorAccessor.actionParamsOutputWriter
  get() = ActionParamsOutputWriter(
    codeOutputStreamMaker,
    codeGenConfig,
  )

internal val ServiceLocatorAccessor.unityMessageSenderOutputWriter
  get() = UnityMessageSenderOutputWriter(
    codeOutputStreamMaker,
    codeGenConfig,
  )
