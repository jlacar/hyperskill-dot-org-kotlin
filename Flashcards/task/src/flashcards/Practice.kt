package flashcards

import java.io.File

fun main(args: Array<String>) =
    println(File("words_sequence.txt").readLines().map { it.length }.max())
