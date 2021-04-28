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
```javascript
    if(true){
        print(x)
    }
    var x = 10
    var y = 20
    var z = 0
    if(x < y){
        z = y - x
    }
```
Talk about above

### Print Statements
```javascript
    print(1)
```
Talk about above

### Variable Statements
```javascript
    var x = 3
    var x : int = 3
```
Talk about above

### Assignment Statements
```javascript
    var x = 0
    x = 10
```
Talk about above


### Return Statements
```javascript
    function foo(x : int) : int {
        y = 2*x
        return y
    }
```
Talk about above

### Function Statements

#### Function Definition Statement
```javascript
    function voidReturn_ObjectInput(x){
        print(x)
    }
```
Talk about above

```javascript
    function voidReturn_DeclaredTypeInput(x : String){
        print(x)
    }
```
Talk about above

```javascript
    function intReturn_IntInput(x : int) : int {
        y = 2*x
        return y
    }
```
Talk about above

#### Function Call Statement
```javascript
    var double = intReturn_IntInput(5)
    voidReturn_ObjectInput(double)
```
Talk about above

### List Literal Expression
```javascript
    [1, 2, 3]
    [true, false, false]
    ["A", "Dog", "8"]
```
Talk about above