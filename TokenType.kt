enum class TokenType {

    // Single-character tokens
    LEFT_PAREN, RIGHT_PAREN,
    LEFT_BRACKET, RIGHT_BRACKET,
    COMMA, DOT, COLON,

    PLUS, MINUS, STAR, SLASH, PERCENT, CARET,

    // One or two character tokens
    EQUAL, EQUAL_EQUAL,
    BANG, BANG_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,
    AND_AND, OR_OR,

    // Literals
    IDENTIFIER,
    NUMBER,
    STRING,
    MULTILINE_STRING,
    DATE,
    TRUE, FALSE,
    NULL,

    // Keywords
    LET, SET,
    IF, THEN, ELSE, ELSEIF, END,
    PRINT, LOG,

    FROM, TO, STEP,

    // Logical words
    AND, OR, NOT,

    // Misc
    NEWLINE, INDENT, DEDENT, EOF, ERROR
}


/*
 file contains the TokenType enumeration -->which lists all possiblecategories of tokens recognized by the language (operators,literals,keywords,punctuation,etc.)
 
 Purpose:
  -to classify each token produced by the Scanner
  -to provide a consistent reference of all valid token types across modules
 
 Ex TokenType entries:
 -Arithmetic: PLUS, MINUS, STAR, SLASH
 -Comparison: LESS, GREATER, EQUAL_EQUAL
 -Logical: AND, OR, BANG    (mapped from dehins)
 -Literals: IDENTIFIER, STRING, NUMBER
 -Keywords: IF, ELSE, TRUE, FALSE, etc.
 
 importance:
  parser & scanner rely on this enumeration to maintain consistency in recognizing & interpreting diff language constructs
 */
