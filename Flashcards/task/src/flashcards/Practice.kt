package flashcards

import java.io.File

/**
 * For practice problems
 */
fun main(args: Array<String>) {
    println(wordCount())
}

fun longestWord() = File("words_sequence.txt").readLines().map { it.length }.max()

fun wordCount() = File("text.txt").readText().split(" ").size