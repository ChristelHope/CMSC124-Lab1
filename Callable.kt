package finlite

//Callable interface for functions and builtins.
//Uses RuntimeValue for type-safe function calls.
interface Callable {
    fun arity(): Int
    fun call(interpreter: Interpreter?, arguments: List<RuntimeValue?>): RuntimeValue?
}

//Create a builtin function from a lambda.
//The lambda receives strongly-typed RuntimeValue arguments.
fun builtin(fn: (List<RuntimeValue?>) -> RuntimeValue?): Callable {
    return object : Callable {
        override fun arity(): Int = -1
        override fun call(interpreter: Interpreter?, arguments: List<RuntimeValue?>): RuntimeValue? {
            return fn(arguments)
        }
        override fun toString(): String = "<builtin>"
    }
}