package lox

import Interpreter
import lox.parser.*
import lox.scanner.Token
import java.util.*
import error as loxError
import lox.Visitor as StmtVisitor
import lox.parser.Visitor as ExprVisitor


internal class Resolver(interpreter: Interpreter) : ExprVisitor<Void?>, StmtVisitor<Void?> {
    private val interpreter: Interpreter
    private val scopes = Stack<MutableMap<String, Boolean>>()

    init {
        this.interpreter = interpreter
    }

    private fun resolve(statements: List<Stmt?>) {
        for (statement in statements) {
            resolve(statement!!)
        }
    }

    private fun beginScope() {
        scopes.push(HashMap<String, Boolean>())
    }

    private fun endScope() {
        scopes.pop()
    }

    private fun declare(name: Token) {
        if (scopes.isEmpty()) return
        val scope: MutableMap<String, Boolean> = scopes.peek()
        scope[name.lexeme] = false
    }

    private fun define(name: Token) {
        if (scopes.isEmpty()) return
        scopes.peek()[name.lexeme] = true
    }

    private fun resolveLocal(expr: Expr, name: Token) {
        for (i in scopes.indices.reversed()) {
            if (scopes[i].containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size - 1 - i)
                return
            }
        }
    }

    override fun visitBlockStmt(stmt: Block): Void? {
        beginScope()
        resolve(stmt.statements)
        endScope()
        return null
    }

    override fun visitExpressionStmt(stmt: Expression): Void? {
        resolve(stmt.expression)
        return null
    }

    override fun visitFunctionStmt(stmt: Function): Void? {
        declare(stmt.name)
        define(stmt.name)
        resolveFunction(stmt)
        return null
    }

    override fun visitIfStmt(stmt: If): Void? {
        resolve(stmt.condition)
        resolve(stmt.thenBranch)
        if (stmt.elseBranch != null) resolve(stmt.elseBranch)
        return null
    }

    override fun visitPrintStmt(stmt: Print): Void? {
        resolve(stmt.expression)
        return null
    }

    override fun visitReturnStmt(stmt: Return): Void? {
        if (stmt.value != null) {
            resolve(stmt.value)
        }
        return null
    }

    override fun visitVarStmt(stmt: Var): Void? {
        declare(stmt.name)
        if (stmt.initializer != null) {
            resolve(stmt.initializer)
        }
        define(stmt.name)
        return null
    }

    override fun visitWhileStmt(stmt: While): Void? {
        resolve(stmt.condition)
        resolve(stmt.body)
        return null
    }


    override fun visitAssignExpr(expr: Assign): Void? {
        resolve(expr.value)
        resolveLocal(expr, expr.name)
        return null
    }

    override fun visitBinaryExpr(expr: Binary): Void? {
        resolve(expr.left)
        resolve(expr.right)
        return null
    }

    override fun visitCallExpr(expr: Call): Void? {
        resolve(expr.callee)
        for (argument in expr.arguments) {
            resolve(argument)
        }
        return null
    }

    override fun visitGroupingExpr(expr: Grouping): Void? {
        resolve(expr.expression)
        return null
    }

    override fun visitLiteralExpr(expr: Literal): Void? {
        return null
    }

    override fun visitLogicalExpr(expr: Logical): Void? {
        resolve(expr.left)
        resolve(expr.right)
        return null
    }

    override fun visitUnaryExpr(expr: Unary): Void? {
        resolve(expr.right)
        return null
    }

    override fun visitVariableExpr(expr: Variable): Void? {
        if (!scopes.isEmpty() &&
            scopes.peek()[expr.name.lexeme] == false
        ) {
            loxError(
                expr.name,
                "Can't read local variable in its own initializer."
            )
        }
        resolveLocal(expr, expr.name)
        return null
    }

    private fun resolve(stmt: Stmt) {
        stmt.accept(this)
    }

    private fun resolve(expr: Expr) {
        expr.accept(this)
    }

    private fun resolveFunction(function: Function) {
        beginScope()
        for (param in function.params) {
            declare(param)
            define(param)
        }
        resolve(function.body)
        endScope()
    }
}