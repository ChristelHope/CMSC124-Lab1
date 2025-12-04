package finlite

class RuntimeError(val token: Token?, message: String) : RuntimeException(message)

/**
 * ReturnValue is used to unwind the call stack when a return statement is executed.
 * It carries the return value through the exception mechanism.
 */
class ReturnValue(val value: RuntimeValue?) : RuntimeException()
