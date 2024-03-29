package tools

import java.io.IOException
import java.io.PrintWriter
import kotlin.system.exitProcess


object GenerateAst {
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.size != 1) {
            System.err.println("Usage: generate_ast <output directory>")
            exitProcess(64)
        }
        val outputDir = args[0]
        defineAst(
            outputDir, "Expr", listOf(
                "Assign   : Token name, Expr value",
                "Binary   : Expr left, Token operator, Expr right",
                "Call     : Expr callee, Token paren, List<Expr> arguments",
                "Grouping : Expr expression",
                "Literal  : Any? value",
                "Logical  : Expr left, Token operator, Expr right",
                "Unary    : Token operator, Expr right",
                "Variable : Token name"

            )
        )
        defineAst(
            outputDir, "Stmt", listOf(
                "Block      : List<Stmt> statements",
                "Expression : Expr expression",
                "Function   : Token name, List<Token> params," + " List<Stmt> body",
                "If         : Expr condition, Stmt thenBranch," + " Stmt elseBranch",
                "Print      : Expr expression",
                "Return     : Token keyword, Expr value",
                "Var        : Token name, Expr initializer",
                "While      : Expr condition, Stmt body"
            )
        )
    }

    @Throws(IOException::class)
    private fun defineAst(
        outputDir: String, baseName: String, types: List<String>
    ) {
        val path = "$outputDir/$baseName.kt"
        val writer = PrintWriter(path, "UTF-8")
        writer.println("package lox.parser")
        writer.println()
        writer.println("import lox.scanner.Token")
        writer.println()
        writer.println("abstract class $baseName {")
        // The base accept() method.
        writer.println()
        writer.println("\tabstract fun <R> accept(visitor : Visitor<R>  ) : R")

        writer.println("}")
        writer.println()

        for (type in types) {
            val (className, fields) = type.split(":").map { it.trim() }
            defineType(writer, baseName, className, fields)
        }

        defineVisitor(writer, baseName, types)

        writer.close()
    }

    private fun defineType(
        writer: PrintWriter, baseName: String,
        className: String, fields: String
    ) {
        var initializer = ""

        for (field in fields.split(", ")) {
            val (type, name) = field.split(" ").map { it.trim() }
            initializer += " val $name:$type,"
        }

        initializer = initializer.dropLast(1)

        writer.println("class $className($initializer) : $baseName() {")
        writer.println("\toverride fun <R> accept(visitor: Visitor<R>) = visitor.visit$className$baseName(this)")
        writer.println("}")

        writer.println()
    }

    private fun defineVisitor(writer: PrintWriter, baseName: String, types: List<String>) {
        writer.println("interface Visitor<R> {")

        for (type in types) {
            val typeName = type.split(":")[0].trim()
            writer.println("\tfun visit$typeName$baseName (${baseName.lowercase()}: $typeName): R")
        }

        writer.println("}")
    }
}