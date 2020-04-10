package flashcards

class Card(val term: String, var def: String = "") {
    var mistakes: Int = 0
    var asked: Int = 0

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

    fun isDefinedAs(def: String): Boolean = this.def?.equals(def, ignoreCase = true)

    /**
     * Two Cards are equal if they have the same term.
     */
    override fun equals(other: Any?): Boolean = other is Card && term.equals(other.term, ignoreCase = true)
    override fun hashCode(): Int = term.hashCode()

    override fun toString(): String = "$term$SEP$def$SEP$asked$SEP$mistakes"
}
