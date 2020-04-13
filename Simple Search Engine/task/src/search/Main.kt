package search

import java.io.File

fun main(args: Array<String>) {
    val people: List<String> = readDataFile(args)
    do {
        """${header("Menu")}
        |1. Find a person
        |2. Print all people")
        |0. Exit""".trimMargin().also(::println)

        val choice = readLine()!!.toInt()
        when (choice) {
            0 -> println("\nBye!")
            1 -> query(people)
            2 -> list(people)
            else -> println("Incorrect option! Try again.")
        }
    } while (choice != 0)
}

fun readDataFile(args: Array<String>): List<String> {
    return File(args[args.indexOf("--data") + 1]).readLines()
}

private fun header(title: String) = "\n=== $title ==="

private fun query(people: List<String>) {
    println("Enter a name or email to search all suitable people.")
    val value = readLine()!!.trim()
    val matches = people.filter { it.contains(value, ignoreCase = true) }
    when {
        matches.isNotEmpty() -> matches.forEach(::println)
        else -> println("No matching people found.")
    }
}

private fun list(people: List<String>) {
    println(header("List of people"))
    people.forEach(::println)
}