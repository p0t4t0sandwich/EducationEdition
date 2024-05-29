package dev.neuralnexus.scifi

fun main(args: Array<String>) {
    if (args.size < 2) {
        println("Usage: -i <inputStructureFile> -f <nameTxtFile> -o <outputDir>")
        return
    }
    if (args.size % 2 != 0) {
        println("Invalid number of arguments")
        return
    }

    val argsMap = args.toList().chunked(2).associate { it[0] to it[1] }
    // -h -- help
    // -i -- inputStructureFile
    // -f -- nameTxtFile
    // -o -- outputDir

    if (argsMap["-h"] != null) {
        println("Usage: -i <inputStructureFile> -f <nameTxtFile> -o <outputDir>")
        return
    }
    if (argsMap["-i"] == null) {
        println("No input structure file specified")
        return
    }
    if (argsMap["-f"] == null) {
        println("No name txt file specified")
        return
    }
    if (argsMap["-o"] == null) {
        println("No output directory specified")
        return
    }

    val inputStructureFile = argsMap["-i"]!!
    val nameTxtFile = argsMap["-f"]!!
    val outputDir = argsMap["-o"]!!
}