package finlite

class FinanceRuntime {

    // NPV: Net Present Value
    // Accepts Rate for discount rate, returns Money if initial cashflow is Money, else Double
    fun npv(rate: Double, cashflows: List<Double>): Double =
        cashflows.withIndex().sumOf { (i, cf) ->
            cf / Math.pow(1 + rate, i.toDouble())
        }

    fun npvWithRate(rate: Rate, cashflows: List<Double>): Double =
        npv(rate.value, cashflows)

    // IRR: Internal Rate of Return (returns a Rate)
    fun irr(cashflows: List<Double>): Double {
        var guess = 0.1
        repeat(100) {
            val f = npv(guess, cashflows)
            val fPrime = cashflows.withIndex().sumOf { (i, cf) ->
                -i * cf / Math.pow(1 + guess, i.toDouble() + 1)
            }
            if (fPrime == 0.0) return guess  // Avoid division by zero
            guess -= f / fPrime
        }
        return guess
    }

    fun irrAsRate(cashflows: List<Double>): Rate {
        return Rate(irr(cashflows))
    }

    // PV: Present Value
    fun pv(rate: Double, nper: Int, pmt: Double?, fv: Double?): Double {
        if (rate == 0.0) {
            // Special case: zero rate
            return -(pmt ?: 0.0) * nper - (fv ?: 0.0)
        }
        return (pmt ?: 0.0) * (1 - (1 / Math.pow(1 + rate, nper.toDouble()))) / rate -
            (fv ?: 0.0) / Math.pow(1 + rate, nper.toDouble())
    }

    fun pvWithRate(rate: Rate, nper: Int, pmt: Double?, fv: Double?): Double =
        pv(rate.value, nper, pmt, fv)

    // FV: Future Value
    fun fv(rate: Double, nper: Int, pmt: Double?, pv: Double?): Double {
        if (rate == 0.0) {
            // Special case: zero rate
            return (pv ?: 0.0) + (pmt ?: 0.0) * nper
        }
        return (pv ?: 0.0) * Math.pow(1 + rate, nper.toDouble()) +
            (pmt ?: 0.0) * ((Math.pow(1 + rate, nper.toDouble()) - 1) / rate)
    }

    fun fvWithRate(rate: Rate, nper: Int, pmt: Double?, pv: Double?): Double =
        fv(rate.value, nper, pmt, pv)

    // WACC: Weighted Average Cost of Capital (returns a Rate)
    fun wacc(ew: Double, dw: Double, ce: Double, cd: Double, tax: Double): Double =
        ew * ce + dw * cd * (1 - tax)

    fun waccAsRate(ewRate: Rate, dwRate: Rate, ceRate: Rate, cdRate: Rate, taxRate: Rate): Rate =
        Rate(wacc(ewRate.value, dwRate.value, ceRate.value, cdRate.value, taxRate.value))

    // CAPM: Capital Asset Pricing Model (returns a Rate)
    fun capm(rf: Double, beta: Double, rm: Double): Double =
        rf + beta * (rm - rf)

    fun capmWithRates(rfRate: Rate, beta: Double, rmRate: Rate): Rate =
        Rate(capm(rfRate.value, beta, rmRate.value))

    // VAR: Value at Risk
    fun varCalc(portfolio: Any?, confidence: Double): Double {
        val list = convertToListOfDouble(portfolio)
        if (list.isEmpty()) return 0.0
        
        val mean = list.average()
        val std = Math.sqrt(list.map { (it - mean) * (it - mean) }.average())
        val z = when (confidence) {
            0.95 -> 1.65
            0.99 -> 2.33
            else -> 1.0
        }
        return z * std
    }

    fun varCalcAsRate(portfolio: Any?, confidence: Double): Rate =
        Rate(varCalc(portfolio, confidence))

    // SMA: Simple Moving Average
    fun sma(values: List<Double>, period: Int): List<Double> =
        values.windowed(period).map { it.average() }

    // EMA: Exponential Moving Average
    fun ema(values: List<Double>, period: Int): List<Double> {
        if (values.isEmpty()) return emptyList()
        
        val k = 2.0 / (period + 1)
        val result = mutableListOf(values.first())
        for (i in 1 until values.size) {
            val prev = result.last()
            result.add(values[i] * k + prev * (1 - k))
        }
        return result
    }

    // Amortization Schedule (returns Money values if principal is Money)
    fun amortize(principal: Double, rate: Double, periods: Int): List<Map<String, Double>> {
        if (rate == 0.0) {
            // Special case: zero rate
            val payment = principal / periods
            var balance = principal
            val table = mutableListOf<Map<String, Double>>()
            repeat(periods) {
                balance -= payment
                table.add(
                    mapOf(
                        "payment" to payment,
                        "interest" to 0.0,
                        "principal" to payment,
                        "balance" to maxOf(0.0, balance)
                    )
                )
            }
            return table
        }

        val payment = (principal * rate) / (1 - Math.pow(1 + rate, -periods.toDouble()))
        var balance = principal
        val table = mutableListOf<Map<String, Double>>()

        repeat(periods) {
            val interest = balance * rate
            val principalPay = payment - interest
            balance -= principalPay
            table.add(
                mapOf(
                    "payment" to payment,
                    "interest" to interest,
                    "principal" to principalPay,
                    "balance" to maxOf(0.0, balance)
                )
            )
        }
        return table
    }


    //Safely converts a value to List<Double>.
    //Handles various input types and converts them to Double.
    private fun convertToListOfDouble(value: Any?): List<Double> {
        val list = value as? List<*> ?: throw IllegalArgumentException("Expected a list")
        return list.mapNotNull { 
            when (it) {
                is Double -> it
                is Number -> it.toDouble()
                else -> it.toString().toDoubleOrNull()
            }
        }
    }
}

