package search

fun main() {
    // call one of the *Problem() functions below
    ranchStringProblem()
}

fun ranchStringProblem() {
    listOf("Jack", " Jack Bauer", "Jack '24' Baeur ", " Jack and the Terrorist Beanstalk ")
    .forEach { phrase ->
        phrase.trim().let {
            when (it.split(" ").size) {
                1 -> "one part"
                2 -> "two parts"
                3 -> "three parts"
                else -> "lots of parts"
            }.let { nParts -> """"$it" has $nParts""" }
        }.also(::println)
    }
}

fun currencyProblem() {
    currencyComparison("Germany", "France")
    currencyComparison("Australia", "Canada")
    currencyComparison("Canada", "Philippines")
}

fun companionObjectProblem() {
    listOf("Alice", "Bob", "Charlie", "David", "Edward").forEach {
        Player.create(it).also { player -> println("${player.id} ${player.name} ${player.hp}") }
    }
}

enum class DangerLevel(val severity: Int) {
    HIGH(3), MEDIUM(2), LOW(1);

    fun getLevel(): Int = severity
}

class Player private constructor(val id: Int, val name: String, val hp: Int) {
    companion object Factory {
        private var generatedId = 1
        fun create(name: String): Player = Player(generatedId++, name, hp = 100)
    }
}

fun currencyComparison(vararg countries: String) {
    try {
        val (a, b) = countries.map { Country.valueOf(it) }
        when {
            a != null && b != null -> a.hasCurrency(b.currency)
            else -> false
        }.also(::println)
    } catch (iae: IllegalArgumentException) {
        println(false)
    }
}

enum class Country(val currency: String) {
    Germany("Euro"),
    Mali("CFA franc"),
    Dominica("Eastern Caribbean dollar"),
    Canada("Canadian dollar"),
    Spain("Euro"),
    Australia("Australian dollar"),
    Brazil("Brazilian real"),
    Senegal("CFA franc"),
    France("Euro"),
    Grenada("Eastern Caribbean dollar"),
    Kiribati("Australian dollar");

    fun hasCurrency(currency: String) = this.currency == currency
}