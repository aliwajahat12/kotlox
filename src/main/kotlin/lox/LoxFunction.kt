package lox

import Interpreter

class LoxFunction(private val declaration: Function) : LoxCallable {


    override fun arity(): Int {
        return declaration.params.size
    }

    override fun call(interpreter: Interpreter, arguments: MutableList<Any?>): Any? {
        val environment = Environment(interpreter.globals)
        for (i in declaration.params.indices) {
            environment.define(
                declaration.params[i].lexeme,
                arguments[i]
            )
        }

        try {
            interpreter.executeBlock(declaration.body, environment)
        } catch (returnValue: ReturnException) {
            return returnValue.value
        }
        
        return null
    }

    override fun toString(): String {
        return "<fn " + declaration.name.lexeme + ">"
    }
}