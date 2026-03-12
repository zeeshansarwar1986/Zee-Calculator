package com.example.zeeshan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorApp()
        }
    }
}

@Composable
fun CalculatorApp() {
    var expression by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("0") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1C1E))
            .padding(16.dp),
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = expression.ifEmpty { "0" },
            color = Color.White,
            fontSize = 48.sp,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            maxLines = 1
        )

        Text(
            text = "= $result",
            color = Color(0xFF8E8E93),
            fontSize = 32.sp,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            maxLines = 1
        )

        val buttons = listOf(
            listOf(CalculatorButton("C", Color(0xFF8E8E93), Color.Black), CalculatorButton("(", Color(0xFF505053), Color.White), CalculatorButton(")", Color(0xFF505053), Color.White), CalculatorButton("÷", Color(0xFFFF9500), Color.White)),
            listOf(CalculatorButton("7", Color(0xFF505053), Color.White), CalculatorButton("8", Color(0xFF505053), Color.White), CalculatorButton("9", Color(0xFF505053), Color.White), CalculatorButton("×", Color(0xFFFF9500), Color.White)),
            listOf(CalculatorButton("4", Color(0xFF505053), Color.White), CalculatorButton("5", Color(0xFF505053), Color.White), CalculatorButton("6", Color(0xFF505053), Color.White), CalculatorButton("-", Color(0xFFFF9500), Color.White)),
            listOf(CalculatorButton("1", Color(0xFF505053), Color.White), CalculatorButton("2", Color(0xFF505053), Color.White), CalculatorButton("3", Color(0xFF505053), Color.White), CalculatorButton("+", Color(0xFFFF9500), Color.White)),
            listOf(CalculatorButton("0", Color(0xFF505053), Color.White).copy(span = 2), CalculatorButton(".", Color(0xFF505053), Color.White), CalculatorButton("=", Color(0xFFFF9500), Color.White))
        )

        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { button ->
                    val span = button.span ?: 1
                    CalculatorButton(
                        text = button.text,
                        backgroundColor = button.backgroundColor,
                        textColor = button.textColor,
                        modifier = Modifier
                            .weight(span.toFloat())
                            .aspectRatio(if (button.text == "0") 2f else 1f)
                    ) {
                        when (button.text) {
                            "C" -> {
                                expression = ""
                                result = "0"
                            }
                            "=" -> {
                                try {
                                    val evalResult = evaluateExpression(expression)
                                    result = evalResult
                                    expression = evalResult
                                } catch (e: Exception) {
                                    result = "Error"
                                }
                            }
                            "÷" -> expression += "/"
                            "×" -> expression += "*"
                            else -> expression += button.text
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

data class CalculatorButton(
    val text: String,
    val backgroundColor: Color,
    val textColor: Color,
    val span: Int? = null
)

@Composable
fun CalculatorButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .clip(CircleShape),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

fun evaluateExpression(expression: String): String {
    val cleanExpr = expression.replace("×", "*").replace("÷", "/")
    return cleanExpr.toDoubleOrNull()?.let { result ->
        if (result == result.toLong().toDouble()) {
            result.toLong().toString()
        } else {
            String.format("%.8f", result).trimEnd('0').trimEnd('.')
        }
    } ?: try {
        val result = evaluateSimple(cleanExpr)
        if (result == result.toLong().toDouble()) {
            result.toLong().toString()
        } else {
            String.format("%.8f", result).trimEnd('0').trimEnd('.')
        }
    } catch (e: Exception) {
        "Error"
    }
}

fun evaluateSimple(expr: String): Double {
    val tokens = mutableListOf<String>()
    var current = ""

    for (char in expr) {
        when {
            char.isDigit() || char == '.' -> current += char
            char in "+-*/" -> {
                if (current.isNotEmpty()) {
                    tokens.add(current)
                    current = ""
                }
                tokens.add(char.toString())
            }
        }
    }
    if (current.isNotEmpty()) tokens.add(current)

    var result = tokens[0].toDouble()
    var i = 1

    while (i < tokens.size) {
        val op = tokens[i]
        val nextNum = tokens[i + 1].toDouble()
        result = when (op) {
            "+" -> result + nextNum
            "-" -> result - nextNum
            "*" -> result * nextNum
            "/" -> if (nextNum != 0.0) result / nextNum else throw Exception("Divide by zero")
            else -> result
        }
        i += 2
    }

    return result
}
