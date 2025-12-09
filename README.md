CMSC 124 Lab
'FinLite' Programming Language

Creators
Eleah Joy Melchor - levi00sa
Christel Hope Ong - ChristelHope

**Language Overview**
    FinLite is a domain-specific programming language designed specifically for finance students and beginner analysts. It aims to simplify learning financial modeling by combining the familiarity of spreadsheets with essential programming concepts. FinLite removes the complexity of traditional languages while allowing its users to automate calculations, build models, and perform scenario and risk analysis using a clear, finance-native syntax.

FinLite supports:
    -  Variables, expressions, and assignments
    -  Tables and column operations
    -  Built-in financial functions (NPV, IRR, CAPM, amortization)
    -  Conditionals and control flow
    -  Scenarios, simulations, and time-series data
    -  Portfolio and ledger operations
    -  Functional utilities (MAP, FILTER, REDUCE)
    -  Lambdas and closures     
    -  Easy printing and data inspection

**Keywords**
        LET                 - used to declare a variable
        SET                 - Used to assign/reassign a value to an existing variable
        TABLE               - used to define a table
        ROW                 - Used to define a single row inside a table
        IF, ELSE, ELSEIF    - conditionals
        RETURN              - returns a value from function
        END                 - Used to close blocks
        PRINT               - prints a value to console
        LOG                 - Writes to logs or report sections
        ASSERT              - checks correctness of financial and logical conditions
        ERROR               - Raise an error
        TRUE, FALSE         - Boolean literals
        NULL                - Empty value
        CASHFLOW            - Define cashflow block
        SCENARIO            - Declare scenario
        RUN	Execute         - scenario/model
        DISCOUNT            - Perform discounting
        YEAR                - Built-in for time-index rows
        RATE                - Reserved inside finance operations
        PORTFOLIO           - Portfolio block
        ENTRY               - Accounting entry
        DEBIT / CREDIT      - Ledger operations
        LEDGER              - Interact with ledger
        FROM / TO / STEP    - Range specifications
        SENSITIVITY	        - Generate sensitivity tables
        SIMULATE            - Run simulations
        TIMESERIES          - Time-based dataset
        DATE                - Date literal/index
        FILTER              - Filter table rows
        MAP	Row-wise        - transform
        AGGREGATE           - Sum/avg/min/max
    
**Operators**
        Arithmetic:
            Addition +
            Subtraction -
            Multiplication *
            Division /
            Remainder/Rem %
            
        Comparison:
            Equal ==
            Not equal !=
            Less than <
            Greater than >
            Less than or equal to <=
            Greater than or equal to >=
        
        Logical:
            Not !
            Logical And &&
            Logical Or ||
       
        Assignment:
            Equal =
            Multiplication assignment *=
            Addition assignment +=
            Subtraction assignment -=
            Division assignment /=

        Other:
            Increment INC (not implemented)
            Decrement DEC (not implemented)
            Colon :

**Literals**
    Integers: 42, 4.2, 0, -42, -1500.75, 0.12, 1.2e6, 5e-3
    
    Money: 1000 PHP, 2000 USD, 5000 JPY
    
    Strings: "hello", "abc\nxyz"
    
    Multi-line string:
        """
        This is a
        multi-line string
        """
        
    Characters: single characters inside single quotes
    
    Boolean literals: TRUE, FALSE
    
    Null: NULL
    
    Date literals: YYYY-MM-DD
    
    Time series literals: 
        TIMESERIES(
            start="2024-01-01",
            end="2024-12-31",
            freq="monthly"
        )
        Table literals: 
        TABLE(
            YEAR: [1, 2, 3],
            INFLOW: [50000, 60000, 70000],
            OUTFLOW: [20000, 30000, 35000]
        )
        
    Collections: 
        Arrays & Nested: [a, b, c], [[1,2],[3,4]]

**Identifiers**
    Rules for variable names: 
        Start with A-Z, a-z, and underscore
        May contain letters, digits, underscores
        **Case sensitive** for simplifying implementation and reducing ambiguity 
        Naming conventions: lowerCamelCase for variables and functions, UPPERCASE for constants
        Identifiers cannot contain dots (.)

**Comments**
    Single-line comments: # comment here
    Multi-line comments: 
            ###
            block comment
            ###

**Syntax Style**
    Whitespace insignificant
    Statements end with semicolon or newline
    Blocks end with END
    Uses () and [] for grouping
    Indentation optional (non-significant) but recommended

**Sample Code**
    Variable Declarations
        LET rate = 0.12 
        LET years = 5 
        LET principal = 100000 
        LET futureValue = FV(rate, years, principal, 0) PRINT futureValue

    Cashflow Table + NPV
        LET CF = TABLE(
        YEAR:    [1, 2, 3, 4],
        INFLOW:  [50000, 60000, 70000, 80000],
        OUTFLOW: [20000, 25000, 30000, 35000]
        )
        LET rate = 0.12
        LET netCF = CF.INFLOW - CF.OUTFLOW
        LET npvValue = NPV(netCF, rate)
        PRINT "Net Present Value: " + npvValue

    Conditionals
        IF npv > 0 THEN
            PRINT "Project is profitable"
        ELSE
            PRINT "Project is not viable"
        END
        ```
    While Loop
       LET i = 1
        WHILE i <= 3 DO
            PRINT i
            SET i = i + 1
        END

    For Loop
        FOR j IN 1 TO 3 DO
            PRINT j
        END

    Scenarios (What-If Analysis)
        ```finlite
        LET rate = 0.10
        LET growth = 0.08
        
        SCENARIO Bull:
            LET rate = 0.09
            LET growth = 0.12
        END
        
        SCENARIO Bear:
            LET rate = 0.13
            LET growth = 0.03
        END
        
        RUN Bull ON valuation_model
        RUN Bear ON valuation_model

    Time Series
        LET ts = TIMESERIES(
            start="2024-01-01",
            end="2024-12-31",
            freq="monthly"
        )
        
        FOR d IN ts DO
            PRINT d
        END

    Simulation:
        SIMULATE 1000 TIMES:
            LET shock = RANDOM(-0.05, 0.05)
            LET result = base + shock
        END

    Full Example
        LET CF = TABLE(
        YEAR:    [1, 2, 3],
        INFLOW:  [80000, 85000, 90000],
        OUTFLOW: [30000, 35000, 40000]
        )
    
        LET netCF = CF.INFLOW - CF.OUTFLOW
    
        LET rate = 0.11
        LET npv = NPV(netCF, rate)
    
        PRINT "NPV =" + npv
    
        IF npv > 0 THEN
            PRINT "Decision: ACCEPT project."
        ELSE
            PRINT "Decision: REJECT project."
        END

**Design Rationale**

    Finance students often struggle with syntax, complex data structures, learning Python/R, and fear of “traditional coding”. With the current advancement in finance tech, it is essential to aid them in learning and understanding financial modeling, risk analysis, model automations, data analysis, data manipulation, and more. Finance students typically think in cash flows, formulas, tables, transactions, scenarios, and risk, so we designed FinLite to be spreadsheet-native or Excel-like. By combining the familiarity of spreadsheets with the structure and clarity of code, FinLite makes financial modeling more intuitive and accurate. Instead of manually handling long formulas or error-prone cell references, students can describe financial scenarios, transactions, portfolios, and cash flow using readable and financial-friendly syntax.
    
    The language includes built-in functions for common tasks–NPV, IRR, discounting, amortization, risk metrics–so users can compute advanced financial values without reinventing formulas. 

    The language is not:
        Excel         - because it is scriptable and repeatable
        Python        - because it is simple and domain-focused
        R             - since it is beginner-friendly
    
    The language aims to support scenarios, sensitivity analysis, and time series operations so students can perform “what-if” analyses that require complicated Excel calculations. More advanced features such as multi-currency, date systems, and full portfolio simulations are planned for future versions.

**Context-Free Grammar:**
        program        → declaration* EOF ;
        declaration    → "LET" IDENTIFIER "=" expression
                    | statement ;
        statement      → "SET" IDENTIFIER "=" expression
                    | "PRINT" expression
                    | ifStmt
                    | scenarioStmt
                    | expression ";" ;
        ifStmt         → "IF" expression "THEN" statement* "END" ;
        scenarioStmt   → "SCENARIO" IDENTIFIER ":" declaration* "END" ;
        expression     → equality ;
        equality       → comparison ( ( "==" | "!=" ) comparison )* ;
        comparison     → term ( ( "<" | "<=" | ">" | ">=" ) term )* ;
        term           → factor ( ( "+" | "-" ) factor )* ;
        factor         → unary ( ( "*" | "/" | "%" ) unary )* ;
        unary          → ( "!" | "-" ) unary
                    | primary ;
        primary        → NUMBER
                    | STRING
                    | TRUE
                    | FALSE
                    | NULL
                    | IDENTIFIER ( "." IDENTIFIER )?
                    | tableLiteral
                    | "(" expression ")" ;
        tableLiteral   → "TABLE" "(" field ( "," field )* ")" ;
        field          → IDENTIFIER ":" "[" exprList "]" ;
        exprList       → expression ( "," expression )* ;
