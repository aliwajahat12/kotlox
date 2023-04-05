# kotlox

An implementation of lox interpretted language from Crafting Interpretters book in Kotlin

https://craftinginterpreters.com/

## Learnings and Summary

### Chapter 08: Statements and State

The chapter starts by the introduction of statements, that would consist of print statements and expression statements.
We define a new defineAst function in GenerateAst.kot and include statements which would create a class of Statement,
Print and Expression along with its Visitor functions. In the parse function we than make a list of statements, iterate
through tokens and add statements in the list. Print Statements are differentiated by PRINT keyword and ; in the end.
Between these two can be any expression. We then implement the visitor class of Statement class. For print statements we
use Kotlin's built in println method, and we execute the expressions by making a wrapper function which calls the
visitor methods.  
Next, we start with variables and its declarations. The declaration is a different type of statement, so we handle it by
adding it into generateAst function and making another class in Statements.kot along with its visitor method. We also
add Variable
class and visitor function to our Expr class. In the parse function, before calling statement, we now define and call
declaration function which checks if VAR keyword exists to navigate the code to declaration else it goes to statement as
before. If we have a new declaration, the code branches to varDeclaration function which returns a new Var Stmt
object.  
We keep a record of all the variables simply by making a hashmap / dict
/ map. To wrap our hashmap into a class we make a new class Environment.kot and define GET and DEFINE functions to store
and retrieve values from variables. To interpret these variables, we make a new environment object inside Interpreter
class.




