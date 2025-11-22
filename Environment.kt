class Environment(
    private val enclosing: Environment? = null
) {
    private val values = mutableMapOf<String, Any?>()

    fun define(name: String, value: Any?) { values[name] = value }

    fun assign(name: String, value: Any?) {
        if (values.containsKey(name)) {
            values[name] = value
            return
        }
        enclosing?.assign(name, value)
            ?: error("Undefined variable '$name'")
    }

    fun get(name: String): Any? =
        values[name] ?: enclosing?.get(name)
        ?: error("Undefined variable '$name'")
}
