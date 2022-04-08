package org.example.split

import kotlinx.cli.*
import java.io.File

fun main(args: Array<String>) {
    val parser = ArgParser("split", strictSubcommandOptionsOrder = true)
    val decimalNames by parser.option(ArgType.Boolean, shortName = "d", description = "Numeric-named output files")
        .default(false)
    val lineSeparation by parser.option(ArgType.Int, shortName = "l", description = "Number of lines to split")
        .default(100)
    val signSeparation by parser.option(ArgType.Int, shortName = "c", description = "Number of symbols to split")
    val numberSeparation by parser.option(ArgType.Int, shortName = "n", description = "Number of split files")
    val output by parser.option(ArgType.String, shortName = "o", description = "Output file name").default("x")
    val input by parser.argument(ArgType.String, description = "Input file")

    parser.parse(args)

    val outputName = if (output == "-") input else output

    if ((lineSeparation != 100 && (signSeparation != null || numberSeparation != null)) ||
        (signSeparation != null && numberSeparation != null)) throw Exception("Указано несколько флагов управления" +
            "размером")

    when {
        signSeparation != null -> symbolSplit(input, outputName, signSeparation!!, decimalNames)
        numberSeparation != null -> numberSplit(input, outputName, numberSeparation!!, decimalNames)
        else -> lineSplit(input, outputName, lineSeparation, decimalNames)
    }
}

// Все файлы, как входные так и выходные, расположены в папке \files
fun filePath(name: String) = System.getProperty("user.dir") + "\\files\\" + name + ".txt"

// Индекс выходного файла, 1 2 3 или aa ab ac
fun letterName(num: Int, numName: Boolean): String = if (numName) (num + 1).toString() else {
    if (num > 675) throw Exception("Превышено допустимое количество выходных файлов с нечисловым индексом")
    val c1 = 'a' + num % 26
    val c2 = 'a' + num / 26
    "$c2$c1"
}

// -l flag
fun lineSplit(inputName: String, outputName: String, numOfLines: Int, numericName: Boolean) {
    val f = File(filePath(inputName))
    var nameNew = outputName + letterName(0, numericName)
    var fileNew = File(filePath(nameNew))
    var writer = fileNew.bufferedWriter()
    for ((i, line) in f.readLines().withIndex()) {
        if (i != 0 && i % numOfLines == 0) {
            writer.close()
            println("Файл $nameNew был записан")
            nameNew = outputName + letterName(i / numOfLines, numericName)
            fileNew = File(filePath(nameNew))
            writer = fileNew.bufferedWriter()
        }
        writer.write(line)
        writer.newLine()
    }
    writer.close()
    println("Файл $nameNew был записан")
}

// -c flag
fun symbolSplit(inputName: String, outputName: String, numOfSymbols: Int, numericName: Boolean) {
    val f = File(filePath(inputName)).readText().chunked(numOfSymbols)

    for ((index, part) in f.withIndex()) {
        val nameNew = outputName + letterName(index, numericName)
        File(filePath(nameNew)).writeText(part)
        println("Файл $nameNew был записан")
    }
}

// -n flag
fun numberSplit(inputName: String, outputName: String, numOfSplit: Int, numericName: Boolean) {
    val f = File(filePath(inputName)).readText()
    val numOfParts = f.length / numOfSplit
    val qq = f.chunked(numOfParts).toMutableList()
    if (qq.size > numOfSplit) for (i in numOfSplit until qq.size) qq[numOfSplit-1] += qq[i]

    for ((index, part) in qq.withIndex()) {
        if (index + 1 > numOfSplit) break
        val nameNew = outputName + letterName(index, numericName)
        File(filePath(nameNew)).writeText(part)
        println("Файл $nameNew был записан")
    }
}