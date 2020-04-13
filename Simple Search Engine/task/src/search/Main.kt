package search

fun main() {
    val people: List<String> = inputPeople()
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

private fun header(title: String) = "\n=== $title ==="

private fun inputPeople(): List<String> {
    val data = mutableListOf<String>()
    println("Enter the number of people:")
    val count = readLine()!!.toInt()
    println("Enter all people:")
    repeat(count) { data.add(readLine()!!) }
    return data
}

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