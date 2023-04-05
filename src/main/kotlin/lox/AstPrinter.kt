import lox.parser.*
import lox.scanner.Token
import lox.scanner.TokenType


class AstPrinter : Visitor<String> {
    fun print(expr: Expr): String = expr.accept(this)
    override fun visitAssignExpr(expr: Assign): String = parenthesize(expr.name.lexeme, expr.value)

    override fun visitBinaryExpr(expr: Binary): String = parenthesize(expr.operator.lexeme, expr.left, expr.right)

    override fun visitGroupingExpr(expr: Grouping): String = parenthesize("group", expr.expression)

    override fun visitLiteralExpr(expr: Literal): String = if (expr.value == null) "nil" else expr.value.toString()

    override fun visitUnaryExpr(expr: Unary): String = parenthesize(expr.operator.lexeme, expr.right)
    override fun visitVariableExpr(expr: Variable): String = expr.name.toString()

    private fun parenthesize(name: String, vararg exprs: Expr): String {
        val builder = StringBuilder()
        builder.append("(").append(name)
        for (expr in exprs) {
            builder.append(" ")
            builder.append(expr.accept(this))
        }
        builder.append(")")
        return builder.toString()
    }
}


fun main(args: Array<String>) {
    val expression: Expr = Binary(
        Unary(
            Token(TokenType.MINUS, "-", null, 1),
            Literal(123)
        ),
        Token(TokenType.STAR, "*", null, 1),
        Grouping(
            Literal(45.67)
        )
    )
    println(AstPrinter().print(expression))
}