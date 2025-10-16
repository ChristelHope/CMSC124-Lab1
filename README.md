LAB2: Parser
CFG Section
  program        → expression ;
  expression     → term ( ( "+" | "-" ) term )* ;
  term           → factor ( ( "*" | "/" ) factor )* ;
  factor         → NUMBER
                 | IDENTIFIER
                 | "(" expression ")"
                 | "-" factor ;

LAB1: Scanner
"Teleris"

Creator
Eleah Joy Melchor
Christel Hope Ong

Language Overview [Provide a brief description of your programming language - what it's a designed for, its main characteristics]
    Teleris is a small yet dynamically typed language designed to be readable and easy to use. Targets learners and small compiler experiments. Syntax is familiar to scripting languages (clear keywords, {} blocks, C-style operators) while maintaining the constructs needed to explore language tooling (scanner to parser, then to interpreter) 

Keywords [List all reserved words that cannot be used as identifiers - include the keyword and a brief description of its purpose]

    set
    used to declare variable
    keep
    used to declare constants
    fn
    used to declare functions
    return
    returns the output of function
    if, else
    conditionals
    while, for, in
    looping and iterations
    stop & continue
    used to control loops
    true, false, null
    boolean literals
    class
    Create objects
    extends 
    For inheritance
    import & export 
    And                                                             
    True only if both operands are true
    or
    True when at least one operand is true
    not
    Negates the expression

Operators [List all operators organized by category (arithmetic, comparison, logical, assignment, etc.)]

    Addition
    +
    Subtraction
    -
    Multiplication
    *
    Division
    /
    Remainder/Rem
    %
    Equal equal
    ==
    Not equal
    !=
    Less than
    <
    Greater than
    >
    Less than or equal to
    <=
    Greater than or equal to
    >=
    Not
    !
    Logical And
    &&
    Logical Or
    ||
    Equal
    =
    Multiplication assignment
    *=
    Addition assignment
    +=
    Subtraction assignment
    -=
    Division assignment
    /=
    Increment
    ++
    Decrement
    –
    Colon
    :

Literals [Describe the format and syntax for each type of literal value (e.g., numbers, strings, characters, etc.) your language supports]
    Numbers: 42, 4.2
    Strings: use double-quotations with \n and \t
    Booleans: true, false
    Null: null
    Collections: 
    Arrays: [a, b, c]
    Maps: { name: “Eleah” }

Identifiers [Define the rules for valid identifiers (variable names, function names, etc.) and whether they are case-sensitive]
    Rules for variable names: 
        Start with A-Z, a-z, dollar sign ($), and underscore.
        Remaining characters may be letters, digits, or underscores.
        Case sensitive for simplifying implementation and reducing ambiguity 
        Naming conventions: camelCase for variables and functions, and PascalCase for classes and types
    Comments [Describe the syntax for comments and whether nested comments are supported]
        Single-line comments: //
        Multi-line comments: /- -/
    Syntax Style [Describe whether whitespace is significant, how statements are terminated, and what delimiters are used for blocks and grouping]
        Indentation-sensitive
        What if automatic semicolon insertion 
        Grouping: { … } for blocks and ( … ) for expression grouping
        Function syntax:
            fn varName (parameter1, parameter2) { … }
        Module boundary: 
            Import lib from “lib”


Sample Code [Provide a few examples of valid code in your language to demonstrate the syntax and features]
    Variable & arithmetic

        let x = 20
        var y = 3.14
        x = x + 10
        Function

        fn add(a,b){
            return a+b;
        } let z =add(2,3)

    Condition & loops

        if(x>0){
            x=x-1;
        } else {
            x=0;
        }
        for (i in [1,2,3]){
            print(i)
        }

    Arrays & maps

        let arr=[1,2,3]
        let person = {name:"Eleah", age: 21}

    Comments & strings

        //single line comment
        /-
        multiple line comment
        -/
        let s="this that \"quote\" "

Design Rationale [Explain the reasoning behind your design choices]

    The design of Telereis was made to be simple, readable, and familiar so that those who learns it can focus on studying programming languages functionalities (how they work) instead of struggling with syntax (especially for those who don’t use/learn one programming language and want to know different programming languages). The style of this programming language is kind of similar to some other common languages such as C and JS, since it is easier to understand keywords, operators, and control flow (and we want to make it to be like that way as much as possible). It also uses dynamic typing so it's not needed for variables to use declaration so that it is lighter and faster to implement (for classroom projects/learning purposes).

    The keywords are purposefully chosen to be clear (e.g. set, fn, const) & to separate mutable and constant variables so that the difference can easily be seen between changeable and fixed values. Arrays and maps were included to give enough tools for real examples yet w/o making it too complicated. String w/ escape characters, common operators, and comments added as to be used for normal programs and to practice parsing & scanning…so basically the goal is to make it small yet somehow still powerful enough to write clear examples and test the full route from scanner to interpreter. So far, we have defined the vocabulary of Teleris for this activity, and we hope and plan to continue making it work for later projects.
