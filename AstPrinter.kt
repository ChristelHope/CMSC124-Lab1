package finlite
import finlite.TokenType.*
import finlite.Stmt.*
import finlite.Expr.*


object AstPrinter {
    fun print(stmt: Stmt): String =
        when (stmt) {
            is Stmt.Let -> "(let ${stmt.name.lexeme} ${printExpr(stmt.initializer)})"
            is Stmt.SetStmt -> "(set ${stmt.name.lexeme} ${printExpr(stmt.value)})"
            is Stmt.ExpressionStmt -> printExpr(stmt.expression)
            is Stmt.Print -> "(print ${printExpr(stmt.value)})"
            is Stmt.WhileStmt -> "(while ${printExpr(stmt.condition)} ${print(stmt.body)})"
            is Stmt.For -> {
                val rangeStr = "${printExpr(stmt.start)} to ${printExpr(stmt.end)}" + (stmt.step?.let { " step ${printExpr(it)}" } ?: "")
                "(for ${stmt.variable.lexeme} $rangeStr ${print(stmt.body)})"
            }
            is Stmt.ForEach -> "(foreach ${stmt.variable.lexeme} in ${printExpr(stmt.iterable)} ${print(stmt.body)})"
            is Stmt.Block -> {
                val body = stmt.statements.joinToString(" ") { print(it) }
                "(block $body)"
            }
            is Stmt.IfStmt -> {
                val elseifParts = stmt.elseifBranches.joinToString(" ") { (cond, branch) ->
                    "(elseif ${printExpr(cond)} ${print(branch)})"
                }
                val elsePart = stmt.elseBranch?.let { " ${print(it)}" } ?: ""
                val elseifStr = if (elseifParts.isNotEmpty()) " $elseifParts" else ""
                "(if ${printExpr(stmt.condition)} ${print(stmt.thenBranch)}$elseifStr$elsePart)"
            }
            is Stmt.ReturnStmt -> {
                val value = stmt.value?.let { " ${printExpr(it)}" } ?: ""
                "(return$value)"
            }
            is Stmt.ErrorStmt -> "(error)"
            is Stmt.Scenario -> {
                val body = stmt.statements.joinToString(" ") { print(it) }
                "(scenario ${stmt.name.lexeme} $body)"
            }
            is FinanceStmt.LedgerEntryStmt -> printLedgerEntry(stmt)
            is FinanceStmt.PortfolioStmt  -> printPortfolio(stmt)
            is FinanceStmt.ScenarioStmt   -> printScenario(stmt)
            is FinanceStmt.SimulateStmt   -> printSimulate(stmt)
            is FinanceStmt.RunStmt -> printRun(stmt)
        }
        
    fun printExpr(expr: Expr): String =
        when (expr) {
            is Expr.Literal -> literalToString(expr.value)
            is Expr.Variable -> expr.name.lexeme
            is Expr.Grouping -> "(group ${printExpr(expr.expression)})"
            is Expr.Unary -> {
                val symbol = when (expr.operator.type) {
                    TokenType.BANG -> "!"
                    TokenType.MINUS -> "-"
                    TokenType.NOT -> "not"
                    else -> expr.operator.lexeme
                }
                "($symbol ${printExpr(expr.right)})"
            }
            is Expr.Binary -> "(${expr.operator.lexeme} ${printExpr(expr.left)} ${printExpr(expr.right)})"
            is Expr.Call -> {
                val args = expr.arguments.joinToString(", ") { printExpr(it) }
                "(call ${printExpr(expr.callee)} ($args))"
            }
            is Expr.ListLiteral -> {
                val elems = expr.elements.joinToString(", ") { printExpr(it) }
                "[$elems]"
            }
            is Expr.Subscript -> {
                val end = expr.end?.let { ":${printExpr(it)}" } ?: ""
                "(subscript ${printExpr(expr.container)} [${printExpr(expr.index)}$end])"
            }
            is Expr.Assign -> "(assign ${expr.name.lexeme} ${printExpr(expr.value)})"
            is FinanceExpr -> printFinanceExpr(expr)
            else -> "(unknown-expr ${expr::class.simpleName})"
        }

    private fun printFinanceExpr(expr: FinanceExpr): String =
        when (expr) {
            is FinanceExpr.NPV -> "(NPV ${printExpr(expr.cashflows)} ${printExpr(expr.rate)})"
            is FinanceExpr.IRR -> "(IRR ${printExpr(expr.cashflows)})"
            is FinanceExpr.PV -> {
                val pmt = expr.pmt?.let { " pmt=${printExpr(it)}" } ?: ""
                val fv = expr.fv?.let { " fv=${printExpr(it)}" } ?: ""
                "(PV rate=${printExpr(expr.rate)} nper=${printExpr(expr.nper)}$pmt$fv)"
            }
            is FinanceExpr.FV -> {
                val pmt = expr.pmt?.let { " pmt=${printExpr(it)}" } ?: ""
                val pv = expr.pv?.let { " pv=${printExpr(it)}" } ?: ""
                "(FV rate=${printExpr(expr.rate)} nper=${printExpr(expr.nper)}$pmt$pv)"
            }
            is FinanceExpr.WACC -> "(WACC ew=${printExpr(expr.equityWeight)} dw=${printExpr(expr.debtWeight)} ce=${printExpr(expr.costOfEquity)} cd=${printExpr(expr.costOfDebt)} tax=${printExpr(expr.taxRate)})"
            is FinanceExpr.CAPM -> "(CAPM beta=${printExpr(expr.beta)} rf=${printExpr(expr.riskFree)} prem=${printExpr(expr.premium)})"
            is FinanceExpr.VAR -> "(VAR portfolio=${printExpr(expr.portfolio)} confidence=${printExpr(expr.confidence)})"
            is FinanceExpr.SMA -> "(SMA series=${printExpr(expr.series)} period=${printExpr(expr.period)})"
            is FinanceExpr.EMA -> "(EMA series=${printExpr(expr.series)} period=${printExpr(expr.period)})"
            is FinanceExpr.Amortize -> "(Amortize principal=${printExpr(expr.principal)} rate=${printExpr(expr.rate)} periods=${printExpr(expr.periods)})"
            is FinanceExpr.CashflowLiteral -> {
                val entries = expr.entries.joinToString(" ") { 
                    "${it.type.name.lowercase()}:${printExpr(it.amount)}"
                }
                "(cashflow $entries)"
            }
            is FinanceExpr.PortfolioLiteral -> "(portfolio assets=${printExpr(expr.assets)} weights=${printExpr(expr.weights)})"
            is FinanceExpr.TableLiteral -> {
                val cols = expr.values.entries.joinToString(", ") { (name, expr) ->
                    "$name=${printExpr(expr)}"
                }
                "(table $cols)"
            }
            is FinanceExpr.ColumnAccess -> "${printExpr(expr.tableExpr)}.${expr.column.lexeme}"
            //is FinanceExpr.TimeSeriesLiteral -> "(timeseries ${printExpr(expr.values)})"
            else -> "(unknown-finance-expr ${expr::class.simpleName})"
        }

    private fun literalToString(value: Any?): String =
        when (value) {
            null -> "nil"
            is Double -> value.toString()
            is Boolean -> if (value) "true" else "false"
            is String -> "\"$value\""
            else -> value.toString()
        }
        
    // ============
    // HELPERS
    // ============
    private fun printLedgerEntry(entry: FinanceStmt.LedgerEntryStmt): String =
        "(ledger ${entry.type.name.lowercase()} ${entry.account.lexeme} ${printExpr(entry.amount)})"

    private fun printPortfolio(stmt: FinanceStmt.PortfolioStmt): String {
        val entries = stmt.entries.joinToString(" ") { printLedgerEntry(it) }
        return "(portfolio ${stmt.name.lexeme} $entries)"
    }

    private fun printScenario(stmt: FinanceStmt.ScenarioStmt): String =
        "(scenario ${stmt.name.lexeme} ${print(stmt.body)})"

    private fun printSimulate(stmt: FinanceStmt.SimulateStmt): String {
        val runs = stmt.runs?.let { " runs=${printExpr(it)}" } ?: ""
        val step = stmt.step?.let { " step=${printExpr(it)}" } ?: ""
        return "(simulate ${stmt.scenarioName.lexeme}$runs$step)"
    }
    
    private fun printRun(stmt: FinanceStmt.RunStmt): String {
        return "(run ${stmt.scenarioName.lexeme} on ${stmt.modelName.lexeme})"
    }
}