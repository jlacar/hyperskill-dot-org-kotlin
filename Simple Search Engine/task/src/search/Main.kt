package search

private val data = mutableListOf<String>()

fun main() {
    inputPeople()
    do {
        """${header("Menu")}
        |1. Find a person
        |2. Print all people")
        |println("0. Exit")""".trimMargin().also(::println)

        val choice = readLine()!!.toInt()
        when (choice) {
            0 -> println("\nBye!")
            1 -> query()
            2 -> list()
            else -> println("Incorrect option! Try again.")
        }
    } while (choice != 0)
}

private fun header(title: String) = println("\n=== $title ===")

private fun inputPeople() {
    println("Enter the number of people:")
    val count = readLine()!!.toInt()
    println("Enter all people:")
    repeat(count) { data.add(readLine()!!) }
}

private fun query() {
    println("Enter a name or email to search all suitable people.")
    val value = readLine()!!.trim()
    val matches = data.filter { it.contains(value, ignoreCase = true) }
    if (matches.isNotEmpty()) {
        matches.forEach(::println)
    } else {
        println("No matching people found.")
    }
}

private fun list() {
    println(header("List of people"))
    data.forEach(::println)
}