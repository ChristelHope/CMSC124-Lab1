class Scanner(private val source: String) {

    private var start = 0                // start index of current lexeme
    private var current = 0              // index currently being read
    private var line = 1                 // line counter (for error reporting)

    private val tokens = mutableListOf<Token>()
    private val indentStack = mutableListOf<Int>()  // Track indentation levels
    private var atStartOfLine = true     // Track if we're at start of a line

    // PUBLIC: scanTokens() – entry point
    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            start = current
            if (atStartOfLine) {
                handleIndentation()
            }
            scanToken()
        }

        while (indentStack.isNotEmpty()) {
            indentStack.removeAt(indentStack.size - 1)
            tokens.add(Token(TokenType.DEDENT, "", null, line))
        }

        tokens.add(Token(TokenType.EOF, "", null, line))
        return tokens
    }

    // Basic helpers
    private fun isAtEnd(): Boolean = current >= source.length

    private fun advance(): Char = source[current++]

    private fun peek(): Char =
        if (isAtEnd()) '\u0000' else source[current]

    private fun peekNext(): Char =
        if (current + 1 >= source.length) '\u0000' else source[current + 1]

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != expected) return false
        current++
        return true
    }

    private fun addToken(type: TokenType, literal: Any? = null) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

        private fun handleIndentation() {
        var indentLevel = 0
        while (peek() == ' ' || peek() == '\t') {
            if (peek() == ' ') {
                indentLevel++
            } else if (peek() == '\t') {
                indentLevel += 4 // Treat tab as 4 spaces
            }
            advance()
        }
        
        // Skip if line is empty (only whitespace)
        if (peek() == '\n' || isAtEnd()) {
            atStartOfLine = false
            return
        }
        
        val currentIndent = if (indentStack.isEmpty()) 0 else indentStack.last()
        
        if (indentLevel > currentIndent) {
                                                                                                        // Indent
            indentStack.add(indentLevel)
            tokens.add(Token(TokenType.INDENT, "", indentLevel, line))
        } else if (indentLevel < currentIndent) {
                                                                                                        // Dedent
            while (indentStack.isNotEmpty() && indentStack.last() > indentLevel) {
                indentStack.removeAt(indentStack.size - 1)
                tokens.add(Token(TokenType.DEDENT, "", null, line))
            }
            if (indentStack.isEmpty() || indentStack.last() != indentLevel) {
                println("Error: inconsistent indentation at line $line")
            }
        }
        
        atStartOfLine = false
    }


    private fun scanToken() {
        val c = advance()

        when (c) {

            // Delimiters
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '[' -> addToken(TokenType.LEFT_BRACKET)
            ']' -> addToken(TokenType.RIGHT_BRACKET)
            ',' -> addToken(TokenType.COMMA)
            ':' -> addToken(TokenType.COLON)
            '.' -> addToken(TokenType.DOT)

            // Arithmetic
            '+' -> addToken(TokenType.PLUS)
            '-' -> addToken(TokenType.MINUS)
            '*' -> addToken(TokenType.STAR)
            '%' -> addToken(TokenType.PERCENT)
            '^' -> addToken(TokenType.CARET)

            // Slash / comments
            '/' -> {
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) advance()
                } else if (match('*')) {
                    handleBlockComment()
                } else {
                    addToken(TokenType.SLASH)
                }
            }

            // Comparison
            '=' -> addToken(if (match('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL)
            '!' -> addToken(if (match('=')) TokenType.BANG_EQUAL else TokenType.BANG)
            '<' -> addToken(if (match('=')) TokenType.LESS_EQUAL else TokenType.LESS)
            '>' -> addToken(if (match('=')) TokenType.GREATER_EQUAL else TokenType.GREATER)

            // Logical operators
            '&' -> {
                if (match('&')) addToken(TokenType.AND_AND)
                else addToken(TokenType.ERROR, "&")
            }
            '|' -> {
                if (match('|')) addToken(TokenType.OR_OR)
                else addToken(TokenType.ERROR, "|")
            }

            // Strings
            '"' -> {
                if (match('"') && match('"')) multilineString()
                else string()
            }

            // Whitespace
            ' ', '\r', '\t' -> {}
            
            // Newline
            '\n' -> {
                line++
                atStartOfLine = true
                addToken(TokenType.NEWLINE)
            }

            // Literals & identifiers
            else -> when {
                c.isDigit() -> numberOrMoneyOrDate()
                c.isLetter() || c == '_' -> identifierOrKeywordOrMoney()
                else -> {
                    error("Unexpected character '$c'")
                    addToken(TokenType.ERROR, c.toString())
                }
            }
        }
    }

    // Block comment scanning
    private fun handleBlockComment() {
        while (!isAtEnd()) {
            if (peek() == '*' && peekNext() == '/') {
                advance()
                advance()
                return
            }
            if (peek() == '\n') line++
            advance()
        }
        error("Unterminated block comment")
    }

    // NUMBERS, MONEY, DATES
    private fun numberOrMoneyOrDate() {

        while (peek().isDigit() || peek() == '_' || peek() == ',') advance()

        // Decimal fraction
        if (peek() == '.' && peekNext().isDigit()) {
            advance()
            while (peek().isDigit() || peek() == '_' || peek() == ',') advance()
        }

        val raw = source.substring(start, current)
        val normalized = raw.replace(",", "").replace("_", "")

        // DATE literal check: YYYY-MM-DD
        if (isDateLiteral()) {
            val dateText = source.substring(start, start + 10)
            current = start + 10
            addToken(TokenType.DATE, dateText)
            return
        }

        val numericValue = normalized.toDouble()

        // Plain number
        addToken(TokenType.NUMBER, numericValue)
    }


    private fun isDateLiteral(): Boolean {
        if (start + 9 >= source.length) return false
        val segment = source.substring(start, start + 10)
        return Regex("""\d{4}-\d{2}-\d{2}""").matches(segment)
    }

    // IDENTIFIERS, KEYWORDS, ISO MONEY PREFIXES
    private fun identifierOrKeywordOrMoney() {

        while (peek().isLetterOrDigit() || peek() == '_') advance()

        val text = source.substring(start, current)
        val lower = text.lowercase()

        // keyword match
        val keyword = keywords[lower]
        if (keyword != null) {
            addToken(keyword)
            return
        }

        // Regular identifier
        addToken(TokenType.IDENTIFIER, text)
    }

    // STRINGS
    private fun string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++
            advance()
        }

        if (isAtEnd()) {
            error("Unterminated string literal")
            return
        }

        advance()

        val content = source.substring(start + 1, current - 1)
        addToken(TokenType.STRING, content)
    }


    private fun multilineString() {
        // We have already consumed """.
        while (!isAtEnd()) {
            if (peek() == '"' && peekNext() == '"'
                && source.getOrNull(current + 2) == '"'
            ) {
                advance(); advance(); advance() // consume """
                val content = source.substring(start + 3, current - 3)
                addToken(TokenType.MULTILINE_STRING, content)
                return
            }
            if (peek() == '\n') line++
            advance()
        }

        error("Unterminated multiline string")
    }

    // Utility: skip whitespace (not newline)
    private fun skipWhitespace() {
        while (peek() == ' ' || peek() == '\t' || peek() == '\r') advance()
    }

    private fun error(message: String) {
        System.err.println("[line $line] Error: $message")
    }

    // Safe character access
    private fun CharSequence.getOrNull(i: Int): Char? =
        if (i in 0 until this.length) this[i] else null
}
