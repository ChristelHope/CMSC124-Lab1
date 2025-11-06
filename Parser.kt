class ParseError(message: String) : RuntimeException(message)

class Parser(private val tokens: List<Token>) {
    private var current = 0

    fun parse(): List<Stmt> {
        val statements = mutableListOf<Stmt>()
        while (!isAtEnd()) {
            statements.add(declaration())
        }
        return statements
    }

    // declaration → functionDeclaration | statement
    private fun declaration(): Stmt {
        return try {
            if (match(TokenType.FN)) return functionDeclaration("function")
            statement()
        } catch (e: ParseError) {
            synchronize()
            throw e
        }
    }

    // functionDeclaration
    private fun functionDeclaration(kind: String): Stmt.Function {
        val name = consume(TokenType.IDENTIFIER, "Expect $kind name.")
        consume(TokenType.LEFT_PAREN, "Expect left parenthesis after $kind name.")
        
        val parameters = mutableListOf<Token>()
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                if (parameters.size >= 255) {
                    error(peek(), "Can't have more than 255 parameters.")
                }
                parameters.add(consume(TokenType.IDENTIFIER, "Expect parameter name."))
            } while (match(TokenType.COMMA))
        }
        consume(TokenType.RIGHT_PAREN, "Expect right parenthesis after parameters.")
        consume(TokenType.LEFT_BRACE, "Expect left brace before $kind body.")
        val body = block()
        return Stmt.Function(name, parameters, body.statements)
    }

    // statement → expressionStmt | blockStmt
    private fun statement(): Stmt {
        if (match(TokenType.LEFT_BRACE)) return Stmt.Block(block().statements)
        return expressionStatement()
    }

    // block → "{" declaration* "}"
    private fun block(): Stmt.Block {
        val statements = mutableListOf<Stmt>()
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration())
        }
        consume(TokenType.RIGHT_BRACE, "Expect right brace after block.")
        return Stmt.Block(statements)
    }

    // expressionStmt → expression ";"
    private fun expressionStatement(): Stmt {
        val expr = expression()
        consume(TokenType.SEMICOLON, "Expect ';' after expression.")
        return Stmt.Expression(expr)
    }

    // expression → equality
    private fun expression(): Expr {
        return equality()
    }

    // equality → comparison ( ( "!=" | "==" ) comparison )*
    private fun equality(): Expr {
        var expr = comparison()
        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            val op = previous()
            val right = comparison()
            expr = Expr.Binary(expr, op, right)
        }
        return expr
    }

    // comparison → term ( ( ">" | ">=" | "<" | "<=" ) term )*
    private fun comparison(): Expr {
        var expr = term()
        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            val op = previous()
            val right = term()
            expr = Expr.Binary(expr, op, right)
        }
        return expr
    }

    // term → factor ( ( "-" | "+" ) factor )*
    private fun term(): Expr {
        var expr = factor()
        while (match(TokenType.MINUS, TokenType.PLUS)) {
            val op = previous()
            val right = factor()
            expr = Expr.Binary(expr, op, right)
        }
        return expr
    }

    // factor → unary ( ( "/" | "*" ) unary )*
    private fun factor(): Expr {
        var expr = unary()
        while (match(TokenType.SLASH, TokenType.STAR)) {
            val op = previous()
            val right = unary()
            expr = Expr.Binary(expr, op, right)
        }
        return expr
    }

    // unary → ( "!" | "-" ) unary | call
    private fun unary(): Expr {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            val op = previous()
            val right = unary()
            return Expr.Unary(op, right)
        }
        return call()
    }

    // call → primary ( "(" arguments? ")" )*
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

    // finishCall → "(" arguments? ")"
    private fun finishCall(callee: Expr): Expr {
        val arguments = mutableListOf<Expr>()
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                if (arguments.size >= 255) {
                    error(peek(), "Can't have more than 255 arguments.")
                }
                arguments.add(expression())
            } while (match(TokenType.COMMA))
        }
        val paren = consume(TokenType.RIGHT_PAREN, "Expect ')' after arguments.")
        return Expr.Call(callee, paren, arguments)
    }

    // primary → NUMBER | STRING | "true" | "false" | "null" | IDENTIFIER | "(" expression ")"
    private fun primary(): Expr {
        if (match(TokenType.NUMBER)) {
            return Expr.Literal(previous().literal)
        }
        if (match(TokenType.STRING)) {
            return Expr.Literal(previous().literal)
        }
        if (match(TokenType.TRUE)) return Expr.Literal(true)
        if (match(TokenType.FALSE)) return Expr.Literal(false)
        if (match(TokenType.NULL)) return Expr.Literal(null)
        if (match(TokenType.IDENTIFIER)) {
            return Expr.Variable(previous())
        }

        if (match(TokenType.LEFT_PAREN)) {
            val expr = expression()
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.")
            return Expr.Grouping(expr)
        }

        throw error(peek(), "Expect expression.")
    }

    // HELPERS
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

    private fun check(type: TokenType): Boolean {
        if (isAtEnd()) return false
        return peek().type == type
    }

    private fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    private fun isAtEnd(): Boolean = peek().type == TokenType.EOF

    private fun peek(): Token = tokens[current]

    private fun previous(): Token = tokens[current - 1]

    private fun error(token: Token, message: String): ParseError {
        val where = if (token.type == TokenType.EOF) "at end" else "at '${token.lexeme}'"
        val full = "[line ${token.line}] Error $where: $message"
        System.err.println(full)
        return ParseError(full)
    }

    private fun synchronize() {
        advance()
        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMICOLON) return
            when (peek().type) {
                TokenType.FN, TokenType.IF, TokenType.WHILE, TokenType.FOR, TokenType.RETURN -> return
                else -> advance()
            }
        }
    }
}
