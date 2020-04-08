package flashcards

import java.io.File

fun main() {
    val deck = mutableMapOf<String, String>()
    do {
        println("Input the action (add, remove, import, export, ask, exit):")
        val action = readLine()!!.toLowerCase()
        when (action) {
            "add" -> addTo(deck)
            "remove" -> removeFrom(deck)
            "import" -> loadInto(deck)
            "export" -> dumpFrom(deck)
            "ask" -> if (deck.isNotEmpty()) askAbout(deck)
        }
    } while (action != "exit")
    println("Bye bye!")
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
        println(if (definition == answer) "Correct answer."
            else "Wrong answer. The correct one is \"${definition}\"${hintFor(deck, answer)}")
    }
}

// simple strategy: don't care about repeats
fun anyCard(deck: Map<String, String>): Pair<String, String> {
    return with(deck.keys.random()) { Pair(this, deck[this]!!) }
}

// no repeat strategy: avoid asking about same card twice in a row
fun anythingBut(lastCard: String, deck: Map<String, String>): Pair<String, String> {
    var card = deck.keys.random()
    while (card == lastCard) {
        card = deck.keys.random()
    }
    return Pair(card, deck[card]!!)
}

private fun hintFor(deck: Map<String, String>, answer: String): String {
    return deck.entries.find { it.value == answer }?.let { ", you've just written the definition of \"${it.key}\"." }
            ?: "."
}