/**
 * Copyright (c) 2024 Dylan Sperrer - dylan@sperrer.ca
 * The project is Licensed under GPL-3.0
 */

package dev.neuralnexus.scifi

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import java.io.File
import java.util.Locale

val gson: Gson = GsonBuilder().setPrettyPrinting().create()

// Unzips a file and returns the contents in memory, keeping the directory structure
fun unzipFile(name: String): List<File> {
  val files = mutableListOf<File>()
  val zipFile = File(name)
  val destDir = File("." + File.separator + ".tmp" + File.separator + zipFile.nameWithoutExtension)
  destDir.mkdir()
  val buffer = ByteArray(1024)
  val zip = java.util.zip.ZipFile(zipFile)
  val entries = zip.entries()
  while (entries.hasMoreElements()) {
    val entry = entries.nextElement()
    val entryFile = File(destDir, entry.name)
    if (entry.isDirectory) {
      entryFile.mkdirs()
    } else {
      entryFile.parentFile.mkdirs()
      val out = entryFile.outputStream()
      val `in` = zip.getInputStream(entry)
      var len: Int
      while (`in`.read(buffer).also { len = it } > 0) {
        out.write(buffer, 0, len)
      }
      out.close()
      `in`.close()
      files.add(entryFile)
    }
  }
  return files
}

// Finds all .mcworld files recursively in a directory and unzips them
fun unzipAllMcWorldFiles(string: String): List<File> {
  println("Unzipping all .mcworld files in $string")
  val files = mutableListOf<File>()
  File(string).walkTopDown().forEach {
    if (it.extension == "mcworld") {
      println("Unzipping ${it.absolutePath}")
      files.addAll(unzipFile(it.absolutePath))
    }
  }
  return files
}

// Merges all .json files into a single JsonObject
fun mergeJson(
    filename: String,
    files: List<File>,
): JsonObject {
  val json = JsonObject()
  files
      .stream()
      .filter { file -> file.name == filename }
      .forEach { file ->
        println("Merging ${file.absolutePath}")
        gson.fromJson(file.readText(), JsonObject::class.java).entrySet().forEach { entry ->
          if (json.has(entry.key)) {
            val existing = json.get(entry.key)
            if (existing.isJsonObject && entry.value.isJsonObject) {
              for ((key, value) in entry.value.asJsonObject.entrySet()) {
                existing.asJsonObject.add(key, value)
              }
            } else if (existing.isJsonArray && entry.value.isJsonArray) {
              for (element in entry.value.asJsonArray) {
                existing.asJsonArray.add(element)
              }
            } else {
              json.add(entry.key, entry.value)
            }
          } else {
            json.add(entry.key, entry.value)
          }
        }
      }
  return json
}

fun main(args: Array<String>) {
  if (args.size < 2) {
    println("Usage: -f <searchFor> -i <inputDir> -o <outputDir> -j <isJson>")
    return
  }
  if (args.size % 2 != 0) {
    println("Invalid number of arguments")
    return
  }

  val argsMap = args.toList().chunked(2).associate { it[0] to it[1] }
  // -h -- help
  // -f -- searchFor
  // -i -- inputDir
  // -o -- outputDir
  // -j -- isJson
  if (argsMap["-h"] != null) {
    println("Usage: -f <searchFor> -i <inputDir> -o <outputDir> -j <isJson>")
    return
  }
  if (argsMap["-f"] == null) {
    println("No searchFor specified")
    return
  }
  if (argsMap["-i"] == null) {
    println("No input directory specified")
    return
  }
  if (argsMap["-o"] == null) {
    println("No output directory specified")
    return
  }

  val searchFor = argsMap["-f"]!!
  val inputDir = argsMap["-i"]!!
  val outputDir = argsMap["-o"]!!
  // isJson covers y/n/yes/no/true/false/1/0/t/f
  var isJson = false
  val jsonTrue = setOf("y", "yes", "true", "1", "t")
  if (argsMap["-j"] != null && jsonTrue.contains(argsMap["-j"]!!.lowercase(Locale.getDefault()))) {
    isJson = true
  }

  val files = unzipAllMcWorldFiles(inputDir)
  val outputFile = File(outputDir + File.separator + searchFor)
  outputFile.writeText("")
  if (isJson) {
    val json = mergeJson(searchFor, files)
    outputFile.writeText(gson.toJson(json))
  } else {
    files
        .stream()
        .filter { file -> file.name == searchFor }
        .forEach { file ->
          println("Copying ${file.absolutePath}")
          outputFile.appendText(file.readText() + "\n")
        }
  }

  // delete the temporary directory
  File("." + File.separator + ".tmp").deleteRecursively()

  println("Done")
}
