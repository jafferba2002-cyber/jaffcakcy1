package com.example

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.absoluteValue

data class HistoryItem(val id: Long, val expr: String, val res: String)

class CalculatorViewModel : ViewModel() {

    // Main states
    var expression by mutableStateOf("")
        private set

    var result by mutableStateOf("")
        private set

    var lastAnswer by mutableStateOf("0")
        private set

    var useDegrees by mutableStateOf(false)

    var variableXValue by mutableStateOf("1.0")

    var hapticFeedbackEnabled by mutableStateOf(true)

    // Current app section (0 = KEYPAD, 1 = THEMES)
    var currentTab by mutableStateOf(0)

    // History logs
    private var nextHistoryId = 1L
    private val _history = MutableStateFlow<List<HistoryItem>>(emptyList())
    val history: StateFlow<List<HistoryItem>> = _history.asStateFlow()

    // Theme state
    var currentTheme by mutableStateOf(CalcTheme.BentoGrid)
        private set

    // State for Custom Theme Builder
    var customBackground by mutableStateOf(Color(0xFF121212))
    var customDisplayBackground by mutableStateOf(Color(0xFF1E1E1E))
    var customKeyboardBackground by mutableStateOf(Color(0xFF181818))
    var customNumberKey by mutableStateOf(Color(0xFF2E2E2E))
    var customNumberText by mutableStateOf(Color(0xFFFFFFFF))
    var customOperatorKey by mutableStateOf(Color(0xFF424242))
    var customOperatorText by mutableStateOf(Color(0xFFFF9800))
    var customTrigFnKey by mutableStateOf(Color(0xFF37474F))
    var customTrigFnText by mutableStateOf(Color(0xFF00E676))
    var customEqualsKey by mutableStateOf(Color(0xFFFF5722))
    var customEqualsText by mutableStateOf(Color(0xFFFFFFFF))
    var customAccent by mutableStateOf(Color(0xFFFF5722))



    fun selectTheme(theme: CalcTheme) {
        currentTheme = theme
        if (theme.id == "custom") {
            applyCustomTheme()
        }
    }

    fun updateCustomColor(element: String, color: Color) {
        when (element) {
            "background" -> customBackground = color
            "displayBackground" -> customDisplayBackground = color
            "keyboardBackground" -> customKeyboardBackground = color
            "numberKey" -> customNumberKey = color
            "numberText" -> customNumberText = color
            "operatorKey" -> customOperatorKey = color
            "operatorText" -> customOperatorText = color
            "trigFnKey" -> customTrigFnKey = color
            "trigFnText" -> customTrigFnText = color
            "equalsKey" -> customEqualsKey = color
            "equalsText" -> customEqualsText = color
            "accent" -> customAccent = color
        }
        if (currentTheme.id == "custom") {
            applyCustomTheme()
        }
    }

    fun applyCustomTheme() {
        currentTheme = CalcTheme(
            id = "custom",
            name = "My Custom Design",
            isDark = true,
            background = customBackground,
            displayBackground = customDisplayBackground,
            keyboardBackground = customKeyboardBackground,
            numberKey = customNumberKey,
            numberText = customNumberText,
            operatorKey = customOperatorKey,
            operatorText = customOperatorText,
            trigFnKey = customTrigFnKey,
            trigFnText = customTrigFnText,
            equalsKey = customEqualsKey,
            equalsText = customEqualsText,
            accent = customAccent,
            gridLine = customAccent.copy(alpha = 0.2f)
        )
    }

    fun appendToken(token: String) {
        // Handle trig function autocomplete helper
        val processedToken = when (token) {
            "sin", "cos", "tan", "asin", "acos", "atan", "sinh", "cosh", "tanh", "ln", "log", "sqrt", "cbrt", "exp", "abs" -> "$token("
            "Ans" -> lastAnswer
            else -> token
        }
        expression += processedToken
    }

    fun clearAll() {
        expression = ""
        result = ""
    }

    fun backspace() {
        if (expression.isEmpty()) return

        // Backspace function abbreviation helpers smartly
        val functions = listOf(
            "asin(", "acos(", "atan(", "sinh(", "cosh(", "tanh(", "sqrt(", "cbrt(", "abs(", "exp(",
            "sin(", "cos(", "tan(", "log(", "ln("
        )

        var deleted = false
        for (f in functions) {
            if (expression.endsWith(f)) {
                expression = expression.substring(0, expression.length - f.length)
                deleted = true
                break
            }
        }

        if (!deleted) {
            expression = expression.dropLast(1)
        }
    }

    fun calculateResult() {
        if (expression.isEmpty()) {
            result = ""
            return
        }

        try {
            val parser = MathParser(useDegrees = useDegrees)
            val doubleXVal = variableXValue.toDoubleOrNull() ?: 1.0
            val eval = parser.evaluate(expression, doubleXVal)
            
            result = formatResult(eval)
            lastAnswer = result

            // Add item to history
            val newHistory = _history.value.toMutableList()
            newHistory.add(0, HistoryItem(nextHistoryId++, expression, result))
            _history.value = newHistory.take(20) // limit history size to 20
        } catch (e: Exception) {
            result = "Syntax Error"
        }
    }

    private fun formatResult(value: Double): String {
        if (value.isNaN()) return "NaN"
        if (value.isInfinite()) return if (value < 0) "-Infinity" else "Infinity"
        
        // formats large or small decimals nicely
        val stringVal = value.toString()
        return if (stringVal.endsWith(".0")) {
            stringVal.substring(0, stringVal.length - 2)
        } else if (stringVal.contains("E") || stringVal.length > 12) {
            // Check scientific formatting
            String.format("%.6g", value)
        } else {
            // Cut long decimals safely
            val absVal = value.absoluteValue
            if (absVal > 0.0 && absVal < 0.00001) {
                String.format("%.6e", value)
            } else {
                val formatted = String.format("%.8f", value)
                // trim trailing zeroes
                var end = formatted.length - 1
                while (end > 0 && formatted[end] == '0') {
                    end--
                }
                if (formatted[end] == '.') {
                    end--
                }
                formatted.substring(0, end + 1)
            }
        }
    }

    fun loadFromHistory(item: HistoryItem) {
        expression = item.expr
        result = item.res
    }

    fun clearHistory() {
        _history.value = emptyList()
    }
}
