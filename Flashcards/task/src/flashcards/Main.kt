package flashcards

import java.io.File

private val deck = mutableSetOf<Card>()

fun main(args: Array<String>) {
    val commandLineFlags: Flag = Flag(args).apply {
        optional("import")
        optional("export")
    }
    commandLineFlags.getString("import")?.also { readCardsFrom(File(it), verbose = false) }
    menuLoop()
    commandLineFlags.getString("export")?.also { writeCardsTo(File(it)) }
}

private fun menuLoop() {
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
            ":list" -> secretly("list of cards") { listCards() }
            ":logs" -> secretly("log dump") { showLogActivity() }
            "exit" -> printlnLog("Bye bye!")
            else -> printlnLog("Unknown command: $action")
        }
    } while (action != "exit")
}

fun add(card: Card) {
    deck.find { it == card }?.run {
        printlnLog("The card \"$term\" already exists.\n")
        return
    }
    printlnLog("The definition of the card:")
    val def = readLineLog()!!
    deck.find { it.isDefinedAs(def) }?.run {
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
    printIOActionMessage(deck.size, "saved to ${outputFile.name}")
}

fun readCardsFrom(inputFile: File, verbose: Boolean = true) {
    if (!inputFile.exists()) {
        if (verbose) printlnLog("File ${inputFile.name} not found.")
        return
    }
    var count = 0
    inputFile.forEachLine {
        replace(Card.fromString(it))
        count++
    }
    printIOActionMessage(count, "loaded from ${inputFile.name}")
}

private fun replace(card: Card) {
    deck.remove(card)
    deck.add(card)
}

private fun printIOActionMessage(count: Int, action: String) {
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
    val mostMistakes: Int = deck.map(Card::mistakes).max() ?: 0
    if (mostMistakes == 0) {
        printlnLog("There are no cards with errors.")
        return
    }
    printlnLog(hardestMessage(deck.filter { it.mistakes == mostMistakes }, mostMistakes))
}

private fun hardestMessage(hardest: List<Card>, mostMistakes: Int): String =
    if (hardest.size == 1) {
        "The hardest card is \"${hardest.first().term}\". You have $mostMistakes errors answering it."
    } else {
        hardest.map { "\"${it.term}\"" }.joinToString().let {
            "The hardest cards are $it. You have $mostMistakes errors answering them."
        }
    }

private val ioLog = mutableListOf<String>()

fun writeLogsTo(outputFile: File) {
    writeTo(outputFile, ioLog.joinToString("\n", postfix = "\n"))
    printlnLog("The log has been saved.")
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

private fun getCard(): Card {
    printlnLog("The card:")
    return Card(readLineLog()!!)
}

private fun getFile(): File {
    printlnLog("File name:")
    return File(readLineLog()!!)
}

// simple strategy: don't care about repeats
private val anyCard: () -> Card = { deck.random() }

// no repeat strategy: don't ask about same card twice in a row
private fun anythingBut(lastCard: Card): Card {
    var card = deck.random()
    while (card == lastCard) {
        card = deck.random()
    }
    return card
}

private fun secretly(label: String, adminRoutine: () -> Unit) {
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
    deck.find { it.isDefinedAs(answer) }?.run {
        return@hintFor ", you've just written the definition of \"${this.term}\"."
    }
    return "."
}

private fun printlnLog(message: Any) {
    println(message).also { ioLog.addAll(message.toString().split("\n")) }
}

private fun readLineLog(): String? = readLine()?.also { ioLog.add("$it") }
