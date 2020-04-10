package flashcards

/**
 * Manages command line arguments.
 *
 * This is a poor man's variant of the Golang flag package
 */
class Flag(val args: Array<String>) {
    val cliOptions = mutableMapOf<String, Any>()

    constructor(args: Array<String>, required: Array<String>) : this(args) {
        TODO("Required parameters may be supported in the future.")
    }

    fun optional(name: String) = extractOption(name)

    private fun extractOption(name: String, isModeFlag: Boolean = false) {
        if (isModeFlag) {
            cliOptions[name] = true
        } else {
            (args.indexOf("-$name") + 1).let {
                if (it in 1 until args.size) cliOptions[name] = args[it]
            }
        }
    }

    fun getString(name: String): String? = cliOptions[name]?.toString()
}