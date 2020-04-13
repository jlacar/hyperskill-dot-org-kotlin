package search

fun main() {
    readInput()
    runQueries()
}

private val inventory = mutableListOf<InventoryItem>()

private fun readInput() {
    println("Enter the number of cars:")
    val count = readLine()!!.toInt()
    println("Enter all cars, one per line (vin, make, model, year [, price]), comma-separated:")
    repeat(count) {
        val input = readLine()!!.split(",").map { it.trim() }
        when (input.size) {
            4 -> {
                val (vin, make, model, year) = input
                inventory.add(InventoryItem(
                        vin = vin,
                        make = make,
                        model = model,
                        year = year.toInt())
                )
            }
            5 -> {
                val (vin, make, model, year, price) = input
                inventory.add(InventoryItem(
                        vin = vin,
                        make = make,
                        model = model,
                        year = year.toInt(),
                        price = price.toInt())
                )
            }
//            else -> {
//                println("Invalid entry. Please check your input.")
//            }
        }
    }
}

private fun runQueries() {
    println("\nEnter the number of search queries:")
    val count = readLine()!!.trim().toInt()
    repeat(count) {
        println("\nEnter data to search cars:")
        val data = readLine()!!.trim()
        val matches = inventory.filter { it.matchesAny(data) }
        if (matches.isNotEmpty()) {
            matches.joinToString("\n")
        } else {
            "No matching cars found."
        }.also(::println)
    }
}

data class InventoryItem(
        val vin: String,
        val make: String,
        val model: String,
        val year: Int,
        var price: Int = 0) {

    override fun toString() =
            listOf(vin, make, model, year.toString(), price.toString())
                    .joinToString(", ")

    fun matchesAny(data: String): Boolean {
        return when (data.toLowerCase()) {
            vin.toLowerCase(),
            make.toLowerCase(),
            model.toLowerCase(),
            year.toString(),
            price.toString() -> true
            else -> false
        }
    }
}