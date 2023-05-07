# kotlox

An implementation of lox interpreted language from Crafting Interpreters book in Kotlin

https://craftinginterpreters.com/

## Learnings and Summary

### Chapter 08: Statements and State

The chapter starts by the introduction of statements, that would consist of print statements and expression statements.
We define a new `defineAst` function in GenerateAst.kt and include statements which would create a class of
`Statement`, `Print` and `Expression` along with its Visitor functions. In the parse function we than make a
list of statements, iterate through tokens and add statements in the list. Print Statements are differentiated
by `PRINT` keyword and `;` in the end. Between these two can be any expression. We then implement the visitor
class of Statement class. For print statements we use Kotlin's built in `println` method, and we execute the
expressions by making a wrapper function which calls the visitor methods.  
Next, we start with `variables` and its declarations. The declaration is a different type of statement, so we handle it
by adding it into `generateAst` function and making another class in Statements.kt along with its visitor method. We
also add `Variable` class and visitor function to our `Expr` class. In the parse function, before calling statement, we
now define and call declaration function which checks if `VAR` keyword exists to navigate the code to declaration else
it goes to statement as before. If we have a new declaration, the code branches to varDeclaration function which returns
a new `Var Stmt` object.  
We keep a record of all the variables simply by making a hashmap / dict / map. To wrap our hashmap into a class we make
a new class `Environment.kt` and define `GET` and `DEFINE` functions to store and retrieve values from variables. To
interpret these variables, we make a new environment object inside Interpreter class.  
We then move towards Assignments. Assignment is basically an expression which looks like `IDENTIFIER '=' assignment`. We
add `Assign` to syntax tree node by adding it in `defineAst` function in `Expr`. We can't know if the statement will be
an assignment or not until we hit the `=` token so to tackle this we define a function `assignment` where we first get
the expr and then check for '=' sign, if its present, check if expr is the instance of `Variable` and then assign the
value. Next, we override `visitAssignExpr` function in Interpreter class and define a new function `assign` in
Environment class which only assigns the new value if the variable was already there else throws error.
Lastly, we talk about Scopes. Scopes are defined by curly braces `{}`. The variable defined inside the scope should only
be accessible by itself and the scopes that are inside it. Also, if there is a variable of the same name inside and
outside the scope, and the user tries to retrieve the variable, the inner one should be returned. We achieve this by
nesting the environments and then recursively going up if the variable is not found in the environment. For this we
overload `Environment` constructor, one with no arguments and one with the enclosing (parent) environment. Lastly, new
type of `Stmt`, that is `Block` inside `defineAst` function. We then update the `parse` function to check for `{` and
then consume all statements inside it until we get a `}` and finally return a new `Block`. We override
the `visitBlockStmt` function inside Interpreter class where we assign the concerned environment before executing the
statements inside it and then going back to the original environment.  
We can now run the following code with declarations, assignments and scopes:

        var a = "global a";
        var b = "global b";
        var c = "global c";
        {
            var a = "outer a";
            var b = "outer b";
            {
                var a = "inner a";
                print a;
                print b;
                print c;
            }
            print a;
            print b;
            print c;
        }
        print a;
        print b;
        print c;

### Chapter 09: Control Flow

The control flow can be divided into 2 kinds - `conditonal` and `loops`. Conditional statements basically means to
execute or skip a block of code according to the condition. We start by defining a new syntax tree node in `stmt` block
which has `condition`, `then` and `else` Statements. Then we add the if statement code execution in `parser.kt` which
extracts the condition expression, then statement and else statement (if any). We then override the `visitifStatement`
function in `Interpreter.kt` file to execute then statement if the condition evaluates to true and else condition if its
false.  
We follow the same path for `logical operators` and `while loop`, define a tree node, handle it in Parser.kt and then
override its visitor function.
We handle `For Loop` differently as for loop feature is `syntactic sugar` which basically means syntax that would make
it
easier for the user to use the language. We don't basically need for loop as any code that for loop can have can be
written
using while loop. So instead of defining new tree node, we `desugarize` the for statement into our while loop and other
statements. We first extract the initializer, condition, increment statement and body separately. We then do the
following:

- make an array of statement that would run body and then increment
- wrap body and condition into the while loop
- Finally, make an array of statements that would have initializer and body.  
  We now have a statement that we can add to our list of statements which will be interpreted later.   
  By the end of the chapter, our interpreter can run the following code:

        // conditional Execution
        var a = 5;
        var b = 5;
        if (a==b)
        print "equal";
        else
        print "not equal";
        
        // Logical Operators
        print "hi" or 2;
        print nil or "yes";
        
        // While Loop
        while (a < 10)
        {
        print a;
        a = a+1;
        }
        
        // For Loop
        var a = 0;
        var temp;
        for (var b = 1; a < 10000; b = temp + b) {
        print a;
        temp = a;
        a = b;
        }

### Chapter 10: Functions

As the name suggests, in this chapter we introduce functions, its declaration, its body and handle its return
statements. First we define an AST for call i.e. `()` and define its function in Parser class where we consume the
expression than Left parenthesis. After this we run a while loop where for every `(` token, we call a finishCall
function in order to handle the case where there are multiple call for e.g. `foo()()()`. The `fininshCall` function gets
the arguments until `)` is found and then call the function. We also set maximum arguments length to 255, same as Java.
In the end, we override the `visitCallExpr` function where we evaluate callee, get arguments, define a new object for
class `LoxCallable` and calls its `.call` function. We also the handle the case if the callee is not a function, (for
e.g. if it's a string) and compare if there are same number of arguments - `arity` in visitCallExpr method.  
We then explore Kotlin Native functions, how to handle them and make them available for Lox users. We define `time`
method in environment of interpreter class in Class's constructor by defining a new anonymous object that
implements`LoxCallable` object.  
Next we go towards function declarations whose syntax would be `"fun" IDENTIFIER "(" parameters? ")" block`. As usual,
we add `Function` in GenerateAST, handle its call in `Parser.kt` and define a function that would parse the code. The
function starts by extracting `IDENTIFIER`, consume `(`, gets argument list, consume `)` and then consumes block
encapsulated by `{}`.  
To implement function objects, we make a class `LoxFunction` that implements `LoxCallable`. We override the `call`
function where we define its environment, puts arguments in that environment and then execute the block using that
environment. In the end of function, we `return null`. We also override its `arity` method and `toString` methods to get
number of arguments and print the name of function respectively. Finally, we add declaration of the function in our
environment.  
We then move to return statements. To simplify the work, after defining return in GenerateAST and overriding it in
Interpreter class, when we see the keyword `return`, we throw an error which we catch in `call()` method
in `LoxFunction` class where extract the return value and return it.  
The only that was left was the issue of local objects and closure, what if we return the invocation of the function
outside the function? Its scope gets destroyed. So to cater this, we send the current environment to the `LoxFunction`
as an argument to the constructor which makes the new environment in `call` function with the previous environment (
closure). Our Interpreter can now run the following code:

    //function declaration and calling
    
    fun sayHi(first, last) {
    print "Hi, " + first + " " + last + "!";
    }
    
    sayHi("Dear", "Reader");
    print sayHi;
    
    
    // returning from function using recursion

    fun fib(n) {
    if (n <= 1) return n;
    return fib(n - 2) + fib(n - 1);
    }
    
    for (var i = 0; i < 20; i = i + 1) {
    print fib(i);
    }
    
    
    // Local Functions and Closures

    fun makeCounter() {
    var i = 0;
    fun count() {
    i = i + 1;
    print i;
    }
    
    return count;
    }
    
    var counter = makeCounter();
    counter(); // "1".
    counter(); // "2".



