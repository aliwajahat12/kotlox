package lox.scanner

class Token(
    private val type: TokenType,
    val lexeme: String,
    private val literal: Any?,
    private val line: Int
) {

    override fun toString(): String {
        return "$type $lexeme $literal $line"
    }
}