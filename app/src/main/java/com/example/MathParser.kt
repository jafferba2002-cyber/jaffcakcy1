package com.example

import kotlin.math.*

class MathParser(
    private val useDegrees: Boolean = false
) {
    fun evaluate(expression: String, xValue: Double = 0.0): Double {
        val prepared = preprocess(expression)
        if (prepared.isEmpty()) return 0.0
        return ParserInstance(prepared, xValue, useDegrees).parse()
    }

    private fun preprocess(expr: String): String {
        return expr
            .replace(" ", "")
            .replace("π", "pi")
            .replace("PI", "pi")
            .replace("Pi", "pi")
            .replace("×", "*")
            .replace("÷", "/")
    }

    private class ParserInstance(
        private val str: String,
        private val xVal: Double,
        private val useDegrees: Boolean
    ) {
        private var pos = -1
        private var ch = 0

        private fun nextChar() {
            ch = if (++pos < str.length) str[pos].code else -1
        }

        private fun eat(charToEat: Int): Boolean {
            while (ch == ' '.code) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < str.length) throw RuntimeException("Unexpected character: " + ch.toChar())
            return x
        }

        private fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                if (eat('+'.code)) x += parseTerm()
                else if (eat('-'.code)) x -= parseTerm()
                else break
            }
            return x
        }

        private fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                if (eat('*'.code) || eat('×'.code)) {
                    x *= parseFactor()
                } else if (eat('/'.code) || eat('÷'.code)) {
                    val divisor = parseFactor()
                    if (divisor == 0.0) throw ArithmeticException("Division by zero")
                    x /= divisor
                } else {
                    // Implicit multiplication
                    if (ch == '('.code || ch == 'x'.code || ch == 'p'.code || ch == 'e'.code || (ch in 'a'.code..'z'.code)) {
                        // Avoid splitting function names like sin, ln, exp, etc.
                        if (isFunctionStart(ch)) {
                            x *= parseFactor()
                        } else if (ch == 'x'.code) {
                            x *= parseFactor()
                        } else {
                            break
                        }
                    } else {
                        break
                    }
                }
            }
            return x
        }

        private fun peek(): Char {
            return if (pos + 1 < str.length) str[pos + 1] else '\u0000'
        }

        private fun isFunctionStart(charInt: Int): Boolean {
            val s = charInt.toChar()
            return s == '(' || s == 'x' || s == 'p' || s == 'e' || s in 'a'..'z'
        }

        private fun parseFactor(): Double {
            if (eat('+'.code)) return parseFactor()
            if (eat('-'.code)) return -parseFactor()

            var x: Double
            val startPos = this.pos
            if (eat('('.code)) {
                x = parseExpression()
                eat(')'.code)
            } else if (ch in '0'.code..'9'.code || ch == '.'.code) {
                while (ch in '0'.code..'9'.code || ch == '.'.code) nextChar()
                x = str.substring(startPos, this.pos).toDouble()
            } else if (ch == 'x'.code && peek() != 'i' && peek() != 'p') {
                // Just the variable x
                eat('x'.code)
                x = xVal
            } else if (ch in 'a'.code..'z'.code || ch == 'π'.code) {
                while (ch in 'a'.code..'z'.code || ch == 'π'.code) nextChar()
                val name = str.substring(startPos, this.pos)
                if (name == "pi" || name == "π") {
                    x = PI
                } else if (name == "e" && ch != 'x'.code) {
                    x = kotlin.math.E
                } else {
                    if (eat('('.code)) {
                        val arg = parseExpression()
                        eat(')'.code)
                        x = when (name) {
                            "sin" -> if (useDegrees) sin(Math.toRadians(arg)) else sin(arg)
                            "cos" -> if (useDegrees) cos(Math.toRadians(arg)) else cos(arg)
                            "tan" -> if (useDegrees) tan(Math.toRadians(arg)) else tan(arg)
                            "asin" -> if (useDegrees) Math.toDegrees(asin(arg)) else asin(arg)
                            "acos" -> if (useDegrees) Math.toDegrees(acos(arg)) else acos(arg)
                            "atan" -> if (useDegrees) Math.toDegrees(atan(arg)) else atan(arg)
                            "sinh" -> sinh(arg)
                            "cosh" -> cosh(arg)
                            "tanh" -> tanh(arg)
                            "sqrt" -> sqrt(arg)
                            "cbrt" -> cbrt(arg)
                            "log" -> log10(arg)
                            "ln" -> ln(arg)
                            "exp" -> exp(arg)
                            "abs" -> abs(arg)
                            else -> throw RuntimeException("Unknown function: $name")
                        }
                    } else {
                        if (name == "e") {
                            x = kotlin.math.E
                        } else if (name == "x") {
                            x = xVal
                        } else {
                            throw RuntimeException("Expected parameter after function: $name")
                        }
                    }
                }
            } else {
                throw RuntimeException("Unexpected character: " + ch.toChar())
            }

            if (eat('^'.code)) {
                x = x.pow(parseFactor())
            }

            // Suffix check for percent / factorial
            while (true) {
                if (eat('%'.code)) {
                    x /= 100.0
                } else if (eat('!'.code)) {
                    x = factorial(x)
                } else {
                    break
                }
            }

            return x
        }

        private fun factorial(n: Double): Double {
            if (n < 0.0) return Double.NaN
            val intN = n.toInt()
            if (intN.toDouble() != n) {
                return exp(logGamma(n + 1))
            }
            if (intN > 170) return Double.POSITIVE_INFINITY
            var res = 1.0
            for (i in 1..intN) res *= i
            return res
        }

        private fun logGamma(x: Double): Double {
            var tmp = x + 5.5
            tmp -= (x + 0.5) * ln(tmp)
            var ser = 1.000000000190015
            val cof = doubleArrayOf(
                76.18009172947146,
                -86.50532032941677,
                24.01409824083091,
                -1.231739572450155,
                0.1208650973866179e-2,
                -0.5395239384953e-5
            )
            var xx = x
            for (j in 0..5) {
                xx += 1.0
                ser += cof[j] / xx
            }
            return -tmp + ln(2.5066282746310005 * ser / x)
        }
    }
}
