# Catscript Guide

## Introduction

## Features

### For loops
```javascript
    for(x in[1,2,3]){
        print(x)
    }
```

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

### Print Statements
```javascript
    print(1)
```

### Variable Statements
```javascript
    var x = 3
    var x : int = 3
```

### Assignment Statements
```javascript
    var x = 0
    x = 10
```

### Return Statements
```javascript
    function foo(x : int) : int {
        y = 2*x
        return y
    }
```

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

### List Literal Expression
```javascript
    [1, 2, 3]
    [true, false, false]
    ["A", "Dog", "8"]
```
