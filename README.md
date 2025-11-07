Teleris (Taglish Slang Programming Language)

Creator
Eleah Joy Melchor
Christel Hope Ong


[GRAMMAR]
    Lab2 CFG:
    program        → expression ;
    expression     → term ( ( "+" | "-" ) term )* ;
    term           → factor ( ( "*" | "/" ) factor )* ;
    factor         → NUMBER
                    | IDENTIFER    | "(" expression ")"  | "-" factor ;
                   


Language Overview [Provide a brief description of your programming language - what it's a designed for, its main characteristics]
    Teleris is a Taglish-coded barkada scripting language designed to make programming relatable, funny, and expressive for Filipino learners/users. It keeps C/JS familiar for those who want to learn it while embracing the modern Filipino linguistic culture (Gen Z slang)  for keywords to mix programming logic with cultural humor.
    Designed to be beginner-friendly, easy to read, ideal for programming languages experiments, and shows how language design can also be artistic, cultural, and humorous, not only technical.
    Teleris is a small yet dynamically typed language designed to be readable and easy to use. Targets learners and small compiler experiments. Syntax is familiar to scripting languages (clear keywords, {} blocks, C-style operators) while maintaining the constructs needed to explore language tooling (scanner to parser, then to interpreter) 

Keywords [List all reserved words that cannot be used as identifiers - include the keyword and a brief description of its purpose]

    may
    used to declare variable
    pak
    Used to set
    solid
    used to declare constants
    eksena
    used to declare functions
    pumapapel
    Used to call functions
    balik
    returns the output of function
    kung, hala, hala kung
    conditionals
    awra, forda, bet
    looping and iterations
    amaccana, game
    used to control loops
    omsim, charot, olats (null)
    boolean literals
    peg
    Create objects
    naol
    For inheritance
    ermats & erpats 
    Imports and exports
    amp                                                     
    True only if both operands are true
    baka
    True when at least one operand is true
    dehins
    Negates the expression
    chika
    print
    DANGEROUS
    error



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

    equal
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
    Booleans: omsim (true), charot (false)
    Null: olats (null or nil)
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
    Multi-line comments: /- … -/
    Syntax Style [Describe whether whitespace is significant, how statements are terminated, and what delimiters are used for blocks and grouping]
    Indentation-sensitive
    Semicolon automatically inserted
    C/JS-style braces Grouping: { … } for blocks 
    ( … ) for expression grouping
    Function syntax:
    eksena functionName(parameter1, parameter2) { … }
    Module boundary: 
    Import lib from “lib”

Sample Code [Provide a few examples of valid code in your language to demonstrate the syntax and features]
    Variable & arithmetic
    may x = 20		//declare var
    may y = 3.14
    pak x = x + 10		//assign new val	
    chika(x) // print Output: 30	

    may edad = 21	
    solid pi = 3.14		
    pak edad = edad + 1	

    Function
    eksena add(a, b) {	//function declaration
        balik a + b		//return
    }

    may z = pumapapel add(2, 3)		//function call
    chika(z) // Output: 5


    eksena greet(pangalan) {
        balik "Hello " + pangalan + "! Kamusta coding journey mo?"
    }

    may res = pumapapel greet("Beshy")
    chika (res)
    Conditionals
    kung (edad > 18) {
        chika("Legal ka na, go awra!")
    } hala {
        chika("Minor ka pa, uwian na!")
    }
    Loops
    forda (i bet [1,2,3]) {
        chika(i)
    }
    awra (omsim) {
        chika("Coding pa more")
        amaccana //break
    }

    awra (omsim) {
        chika("Loop start")
        game        // continue
        amaccana    // break
    }

    Arrays & maps
    may tropa = ["Eleah", "Chong", "Joy"]

    may tao = {
        name: "Eleah",
        age: 21
    }
    Comments & strings
    // Beshy, comment lang ako
    /-
    Chika chika lang bes
    -/

    may s = "Learning \"Teleris\" kasi trip ko lang!"


Design Rationale [Explain the reasoning behind your design choices]
    Teleris is not only beginner-friendly but also is a taglish-friendly language that aims to create a balance between academic and cultural goals, aside from expressing creativity, enjoyment, and freedom in programming language design. It is dynamic typed for faster classroom and compiler learning, to study lexing, parsing, ASL, language semantics, and minimal syntax means implementation shouldn’t be too complex, have a familiar C/JS flow, and dynamic typing for simplicity, while making programming feel like Pinoy, fun, and full of emotions by using Filipino slang keywords & humor-driven messages to motivate learners, and encouraging creativity in programming language design.
    By modifying keyword vocabulary while preserving grammatical expression, Teleris maintains technical validity while being unique and creative enough to be considered a custom language, and preserving core language design concepts while bringing Filipino culture into code. This language helps prove that programming languages aren’t only about logic, but also are culture, emotion, and identity. 

