package finlite

// Currency enum
enum class Currency {
    USD, PHP, EUR, GBP, JPY, AUD, CAD, CHF, CNY;

    companion object {
        fun fromString(code: String): Currency? =
            entries.firstOrNull { it.name == code.uppercase() }
    }
}

// Money literal for token parsing
data class FinLiteMoneyLiteral(
    val currency: Currency,
    val amount: Double
)

// Financial type system
data class Money(
    val amount: Double,
    val currency: String = "USD"
) {
    operator fun plus(other: Money): Money {
        if (currency != other.currency) {
            throw IllegalArgumentException(
                "Cannot add Money with different currencies: $currency vs ${other.currency}"
            )
        }
        return Money(amount + other.amount, currency)
    }

    operator fun minus(other: Money): Money {
        if (currency != other.currency) {
            throw IllegalArgumentException(
                "Cannot subtract Money with different currencies: $currency vs ${other.currency}"
            )
        }
        return Money(amount - other.amount, currency)
    }

    operator fun times(multiplier: Double): Money {
        return Money(amount * multiplier, currency)
    }

    operator fun times(rate: Rate): Money {
        return Money(amount * rate.value, currency)
    }

    operator fun div(divisor: Double): Money {
        if (divisor == 0.0) throw ArithmeticException("Division by zero in Money")
        return Money(amount / divisor, currency)
    }

    fun toDouble(): Double = amount

    override fun toString(): String = "$currency $amount"
}

//Represents an interest rate or discount rate (e.g., 0.05 for 5%).

data class Rate(
    val value: Double  // 0.0 to 1.0 range (e.g., 0.05 = 5%)
) {
    init {
        if (value < 0.0 || value > 1.0) {
            throw IllegalArgumentException("Rate must be between 0.0 and 1.0, got $value")
        }
    }

    operator fun plus(other: Rate): Rate = Rate(value + other.value)
    operator fun minus(other: Rate): Rate = Rate(value - other.value)
    operator fun times(multiplier: Double): Rate = Rate(value * multiplier)
    operator fun div(divisor: Double): Rate {
        if (divisor == 0.0) throw ArithmeticException("Division by zero in Rate")
        return Rate(value / divisor)
    }

    fun toDouble(): Double = value
    fun toPercentage(): Percentage = Percentage(value * 100.0)

    override fun toString(): String = "${(value * 100).format(2)}%"
}

//Represents a percentage (e.g., 5.0 for 5%).
data class Percentage(
    val value: Double  // 0.0 to 100.0 range
) {
    init {
        if (value < 0.0 || value > 100.0) {
            throw IllegalArgumentException("Percentage must be between 0.0 and 100.0, got $value")
        }
    }

    operator fun plus(other: Percentage): Percentage = Percentage(value + other.value)
    operator fun minus(other: Percentage): Percentage = Percentage(value - other.value)
    operator fun times(multiplier: Double): Percentage = Percentage(value * multiplier)
    operator fun div(divisor: Double): Percentage {
        if (divisor == 0.0) throw ArithmeticException("Division by zero in Percentage")
        return Percentage(value / divisor)
    }

    fun toDouble(): Double = value
    fun toRate(): Rate = Rate(value / 100.0)

    override fun toString(): String = "${value.format(2)}%"
}

/**
 * Represents a time period (e.g., years, months, days).
 */
data class TimePeriod(
    val value: Double,
    val unit: TimeUnit = TimeUnit.YEARS
) {
    enum class TimeUnit {
        DAYS, MONTHS, YEARS;

        fun toDays(): Double = when (this) {
            DAYS -> 1.0
            MONTHS -> 30.44  // average days per month
            YEARS -> 365.25   // accounting for leap years
        }
    }

    fun toYears(): Double = value * (unit.toDays() / TimeUnit.YEARS.toDays())
    fun toDays(): Double = value * unit.toDays()

    override fun toString(): String = "$value ${unit.name.lowercase()}"
}

// format doubles with decimal places.
fun Double.format(decimals: Int): String {
    return String.format("%.${decimals}f", this)
}

//parse Money from string literals like "1000 USD" or "EUR 500".
fun parseMoney(amount: Double, currencyCode: String): Money {
    return Money(amount, currencyCode.uppercase())
}

//parse Rate from percentage value (e.g., 5.0 -> 0.05).
fun parseRate(percentageValue: Double): Rate {
    return Rate(percentageValue / 100.0)
}

//parse Percentage from decimal value (e.g., 0.05 -> 5.0)
fun parsePercentage(decimalValue: Double): Percentage {
    return Percentage(decimalValue * 100.0)
}

// ============================================
// Financial Data Structures
// ============================================

data class FinTable(
    val columns: Map<String, List<Double>>
) {
    fun getColumn(name: String): List<Double> =
        columns[name] ?: error("Unknown column '$name'")
}

data class Cashflow(
    val flows: List<Double>
)

data class Portfolio(
    val assets: List<Double>, 
    val weights: List<Double>
)

data class LedgerEntry(
    val date: String,
    val debit: Double?,
    val credit: Double?,
    val description: String
)

data class Ledger(
    val entries: MutableList<LedgerEntry> = mutableListOf()
)

// ============================================
// Table Operations
// ============================================

object FinLiteTableOps {
    fun filter(table: FinTable, column: String, predicate: (Double) -> Boolean): FinTable {
        val target = table.getColumn(column)
        val mask = target.map(predicate)

        val newCols = table.columns.mapValues { (_, col) ->
            col.zip(mask).filter { it.second }.map { it.first }
        }

        return FinTable(newCols)
    }

    fun join(left: FinTable, right: FinTable): FinTable {
        val merged = left.columns + right.columns
        return FinTable(merged)
    }

    fun columnApply(table: FinTable, name: String, transform: (Double) -> Double): FinTable {
        val newCols = table.columns.toMutableMap()
        newCols[name] = table.getColumn(name).map(transform)
        return FinTable(newCols)
    }
}

