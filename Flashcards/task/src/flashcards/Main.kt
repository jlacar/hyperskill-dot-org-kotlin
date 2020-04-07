package flashcards

fun main() {
    quiz(createDeck())
}

typealias DuplicateCheck = (String) -> Boolean

private fun createDeck(): Map<String, String> {
    println("Input the number of cards:")
    val count = readLine()!!.toInt()

    val deck = mutableMapOf<String, String>()
    for (i in 1..count) {
        deck[getCard(i, deck::containsKey)] = getDefinition(i, deck::containsValue)
    }
    return deck
}

private fun getCard(i: Int, reject: DuplicateCheck): String {
    println("The card #$i:")
    var card = readLine()!!
    while (reject(card)) {
        println("The card \"$card\" already exists. Try again:")
        card = readLine()!!
    }
    return card
}

private fun getDefinition(i: Int, reject: DuplicateCheck): String {
    println("The definition of the card #$i:")
    var definition = readLine()!!
    while (reject(definition)) {
        println("The definition \"$definition\" already exists. Try again:")
        definition = readLine()!!
    }
    return definition
}

private fun quiz(deck: Map<String, String>) {
    for ((card, definition) in deck.toList()) {
        println("Print the definition of \"$card\":")
        val answer = readLine()!!
        println(if (definition == answer) "Correct answer."
                else "Wrong answer. The correct one is \"${definition}\"${hintFor(answer, deck.entries)}")
    }
}

private fun hintFor(answer: String, entries: Set<Map.Entry<String, String>>) = entries
    .find { it.value == answer }?.let { ", you've just written the definition of \"${it.key}\"." } ?: "."
