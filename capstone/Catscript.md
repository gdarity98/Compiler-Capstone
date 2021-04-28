# Catscript Guide

## Introduction
Catscript is a statically typed programming language. 
We implemented a recursive descent parser. This means that the implementation
for parsing this coding language closely follows the grammar. Implementing this
compiler was done in five stages, Tokenize, Parse, Evaluate, Compile, and lastly
Transpile. Catscript has List literals and local variable type inference; two 
features that Java does not have! Catscript has standard control flow statements
in the form of the for and if statements. Catscript also contains function definitions,
expressions, and a type system. Catscript was created using Java.

## Features
The following features that are discussed are Statement or Expression classes. 
All classes contain some form of getter and setter methods. Statements and Expressions all contain 
validation, execute, transpile, and compile methods. Transpile has not been implemented. 
There are some classes that still need compile implementation as well. We did not implement 
constructors for the Statement classes. You create an empty new instance of the object. Expressions
have constructors that you send all the private variable information into. This often
includes the value of the expression, the left-hand side, the right-hand side, the operator,
another expression, etc. It depends on the type of expression.

### For loops
The for statement has three private variables: expression, variableName, and body.
In order to set the variables you call the set methods on the created forStatement.
The expression is the section within the initial parentheses. Lastly, the body
is a List containing Statements. With all of this in mind, below is a proper 
code block for a for loop.
```javascript
    for(x in [1, 2, 3]){
        print(x)
    }
```
The validation methods checks to make sure there is a proper list within the 
expression section of the for loop. Execute is exactly how it sounds. It executes
the body at runtime. It is pretty similar to a java for loop in implementation.

### If Statements
The if statement contains three private variables. Expression for the initial 
parentheses. Next is a List of Statements called trueStatements. If the 
expression evaluates to true then the trueStatements will be evaluated. Otherwise,
the next private variable comes into play. It is another List of Statements, but this
contains the falseStatements. These false statements could be more if statements.
Below is a valid code block for an if statement.
```javascript
    if(true){
        print(x)
    }
    var x = 10
    var y = 20
    var z = 0
    if(x < y){
        z = y - x
    }else if(y < x){
        z = x - y
    }
```
Validation makes sure that the expression evaluates to a Boolean value. It also goes
through all the true and false Statements and validates them.

### Print Statements
The print statement only has one private variable. It is the expression that the 
statement is to print. Execution of the print statement involves the execution
of the expression and then calling a printing function to take the output and 
print it.
```javascript
    print(1)
```
The validate method makes sure that the expression that is to be printed is valid.

### Variable Statements
The variable has four private variables. Two of which seem to do the same thing! 
These are the type and explicitType variables. Type is so that we can 
keep track of type inference within CatScript. The other two hold the variableName
and the expression that is attached to the variable. During execution these are used
to place the variableName and executed expression onto the scope of our program.
```javascript
    var x = 3
    var x : int = 3
```
The validate method checks for duplicated names within the scope. It checks if there
is an explicit type or if there is an inferred type. If explicit type is not null then
it verifies that the expression evaluates to the correct type.

### Assignment Statements
The assignment statement has the private variables that contain the expression to be
assigned and the name of the variable to be assigned to. 
```javascript
    var x = 0
    x = 10
```
The validate function makes sure that there is a variable of the name within the scope.
It then checks and makes sure that the expression is of a compatible type.

### Return Statements
The return statement contains the function definition statement it is contained within as
well as the expression to be returned. The validation method checks that the 
expression evaluated type is the same as the function definition return type.
```javascript
    function foo(x : int) : int {
        y = 2*x
        return y
    }
```
The evaluation method actually throws a return exception with the evaluated private
expression. This exception helps the CatScriptProgram know when a return has been 
evaluated for a function.

### Function Statements

#### Function Definition Statement
The function definition statement contains the name of the function, return type,
argument types and argument names, and lastly a List of statements that are the body
of the function.
```javascript
    function voidReturn_ObjectInput(x){
        print(x)
    }

    function voidReturn_DeclaredTypeInput(x : String){
        print(x)
    }
    
    function intReturn_IntInput(x : int) : int {
        y = 2*x
        return y
    }
```
Validate checks to make sure the function name is not already used. It then validates
all the statements in the body. Lastly it validates return coverage.

#### Function Call Statement
The function call statement contains the function call expression. The function call expression
contains all necessary information of the function call: name, type, and arguments being sent in.
```javascript
    var double = intReturn_IntInput(5)
    voidReturn_ObjectInput(double)
```
Within the validate method it validates the expression it contains. The execution 
method gets the function definition the function call is connected to. We then 
evaluate arguments from the function call expression. We add all the arguments
we are sending into the function to the runtime scope. Lastly we invoke the function.

### List Literal Expression
List literal expression contains a List of expressions called values. It also
has a type associated with it. 
```javascript
    [1, 2, 3]
    [true, false, false]
    ["A", "Dog", "8"]
```
The validate method validates all items within the values List. Next type checking
is performed to make sure all values match the List type. Evaluate creates an ArrayList
and fills it with evaluated values from the list. Then that ArrayList is returned.