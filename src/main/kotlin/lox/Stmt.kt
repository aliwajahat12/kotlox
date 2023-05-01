package lox

import lox.parser.Expr
import lox.scanner.Token

abstract class Stmt {

	abstract fun <R> accept(visitor : Visitor<R>  ) : R
}

class Block( val statements:List<Stmt?>) : Stmt() {
	override fun <R> accept(visitor: Visitor<R>) = visitor.visitBlockStmt(this)
}

class Expression( val expression:Expr) : Stmt() {
	override fun <R> accept(visitor: Visitor<R>) = visitor.visitExpressionStmt(this)
}

class If( val condition:Expr, val thenBranch:Stmt, val elseBranch:Stmt?) : Stmt() {
	override fun <R> accept(visitor: Visitor<R>) = visitor.visitIfStmt(this)
}

class Print( val expression:Expr) : Stmt() {
	override fun <R> accept(visitor: Visitor<R>) = visitor.visitPrintStmt(this)
}

class Var( val name:Token, val initializer: Expr?) : Stmt() {
	override fun <R> accept(visitor: Visitor<R>) = visitor.visitVarStmt(this)
}

interface Visitor<R> {
	fun visitBlockStmt (stmt: Block): R
	fun visitExpressionStmt (stmt: Expression): R
	fun visitIfStmt (stmt: If): R
	fun visitPrintStmt (stmt: Print): R
	fun visitVarStmt (stmt: Var): R
}
