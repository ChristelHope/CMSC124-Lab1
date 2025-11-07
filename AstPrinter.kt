object AstPrinter {
    fun print(expr: Expr): String {
        return when (expr) {
            is Expr.Literal -> literalToString(expr.value)
            is Expr.Variable -> expr.name.lexeme
            is Expr.Grouping -> "(group ${print(expr.expression)})"
            is Expr.Unary -> {
                val symbol = when (expr.operator.type) {
                    TokenType.BANG -> "!"
                    TokenType.MINUS -> "-"
                    else -> expr.operator.lexeme
                }
                "($symbol ${print(expr.right)})"
            }
            is Expr.Binary -> "(${expr.operator.lexeme} ${print(expr.left)} ${print(expr.right)})"
            is Expr.Assign -> "(${expr.name.lexeme} = ${print(expr.value)})"
            is Expr.Call -> "(call ${print(expr.callee)} ${expr.arguments.joinToString(" ") { print(it) }})"
            is Expr.Get -> "(get ${print(expr.obj)} ${expr.name.lexeme})"
            is Expr.This -> "this"
        }
    }

    private fun literalToString(value: Any?): String {
        return when (value) {
            null -> "nil"
            is Double -> value.toString()
            else -> value.toString()
        }
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
