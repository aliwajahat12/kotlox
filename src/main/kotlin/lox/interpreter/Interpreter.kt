package lox.interpreter

import lox.parser.*
import lox.scanner.TokenType.*


class Interpreter : Visitor<Any?> {
    override fun visitBinaryExpr(expr: Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        when (expr.operator.type) {
            GREATER -> return left as Double > right as Double
            GREATER_EQUAL -> return left as Double >= right as Double
            LESS -> return left as Double < right as Double
            LESS_EQUAL -> return left as Double <= right as Double
            MINUS -> return left as Double - right as Double
            SLASH -> return left as Double / right as Double
            STAR -> return left as Double * right as Double
            PLUS ->
                if (left is Double && right is Double) {
                    return left + right
                } else if (left is String && right is String) {
                    return left + right
                }

            BANG_EQUAL -> return !isEqual(left, right)
            EQUAL_EQUAL -> return isEqual(left, right)

            else -> {}
        }

        // Unreachable.

        // Unreachable.
        return null
    }

    override fun visitGroupingExpr(expr: Grouping): Any? {
        return evaluate(expr.expression)
    }

    override fun visitLiteralExpr(expr: Literal): Any? {
        return expr.value
    }

    override fun visitUnaryExpr(expr: Unary): Any? {
        val right = evaluate(expr.right)

        when (expr.operator.type) {
            MINUS -> return -right as Double
            BANG -> return !isTruthy(right)
            else -> {}
        }

        // Unreachable.
        return null
    }

    private fun evaluate(expr: Expr): Any? {
        return expr.accept(this)
    }

    private fun isTruthy(`object`: Any?): Boolean {
        if (`object` == null) return false
        return if (`object` is Boolean) `object` else true
    }

    private fun isEqual(a: Any?, b: Any?): Boolean {
        if (a == null && b == null) return true
        return if (a == null) false else a == b
    }
}