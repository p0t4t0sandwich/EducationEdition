/**
 * Copyright (c) 2024 Dylan Sperrer - dylan@sperrer.ca
 * The project is Licensed under GPL-3.0
 */

package dev.neuralnexus.scifi

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import java.io.File
import java.util.zip.ZipFile

// Unzips a file and returns the contents in memory, keeping the directory structure
fun unzipFile(name: String): List<File> {
  return ZipFile(name)
      .entries()
      .toList()
      .stream()
      .map { entry ->
        File(entry.name).apply {
          if (entry.isDirectory) {
            mkdirs()
          } else {
            if (parentFile != null) {
              parentFile.mkdirs()
            }
            outputStream().use { output ->
              ZipFile(name).getInputStream(entry).use { input -> input.copyTo(output) }
            }
          }
        }
      }
      .toList()
}

// Finds all .mcworld files recursively in a directory and unzips them
fun unzipAllMcWorldFiles(string: String): List<File> {
    val files = mutableListOf<File>()
    File(string).walkTopDown().forEach {
        if (it.extension == "mcworld") {
        files.addAll(unzipFile(it.absolutePath))
        }
    }
    return files
}

// Merges all education.json files into a single JsonObject
fun educationJson(files: List<File>): JsonObject {
  val gson = GsonBuilder().setPrettyPrinting().create()
  val json = JsonObject()
  files
      .stream()
      .filter { file -> file.name == "education.json" }
      .forEach { file ->
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

fun main() {
  val json = educationJson(unzipAllMcWorldFiles("../../MCWorldFiles"))
  println(json)
}
