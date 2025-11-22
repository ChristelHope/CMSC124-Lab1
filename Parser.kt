import TokenType.*

class Parser(private val tokens: List<Token>) {

    private var current = 0

    fun parse(): List<Stmt> {
        val statements = mutableListOf<Stmt>()
        while (!isAtEnd()) {
            statements.add(statement())
        }
        return statements
    }

    // ==========================================================
    // STATEMENTS
    // ==========================================================

    private fun statement(): Stmt {
        if (match(TokenType.LET)) return letStmt()
        if (match(TokenType.SET)) return setStmt()
        if (match(TokenType.PRINT, TokenType.LOG)) return printStmt()
        if (match(TokenType.IF)) return ifStmt()

        return exprStmt()
    }

    private fun letStmt(): Stmt {
        val name = consume(TokenType.IDENTIFIER, "Expect variable name.")
        consume(TokenType.EQUAL, "Expect '=' after variable name.")
        val initializer = expression()
        if (match(TokenType.NEWLINE) || match(TokenType.EOF)) {
            // ok
        } else {
            error(peek(), "Expect newline after LET statement.")
        }
        return Stmt.Let(name, initializer)
    }

    private fun setStmt(): Stmt {
        val name = consume(TokenType.IDENTIFIER, "Expect variable name.")
        consume(TokenType.EQUAL, "Expect '=' after variable name.")
        val value = expression()
        consume(TokenType.NEWLINE, "Expect newline after SET statement.")
        return Stmt.SetStmt(name, value)
    }

    private fun printStmt(): Stmt {
        val expr = expression()
        consume(TokenType.NEWLINE, "Expect newline after PRINT.")
        return Stmt.Print(expr)
    }

    private fun exprStmt(): Stmt {
        val expr = expression()
        consume(TokenType.NEWLINE, "Expect newline after expression.")
        return Stmt.ExpressionStmt(expr)
    }

    private fun ifStmt(): Stmt {
        val condition = expression()
        consume(TokenType.THEN, "Expect THEN after IF condition.")
        consume(TokenType.NEWLINE, "Expect newline after THEN.")

        val thenBranch = block()

        var elseBranch: Stmt? = null
        if (match(TokenType.ELSE)) {
            consume(TokenType.NEWLINE, "Expect newline after ELSE.")
            elseBranch = block()
        }

        consume(TokenType.END, "Expect END after IF block.")
        consume(TokenType.NEWLINE, "Expect newline after END.")

        return Stmt.IfStmt(condition, thenBranch, elseBranch)
    }

    private fun block(): Stmt {
        val statements = mutableListOf<Stmt>()
        while (!check(TokenType.END) && !check(TokenType.ELSE) && !isAtEnd()) {
            statements.add(statement())
        }
        return Stmt.Block(statements)
    }

    // ==========================================================
    // EXPRESSIONS (Pratt Parser)
    // ==========================================================

    private fun expression(): Expr = assignment()

    private fun assignment(): Expr {
        val expr = logicOr()

        if (match(TokenType.EQUAL)) {
            val equals = previous()
            val value = assignment()

            if (expr is Expr.Variable) {
                return Expr.Variable(expr.name.copy(type = TokenType.IDENTIFIER)).let {
                    Expr.Binary(expr, equals, value)
                }
            }

            error(equals, "Invalid assignment target.")
        }

        return expr
    }

    private fun logicOr(): Expr {
        var expr = logicAnd()

        while (match(TokenType.OR, TokenType.OR_OR)) {
            val op = previous()
            val right = logicAnd()
            expr = Expr.Binary(expr, op, right)
        }

        return expr
    }

    private fun logicAnd(): Expr {
        var expr = equality()

        while (match(TokenType.AND, TokenType.AND_AND)) {
            val op = previous()
            val right = equality()
            expr = Expr.Binary(expr, op, right)
        }

        return expr
    }

    private fun equality(): Expr {
        var expr = comparison()

        while (match(TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL)) {
            val op = previous()
            val right = comparison()
            expr = Expr.Binary(expr, op, right)
        }

        return expr
    }

    private fun comparison(): Expr {
        var expr = term()

        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL,
                     TokenType.LESS, TokenType.LESS_EQUAL)) {
            val op = previous()
            val right = term()
            expr = Expr.Binary(expr, op, right)
        }

        return expr
    }

    private fun term(): Expr {
        var expr = factor()

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            val op = previous()
            val right = factor()
            expr = Expr.Binary(expr, op, right)
        }

        return expr
    }

    private fun factor(): Expr {
        var expr = unary()

        while (match(TokenType.STAR, TokenType.SLASH, TokenType.PERCENT)) {
            val op = previous()
            val right = unary()
            expr = Expr.Binary(expr, op, right)
        }

        return expr
    }

    private fun unary(): Expr {
        if (match(TokenType.BANG, TokenType.NOT, TokenType.MINUS)) {
            val op = previous()
            val right = unary()
            return Expr.Unary(op, right)
        }

        return call()
    }

    private fun call(): Expr {
        var expr = primary()

        while (true) {
            if (match(TokenType.LEFT_PAREN)) {
                expr = finishCall(expr)
            } else {
                break
            }
        }

        return expr
    }

    private fun primary(): Expr {
        if (match(TokenType.TRUE)) return Expr.Literal(true)
        if (match(TokenType.FALSE)) return Expr.Literal(false)
        if (match(TokenType.NULL)) return Expr.Literal(null)
        
        if (match(TokenType.NUMBER, TokenType.STRING, TokenType.MULTILINE_STRING)) {
            return Expr.Literal(previous().literal)
        }
        
        // Handle identifiers
        if (match(TokenType.IDENTIFIER)) {
            return Expr.Variable(previous())
        }
        
        if (match(TokenType.LEFT_PAREN)) {
            val expr = expression()
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.")
            return Expr.Grouping(expr)
        }
        
        if (match(TokenType.LEFT_BRACKET)) {
            val elements = mutableListOf<Expr>()
            if (!check(TokenType.RIGHT_BRACKET)) {
                do {
                    elements.add(expression())
                } while (match(TokenType.COMMA))
            }
            consume(TokenType.RIGHT_BRACKET, "Expect ']' after list elements.")
            return Expr.ListLiteral(elements)
        }
        
        throw error(peek(), "Expected expression.")
    }

    private fun finishCall(callee: Expr): Expr {
        val args = mutableListOf<Expr>()
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                if (args.size >= 255) {
                    error(peek(), "Can't have more than 255 arguments.")
                }
                args.add(expression())
            } while (match(TokenType.COMMA))
        }
        val paren = consume(TokenType.RIGHT_PAREN, "Expect ')' after arguments.")

        return Expr.Call(callee, paren, args)
    }

    // ==========================================================
    // Helpers
    // ==========================================================

    private fun match(vararg types: TokenType): Boolean {
        for (t in types) {
            if (check(t)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()
        throw error(peek(), message)
    }

    private fun check(type: TokenType): Boolean =
        if (isAtEnd()) false else peek().type == type

    private fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    private fun isAtEnd(): Boolean =
        peek().type == TokenType.EOF

    private fun peek(): Token = tokens[current]

    private fun previous(): Token = tokens[current - 1]

    // ==========================================================
    // Error handling
    // ==========================================================
    private fun error(token: Token, message: String): RuntimeException {
        System.err.println("[line ${token.line}] Error at '${token.lexeme}': $message")
        return RuntimeException(message)
    }

    private fun synchronize() {
        advance()

        while (!isAtEnd()) {
            if (previous().type == TokenType.NEWLINE) return

            when (peek().type) {
                TokenType.LET, TokenType.SET, TokenType.IF,
                TokenType.PRINT, TokenType.LOG ->
                    return
                else -> {}
            }

            advance()
        }
    }
}
