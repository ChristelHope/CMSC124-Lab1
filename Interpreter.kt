class Interpreter {
    private var environment = Environment()

    fun execute(stmt: Stmt) {
        when (stmt) {
            is Stmt.Let -> {
                val value = evaluate(stmt.initializer)
                environment.define(stmt.name.lexeme, value)
            }

            is Stmt.SetStmt -> {
                val value = evaluate(stmt.value)
                environment.assign(stmt.name.lexeme, value)
            }

            is Stmt.Print -> {
                val value = evaluate(stmt.value)
                println(valueToString(value))
            }

            is Stmt.ExpressionStmt -> {
                val result = evaluate(stmt.expression)
                println(valueToString(result))
            }

            is Stmt.Block -> {
                val previous = environment
                environment = environment.createChild()
                try {
                    for (statement in stmt.statements) {
                        execute(statement)
                    }
                } finally {
                    environment = previous
                }
            }

            is Stmt.IfStmt -> {
                val condition = evaluate(stmt.condition)
                if (isTruthy(condition)) {
                    execute(stmt.thenBranch)
                } else {
                    stmt.elseBranch?.let { execute(it) }
                }
            }
        }
    }

    private fun evaluate(expr: Expr): Any? =
        when (expr) {
            is Expr.Literal -> expr.value
            is Expr.Variable -> environment.get(expr.name.lexeme)
            is Expr.Grouping -> evaluate(expr.expression)
            is Expr.Unary -> {
                val right = evaluate(expr.right)
                when (expr.operator.type) {
                    TokenType.MINUS -> {
                        if (right is Double) {
                            -right
                        } else {
                            throw RuntimeError(expr.operator, "Operand must be a number.")
                        }
                    }

                    TokenType.BANG, TokenType.NOT -> !isTruthy(right)
                    else -> throw RuntimeError(expr.operator, "Unknown unary operator.")
                }
            }

            is Expr.Binary -> {
                val left = evaluate(expr.left)
                val right = evaluate(expr.right)
                when (expr.operator.type) {
                    TokenType.PLUS -> when {
                        left is Double && right is Double -> left + right
                        left is String || right is String ->
                            valueToString(left) + valueToString(right)
                        else -> throw RuntimeError(
                            expr.operator,
                            "Operands must be numbers or strings."
                        )
                    }

                    TokenType.MINUS -> {
                        checkNumberOperands(expr.operator, left, right)
                        (left as Double) - (right as Double)
                    }

                    TokenType.STAR -> {
                        checkNumberOperands(expr.operator, left, right)
                        (left as Double) * (right as Double)
                    }

                    TokenType.SLASH -> {
                        checkNumberOperands(expr.operator, left, right)
                        if ((right as Double) == 0.0) {
                            throw RuntimeError(expr.operator, "Division by zero.")
                        }
                        (left as Double) / right
                    }

                    TokenType.GREATER -> {
                        checkNumberOperands(expr.operator, left, right)
                        (left as Double) > (right as Double)
                    }

                    TokenType.GREATER_EQUAL -> {
                        checkNumberOperands(expr.operator, left, right)
                        (left as Double) >= (right as Double)
                    }

                    TokenType.LESS -> {
                        checkNumberOperands(expr.operator, left, right)
                        (left as Double) < (right as Double)
                    }

                    TokenType.LESS_EQUAL -> {
                        checkNumberOperands(expr.operator, left, right)
                        (left as Double) <= (right as Double)
                    }

                    TokenType.EQUAL_EQUAL -> isEqual(left, right)
                    TokenType.BANG_EQUAL -> !isEqual(left, right)
                    else -> throw RuntimeError(expr.operator, "Unknown binary operator.")
                }
            }

            is Expr.ListLiteral -> expr.elements.map { evaluate(it) }
            is Expr.Call -> throw RuntimeError(
                Token(TokenType.ERROR, "", null, 0),
                "Unsupported expression: ${expr::class.simpleName}"
            )
        }

    private fun isTruthy(value: Any?): Boolean {
        if (value == null) return false
        if (value is Boolean) return value
        return true
    }

    private fun isEqual(a: Any?, b: Any?): Boolean {
        if (a == null && b == null) return true
        if (a == null) return false
        return a == b
    }

    private fun checkNumberOperands(operator: Token, left: Any?, right: Any?) {
        if (left is Double && right is Double) return
        throw RuntimeError(operator, "Operands must be numbers.")
    }

    private fun valueToString(value: Any?): String {
        return when (value) {
            null -> "nil"
            is Double ->
                if (value % 1.0 == 0.0) {
                    value.toInt().toString()
                } else {
                    value.toString()
                }

            else -> value.toString()
        }
    }
}

