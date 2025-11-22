val keywords: Map<String, TokenType> = mapOf(

    // Core keywords
    "let" to TokenType.LET,
    "set" to TokenType.SET,

    "if" to TokenType.IF,
    "then" to TokenType.THEN,
    "else" to TokenType.ELSE,
    "elseif" to TokenType.ELSEIF,
    "end" to TokenType.END,

    "print" to TokenType.PRINT,
    "log" to TokenType.LOG,

    // Literals 
    "true" to TokenType.TRUE,
    "false" to TokenType.FALSE,
    "null" to TokenType.NULL,

    "from" to TokenType.FROM,
    "to" to TokenType.TO,
    "step" to TokenType.STEP,

    // Logical words
    "and" to TokenType.AND,
    "or" to TokenType.OR,
    "not" to TokenType.NOT
)
