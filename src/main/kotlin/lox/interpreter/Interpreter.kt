import lox.*
import lox.Function
import lox.parser.*
import lox.scanner.Token
import lox.scanner.TokenType.*
import lox.Visitor as StmtVisitor
import lox.parser.Visitor as ExprVisitor


class Interpreter() : ExprVisitor<Any?>, StmtVisitor<Any?> {
    val globals = Environment()
    private var environment = globals


    init {
        globals.define("clock", object : LoxCallable {
            override fun arity(): Int {
                return 0
            }

            override fun call(interpreter: Interpreter, arguments: MutableList<Any?>): Any {
                return System.currentTimeMillis().toDouble() / 1000.0
            }

            override fun toString(): String {
                return "<native fn>"
            }
        })
    }

    fun interpret(statements: List<Stmt>) {
        try {
            for (statement in statements) {
                execute(statement)
            }
        } catch (error: RuntimeError) {
            runtimeError(error)
        }
    }

    private fun execute(stmt: Stmt) {
        stmt.accept(this)
    }

    override fun visitBlockStmt(stmt: Block): Void? {
        executeBlock(stmt.statements, Environment(environment))
        return null
    }

    fun executeBlock(
        statements: List<Stmt?>, environment: Environment
    ) {
        val previous = this.environment
        try {
            this.environment = environment
            for (statement in statements) {
                execute(statement!!)
            }
        } finally {
            this.environment = previous
        }
    }

    override fun visitBinaryExpr(expr: Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        when (expr.operator.type) {
            GREATER -> {
                checkNumberOperands(expr.operator, left, right)
                return left as Double > right as Double
            }

            GREATER_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                return left as Double >= right as Double
            }

            LESS -> {
                checkNumberOperands(expr.operator, left, right)
                return (left as Double) < (right as Double)
            }

            LESS_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                return left as Double <= right as Double
            }

            MINUS -> {
                checkNumberOperands(expr.operator, left, right)
                return left as Double - right as Double
            }

            SLASH -> {
                checkNumberOperands(expr.operator, left, right)
                return left as Double / right as Double
            }

            STAR -> {
                checkNumberOperands(expr.operator, left, right)
                return left as Double * right as Double
            }

            PLUS -> {
                if (left is Double && right is Double) {
                    return left + right
                } else if (left is String && right is String) {
                    return left + right
                }
                throw RuntimeError(
                    expr.operator, "Operands must be two numbers or two strings."
                )
            }

            BANG_EQUAL -> return !isEqual(left, right)
            EQUAL_EQUAL -> return isEqual(left, right)

            else -> {}
        }

        // Unreachable.
        return null
    }

    override fun visitCallExpr(expr: Call): Any? {
        val callee = evaluate(expr.callee)
        val arguments: MutableList<Any?> = ArrayList()
        for (argument in expr.arguments) {
            arguments.add(evaluate(argument))
        }

        if (callee !is LoxCallable) {
            throw RuntimeError(
                expr.paren, "Can only call functions and classes."
            )
        }

        val function: LoxCallable = callee

        if (arguments.size != function.arity()) {
            throw RuntimeError(
                expr.paren, "Expected " + function.arity() + " arguments but got " + arguments.size + "."
            )
        }

        return function.call(this, arguments)
    }

    override fun visitGroupingExpr(expr: Grouping): Any? {
        return evaluate(expr.expression)
    }

    override fun visitLiteralExpr(expr: Literal): Any? {
        return expr.value
    }

    override fun visitLogicalExpr(expr: Logical): Any? {
        val left = evaluate(expr.left)
        if (expr.operator.type === OR) {
            if (isTruthy(left)) return left
        } else {
            if (!isTruthy(left)) return left
        }
        return evaluate(expr.right)
    }

    override fun visitUnaryExpr(expr: Unary): Any? {
        val right = evaluate(expr.right)

        when (expr.operator.type) {
            MINUS -> {
                checkNumberOperand(expr.operator, right)
                return -(right as Double)
            }

            BANG -> return !isTruthy(right)
            else -> {}
        }

        // Unreachable.
        return null
    }

    override fun visitVariableExpr(expr: Variable): Any? {
        return environment[expr.name]
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

    private fun stringify(`object`: Any?): String {
        if (`object` == null) return "nil"
        if (`object` is Double) {
            var text = `object`.toString()
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length - 2)
            }
            return text
        }
        return `object`.toString()
    }

    private fun checkNumberOperand(operator: Token, operand: Any?) {
        if (operand is Double) return
        throw RuntimeError(operator, "Operand must be a number.")
    }

    private fun checkNumberOperands(
        operator: Token, left: Any?, right: Any?
    ) {
        if (left is Double && right is Double) return
        throw RuntimeError(operator, "Operands must be numbers.")
    }


    override fun visitExpressionStmt(stmt: Expression) {
        evaluate(stmt.expression)
    }

    override fun visitFunctionStmt(stmt: Function): Void? {
        val function = LoxFunction(stmt)
        environment.define(stmt.name.lexeme, function)
        return null
    }

    override fun visitIfStmt(stmt: If) {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch)
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch)
        }
    }

    override fun visitPrintStmt(stmt: Print) {
        val value = evaluate(stmt.expression)
        println(stringify(value))
    }

    override fun visitVarStmt(stmt: Var): Any? {
        var value: Any? = null
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer)
        }

        environment.define(stmt.name.lexeme, value)
        return null
    }

    override fun visitWhileStmt(stmt: While): Void? {
        while (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.body)
        }
        return null
    }

    override fun visitAssignExpr(expr: Assign): Any? {
        val value = evaluate(expr.value)
        environment.assign(expr.name, value)
        return value
    }
}