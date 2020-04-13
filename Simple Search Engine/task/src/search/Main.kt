package search

fun main() {
    readInput()
    runQueries()
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

private fun runQueries() {
    println("\nEnter the number of search queries:")
    val count = readLine()!!.trim().toInt()
    repeat(count) {
        println("\nEnter data to search people:")
        val value = readLine()!!.trim()
        val matches = data.filter { it.matchesAny(value) }
        if (matches.isNotEmpty()) {
            matches.joinToString("\n")
        } else {
            "No matching people found."
        }.also(::println)
    }
}

data class Entry (val first: String, val last: String = "", val email: String = "") {
    override fun toString(): String =
            mutableListOf<String>(first, last, email).joinToString(" ").trim()

    fun matchesAny(value: String): Boolean =
            toString().contains(value, ignoreCase = true)
}