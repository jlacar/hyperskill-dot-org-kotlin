package flashcards

class Card(val term: String, var def: String = "") {
    var mistakes: Int = 0
    var asked: Int = 0

    fun degreeOfDifficulty(): Double? = if (asked != 0) mistakes / asked.toDouble() * 100.0 else null

    fun difficultyAsString() = if (asked == 0) {
        "Not been quized on this one yet."
    } else {
        val s = if (asked == 1) "" else "s"
        val percentCorrect = "%.2f%%".format(100.00 - degreeOfDifficulty()!!)
        "Correctly answered ${asked - mistakes} of $asked time$s ($percentCorrect)"
    }

    companion object Unmarshaller {
        val SEP = "•"
        val NONE = Card(SEP, SEP)
        fun fromString(text: String): Card {
            val (term, def, asked, mistakes) = text.split(SEP)
            return Card(term, def).apply {
                this.asked = asked.toInt()
                this.mistakes = mistakes.toInt()
            }
        }

    }
    fun isDefinedAs(def: String): Boolean = this.def.equals(def, ignoreCase = true)

    /**
     * Two Cards are equal if they have the same term.
     */
    override fun equals(other: Any?): Boolean = other is Card && term.equals(other.term, ignoreCase = true)
    override fun hashCode(): Int = term.hashCode()

    override fun toString(): String = "$term$SEP$def$SEP$asked$SEP$mistakes"
}
