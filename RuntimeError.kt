class RuntimeError(val token: Token, message: String) : RuntimeException(message)

//to handle runtime evaluation errors w line numbers