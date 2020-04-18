package search

private fun index(people: List<String>): Map<String, List<Int>> =
        people.mapIndexed { index, entry ->
            entry.toLowerCase().split(" ").filterNot { it.isNullOrBlank() }
            .associateWithTo(mutableMapOf<String, Int>()) { index }
        }.asSequence().flatMap { it.asSequence() }
        .groupBy({ it.key }, { it.value })

// This works, too:
//    people.withIndex().flatMap { (index, person) ->
//        person.trim().toLowerCase().split(" ").map {
//            word -> IndexedValue(index, word)
//        }
//    }.groupBy({ it.value }, { it.index })

fun main() {
    // call one of the *Problem() functions below
//    ranchStringProblem()
//    ranchAssociateToProblem()
    index(readDataFile()).forEach(::println)
}

data class Product(val name: String, var qty: Int = 0)

fun ranchAssociateToProblem() {
    val allProducts: Map<String, Array<Product>> = mapOf(
            "shoes" to arrayOf(Product("Nike", 1), Product("Adidas")),
            "pants" to arrayOf(Product("Levis", 10), Product("Wrangler"), Product("Dockers", 5))
    )

    val availableProducts = allProducts.mapValues { (_, products) ->
        products.filter { it.qty != 0 }
    }

    allProducts.mapValues { (_, products) -> products.filter {true} }.forEach(::println)
    availableProducts.forEach(::println)
}

private fun readDataFile(): List<String> {
    return """|Dwight Joseph djo@gmail.com
          |Bong Marcaida brods@apophils.org
          |Rene Webb webb@gmail.com
          |Rene Kintanar brods@apophils.org
          |Jesse Kintanar brods@apophils.org
          |Katie Jacobs
          |Dave Chilcott dchilcott@aci.com
          |Bong Cinco brods@apophils.org
          |Erick Harrington harrington@gmail.com
          |Dave Haws david.haws@accenture.com
          |Bong Bayon brods@apophils.org
          |Dave Stought dstought@cisco.com
          |Myrtle Medina
          |Bong Raterta brods@apophils.org
          |Erick Burgess""".trimMargin().split("\n").toList()

//    return """|Dwight Joseph djo@gmail.com
//            |Rene Webb webb@gmail.com
//            |Katie Jacobs
//            |Erick Harrington harrington@gmail.com
//            |Myrtle Medina
//            |Erick Burgess""".trimMargin()
//            .split("\n").toList()
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