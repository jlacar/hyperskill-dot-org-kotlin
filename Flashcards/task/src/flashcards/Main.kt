package flashcards

import java.io.File

fun main() {
    val deck = mutableMapOf<String, String>()
    do {
        println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
        val action = readLine()!!.toLowerCase()
        when (action) {
            "add" -> addTo(deck)
            "remove" -> removeFrom(deck)
            "import" -> loadInto(deck)
            "export" -> dumpFrom(deck)
            "ask" -> if (deck.isNotEmpty()) askAbout(deck)
            "log" -> logAction()
            "hardest card" -> hardestCard()
            "reset stats" -> resetStats()
            ":list" -> printCards(deck)
            ":stats" -> printStats()
        }
    } while (action != "exit")
    println("Bye bye!")
}

fun printStats() {
    TODO("Not yet implemented")
}

fun resetStats() {
    TODO("Not yet implemented")
}

fun hardestCard() {
    TODO("Not yet implemented")
}

fun logAction() {
    TODO("Not yet implemented")
}

private fun printCards(deck: MutableMap<String, String>) {
    deck.forEach { (front, back) -> println("(\"$front\" => \"$back\")") }
    val s = if (deck.size == 1) "" else "s"
    println("Total of ${deck.size} card$s.\n")
}

fun addTo(deck: MutableMap<String, String>) {
    println("The card:")
    val card = readLine()!!
    if (deck.containsKey(card)) {
        println("The card \"$card\" already exists.\n")
        return
    }
    println("The definition of the card:")
    val definition = readLine()!!
    if (deck.containsValue(definition)) {
        println("The definition \"$definition\" already exists.\n")
        return
    }
    deck[card] = definition
    println("The pair (\"$card\":\"$definition\") has been added.\n")
}

fun removeFrom(deck: MutableMap<String, String>) {
    println("The card:")
    val card = readLine()!!
    println(if (deck.remove(card) != null) {
        "The card has been removed.\n"
    } else {
        "Can't remove \"$card\": there is no such card.\n"
    }
    )
}

// Implementation note: using Map.Entry.toString() to dump pairs into a file
const val SEPARATOR = "="

fun dumpFrom(deck: Map<String, String>) {
    println("File name:")
    val outputFile = File(readLine()!!)
    outputFile.writeText(deck.map { "$it" }.joinToString("\n"))
    println("${deck.size} cards have been saved.")
}

fun loadInto(deck: MutableMap<String, String>) {
    println("File name:")
    val inputFile = File(readLine()!!)
    if (!inputFile.exists()) {
        println("File not found.\n")
        return
    }
    var count = 0
    inputFile.forEachLine {
        count++
        val (card, definition) = it.split(SEPARATOR)
        deck[card] = definition
    }
    println("$count cards have been loaded.\n")
}

fun askAbout(deck: Map<String, String>) {
    var lastCard = ""
    val pickRandomCard: () -> Pair<String, String> =
            if (deck.size in (1..2)) {
                // not enough cards to care about repeats
                { anyCard(deck) }
            } else {
                // any card besides lastCard (captured!)
                { anythingBut(lastCard, deck) }
            }

    println("How many times to ask?")
    val count = readLine()!!.toInt()
    repeat(count) {
        val (card, definition) = pickRandomCard()
        lastCard = card

        println("Print the definition of \"$card\":")
        val answer = readLine()!!
        if (definition == answer) {
            println("Correct answer.")
        } else {
            println("Wrong answer. The correct one is \"${definition}\"${hintFor(answer, deck)}")
        }
    }
}

// simple strategy: don't care about repeats
fun anyCard(deck: Map<String, String>): Pair<String, String> =
    with(deck.keys.random()) { Pair(this, deck[this]!!) }

// no repeat strategy: don't ask about same card twice in a row
fun anythingBut(lastCard: String, deck: Map<String, String>): Pair<String, String> {
    var card = deck.keys.random()
    while (card == lastCard) {
        card = deck.keys.random()
    }
    return Pair(card, deck[card]!!)
}

private fun hintFor(answer: String, deck: Map<String, String>): String {
    val matchingCard = deck.entries.find { it.value == answer }
    return matchingCard?.let { ", you've just written the definition of \"${it.key}\"." } ?: "."
}