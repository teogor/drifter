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

package dev.teogor.drifter.ksp.processors

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import dev.teogor.drifter.DrifterEncoder
import dev.teogor.drifter.DrifterMappingKey
import dev.teogor.drifter.DrifterModule
import dev.teogor.drifter.DrifterUnityMethod
import dev.teogor.drifter.codegen.CodeGenerator
import dev.teogor.drifter.codegen.facades.Logger
import dev.teogor.drifter.codegen.model.AdvancedMethodsData
import dev.teogor.drifter.codegen.model.BridgeKeyData
import dev.teogor.drifter.codegen.model.ConverterType
import dev.teogor.drifter.codegen.model.DrifterActionBridgeData
import dev.teogor.drifter.ksp.codegen.KspCodeOutputStreamMaker
import dev.teogor.drifter.ksp.codegen.KspLogger
import kotlin.reflect.KClass

class Processor(
  private val codeGenerator: KSPCodeGenerator,
  private val logger: KSPLogger,
  private val options: Map<String, String>,
) : SymbolProcessor {

  @OptIn(KspExperimental::class)
  override fun process(resolver: Resolver): List<KSAnnotated> {
    Logger.instance = KspLogger(logger)

    val annotatedDrifterBridges = resolver.getDrifterActionBridges()
    val annotatedDrifterConverters = resolver.getDrifterConverters()

    if (
      !annotatedDrifterBridges.iterator().hasNext() &&
      !annotatedDrifterConverters.iterator().hasNext()
    ) {
      return emptyList()
    }

    val sourceFiles = annotatedDrifterBridges.mapNotNull {
      it.containingFile
    } + annotatedDrifterConverters.mapNotNull {
      it.containingFile
    }

    val drifterActionBridges = annotatedDrifterBridges.map { kClass ->
      if (kClass.isDataClass) {
        val drifterModule = kClass.getAnnotationsByType(
          DrifterModule::class,
        ).first()
        val parameters = kClass.primaryConstructor!!.parameters.map { param ->
          val drifterMappingKey = param.getAnnotationsByType(DrifterMappingKey::class).firstOrNull()
          drifterMappingKey?.let {
            BridgeKeyData(
              name = param.name!!.asString(),
              keyName = it.storageKey,
              unityNativeMethod = it.exposedMethod,
              type = param.type.toTypeName(),
            )
          } ?: BridgeKeyData.NOT_PROVIDED.copy(
            name = param.name!!.asString(),
            type = param.type.toTypeName(),
          )
        }
        val ksTypeRef = kClass.annotations.first {
          it.annotationType.resolve().toClassName() == DrifterModule::class.asClassName()
        }.let { dab ->
          val type = dab.arguments.firstOrNull {
            it.name!!.asString() == "methods"
          }?.value as KSType
          type.declaration as KSClassDeclaration
        }
        val externalMethods = if (ksTypeRef.toClassName() != UNIT) {
          ksTypeRef.getDeclaredFunctions()
            .map {
              it to it.getAnnotationsByType(DrifterUnityMethod::class).firstOrNull()
            }
            .filterNot { it.second == null }
            .map { (kFun, param) ->
              val name = param!!.name
              AdvancedMethodsData(
                name = name,
                params = param.parameters.toList(),
              )
            }.toList()
        } else {
          null
        }
        DrifterActionBridgeData(
          name = drifterModule.name,
          receiverGameObject = drifterModule.receiver,
          externalMethods = externalMethods,
          simpleName = kClass.simpleName.asString(),
          packageName = kClass.packageName.asString(),
          params = parameters,
        )
      } else {
        Logger.instance.error("Class '${kClass.simpleName}' is not a data class.")
        DrifterActionBridgeData.INVALID
      }
    }.toList()

    val converters = annotatedDrifterConverters.filter {
      it.returnType != null &&
        it.extensionReceiver != null
    }.map { kFun ->
      val returnType = kFun.returnType!!.toTypeName()
      val receiverType = kFun.extensionReceiver!!.toTypeName()
      ConverterType(
        name = kFun.simpleName.asString(),
        packageName = kFun.packageName.asString(),
        returnType = returnType,
        receiverType = receiverType,
      )
    }.toList()

    CodeGenerator(
      codeOutputStreamMaker = KspCodeOutputStreamMaker(
        codeGenerator = codeGenerator,
        sourceMapper = KspToCodeGenDestinationsMapper(resolver),
      ),
      codeGenConfig = ConfigParser(options).parse(),
    ).generate(
      drifterActionBridges = drifterActionBridges,
      converters = converters,
    )

    return emptyList()
  }

  private fun Resolver.findAnnotations(
    kClass: KClass<*>,
  ) = getSymbolsWithAnnotation(
    kClass.qualifiedName.toString(),
  )

  private fun Resolver.getDrifterActionBridges(): Sequence<KSClassDeclaration> {
    return findAnnotations(DrifterModule::class).filterIsInstance<KSClassDeclaration>()
  }

  private fun Resolver.getDrifterConverters(): Sequence<KSFunctionDeclaration> {
    return findAnnotations(DrifterEncoder::class).filterIsInstance<KSFunctionDeclaration>()
  }
}

val KSClassDeclaration.isDataClass: Boolean
  get() = classKind == ClassKind.CLASS && modifiers.contains(Modifier.DATA)

typealias KSPCodeGenerator = com.google.devtools.ksp.processing.CodeGenerator
