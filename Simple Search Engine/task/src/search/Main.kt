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
            2 -> list(people)
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
    println("Select a matching strategy: ${Strategy.values().joinToString()}")
    val strategy = Strategy.valueOf(readLine()!!.toUpperCase())
    list(strategy.search(getTerms(), people, index))
}

private fun header(title: String) = "\n=== $title ==="

private fun getTerms(): List<String> {
    println("Enter a name or email to search all suitable people.")
    return readLine()!!.trim().toLowerCase().split(" ")
}

private fun list(people: List<String>) {
    if (people.isNotEmpty()) {
        println(header("List of people"))
        people.forEach(::println)
    } else {
        println("No matching people found.")
    }
}

enum class Strategy {
    ALL {
        override fun search(words: List<String>, people: List<String>, index: Map<String, List<Int>>): List<String> {
            val counts = findAny(words, index).groupingBy { it }.eachCount()
            return counts.mapNotNull { (i, count) -> if (count == words.size) people[i] else null }
        }
    },

    ANY {
        override fun search(words: List<String>, people: List<String>, index: Map<String, List<Int>>): List<String> =
            findAny(words, index).distinct().map { people[it] }
    },

    NONE {
        override fun search(words: List<String>, people: List<String>, index: Map<String, List<Int>>): List<String> {
            val anyMatches = ANY.search(words, people, index)
            return people.filter { !anyMatches.contains(it) }
        }
    };

    protected fun findAny(words: List<String>, index: Map<String, List<Int>>) = words.mapNotNull { index[it] }.flatten()

    abstract fun search(words: List<String>, people: List<String>, index: Map<String, List<Int>>): List<String>
}