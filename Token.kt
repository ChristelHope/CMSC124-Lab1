data class Token(                                                                              // token holds info about each recognized piece of source code
    val type: TokenType,                                                                           // category/type of token (from enum above)
    val lexeme: String,                                                                            // the actual text matched from the source
    val literal: Any?,                                                                             // literal value (e.g. number value, string contents), null if none
    val line: Int,                                                                                 // line number in source (for error reporting)
)

/*
file defines the Token data class used throughout the scanner and parser.
 each Token object holds essential info about a lexical unit in the source code.

Purpose:
-to encapsulate all attributes of a token (type, lexeme, literal, line)
-to provide an easy-to-read string representation for debugging and output

Key components:
-data class Token: represents one lexical unit w 4 properties:
      -type: tokenType (enum)
      -lexeme: actual text of the token
      -literal: Any literal value (e.g., number, string)
      -line: line number where token appears
 
Importance:
 -tokens act as the bridge between the Scanner (lexical analysis) and the
 -parser (syntactic analysis), forming the backbone of the language processor.
 */
