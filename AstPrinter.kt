//print readable parenthesized representation of AST
//this prints the AST in the required parenthesized format

// AstPrinter.kt
object AstPrinter {
    fun print(stmt: Stmt): String {
        return when (stmt) {
            is Stmt.Expression -> printExpr(stmt.expression)
            is Stmt.Function -> {
                val params = stmt.params.joinToString(", ") { it.lexeme }
                val body = stmt.body.joinToString(" ") { print(it) }
                "(function ${stmt.name.lexeme} ($params) { $body })"
            }
            is Stmt.Block -> {
                val statements = stmt.statements.joinToString(" ") { print(it) }
                "(block $statements)"
            }
        }
    }

    fun printExpr(expr: Expr): String {
        return when (expr) {
            is Expr.Literal -> literalToString(expr.value)
            is Expr.Variable -> expr.name.lexeme
            is Expr.Grouping -> "(group ${printExpr(expr.expression)})"
            is Expr.Unary -> "(${expr.operator.lexeme} ${printExpr(expr.right)})"
            is Expr.Binary -> "(${expr.operator.lexeme} ${printExpr(expr.left)} ${printExpr(expr.right)})"
            is Expr.Call -> {
                val args = expr.arguments.joinToString(", ") { printExpr(it) }
                "(call ${printExpr(expr.callee)} ($args))"
            }
        }
    }

    private fun literalToString(value: Any?): String {
        return when (value) {
            null -> "nil"
            is Double -> value.toString()
            is Boolean -> if (value) "true" else "false"
            is String -> "\"$value\""
            else -> value.toString()
        }
    }
}
