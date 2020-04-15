package search

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
    return """Dwight Joseph djo@gmail.com
              Rene Webb webb@gmail.com
              Katie Jacobs
              Erick Harrington harrington@gmail.com
              Myrtle Medina
              Erick Burgess""".trimIndent().split("\n").toList()
    //return File(args[args.indexOf("--data") + 1]).readLines()
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

private fun header(title: String) = "\n=== $title ==="

private fun searchTerms() = readLine()!!.trim().toLowerCase()

private fun query(people: List<String>, index: Map<String, List<Int>>) {
    println("Select a matching strategy: ${Strategy.values().joinToString()}")
    val strategy = Strategy.valueOf(readLine()!!)
    println("Enter a name or email to search all suitable people.")
    strategy.search(searchTerms(), people, index).forEach(::println)
}

private fun list(people: List<String>) {
    println(header("List of people"))
    people.forEach(::println)
}

enum class Strategy {

    ALL {
        override fun search(words: String, people: List<String>, index: Map<String, List<Int>>): List<String> {
            words.split(" ").forEach { word ->


            }
            return NO_MATCHES
        }
    },

    ANY {
        override fun search(words: String, people: List<String>, index: Map<String, List<Int>>): List<String> {
            val workingSet = mutableListOf<String>()
            workingSet.addAll(people)
            return workingSet
        }
    },

    NONE {
        override fun search(words: String, people: List<String>, index: Map<String, List<Int>>): List<String> {
            val workingSet = mutableListOf<String>()
            workingSet.addAll(people)
            return workingSet
        }
    };

    protected val NO_MATCHES = listOf<String>("No matching people found.")

    abstract fun search(words: String, people: List<String>, index: Map<String, List<Int>>): List<String>
}