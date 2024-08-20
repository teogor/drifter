/*
 * Copyright 2023 teogor (Teodor Grigor)
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

package dev.teogor.drifter.integration.utilities

import android.content.Context
import dalvik.system.DexFile
import dalvik.system.PathClassLoader
import java.io.IOException
import java.lang.reflect.Field

/**
 * Collection of Reflection-related utility methods.
 */
@Suppress("unused")
internal object ReflectionUtility {
  private lateinit var sPathClassLoaderDexField: Field
  private lateinit var sBaseDexClassLoaderPathListField: Field
  private lateinit var sDexPathListDexElementsField: Field
  private lateinit var sDexPathListElementDexFileField: Field

  init {
    try {
      sPathClassLoaderDexField = PathClassLoader::class.java.getDeclaredField("mDexs")
      sPathClassLoaderDexField.isAccessible = true
    } catch (ignored: NoSuchFieldException) {
    }
    try {
      sBaseDexClassLoaderPathListField = PathClassLoader::class.java.superclass
        .getDeclaredField("pathList")
      sBaseDexClassLoaderPathListField.isAccessible = true
      val sDexPathListClass = sBaseDexClassLoaderPathListField.type
      sDexPathListDexElementsField = sDexPathListClass.getDeclaredField("dexElements")
      sDexPathListDexElementsField.isAccessible = true
      val sDexPathListElementClass = sDexPathListDexElementsField.type
        .componentType
      if (sDexPathListElementClass != null) {
        sDexPathListElementDexFileField = sDexPathListElementClass
          .getDeclaredField("dexFile")
        sDexPathListElementDexFileField.isAccessible = true
      }
    } catch (ignored: NoSuchFieldException) {
    }
  }

  private val dexFiles: HashSet<DexFile>
    get() {
      val dexFiles = HashSet<DexFile>()
      val classLoader = Thread.currentThread().contextClassLoader as PathClassLoader
      try {
        @Suppress("UNCHECKED_CAST")
        val pathClassLoaderDexs = sPathClassLoaderDexField[classLoader]
          as? Array<DexFile> ?: emptyArray()
        dexFiles.addAll(listOf(*pathClassLoaderDexs))
      } catch (ignored: IllegalAccessException) {
      }
      try {
        val dexClassLoaderPathList = sBaseDexClassLoaderPathListField[classLoader]
        val elementsArray = sDexPathListDexElementsField[dexClassLoaderPathList]
        if (elementsArray != null) {
          val elementsArrayLength = java.lang.reflect.Array.getLength(elementsArray)
          for (i in 0 until elementsArrayLength) {
            val elementsArrayElement = java.lang.reflect.Array.get(elementsArray, i)
            val dexFile =
              sDexPathListElementDexFileField[elementsArrayElement] as DexFile
            dexFiles.add(dexFile)
          }
        }
      } catch (ignored: IllegalAccessException) {
      }
      return dexFiles
    }

  /**
   * Returns classes inside the `packageName` package.
   *
   * @param context     Application context.
   * @param packageName Name of the package.
   * @return Classes inside the `packageName` package.
   */
  @JvmStatic
  fun getClassesOfPackage(context: Context, packageName: String?): Array<Class<*>> {
    val dexFiles = dexFiles
    try {
      val packageCodePath = context.packageCodePath

      @Suppress("DEPRECATION")
      val df = DexFile(packageCodePath)
      dexFiles.add(df)
    } catch (e: IOException) {
      e.printStackTrace()
    }
    val classes = HashSet<Class<*>>()
    for (df in dexFiles) {
      @Suppress("DEPRECATION")
      val iterator = df.entries()
      while (iterator.hasMoreElements()) {
        val className = iterator.nextElement()
        if (className.startsWith(packageName!!)) {
          try {
            classes.add(Class.forName(className))
          } catch (e: ClassNotFoundException) {
            // Ignored
          }
        }
      }
    }
    return classes.toTypedArray<Class<*>>()
  }
}
