package search

import java.io.File

/**
 * Stage #5: Inverted Index Search
 */
fun main(args: Array<String>) {
    val people: List<String> = readDataFile(args)
    val index: Map<String, List<Int>> = index(people)
    do {
        """${header("Menu")}
        |1. Find a person
        |2. Print all people")
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
            word.trim().toLowerCase().also { key ->
                (invertedIndex[key] ?: mutableListOf<Int>().also { invertedIndex[key] = it }).add(index)
            }
        }
    }
    return invertedIndex
}

private fun header(title: String) = "\n=== $title ==="

private fun query(people: List<String>, index: Map<String, List<Int>>) {
    println("Enter a name or email to search all suitable people.")
    val word = readLine()!!.trim().toLowerCase()
    val matches = index[word]
    if (matches != null && matches.isNotEmpty()) {
        matches.map { people[it] }.forEach(::println)
    } else {
        println("No matching people found.")
    }
}

private fun list(people: List<String>) {
    println(header("List of people"))
    people.forEach(::println)
}