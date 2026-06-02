package com.example

import androidx.compose.ui.graphics.Color

data class CalcTheme(
    val id: String,
    val name: String,
    val isDark: Boolean,
    val background: Color,
    val displayBackground: Color,
    val keyboardBackground: Color,
    val numberKey: Color,
    val numberText: Color,
    val operatorKey: Color,
    val operatorText: Color,
    val trigFnKey: Color,
    val trigFnText: Color,
    val equalsKey: Color,
    val equalsText: Color,
    val accent: Color, // graphing line, cursor, etc.
    val gridLine: Color
) {
    companion object {
        val BentoGrid = CalcTheme(
            id = "bento_grid",
            name = "Bento Grid",
            isDark = true,
            background = Color(0xFF1C1B1F),
            displayBackground = Color(0xFF2B2930),
            keyboardBackground = Color(0xFF332D41),
            numberKey = Color(0xFF49454F),
            numberText = Color(0xFFE6E1E5),
            operatorKey = Color(0xFF633B48),
            operatorText = Color(0xFFFFD8E4),
            trigFnKey = Color(0xFF31111D),
            trigFnText = Color(0xFFFFB2BE),
            equalsKey = Color(0xFFD0BCFF),
            equalsText = Color(0xFF381E72),
            accent = Color(0xFFD0BCFF),
            gridLine = Color(0x22D0BCFF)
        )

        val Cyberpunk = CalcTheme(
            id = "cyberpunk",
            name = "Cyberpunk Neon",
            isDark = true,
            background = Color(0xFF0D0B14),
            displayBackground = Color(0xFF161224),
            keyboardBackground = Color(0xFF1D1730),
            numberKey = Color(0xFF282042),
            numberText = Color(0xFFE2E0EC),
            operatorKey = Color(0xFF421C52),
            operatorText = Color(0xFFFF7DF3),
            trigFnKey = Color(0xFF1B314E),
            trigFnText = Color(0xFF00FFCC),
            equalsKey = Color(0xFFFF0055),
            equalsText = Color.White,
            accent = Color(0xFF00FFCC),
            gridLine = Color(0x3300FFCC)
        )

        val CosmicAqua = CalcTheme(
            id = "cosmic_aqua",
            name = "Cosmic Aqua",
            isDark = true,
            background = Color(0xFF051726),
            displayBackground = Color(0xFF072138),
            keyboardBackground = Color(0xFF0A2E4E),
            numberKey = Color(0xFF104270),
            numberText = Color(0xFFD3E6FA),
            operatorKey = Color(0xFF0075A2),
            operatorText = Color(0xFFE1F5FE),
            trigFnKey = Color(0xFF02C39A),
            trigFnText = Color(0xFF001F18),
            equalsKey = Color(0xFF00E5FF),
            equalsText = Color(0xFF030F1A),
            accent = Color(0xFF00E5FF),
            gridLine = Color(0x3300E5FF)
        )

        val SunsetGold = CalcTheme(
            id = "sunset_gold",
            name = "Sunset Gold",
            isDark = true,
            background = Color(0xFF230C16),
            displayBackground = Color(0xFF331422),
            keyboardBackground = Color(0xFF451C2F),
            numberKey = Color(0xFF5D2741),
            numberText = Color(0xFFFFDDE6),
            operatorKey = Color(0xFFE85D04),
            operatorText = Color(0xFFFFE6D6),
            trigFnKey = Color(0xFFFAA307),
            trigFnText = Color(0xFF230D00),
            equalsKey = Color(0xFFFFB703),
            equalsText = Color(0xFF211300),
            accent = Color(0xFFFFB703),
            gridLine = Color(0x33FFB703)
        )

        val ForestMint = CalcTheme(
            id = "forest_mint",
            name = "Forest Mint",
            isDark = true,
            background = Color(0xFF0F1E16),
            displayBackground = Color(0xFF152A1F),
            keyboardBackground = Color(0xFF1F3F2E),
            numberKey = Color(0xFF2E6047),
            numberText = Color(0xFFE3F7EE),
            operatorKey = Color(0xFF1A5A3D),
            operatorText = Color(0xFF9FFFC5),
            trigFnKey = Color(0xFF4CE0B3),
            trigFnText = Color(0xFF052117),
            equalsKey = Color(0xFF00E676),
            equalsText = Color(0xFF0F1E16),
            accent = Color(0xFF00E676),
            gridLine = Color(0x3300E676)
        )

        val LavenderDream = CalcTheme(
            id = "lavender",
            name = "Lavender Dream",
            isDark = true,
            background = Color(0xFF130E20),
            displayBackground = Color(0xFF1D1630),
            keyboardBackground = Color(0xFF292044),
            numberKey = Color(0xFF3B2E60),
            numberText = Color(0xFFECE6FF),
            operatorKey = Color(0xFF6B4EAD),
            operatorText = Color(0xFFF3EFFF),
            trigFnKey = Color(0xFFB19DFF),
            trigFnText = Color(0xFF180D3A),
            equalsKey = Color(0xFFFF85AE),
            equalsText = Color(0xFF290C16),
            accent = Color(0xFFB19DFF),
            gridLine = Color(0x22B19DFF)
        )

        val ElegantClassicLight = CalcTheme(
            id = "classic_light",
            name = "Elegant Classic",
            isDark = false,
            background = Color(0xFFF3F4F6),
            displayBackground = Color(0xFFFFFFFF),
            keyboardBackground = Color(0xFFE5E7EB),
            numberKey = Color(0xFFFFFFFF),
            numberText = Color(0xFF1F2937),
            operatorKey = Color(0xFFD1D5DB),
            operatorText = Color(0xFF111827),
            trigFnKey = Color(0xFF3B82F6),
            trigFnText = Color(0xFFFFFFFF),
            equalsKey = Color(0xFF10B981),
            equalsText = Color(0xFFFFFFFF),
            accent = Color(0xFF3B82F6),
            gridLine = Color(0x22111827)
        )

        val PresetThemes = listOf(
            BentoGrid,
            Cyberpunk,
            CosmicAqua,
            SunsetGold,
            ForestMint,
            LavenderDream,
            ElegantClassicLight
        )
    }
}
