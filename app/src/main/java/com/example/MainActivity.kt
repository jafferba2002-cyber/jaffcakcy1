package com.example

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.draw.drawBehind
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: CalculatorViewModel = viewModel()
            JaffCalcyMainScreen(viewModel)
        }
    }
}

@Composable
fun JaffCalcyMainScreen(viewModel: CalculatorViewModel) {
    val theme = viewModel.currentTheme
    val haptic = LocalHapticFeedback.current
    var showSettings by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = theme.background
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            contentAlignment = Alignment.Center
        ) {
            val isTablet = maxWidth > 600.dp

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(max = 500.dp)
                    .fillMaxWidth()
                    .padding(if (isTablet) 24.dp else 12.dp)
            ) {
                HeaderSection(viewModel, theme, haptic) {
                    showSettings = true
                }
                Spacer(modifier = Modifier.height(if (isTablet) 16.dp else 8.dp))

                DisplayPane(viewModel, theme)

                Spacer(modifier = Modifier.height(if (isTablet) 20.dp else 12.dp))

                Box(modifier = Modifier.weight(1f)) {
                    KeypadPane(viewModel, theme, haptic)
                }
            }
        }
    }

    if (showSettings) {
        SettingsDialog(viewModel = viewModel, theme = theme, haptic = haptic) {
            showSettings = false
        }
    }
}

@Composable
fun SettingsDialog(
    viewModel: CalculatorViewModel,
    theme: CalcTheme,
    haptic: HapticFeedback,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .border(1.dp, theme.accent.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = theme.background),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = theme.accent,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Settings",
                            color = if (theme.isDark) Color.White else Color.Black,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    IconButton(
                        onClick = {
                            triggerHaptic(haptic, viewModel.hapticFeedbackEnabled)
                            onDismiss()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close settings",
                            tint = if (theme.isDark) Color.LightGray else Color.DarkGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // 1. TACTILE HAPTIC FEEDBACK ACTIVE SECTION
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(theme.keyboardBackground)
                                .border(0.5.dp, theme.accent.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "TACTILE FEEDBACK",
                                color = theme.accent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Notifications,
                                        contentDescription = "Haptic indicator",
                                        tint = if (viewModel.hapticFeedbackEnabled) theme.accent else Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Button Vibrations",
                                            color = if (theme.isDark) Color.White else Color.Black,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = if (viewModel.hapticFeedbackEnabled) "Active" else "Inactive",
                                            color = Color.Gray,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                Switch(
                                    checked = viewModel.hapticFeedbackEnabled,
                                    onCheckedChange = {
                                        viewModel.hapticFeedbackEnabled = it
                                        triggerHaptic(haptic, it)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = theme.accent,
                                        checkedTrackColor = theme.accent.copy(alpha = 0.3f),
                                        uncheckedThumbColor = Color.Gray,
                                        uncheckedTrackColor = Color.LightGray.copy(alpha = 0.2f)
                                    )
                                )
                            }
                        }
                    }

                    // 2. THEME SELECTION SECTION
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(theme.keyboardBackground)
                                .border(0.5.dp, theme.accent.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "SELECT APP THEME",
                                color = theme.accent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            // Horizontal preset themes row
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(CalcTheme.PresetThemes) { preset ->
                                    val isSelected = viewModel.currentTheme.id == preset.id
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(preset.background)
                                            .border(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) theme.accent else Color.Gray.copy(alpha = 0.3f),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable {
                                                triggerHaptic(haptic, viewModel.hapticFeedbackEnabled)
                                                viewModel.selectTheme(preset)
                                            }
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = preset.name,
                                                color = if (preset.isDark) Color.White else Color.Black,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(preset.accent))
                                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(preset.equalsKey))
                                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(preset.displayBackground))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 3. ABOUT SECTION
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(theme.keyboardBackground)
                                .border(0.5.dp, theme.accent.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "ABOUT",
                                color = theme.accent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "App Name: Calculator",
                                color = if (theme.isDark) Color.White else Color.Black,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Version: 1.0.0",
                                color = if (theme.isDark) Color.LightGray else Color.DarkGray,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Developer: Jaffer Sadiq",
                                color = if (theme.isDark) Color.LightGray else Color.DarkGray,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Brand: Webzio",
                                color = if (theme.isDark) Color.LightGray else Color.DarkGray,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = theme.accent.copy(alpha = 0.2f), thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Description",
                                color = theme.accent,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "This Calculator app is designed to make your daily calculations quick, accurate, and effortless. Whether you are handling basic arithmetic or complex mathematical operations, this app provides a clean and intuitive interface to get the job done efficiently.",
                                color = if (theme.isDark) Color.LightGray else Color.DarkGray,
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Key Features",
                                color = theme.accent,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            val featuresList = listOf(
                                "User-Friendly Design" to "A clean, minimalist interface that makes navigation simple.",
                                "High Precision" to "Delivers fast and error-free results for all your calculations.",
                                "Lightweight" to "Optimized for performance to ensure it runs smoothly on your device without consuming excessive resources."
                            )
                            
                            featuresList.forEach { (title, description) ->
                                Row(
                                    modifier = Modifier.padding(bottom = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(text = "•", color = theme.accent, fontSize = 14.sp)
                                    Column {
                                        Text(
                                            text = title,
                                            color = if (theme.isDark) Color.White else Color.Black,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = description,
                                            color = if (theme.isDark) Color.LightGray else Color.DarkGray,
                                            fontSize = 11.5.sp,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = theme.accent.copy(alpha = 0.2f), thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Contact Information",
                                color = theme.accent,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "I am committed to improving your experience. If you have any feedback, suggestions, or encounter any issues, please feel free to reach out.",
                                color = if (theme.isDark) Color.LightGray else Color.DarkGray,
                                fontSize = 11.5.sp,
                                lineHeight = 15.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Email: contact@webzio.xyz",
                                color = theme.accent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Website: webzio.xyz",
                                color = theme.accent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderSection(viewModel: CalculatorViewModel, theme: CalcTheme, haptic: HapticFeedback, onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // signature Bento grid initials hallmark
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(theme.accent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "JJ",
                    color = if (theme.id == "bento_grid") Color(0xFF381E72) else (if (theme.isDark) Color.Black else Color.White),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }

            Column {
                Text(
                    text = "jaffcalcy",
                    color = if (theme.isDark) Color.White else Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Scientific Calculator",
                    color = if (theme.isDark) Color.LightGray else Color.DarkGray,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.SansSerif
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Rad / Deg Mode Switch Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(theme.keyboardBackground)
                    .border(1.dp, theme.accent.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .clickable {
                        triggerHaptic(haptic, viewModel.hapticFeedbackEnabled)
                        viewModel.useDegrees = !viewModel.useDegrees
                    }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (viewModel.useDegrees) "DEG" else "RAD",
                    color = theme.accent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Mode Switch",
                    tint = theme.accent,
                    modifier = Modifier.size(12.dp)
                )
            }

            // Settings gear button
            IconButton(
                onClick = {
                    triggerHaptic(haptic, viewModel.hapticFeedbackEnabled)
                    onSettingsClick()
                },
                modifier = Modifier
                    .size(36.dp)
                    .background(theme.keyboardBackground, CircleShape)
                    .border(1.dp, theme.accent.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings Menu",
                    tint = theme.accent,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun TabSelectionButton(text: String, isSelected: Boolean, theme: CalcTheme, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) theme.accent.copy(alpha = 0.15f) else theme.keyboardBackground)
            .border(
                width = 1.dp,
                color = if (isSelected) theme.accent else theme.accent.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) theme.accent else (if (theme.isDark) Color.Gray else Color.DarkGray),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun TabRowHeader(viewModel: CalculatorViewModel, theme: CalcTheme, haptic: HapticFeedback) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(theme.keyboardBackground)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val tabs = listOf("KEYBOARD", "THEME")
        tabs.forEachIndexed { index, title ->
            val isActive = viewModel.currentTab == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isActive) theme.accent else Color.Transparent)
                    .clickable {
                        triggerHaptic(haptic, viewModel.hapticFeedbackEnabled)
                        viewModel.currentTab = index
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = if (isActive) (if (theme.isDark) Color.Black else Color.White) else (if (theme.isDark) Color.LightGray else Color.DarkGray),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun DisplayPane(viewModel: CalculatorViewModel, theme: CalcTheme) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, theme.accent.copy(alpha = 0.2f), RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = theme.displayBackground),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.End
        ) {
            // Expression buffer
            Text(
                text = if (viewModel.expression.isEmpty()) "0" else viewModel.expression,
                color = if (viewModel.expression.isEmpty()) Color.Gray else (if (theme.isDark) Color.White else Color.Black),
                fontSize = 28.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.End,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 36.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("display_expression")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Evaluated Result string
            AnimatedVisibility(
                visible = viewModel.result.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = if (viewModel.result.isEmpty()) "" else "= ${viewModel.result}",
                    color = theme.accent,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("display_result")
                )
            }
        }
    }
}

@Composable
fun KeypadPane(viewModel: CalculatorViewModel, theme: CalcTheme, haptic: HapticFeedback) {
    val context = LocalContext.current
    var isAdvancedMode by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Toggle slide between Basic & Advanced mathematical keyboard rows
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(theme.keyboardBackground)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (!isAdvancedMode) theme.accent.copy(alpha = 0.15f) else Color.Transparent)
                    .clickable {
                        triggerHaptic(haptic, viewModel.hapticFeedbackEnabled)
                        isAdvancedMode = false
                    }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Basic",
                    color = if (!isAdvancedMode) theme.accent else Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isAdvancedMode) theme.accent.copy(alpha = 0.15f) else Color.Transparent)
                    .clickable {
                        triggerHaptic(haptic, viewModel.hapticFeedbackEnabled)
                        isAdvancedMode = true
                    }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Symbol",
                    color = if (isAdvancedMode) theme.accent else Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Keypad buttons matrix
        Box(modifier = Modifier.weight(1f)) {
            if (isAdvancedMode) {
                ScientificButtonsGrid(viewModel, theme, haptic)
            } else {
                BasicButtonsGrid(viewModel, theme, haptic)
            }
        }
    }
}

@Composable
fun BasicButtonsGrid(viewModel: CalculatorViewModel, theme: CalcTheme, haptic: HapticFeedback) {
    val rows = listOf(
        listOf("AC" to "action", "()" to "action", "%" to "action", "DEL" to "action"),
        listOf("7" to "number", "8" to "number", "9" to "number", "÷" to "operator"),
        listOf("4" to "number", "5" to "number", "6" to "number", "×" to "operator"),
        listOf("1" to "number", "2" to "number", "3" to "number", "-" to "operator"),
        listOf("0" to "number", "." to "number", "x" to "number", "+" to "operator"),
        listOf("Ans" to "number", "=" to "equals")
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                row.forEach { (label, type) ->
                    val weight = if (label == "=") 2f else 1f
                    CalculatorButton(
                        label = label,
                        type = type,
                        theme = theme,
                        haptic = haptic,
                        hapticsEnabled = viewModel.hapticFeedbackEnabled,
                        modifier = Modifier
                            .weight(weight)
                            .fillMaxHeight()
                    ) {
                        when (label) {
                            "AC" -> viewModel.clearAll()
                            "DEL" -> viewModel.backspace()
                            "=" -> viewModel.calculateResult()
                            "()" -> {
                                // Smart bracket insertions
                                val currentExpr = viewModel.expression
                                val openCount = currentExpr.count { it == '(' }
                                val closeCount = currentExpr.count { it == ')' }
                                if (openCount > closeCount && currentExpr.isNotEmpty() && currentExpr.last().isDigit()) {
                                    viewModel.appendToken(")")
                                } else {
                                    viewModel.appendToken("(")
                                }
                            }
                            else -> viewModel.appendToken(label)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScientificButtonsGrid(viewModel: CalculatorViewModel, theme: CalcTheme, haptic: HapticFeedback) {
    val rows = listOf(
        listOf("AC" to "action", "DEL" to "action", "x" to "number", "^" to "operator"),
        listOf("sin" to "trig", "cos" to "trig", "tan" to "trig", "%" to "operator"),
        listOf("asin" to "trig", "acos" to "trig", "atan" to "trig", "sq" to "operator"), 
        listOf("sinh" to "trig", "cosh" to "trig", "tanh" to "trig", "!" to "operator"),
        listOf("ln" to "trig", "log" to "trig", "π" to "number", "e" to "number"),
        listOf("(" to "operator", ")" to "operator", "abs" to "trig", "=" to "equals")
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                row.forEach { (label, type) ->
                    val actualLabelDisplay = when (label) {
                        "sq" -> "sqrt"
                        else -> label
                    }
                    CalculatorButton(
                        label = actualLabelDisplay,
                        type = type,
                        theme = theme,
                        haptic = haptic,
                        hapticsEnabled = viewModel.hapticFeedbackEnabled,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        when (label) {
                            "AC" -> viewModel.clearAll()
                            "DEL" -> viewModel.backspace()
                            "=" -> viewModel.calculateResult()
                            "sq" -> viewModel.appendToken("sqrt")
                            else -> viewModel.appendToken(label)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(
    label: String,
    type: String,
    theme: CalcTheme,
    haptic: HapticFeedback,
    hapticsEnabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor = when (type) {
        "number" -> theme.numberKey
        "operator", "action" -> theme.operatorKey
        "trig" -> theme.trigFnKey
        "equals" -> theme.equalsKey
        else -> theme.numberKey
    }

    val textColor = when (type) {
        "number" -> theme.numberText
        "operator", "action" -> theme.operatorText
        "trig" -> theme.trigFnText
        "equals" -> theme.equalsText
        else -> theme.numberText
    }

    val tag = "button_${label.lowercase()}"

    val glassBrush = Brush.verticalGradient(
        colors = listOf(
            backgroundColor.copy(alpha = 0.95f),
            backgroundColor.copy(alpha = 0.75f)
        )
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(glassBrush)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.45f),
                        theme.accent.copy(alpha = 0.2f),
                        Color.White.copy(alpha = 0.05f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(1f, 1f)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .drawBehind {
                // Curved liquid sheen highlights
                val glossPath = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width, size.height * 0.42f)
                    quadraticTo(
                        size.width * 0.5f, size.height * 0.52f,
                        0f, size.height * 0.32f
                    )
                    close()
                }

                // Draw glossy specular reflections
                drawPath(
                    path = glossPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.25f),
                            Color.White.copy(alpha = 0.02f)
                        )
                    )
                )

                // Draw captured soft bottom glow reflection
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.5f, size.height),
                        radius = size.width * 0.7f
                    )
                )
            }
            .clickable {
                triggerHaptic(haptic, hapticsEnabled)
                onClick()
            }
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = if (label.length > 3) 14.sp else 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center
        )
    }
}



@Composable
fun ThemeCustomizerPane(viewModel: CalculatorViewModel, theme: CalcTheme, haptic: HapticFeedback) {
    val swatches = listOf(
        Color(0xFF0D0B14), Color(0xFF051726), Color(0xFF230C16), Color(0xFF0F1E16), Color(0xFF130E20), // Dark Core BG
        Color(0xFFF3F4F6), Color(0xFFFFFFFF), // Light core
        Color(0xFFFF0055), Color(0xFF00FFCC), Color(0xFF00E5FF), Color(0xFFFFB703), Color(0xFF00E676), Color(0xFFB19DFF), // Accent core
        Color(0xFF282042), Color(0xFF104270), Color(0xFF5D2741), Color(0xFF2E6047), Color(0xFF3B2E60), Color(0xFF424242)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Preset Palettes",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = theme.accent,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Grid of master preset themes
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalcTheme.PresetThemes.forEach { preset ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (viewModel.currentTheme.id == preset.id) 2.dp else 0.5.dp,
                                color = if (viewModel.currentTheme.id == preset.id) theme.accent else Color.Gray.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                triggerHaptic(haptic, viewModel.hapticFeedbackEnabled)
                                viewModel.selectTheme(preset)
                            },
                        colors = CardDefaults.cardColors(containerColor = preset.background)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = preset.name,
                                color = if (preset.isDark) Color.White else Color.Black,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                MiniThemeDot(preset.background)
                                MiniThemeDot(preset.displayBackground)
                                MiniThemeDot(preset.equalsKey)
                                MiniThemeDot(preset.accent)
                            }
                        }
                    }
                }

                // Custom Slot trigger
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = if (viewModel.currentTheme.id == "custom") 2.dp else 1.dp,
                            color = if (viewModel.currentTheme.id == "custom") theme.accent else theme.accent.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            triggerHaptic(haptic, viewModel.hapticFeedbackEnabled)
                            viewModel.selectTheme(
                                CalcTheme(
                                    id = "custom",
                                    name = "My Custom Design",
                                    isDark = true,
                                    background = viewModel.customBackground,
                                    displayBackground = viewModel.customDisplayBackground,
                                    keyboardBackground = viewModel.customKeyboardBackground,
                                    numberKey = viewModel.customNumberKey,
                                    numberText = viewModel.customNumberText,
                                    operatorKey = viewModel.customOperatorKey,
                                    operatorText = viewModel.customOperatorText,
                                    trigFnKey = viewModel.customTrigFnKey,
                                    trigFnText = viewModel.customTrigFnText,
                                    equalsKey = viewModel.customEqualsKey,
                                    equalsText = viewModel.customEqualsText,
                                    accent = viewModel.customAccent,
                                    gridLine = viewModel.customAccent.copy(alpha = 0.2f)
                                )
                            )
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = if (theme.isDark) Color(0xFF1E1E1E) else Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Settings, contentDescription = "Custom design", tint = theme.accent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "My Custom Design",
                                color = if (theme.isDark) Color.White else Color.Black,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        if (viewModel.currentTheme.id == "custom") {
                            Text(
                                text = "ACTIVE",
                                color = theme.accent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Color Element fine tuning (Visible if custom selected or for editing)
        item {
            Text(
                text = "Interactive Design Editor",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = theme.accent,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "Tap on any element below to change its color block:",
                fontSize = 11.sp,
                color = Color.Gray,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            val customElements = listOf(
                "background" to ("App Background" to viewModel.customBackground),
                "displayBackground" to ("Display Screen Background" to viewModel.customDisplayBackground),
                "keyboardBackground" to ("Keypad Frame Background" to viewModel.customKeyboardBackground),
                "numberKey" to ("Number Keypad Background" to viewModel.customNumberKey),
                "numberText" to ("Number Label Color" to viewModel.customNumberText),
                "operatorKey" to ("Operator Keypad Background" to viewModel.customOperatorKey),
                "operatorText" to ("Operator Label Color" to viewModel.customOperatorText),
                "trigFnKey" to ("Trig-Scientific Keys Background" to viewModel.customTrigFnKey),
                "trigFnText" to ("Trig-Scientific Labels Color" to viewModel.customTrigFnText),
                "equalsKey" to ("Equals Keypad Background" to viewModel.customEqualsKey),
                "equalsText" to ("Equals Label Color" to viewModel.customEqualsText),
                "accent" to ("Active UI Accent Line" to viewModel.customAccent)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                customElements.forEach { (key, info) ->
                    val (label, currentColor) = info
                    var isExpanded by remember { mutableStateOf(false) }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(theme.keyboardBackground)
                            .clickable {
                                triggerHaptic(haptic, viewModel.hapticFeedbackEnabled)
                                isExpanded = !isExpanded
                            }
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            color = if (theme.isDark) Color.LightGray else Color.DarkGray,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(currentColor)
                                    .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                            )
                            Icon(
                                imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Expand color row",
                                tint = theme.accent,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    AnimatedVisibility(visible = isExpanded) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            items(swatches) { color ->
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (color == currentColor) 2.dp else 1.dp,
                                            color = if (color == currentColor) theme.accent else Color.White.copy(alpha = 0.2f),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            triggerHaptic(haptic, viewModel.hapticFeedbackEnabled)
                                            // Force theme to "custom" if they perform editing
                                            viewModel.selectTheme(
                                                CalcTheme(
                                                    id = "custom",
                                                    name = "My Custom Design",
                                                    isDark = true,
                                                    background = viewModel.customBackground,
                                                    displayBackground = viewModel.customDisplayBackground,
                                                    keyboardBackground = viewModel.customKeyboardBackground,
                                                    numberKey = viewModel.customNumberKey,
                                                    numberText = viewModel.customNumberText,
                                                    operatorKey = viewModel.customOperatorKey,
                                                    operatorText = viewModel.customOperatorText,
                                                    trigFnKey = viewModel.customTrigFnKey,
                                                    trigFnText = viewModel.customTrigFnText,
                                                    equalsKey = viewModel.customEqualsKey,
                                                    equalsText = viewModel.customEqualsText,
                                                    accent = viewModel.customAccent,
                                                    gridLine = viewModel.customAccent.copy(alpha = 0.2f)
                                                )
                                            )
                                            viewModel.updateCustomColor(key, color)
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MiniThemeDot(color: Color) {
    Box(
        modifier = Modifier
            .size(14.dp)
            .clip(CircleShape)
            .background(color)
            .border(0.5.dp, Color.White.copy(alpha = 0.4f), CircleShape)
    )
}

private fun triggerHaptic(haptic: HapticFeedback, enabled: Boolean) {
    if (enabled) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }
}

@Composable
fun BentoHapticIndicator(enabled: Boolean, theme: CalcTheme) {
    AnimatedVisibility(
        visible = enabled,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(theme.keyboardBackground)
                    .border(
                        width = 1.dp,
                        color = theme.accent.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Haptic active",
                    tint = theme.accent,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "HAPTIC FEEDBACK ACTIVE",
                    color = if (theme.isDark) Color(0xFFCCC2DC) else Color.DarkGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
