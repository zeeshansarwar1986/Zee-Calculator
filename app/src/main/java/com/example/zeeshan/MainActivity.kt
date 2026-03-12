package com.example.zeeshan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZeeCalculatorTheme {
                ZeeCalculatorApp()
            }
        }
    }
}

@Composable
fun ZeeCalculatorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            background = Color(0xFFF3F3F3), // Light gray background like Windows
            surface = Color.White,
            primary = Color(0xFF0067C0), // Windows Blue
            onPrimary = Color.White,
            onSurface = Color.Black
        ),
        content = content
    )
}

@Composable
fun ZeeCalculatorApp() {
    var primaryDisplay by remember { mutableStateOf("0") }
    var secondaryDisplay by remember { mutableStateOf("") }
    
    // State variables for logic
    var previousValue by remember { mutableStateOf("0") }
    var currentOperation by remember { mutableStateOf("") }
    var isNewInput by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 48.dp) // padding for status bar
    ) {
        // --- Top Bar (Menu, Title, History) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Open Menu */ }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.Black)
            }
            Text(
                text = "Standard",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.padding(start = 8.dp)
            )
            // Empty space
            Spacer(modifier = Modifier.weight(1f))
            // History Icon (using a placeholder Text if icon not available, but let's just make it a simple text for now or skip)
            Text(text = "↺", fontSize = 20.sp, modifier = Modifier.padding(end = 12.dp))
        }

        // --- Display Area ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Push keypad to bottom
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            // Expression History (e.g. 5 + 3 =)
            Text(
                text = secondaryDisplay,
                fontSize = 15.sp,
                color = Color.Gray,
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
            
            // Current Number
            Text(
                text = formatDisplayNumber(primaryDisplay),
                fontSize = 48.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- Memory Buttons ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val memColor = Color.Gray
            MemoryButton("MC", memColor)
            MemoryButton("MR", memColor)
            MemoryButton("M+", memColor)
            MemoryButton("M-", memColor)
            MemoryButton("MS", memColor)
            MemoryButton("M▾", memColor)
        }

        // --- Keypad Section ---
        val btnDefaultBg = Color.White
        val btnDefaultTxt = Color.Black
        val btnActionBg = Color(0xFFF9F9F9)
        val btnActionTxt = Color.Black
        val btnPrimaryBg = Color(0xFF0067C0)
        val btnPrimaryTxt = Color.White
        
        val buttonData = listOf(
            listOf(calcBtn("%", btnActionBg, btnActionTxt), calcBtn("CE", btnActionBg, btnActionTxt), calcBtn("C", btnActionBg, btnActionTxt), calcBtn("⌫", btnActionBg, btnActionTxt)),
            listOf(calcBtn("1/x", btnActionBg, btnActionTxt), calcBtn("x²", btnActionBg, btnActionTxt), calcBtn("²√x", btnActionBg, btnActionTxt), calcBtn("÷", btnActionBg, btnActionTxt)),
            listOf(calcBtn("7", btnDefaultBg, btnDefaultTxt), calcBtn("8", btnDefaultBg, btnDefaultTxt), calcBtn("9", btnDefaultBg, btnDefaultTxt), calcBtn("×", btnActionBg, btnActionTxt)),
            listOf(calcBtn("4", btnDefaultBg, btnDefaultTxt), calcBtn("5", btnDefaultBg, btnDefaultTxt), calcBtn("6", btnDefaultBg, btnDefaultTxt), calcBtn("-", btnActionBg, btnActionTxt)),
            listOf(calcBtn("1", btnDefaultBg, btnDefaultTxt), calcBtn("2", btnDefaultBg, btnDefaultTxt), calcBtn("3", btnDefaultBg, btnDefaultTxt), calcBtn("+", btnActionBg, btnActionTxt)),
            listOf(calcBtn("+/-", btnDefaultBg, btnDefaultTxt), calcBtn("0", btnDefaultBg, btnDefaultTxt), calcBtn(".", btnDefaultBg, btnDefaultTxt), calcBtn("=", btnPrimaryBg, btnPrimaryTxt))
        )

        val handleAction: (String) -> Unit = { action ->
            when (action) {
                in "0".."9" -> {
                    if (isNewInput) {
                        primaryDisplay = action
                        isNewInput = false
                    } else {
                        if (primaryDisplay == "0") primaryDisplay = action
                        else primaryDisplay += action
                    }
                }
                "." -> {
                    if (isNewInput) {
                        primaryDisplay = "0."
                        isNewInput = false
                    } else if (!primaryDisplay.contains(".")) {
                        primaryDisplay += "."
                    }
                }
                "+/-" -> {
                    if (primaryDisplay != "0") {
                        primaryDisplay = if (primaryDisplay.startsWith("-")) {
                            primaryDisplay.substring(1)
                        } else {
                            "-$primaryDisplay"
                        }
                    }
                }
                "C" -> {
                    primaryDisplay = "0"
                    secondaryDisplay = ""
                    previousValue = "0"
                    currentOperation = ""
                    isNewInput = true
                }
                "CE" -> {
                    primaryDisplay = "0"
                    isNewInput = true
                }
                "⌫" -> {
                    if (!isNewInput) {
                        primaryDisplay = if (primaryDisplay.length > 1) {
                            primaryDisplay.dropLast(1)
                        } else {
                            "0"
                        }
                    }
                }
                "+", "-", "×", "÷" -> {
                    if (!isNewInput && currentOperation.isNotEmpty()) {
                        // Calculate intermediate result
                        val result = calculate(previousValue, primaryDisplay, currentOperation)
                        primaryDisplay = result
                    }
                    previousValue = primaryDisplay
                    currentOperation = action
                    secondaryDisplay = "$previousValue $action"
                    isNewInput = true
                }
                "=" -> {
                    if (currentOperation.isNotEmpty()) {
                        val result = calculate(previousValue, primaryDisplay, currentOperation)
                        secondaryDisplay = "$previousValue $currentOperation $primaryDisplay ="
                        primaryDisplay = result
                        currentOperation = ""
                        isNewInput = true
                        previousValue = result
                    }
                }
                "%" -> { // very basic percentage (just divide by 100 for now, standard windows is a bit more complex relating to the previous value)
                    val valD = primaryDisplay.toDoubleOrNull() ?: 0.0
                    primaryDisplay = formatOutput(valD / 100.0)
                    isNewInput = true
                }
                "1/x" -> {
                    val valD = primaryDisplay.toDoubleOrNull() ?: 0.0
                    if (valD != 0.0) {
                        secondaryDisplay = "1/($primaryDisplay)"
                        primaryDisplay = formatOutput(1.0 / valD)
                    } else {
                        primaryDisplay = "Cannot divide by zero"
                    }
                    isNewInput = true
                }
                "x²" -> {
                    val valD = primaryDisplay.toDoubleOrNull() ?: 0.0
                    secondaryDisplay = "sqr($primaryDisplay)"
                    primaryDisplay = formatOutput(valD * valD)
                    isNewInput = true
                }
                "²√x" -> {
                    val valD = primaryDisplay.toDoubleOrNull() ?: 0.0
                    if (valD >= 0) {
                        secondaryDisplay = "√($primaryDisplay)"
                        primaryDisplay = formatOutput(sqrt(valD))
                    } else {
                        primaryDisplay = "Invalid input"
                    }
                    isNewInput = true
                }
            }
        }

        Column(modifier = Modifier.padding(2.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            buttonData.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    row.forEach { btn ->
                        KeypadButton(
                            text = btn.text,
                            bgColor = btn.bgColor,
                            textColor = btn.textColor,
                            modifier = Modifier.weight(1f).aspectRatio(1.25f), // rectangle ratio
                            onClick = { handleAction(btn.text) }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

fun calculate(val1Str: String, val2Str: String, op: String): String {
    val val1 = val1Str.toDoubleOrNull() ?: 0.0
    val val2 = val2Str.toDoubleOrNull() ?: 0.0
    val result = when (op) {
        "+" -> val1 + val2
        "-" -> val1 - val2
        "×" -> val1 * val2
        "÷" -> {
            if (val2 == 0.0) return "Cannot divide by zero"
            val1 / val2
        }
        else -> 0.0
    }
    return formatOutput(result)
}

fun formatOutput(value: Double): String {
    if (value.isInfinite() || value.isNaN()) return "Error"
    val valString = String.format("%.8f", value).trimEnd('0').trimEnd('.')
    val asLong = value.toLong()
    return if (value == asLong.toDouble()) asLong.toString() else valString
}

fun formatDisplayNumber(str: String): String {
    if (str == "Cannot divide by zero" || str == "Invalid input" || str == "Error") return str
    // Just a placeholder for actual localized number formatting 
    // to keep it simple, we just return the string. 
    // In a real scenario, use NumberFormat.
    return str
}

@Composable
fun MemoryButton(text: String, color: Color) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = color,
        modifier = Modifier
            .padding(12.dp)
            .clickable { /* Memory Logic Placeholder */ }
    )
}

data class BtnData(val text: String, val bgColor: Color, val textColor: Color)

fun calcBtn(text: String, bg: Color, textCol: Color): BtnData = BtnData(text, bg, textCol)

@Composable
fun KeypadButton(
    text: String,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.padding(1.dp), // slight gap like the picture
        colors = ButtonDefaults.buttonColors(containerColor = bgColor),
        shape = RoundedCornerShape(4.dp), // Slightly rounded corners
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = if (text == "=") 32.sp else 22.sp,
            fontWeight = if (text in listOf("0","1","2","3","4","5","6","7","8","9")) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
