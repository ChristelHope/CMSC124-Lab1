package finlite
import finlite.Callable

class Environment(
    private val enclosing: Environment? = null,
    val depth: Int = if (enclosing == null) 0 else enclosing.depth + 1
) {
    private val values = mutableMapOf<String, RuntimeValue?>()
    private val functions = mutableMapOf<String, FinLiteFunction>()

    fun define(name: String, value: RuntimeValue?) { 
        values[name] = value 
    }

    fun defineFunc(name: String, fn: FinLiteFunction) { functions[name] = fn }

    fun assign(name: String, value: RuntimeValue?): RuntimeValue? {
        if (values.containsKey(name)) {
            values[name] = value
            return value
        }
        if (enclosing != null) {
            return enclosing.assign(name, value)
        }
        throw RuntimeError(
            null,
            "Undefined variable '$name' at scope depth $depth"
        )
    }
    
    fun get(name: String): RuntimeValue? {
        // Check current scope first
        if (values.containsKey(name)) {
            return values[name]
        }
        // Check enclosing scope
        if (enclosing != null) {
            return enclosing.get(name)
        }
        throw RuntimeError(
            null,
            "Undefined variable '$name' at scope depth $depth"
        )
    }
    
    fun getAtDepth(targetDepth: Int, name: String): RuntimeValue? {
        if (targetDepth < 0 || targetDepth > depth) {
            throw RuntimeError(null, "Invalid scope depth $targetDepth (current depth: $depth)")
        }
        var current: Environment? = this
        var currentDepth = depth
        while (current != null && currentDepth > targetDepth) {
            current = current.enclosing
            currentDepth--
        }
        return current?.get(name)
    }
    
    fun getGlobal(name: String): RuntimeValue? {
        var current: Environment? = this
        while (current?.enclosing != null) {
            current = current.enclosing
        }
        return current?.get(name)
    }
    
    fun getParent(name: String): RuntimeValue? {
        return enclosing?.get(name) ?: throw RuntimeError(
            null,
            "No parent scope available (at depth $depth)"
        )
    }
    
    fun createChild(): Environment {
        return Environment(this)
    }

    fun getOrNull(name: String): RuntimeValue? = 
        if (values.containsKey(name)) values[name] else null
    
    fun getAllVariables(): Map<String, RuntimeValue?> = values.toMap()
    
    fun getScopeChain(): List<Environment> {
        val chain = mutableListOf<Environment>()
        var current: Environment? = this
        while (current != null) {
            chain.add(current)
            current = current.enclosing
        }
        return chain
    }
}

