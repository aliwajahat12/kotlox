package lox.parser

import lox.scanner.Token

abstract class Expr {

    abstract fun <R> accept(visitor: Visitor<R>): R
}

class Assign(val name: Token, val value: Expr) : Expr() {
    override fun <R> accept(visitor: Visitor<R>) = visitor.visitAssignExpr(this)
}

class Binary(val left: Expr, val operator: Token, val right: Expr) : Expr() {
    override fun <R> accept(visitor: Visitor<R>) = visitor.visitBinaryExpr(this)
}

class Call(val callee: Expr, val paren: Token, val arguments: List<Expr>) : Expr() {
    override fun <R> accept(visitor: Visitor<R>) = visitor.visitCallExpr(this)
}

class Grouping(val expression: Expr) : Expr() {
    override fun <R> accept(visitor: Visitor<R>) = visitor.visitGroupingExpr(this)
}

class Literal(val value: Any?) : Expr() {
    override fun <R> accept(visitor: Visitor<R>) = visitor.visitLiteralExpr(this)
}

class Logical(val left: Expr, val operator: Token, val right: Expr) : Expr() {
    override fun <R> accept(visitor: Visitor<R>) = visitor.visitLogicalExpr(this)
}

class Unary(val operator: Token, val right: Expr) : Expr() {
    override fun <R> accept(visitor: Visitor<R>) = visitor.visitUnaryExpr(this)
}

class Variable(val name: Token) : Expr() {
    override fun <R> accept(visitor: Visitor<R>) = visitor.visitVariableExpr(this)
}

interface Visitor<R> {
    fun visitAssignExpr(expr: Assign): R
    fun visitBinaryExpr(expr: Binary): R
    fun visitCallExpr(expr: Call): R
    fun visitGroupingExpr(expr: Grouping): R
    fun visitLiteralExpr(expr: Literal): R
    fun visitLogicalExpr(expr: Logical): R
    fun visitUnaryExpr(expr: Unary): R
    fun visitVariableExpr(expr: Variable): R
}
