import java.lang.RuntimeException

// Global environment and interpreter
val environment = Environment()
val coreEvaluator = Evaluator()

fun main() {                                                                              // REPL: lets the user interactively type code & see the tokens generated
    println("Type in your prompt below and see the tokens generated.")    

    while (true) {
        val input = readMultilineInput() ?: break                                            // read multiline input (exit if null / Ctrl+D)
        if(input.trim().isEmpty()) continue
        
        val scanner = Scanner(input)                                                         // to create scanner for that input
        val tokens = scanner.scanTokens()                                                     // to scan tokens

        val parser = Parser(tokens)                                                           // to create parser for those tokens
        val statements = parser.parse()                                                       // to parse tokens into statements (AST)

        if(statements.isNotEmpty()) {                                                                    // if parsing was successful
              try {
                // Execute all statements
                for (stmt in statements){
                    execute(stmt)
                }

            } catch (e: RuntimeError) {
                //to handle runtime errors
                reportRuntimeError(e)
            } catch (e: Exception) {
                System.err.println("Error: ${e.message}")
            }                                                
        } else {
            println("Parsing error occurred.")                                                // if there was a parsing error
        }
    }
}

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
            for (s in stmt.statements) {
                execute(s)
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

fun evaluate(expr: Expr): Any? {
    return when (expr) {
        is Expr.Literal -> expr.value
        is Expr.Variable -> environment.get(expr.name.lexeme)
        is Expr.Grouping -> evaluate(expr.expression)
        is Expr.Unary -> {
            val right = evaluate(expr.right)
            when (expr.operator.type) {
                TokenType.MINUS -> {
                    if (right is Double) -right
                    else throw RuntimeError(expr.operator, "Operand must be a number.")
                }
                TokenType.BANG, TokenType.NOT -> !isTruthy(right)
                else -> throw RuntimeError(expr.operator, "Unknown unary operator.")
            }
        }
        is Expr.Binary -> {
            val left = evaluate(expr.left)
            val right = evaluate(expr.right)
            when (expr.operator.type) {
                TokenType.PLUS -> {
                    if (left is Double && right is Double) left + right
                    else if (left is String && right is String) left + right
                    else throw RuntimeError(expr.operator, "Operands must be two numbers or two strings.")
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
                    if ((right as Double) == 0.0) throw RuntimeError(expr.operator, "Division by zero.")
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
        is Expr.Call -> {
            val callee = evaluate(expr.callee)
            val arguments = expr.arguments.map { evaluate(it) }
            throw RuntimeError(expr.paren, "Can only call functions.")
        }
        is Expr.ListLiteral -> {
            expr.elements.map { evaluate(it) }
        }
    }
}

fun isTruthy(value: Any?): Boolean {
    if (value == null) return false
    if (value is Boolean) return value
    return true
}

fun isEqual(a: Any?, b: Any?): Boolean {
    if (a == null && b == null) return true
    if (a == null) return false
    return a == b
}

fun checkNumberOperands(operator: Token, left: Any?, right: Any?) {
    if (left is Double && right is Double) return
    throw RuntimeError(operator, "Operands must be numbers.")
}

    /**
    * Reads multiline input from the user, continuing until the input is complete.
    * Returns null if EOF is encountered (Ctrl+D).
    * 
    * Continues reading if:
    * - Input is syntactically incomplete (unmatched brackets, strings, etc.)
    * 
    * Stops reading when:
    * - User presses Enter on an empty line (signals completion for multiple statements)
    * - Input is syntactically complete (single statement)
    */
    fun readMultilineInput(): String? {
        print("> ")
        var input = readLine() ?: return null
        if (input.trim().isEmpty()) return "\n"  // Return just newline for empty input
        
        // Always append newline since readLine() doesn't include it
        input += "\n"
        
        // Keep reading lines until input is complete or user enters empty line
        while (true) {
            // If input is syntactically complete, check if user wants to add more
            // by reading one more line - if it's empty, we're done
            if (isInputComplete(input)) {
                print("... ")
                val nextLine = readLine() ?: return input
                
                // If empty line, user is done entering statements
                if (nextLine.trim().isEmpty()) {
                    return input
                }
                
                // User wants to continue with more statements
                input += nextLine + "\n"
            } else {
                // Input is incomplete, must continue reading
                print("... ")
                val nextLine = readLine() ?: return input
                input += nextLine + "\n"
            }
        }
    }

    /**
    * Checks if the input string is syntactically complete.
    * Returns true if all brackets, parentheses, and strings are properly closed.
    */
    fun isInputComplete(input: String): Boolean {
        var parenCount = 0
        var bracketCount = 0
        var blockDepth = 0
        var inString = false
        var inMultilineString = false
        var i = 0

        fun isIdentifierChar(c: Char): Boolean =
            c.isLetterOrDigit() || c == '_'

        while (i < input.length) {
            val c = input[i]

            // --------------------------------------------
            // MULTILINE STRING """..."""
            // --------------------------------------------
            if (i + 2 < input.length && input.substring(i, i + 3) == "\"\"\"") {
                inMultilineString = !inMultilineString
                i += 3
                continue
            }
            if (inMultilineString) {
                i++
                continue
            }

            // --------------------------------------------
            // REGULAR STRING "..."
            // --------------------------------------------
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

            // --------------------------------------------
            // BRACKETS AND PARENTHESES
            // --------------------------------------------
            when (c) {
                '(' -> parenCount++
                ')' -> parenCount--
                '[' -> bracketCount++
                ']' -> bracketCount--
            }

            // --------------------------------------------
            // KEYWORD SCANNING FOR BLOCK STRUCTURE
            // --------------------------------------------
            if (c.isLetter()) {
                val start = i
                var end = i + 1

                while (end < input.length && isIdentifierChar(input[end])) {
                    end++
                }

                val keyword = input.substring(start, end)

                when (keyword.lowercase()) {
                    "if", "while", "for", "block" ->
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

        // --------------------------------------------
        // CHECK COMPLETION CONDITIONS
        // --------------------------------------------
        return parenCount == 0 &&
            bracketCount == 0 &&
            blockDepth == 0 &&
            !inString &&
            !inMultilineString
    }


fun valueToString(value: Any?): String {
    return when (value) {
        null -> "nil"
        is Double ->
            if (value % 1.0 == 0.0)
                value.toInt().toString()     // remove .0 for whole numbers
            else
                value.toString()             // keep decimals
        else -> value.toString()
    }
}

fun reportRuntimeError(error: RuntimeError) {
    System.err.println("[line ${error.token.line}] Runtime error: ${error.message}")
}


/*
 This file serves as the main entry point of the program. It integrates the
 Scanner and Parser modules to process a source code input, convert it into a stream of tokens, and then parse these tokens into an Abstract Syntax Tree (AST).
 The resulting AST is printed using the AstPrinter for verification.
 
 Purpose:
 -to connect all major components (Scanner, Parser, AST Printer)
 -to execute the full lexical + syntactic analysis flow
 -to simulate a REPL-like environment for user testing and debugging
 
  Key components:
  -main(): initializes the scanner and parser, handles user input, and prints results.
 
Ex:
 >kotlin MainKt
 Input:dehins omsim
 Output:(! true)
 */
