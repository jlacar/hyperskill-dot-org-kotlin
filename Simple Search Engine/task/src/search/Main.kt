package search

import java.io.File

/**
 * Stage #6: Search Strategies
 */
fun main(args: Array<String>) {
    val people: List<String> = readDataFile(args)
    val index: Map<String, List<Int>> = index(people)
    do {
        """
        |${header("Menu")}
        |1. Find a person
        |2. Print all people
        |0. Exit""".trimMargin().also(::println)

        val choice = readLine()!!.toInt()
        when (choice) {
            0 -> println("\nBye!")
            1 -> query(people, index)
            2 -> list(people, "indexed")
            else -> println("Incorrect option! Try again.")
        }
    } while (choice != 0)
}

private fun readDataFile(args: Array<String>): List<String> {
    return File(args[args.indexOf("--data") + 1]).readLines()
}

private fun index(people: List<String>): Map<String, List<Int>> {
    val invertedIndex = mutableMapOf<String, MutableList<Int>>()
    people.forEachIndexed { index, line ->
        line.split(" ").forEach { word ->
            invertedIndex.getOrPut(word.trim().toLowerCase()) { mutableListOf<Int>() }.add(index)
        }
    }
    return invertedIndex
}

private fun query(people: List<String>, index: Map<String, List<Int>>) {
    println("Select a matching strategy: ${SearchStrategy.values().joinToString()}")
    val search = SearchStrategy.valueOf(readLine()!!.toUpperCase())
    list(search.results(getTerms(), people, index), "match $search")
}

private fun header(title: String) = "\n=== $title ==="

private fun getTerms(): List<String> {
    println("Enter search terms separated by space:")
    return readLine()!!.trim().toLowerCase().split(" ")
}

private fun list(people: List<String>, description: String) {
    if (people.isNotEmpty()) {
        printHeader(people, description)
        people.forEach(::println)
    } else {
        println("No people found.")
    }
}

private fun printHeader(people: List<String>, description: String) {
    val qualifier = if (people.size == 1) "person" else "people"
    println(header("${people.size} $qualifier $description"))
}

enum class SearchStrategy {
    ALL {
        override fun results(words: List<String>, people: List<String>, index: Map<String, List<Int>>):
                List<String> {
            val allWords = words.size
            val matchFrequencies = allIndicesOf(words, index).groupingBy { it }.eachCount()
            return matchFrequencies.filter { (_, itMatches) -> itMatches == allWords }.map { people[it.key] }
        }
    },

    ANY {
        override fun results(words: List<String>, people: List<String>, index: Map<String, List<Int>>):
                List<String> = allIndicesOf(words, index).distinct().map { people[it] }
    },

    NONE {
        override fun results(words: List<String>, people: List<String>, index: Map<String, List<Int>>):
                List<String> {
            val anyMatches = ANY.results(words, people, index)
            return people.filterNot { anyMatches.contains(it) }
        }
    };

    protected fun allIndicesOf(words: List<String>, index: Map<String, List<Int>>):
            List<Int> = words.mapNotNull { index[it] }.flatten()

    abstract fun results(words: List<String>, people: List<String>, index: Map<String, List<Int>>): List<String>
}