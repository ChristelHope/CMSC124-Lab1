object AstPrinter {
    fun print(stmt: Stmt): String =
        when (stmt) {
            is Stmt.Let -> "(let ${stmt.name.lexeme} ${printExpr(stmt.initializer)})"
            is Stmt.SetStmt -> "(set ${stmt.name.lexeme} ${printExpr(stmt.value)})"
            is Stmt.ExpressionStmt -> printExpr(stmt.expression)
            is Stmt.Print -> "(print ${printExpr(stmt.value)})"
            is Stmt.Block -> {
                val body = stmt.statements.joinToString(" ") { print(it) }
                "(block $body)"
            }
            is Stmt.IfStmt -> {
                val elsePart = stmt.elseBranch?.let { " ${print(it)}" } ?: ""
                "(if ${printExpr(stmt.condition)} ${print(stmt.thenBranch)}$elsePart)"
            }
        }

    fun printExpr(expr: Expr): String =
        when (expr) {
            is Expr.Literal -> literalToString(expr.value)
            is Expr.Variable -> expr.name.lexeme
            is Expr.Grouping -> "(group ${printExpr(expr.expression)})"
            is Expr.Unary -> {
                val symbol = when (expr.operator.type) {
                    TokenType.BANG -> "!"
                    TokenType.MINUS -> "-"
                    else -> expr.operator.lexeme
                }
                "($symbol ${printExpr(expr.right)})"
            }
            is Expr.Binary -> "(${expr.operator.lexeme} ${printExpr(expr.left)} ${printExpr(expr.right)})"
            is Expr.Call -> {
                val args = expr.arguments.joinToString(", ") { printExpr(it) }
                "(call ${printExpr(expr.callee)} ($args))"
            }
            is Expr.ListLiteral -> {
                val elems = expr.elements.joinToString(", ") { printExpr(it) }
                "[ $elems ]"
            }
        }

    private fun literalToString(value: Any?): String =
        when (value) {
            null -> "nil"
            is Double -> value.toString()
            is Boolean -> if (value) "true" else "false"
            is String -> "\"$value\""
            else -> value.toString()
        }
}

/*
this file implements the AstPrinter class, which traverses the Abst Syntax Tree & converts it into a readable, parenthesized string representation
 
 Purpose:
- to visualize the internal structure of the parsed syntax tree
- to assist with debugging and verifying parser output
 
 Ex:
>1 + 2 * 3
 (+ 1 (* 2 3))

key function:
 -print(expr: Expr): Returns a string representation of the expression tree
 
 importance:
  AstPrinter provides valuable insight into how the parser understands the input,
 ensuring correctness and aiding in debugging or later interpretation stages
 */
