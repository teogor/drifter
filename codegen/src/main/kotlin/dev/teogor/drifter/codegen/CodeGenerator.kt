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

@file:Suppress("ObjectPropertyName")

package dev.teogor.drifter.codegen

import dev.teogor.drifter.codegen.facades.CodeOutputStreamMaker
import dev.teogor.drifter.codegen.model.CodeGenConfig
import dev.teogor.drifter.codegen.model.ConverterType
import dev.teogor.drifter.codegen.model.DrifterActionBridgeData
import dev.teogor.drifter.codegen.servicelocator.ServiceLocatorAccessor
import dev.teogor.drifter.codegen.servicelocator.actionMappingsOutputWriter
import dev.teogor.drifter.codegen.servicelocator.actionParamsOutputWriter
import dev.teogor.drifter.codegen.servicelocator.keyConstantsOutputWriter
import dev.teogor.drifter.codegen.servicelocator.unityMessageSenderOutputWriter

class CodeGenerator(
  override val codeOutputStreamMaker: CodeOutputStreamMaker,
  override val codeGenConfig: CodeGenConfig,
) : ServiceLocatorAccessor {

  fun generate(
    drifterActionBridges: List<DrifterActionBridgeData>,
    converters: List<ConverterType>,
  ) {
    drifterActionBridges.filterNot {
      it.isError
    }.forEach { drifterActionBridge ->
      keyConstantsOutputWriter.write(drifterActionBridge)
      val actionMappings = actionMappingsOutputWriter.write(drifterActionBridge)
      val actionParams = actionParamsOutputWriter.write(
        drifterActionBridge,
        converters,
      )
      if (drifterActionBridge.receiverGameObject.isNotEmpty()) {
        unityMessageSenderOutputWriter.write(
          drifterActionBridge,
          actionParams,
          actionMappings,
        )
      }
    }
  }
}
