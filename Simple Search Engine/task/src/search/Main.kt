package search

fun main() {
    readInput()
    do {
        printHeader("Menu")
        println("1. Find a person")
        println("2. Print all people")
        println("0. Exit")
        val choice = readLine()!!.toInt()
        when (choice) {
            0 -> println("\nBye!")
            1 -> runQuery()
            2 -> listEntries()
            else -> println("Incorrect option! Try again.")
        }
    } while (choice != 0)
}

fun printHeader(title: String) = println("\n=== $title ===")

fun runQuery() {
    println("Enter a name or email to search all suitable people.")
    val value = readLine()!!.trim()
    val matches = data.filter { it.matchesAny(value) }
    if (matches.isNotEmpty()) {
        matches.joinToString("\n")
    } else {
        "No matching people found."
    }.also(::println)
}

fun listEntries() {
    printHeader("List of people")
    println(data.joinToString("\n"))
}

private val data = mutableListOf<Entry>()

private fun readInput() {
    println("Enter the number of people:")
    val count = readLine()!!.toInt()
    println("Enter all people:")
    repeat(count) {
        val input = readLine()!!.split(" ").map { it.trim() }
        when (input.size) {
            1 -> {
                data.add(Entry(input.first()))
            }
            2 -> {
                val (first, last) = input
                data.add(Entry(first, last))
            }
            3 -> {
                val (first, last, email) = input
                data.add(Entry(first, last, email))
            }
        }
    }
}

data class Entry(
        val first: String,
        val last: String = "",
        val email: String = "") {

    override fun toString(): String =
        listOf<String>(first, last, email).joinToString(" ").trim()

    fun matchesAny(value: String): Boolean =
        toString().contains(value, ignoreCase = true)
}