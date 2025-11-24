fun main() {
    println("=== Enter prompt: ===")
    val interpreter = Interpreter()
    
    while (true) {
        val input = readMultilineInput() ?: break
        if (input.trim().isEmpty()) continue

        val scanner = Scanner(input)
        val tokens = scanner.scanTokens()

        val parser = Parser(tokens)
        val stmt = parser.parse()

        try {
            interpreter.execute(stmt)
        } catch (e: RuntimeError) {
            reportRuntimeError(e)
        } catch (e: Exception) {
            System.err.println("Error: ${e.message}")
        }
    }
}

fun reportRuntimeError(error: RuntimeError) {
    System.err.println("[line ${error.token.line}] Runtime error: ${error.message}")
}

fun readMultilineInput(): String? {
    print("> ")
    var input = readLine() ?: return null
    if (input.trim().isEmpty()) return "\n"
    input += "\n"
    //Keep reading lines until input is complete or user enters empty line
    while (true) {
        if (isInputComplete(input)) {
            print("... ")
            val nextLine = readLine() ?: return input
            if (nextLine.trim().isEmpty()) {
                return input
            }
            input += nextLine + "\n"
        } else {
            print("... ")
            val nextLine = readLine() ?: return input
            input += nextLine + "\n"
        }
    }
}

fun isInputComplete(input: String): Boolean {
    var parenthesesCount = 0
    var bracketCount = 0
    var braceCount = 0
    var blockDepth = 0
    var inString = false
    var inMultilineString = false
    var i = 0

    fun isIdentifierChar(c: Char): Boolean =
        c.isLetterOrDigit() || c == '_'

    while (i < input.length) {
        val c = input[i]

        // MULTILINE STRING """..."""
        if (i + 2 < input.length && input.substring(i, i + 3) == "\"\"\"") {
            inMultilineString = !inMultilineString
            i += 3
            continue
        }
        if (inMultilineString) {
            i++
            continue
        }

        // REGULAR STRING "..."
        if (c == '"') {
            var escaped = false
            var j = i - 1
            while (j >= 0 && input[j] == '\\') {
                escaped = !escaped
                j--
            }
            if (!escaped) inString = !inString
            i++
            continue
        }
        if (inString) {
            i++
            continue
        }

        // BRACKETS AND PARENTHESES
        when (c) {
            '(' -> parenthesesCount++
            ')' -> parenthesesCount--
            '[' -> bracketCount++
            ']' -> bracketCount--
            '{' -> braceCount++
            '}' -> braceCount--
        }

        // KEYWORD SCANNING FOR BLOCK STRUCTURE
        if (c.isLetter()) {
            val start = i
            var end = i + 1

            while (end < input.length && isIdentifierChar(input[end])) {
                end++
            }

            val keyword = input.substring(start, end)

            when (keyword.lowercase()) {
                "if", "while", "for", "scenario", "portfolio", "block" ->
                    blockDepth++

                "end" ->
                    blockDepth--

                // optional: "else" does NOT change block depth
            }

            i = end
            continue
        }

        i++
    }

    // CHECK COMPLETION CONDITIONS
    return parenthesesCount == 0 &&
        bracketCount == 0 &&
        braceCount == 0 &&
        blockDepth == 0 &&
        !inString &&
        !inMultilineString
}
