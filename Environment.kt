class Environment(
    private val enclosing: Environment? = null
) {
    private val values = mutableMapOf<String, Any?>()

    fun define(name: String, value: Any?) { 
        values[name] = value 
    }

    fun assign(name: String, value: Any?) {
        if (values.containsKey(name)) {
            values[name] = value
            return
        }
        if (enclosing != null) {
            enclosing.assign(name, value)
            return
        }
        // Create a dummy token for error reporting
        throw RuntimeError(
            Token(TokenType.IDENTIFIER, name, null, 0),
            "Undefined variable '$name'"
        )
    }

    fun get(name: String): Any? {
        // Check current scope first
        if (values.containsKey(name)) {
            return values[name]
        }
        // Check enclosing scope
        if (enclosing != null) {
            return enclosing.get(name)
        }
        // Create a dummy token for error reporting
        throw RuntimeError(
            Token(TokenType.IDENTIFIER, name, null, 0),
            "Undefined variable '$name'"
        )
    }
    
    /**
     * Creates a new environment with this environment as the enclosing scope.
     * Used for block statements to create nested scopes.
     */
    fun createChild(): Environment {
        return Environment(this)
    }
}
