package com.nexus.porsuk.ui.theme

import androidx.compose.ui.graphics.Color

// AQUA CONCEPT DESIGN TOKENS
val PrimaryTeal = Color(0xFF00A388)
val AquaNew = Color(0xFF22B8D9)
val TealSoft = Color(0xFFE1F5EF)
val AquaSoft = Color(0xFFDEF4F9)
val Violet = Color(0xFF7C6CF0)
val VioletSoft = Color(0xFFECE9FE)
val Gold = Color(0xFFE8A93B)
val GoldSoft = Color(0xFFFBF1DD)
val Coral = Color(0xFFFF7A59)
val BackgroundNew = Color(0xFFF3F6F5)
val CardNew = Color(0xFFFFFFFF)
val LineBorder = Color(0xFFE7EDEA)
val InkText = Color(0xFF12201B)
val SubText = Color(0xFF6B7C76)
val NegatifRed = Color(0xFFE15577)
val RedSoft = Color(0xFFFDEAF0)
val DemirCelik = Color(0xFF8AA6FF)
val WarningGold = Color(0xFFF9A825)
val EmeraldNew = Color(0xFF10B981)
val PozitifGreen = EmeraldNew
val RoseNew = Color(0xFFF43F5E)
val AmberNew = Color(0xFFF59E0B)
val AmberWarning = AmberNew

// COMPATIBILITY ALIASES (Map existing design vars to new system colors)
val Background = BackgroundNew
val Surface = CardNew
val SurfaceAlt = LineBorder
val BorderLine = LineBorder
val TextPrimary = InkText
val TextMuted = SubText
val Aqua = PrimaryTeal
val AquaDeep = PrimaryTeal
val AquaGradient = AquaNew
val AquaLight = AquaSoft
val PositiveGreen = PrimaryTeal
val NegativeRed = NegatifRed
val Orange = Color(0xFFF97316)
val OrangeLight = Color(0xFFF97316).copy(alpha = 0.08f)

// DARK MODE (Pro Koyu - preserved if referenced)
val BackgroundDark = Color(0xFF090D14)
val SurfaceDark = Color(0xFF111827)
val BorderDark = Color(0xFF1F2937)
val AquaDark = Color(0xFF2DD4BF)
val AquaBrightDark = Color(0xFF5EEAD4)
val PositiveDark = Color(0xFF34D399)
val NegativeDark = Color(0xFFFB7185)
