package flashcards

import java.lang.RuntimeException

/**
 * Manages command line arguments.
 *
 * This is a poor man's variant of the Golang flag package
 */
class Flag(private val args: Array<String>) {
    private val cliOptions = mutableMapOf<String, String?>()

    fun option(name: String, default: String? = null, isModeFlag: Boolean = false) {
        val optionIndex = args.indexOf("-$name")
        if (optionIndex >= 0) {
            cliOptions[name] = if (isModeFlag) {
                "true"
            } else {
                if (noArgumentFor(optionIndex)) throw RuntimeException("missing argument for option -$name")
                args[optionIndex + 1]
            }
        } else {
            cliOptions[name] = default
        }
    }

    private fun noArgumentFor(optionIndex: Int): Boolean =
        optionIndex !in args.indices || args[optionIndex + 1].startsWith("-")

    fun get(name: String): String? = cliOptions[name]
    fun getMode(name: String): Boolean = (cliOptions[name] ?: "false") == "true"
}
