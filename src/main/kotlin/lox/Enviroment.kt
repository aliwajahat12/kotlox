package lox

import lox.scanner.Token

internal class Environment {
    private val values = hashMapOf<String, Any?>()

    operator fun get(name: Token): Any? {
        if (values.containsKey(name.lexeme)) {
            return values[name.lexeme]
        }
        throw RuntimeError(
            name,
            "Undefined variable '" + name.lexeme + "'."
        )
    }

    fun define(name: String, value: Any?) {
        values[name] = value
    }


}