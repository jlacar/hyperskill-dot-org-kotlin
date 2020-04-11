package flashcards

import java.io.File

private val deck = mutableSetOf<Card>()

fun main(args: Array<String>) {
    val commandLineOptions: Flag = Flag(args).apply {
        option("import")
        option("export")
    }
    commandLineOptions.get("import")?.also { readCardsFrom(File(it), verbose = false) }
    menuLoop(commandLineOptions)
    commandLineOptions.get("export")?.also { writeCardsTo(File(it)) }
}

private fun menuLoop(options: Flag) {
    do {
        printlnLog("\nInput the action (add, remove, import, export, ask, exit, log, hardest, easiest, not asked, reset stats):")
        val action = readLineLog()!!.toLowerCase()
        when (action) {
            "add" -> add(getCard())
            "remove" -> remove(getCard())
            "import" -> getFile(options.get("import"))?.also { readCardsFrom(it) }
            "export" -> getFile(options.get("export"))?.also { writeCardsTo(it) }
            "ask" -> if (deck.isNotEmpty()) quiz()
            "log" -> getFile(options.get("import"))?.also { writeLogsTo(it) }
            "hardest" -> hardestCard()
            "easiest" -> easiestCard()
            "not asked" -> notAsked()
            "reset stats" -> resetStats()
            ":stats" -> secretly("stats") { showStats() }
            ":list" -> secretly("list of cards") { listCards() }
            ":logs" -> secretly("log dump") { showLogActivity() }
            "exit" -> printlnLog("Bye bye!")
            else -> printlnLog("Unknown command: $action")
        }
    } while (action != "exit")
}

private fun notAsked() {
    val notYetQuizzed = deck.filter { it.asked == 0 }
    notYetQuizzed.forEach { printlnLog("\"${it.term}\" : \"${it.def}\"") }
    printListSummary(notYetQuizzed.size, "quizzed on yet", negateAction = true)
}

private fun add(card: Card) {
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

private fun remove(card: Card) {
    printlnLog(if (deck.remove(card)) "The card has been removed."
    else "Can't remove \"${card.term}\": there is no such card.")
}

private fun writeCardsTo(outputFile: File) {
    writeTo(outputFile, deck.joinToString("\n", postfix = "\n"))
    printListSummary(deck.size, "saved to ${outputFile.name}")
}

private fun readCardsFrom(inputFile: File, verbose: Boolean = true) {
    if (!inputFile.exists()) {
        if (verbose) printlnLog("File ${inputFile.name} not found.")
        return
    }
    var count = 0
    inputFile.forEachLine {
        replace(Card.fromString(it))
        count++
    }
    printListSummary(count, "loaded from ${inputFile.name}")
}

private fun replace(card: Card) {
    deck.remove(card)
    deck.add(card)
}

private fun printListSummary(count: Int, action: String, negateAction: Boolean = false) {
    val nt = if (negateAction) "n't" else ""
    printlnLog(when (count) {
        1 -> " has$nt"
        else -> "s have$nt"
    }.let { "$count card$it been $action." })
}

private fun resetStats() {
    deck.forEach {
        it.asked = 0
        it.mistakes = 0
    }
    printlnLog("Card statistics has been reset.")
}

private fun easiestCard() = reportExtremeStat({ it.min() }, "right", "best")

private fun hardestCard() = reportExtremeStat({ it.max() }, "wrong", "least", bailOnNoMistakes = true)

private fun reportExtremeStat(query: (List<Double>) -> Double?, result: String, adjective: String,
                              bailOnNoMistakes: Boolean = false) {
    val failRates = deck.mapNotNull { it.degreeOfDifficulty() }
    if (nothingIn(failRates)) {
        return
    }
    if (failRates.max() == 0.0) {
        printlnLog("You haven't made any mistakes yet!")
        if (bailOnNoMistakes) return
    }
    val theMostest = query(failRates)
    printlnLog(trackRecord(deck.filter { it.degreeOfDifficulty() == theMostest }, result, adjective))
}

private fun nothingIn(scores: List<Double>): Boolean =
    if (scores.isEmpty()) {
        printlnLog("No data available: quiz yourself with the 'ask' command first.")
        true
    } else {
        false
    }

private fun trackRecord(data: List<Card>, result: String, adjective: String): String =
    if (data.size == 1) {
        "The card you keep getting $result is \"${data.first().term}\". ${data.first().difficultyAsString()}"
    } else {
        data.joinToString("\n", prefix = "The cards you $adjective remember are:\n")
            { "\"${it.term}\": ${it.difficultyAsString()}" }
    }

private val ioLog = mutableListOf<String>()

private fun writeLogsTo(outputFile: File) {
    writeTo(outputFile, ioLog.joinToString("\n", postfix = "\n"))
    printlnLog("The log has been saved to ${outputFile.name}.")
}

private fun writeTo(outputFile: File, text: String) = outputFile.writeText(text)

private fun quiz() {
    var lastCard: Card = Card.NONE
    val pickRandomCard: () -> Card = if (deck.size in (1..2)) anyCard else { { anythingBut(lastCard) } }

    printlnLog("How many times to ask?")
    val count = readLineLog()!!.toInt()
    repeat(count) {
        val card = pickRandomCard()
        card.asked++
        lastCard = card

        printlnLog("Print the definition of \"${card.term}\":")
        val answer = readLineLog()!!
        if (card.isDefinedAs(answer)) {
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

private fun getFile(defaultName: String?): File? {
    val default = if (defaultName.isNullOrBlank()) "" else " ($defaultName)"
    printlnLog("File name$default:")
    val name = readLineLog()!!
    return if (name.isNotBlank()) File(name)
        else if (defaultName.isNullOrBlank()) null
        else File(defaultName)
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

private fun showStats() = deck.forEach { println("\"${it.term}\": ${it.difficultyAsString()}") }
        .also { printListSummary(deck.size, "defined") }

private fun listCards() = deck.forEach { println("\"${it.term}\": \"${it.def}\" (${it.asked - it.mistakes}/${it.asked}) ${it.mistakes} mistakes" ) }
        .also { printListSummary(deck.size, "defined") }

private fun hintFor(answer: String): String {
    deck.find { it.isDefinedAs(answer) }?.run {
        return@hintFor ", you've just written the definition of \"${this.term}\"."
    }
    return "."
}

private fun printlnLog(message: Any) {
    println(message).also { ioLog.addAll(message.toString().split("\n")) }
}

private fun readLineLog(): String? = readLine()?.also { ioLog.add(it) }
