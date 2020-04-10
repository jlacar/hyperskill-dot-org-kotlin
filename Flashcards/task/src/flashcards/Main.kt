package flashcards

import java.io.File

class Card(val term: String, var def: String = "") {
    var mistakes: Int = 0

    companion object Unmarshaller {
        val SEP = "•"
        val NONE = Card(SEP, SEP)
        fun fromString(text: String): Card {
            val (term, def, mistakes) = text.split(SEP)
            return Card(term, def).apply { this.mistakes = mistakes.toInt() }
        }
    }

    override fun toString(): String = "$term$SEP$def$SEP$mistakes"
    override fun equals(other: Any?): Boolean = other is Card && term == other.term
    override fun hashCode(): Int = term.hashCode()
}

private val deck = mutableSetOf<Card>()

fun main() {
    do {
        printlnLog("\nInput the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
        val action = readLineLog()!!.toLowerCase()
        when (action) {
            "add" -> add(getCard())
            "remove" -> remove(getCard())
            "import" -> readCardsFrom(getFile())
            "export" -> writeCardsTo(getFile())
            "ask" -> if (deck.isNotEmpty()) quiz()
            "log" -> writeLogsTo(getFile())
            "hardest card" -> hardestCard()
            "reset stats" -> resetStats()
            ":list" -> backChannel("list of cards", { listCards() })
            ":logs" -> backChannel("log dump", { showLogActivity() })
        }
    } while (action != "exit")
    printlnLog("Bye bye!")
}

fun add(card: Card) {
    deck.find { it == card }?.run {
        printlnLog("The card \"$term\" already exists.\n")
        return
    }
    printlnLog("The definition of the card:")
    val def = readLineLog()!!
    deck.find { it.def == def }?.run {
        printlnLog("The definition \"$def\" already exists.")
        return
    }
    deck.add(Card(card.term, def))
    printlnLog("The pair (\"${card.term}\":\"$def\") has been added.")
}

fun remove(card: Card) {
    printlnLog(if (deck.remove(card)) "The card has been removed."
    else "Can't remove \"${card.term}\": there is no such card.")
}

fun writeCardsTo(outputFile: File) {
    writeTo(outputFile, deck.joinToString("\n", postfix = "\n"))
    printCardActionMessage(deck.size, "saved")
}

fun readCardsFrom(inputFile: File) {
    if (!inputFile.exists()) {
        printlnLog("File not found.")
        return
    }
    var count = 0
    inputFile.forEachLine {
        replace(Card.fromString(it))
        count++
    }
    printCardActionMessage(count, "loaded")
}

private fun replace(card: Card) {
    deck.remove(card)
    deck.add(card)
}

private fun printCardActionMessage(count: Int, action: String) {
    printlnLog(when (count) {
        1 -> "1 card has"
        else -> "$count cards have"
    }.let { "$it been $action." })
}

fun resetStats() {
    deck.forEach { it.mistakes = 0 }
    printlnLog("Card statistics has been reset.")
}

fun hardestCard() {
    val maxMistakes: Int = deck.maxBy { it.mistakes }?.mistakes ?: 0
    if (maxMistakes == 0) {
        printlnLog("There are no cards with errors.")
        return
    }
    val hardestCards = deck.filter { it.mistakes == maxMistakes }
    if (hardestCards.size == 1) {
        val hardest = hardestCards.first()
        printlnLog("The hardest card is \"${hardest.term}\". You have $maxMistakes errors answering it.")
    } else {
        val hardestTerms = hardestCards.map { "\"${it.term}\"" }.joinToString()
        printlnLog("The hardest cards are $hardestTerms. You have $maxMistakes errors answering them.")
    }
}

fun writeLogsTo(outputFile: File) {
    writeTo(outputFile, ioLog.joinToString("\n", postfix = "\n"))
    printlnLog("The log has been saved.")
}

private fun getCard(): Card {
    printlnLog("The card:")
    return Card(readLineLog()!!)
}

private fun getFile(): File {
    printlnLog("File name:")
    return File(readLineLog()!!)
}

fun writeTo(outputFile: File, text: String) = outputFile.writeText(text)

fun quiz() {
    var lastCard: Card = Card.NONE
    val pickRandomCard: () -> Card = if (deck.size in (1..2)) anyCard else { { anythingBut(lastCard) } }

    printlnLog("How many times to ask?")
    val count = readLineLog()!!.toInt()
    repeat(count) {
        val card = pickRandomCard()
        lastCard = card

        printlnLog("Print the definition of \"${card.term}\":")
        val answer = readLineLog()!!
        if (answer == card.def) {
            printlnLog("Correct answer.")
        } else {
            card.mistakes++
            printlnLog("Wrong answer. The correct one is \"${card.def}\"${hintFor(answer)}")
        }
    }
}

// simple strategy: don't care about repeats
val anyCard: () -> Card = { deck.random() }

// no repeat strategy: don't ask about same card twice in a row
fun anythingBut(lastCard: Card): Card {
    var card = deck.random()
    while (card == lastCard) {
        card = deck.random()
    }
    return card
}

private val ioLog = mutableListOf<String>()

private fun backChannel(label: String, adminRoutine: () -> Unit) {
    val down = "\u25BC".repeat(10)
    val up = "\u25B2".repeat(10)
    println("$down $label $down")
    adminRoutine()
    println("$up $label $up")
}

private fun showLogActivity() = ioLog.forEach { println(it) }

private fun listCards() {
    deck.forEach { println(it) }
    val s = if (deck.size == 1) "" else "s"
    println("Total of ${deck.size} card$s.")
}

private fun hintFor(answer: String): String {
    deck.find { it.def == answer }?.run {
        return@hintFor ", you've just written the definition of \"${this.term}\"."
    }
    return "."
}

private fun printlnLog(message: Any) {
    println(message).also { ioLog.addAll(message.toString().split("\n")) }
}

private fun readLineLog(): String? = readLine()?.also { ioLog.add("$it") }