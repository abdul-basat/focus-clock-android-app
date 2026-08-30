package com.sprinthon.focusclock.domain.model

data class CuratedColor(
    val id: String,
    val name: String,
    val hexValue: Long,
    val category: String
)

object CuratedColors {
    val Neutrals = listOf(
        CuratedColor("amoled_black", "AMOLED Black", 0xFF000000, "Neutral"),
        CuratedColor("charcoal", "Charcoal", 0xFF1A1A1E, "Neutral"),
        CuratedColor("slate", "Slate", 0xFF242830, "Neutral"),
        CuratedColor("warm_gray", "Warm Gray", 0xFF2C2A29, "Neutral"),
        CuratedColor("soft_white", "Soft White", 0xFFF0F0F2, "Neutral")
    )

    val Calm = listOf(
        CuratedColor("deep_navy", "Deep Navy", 0xFF0D1B2A, "Calm"),
        CuratedColor("forest", "Forest Green", 0xFF0F251E, "Calm"),
        CuratedColor("deep_teal", "Deep Teal", 0xFF0B252C, "Calm"),
        CuratedColor("muted_plum", "Muted Plum", 0xFF241628, "Calm"),
        CuratedColor("earth_brown", "Earth Brown", 0xFF251D18, "Calm")
    )

    val Accents = listOf(
        CuratedColor("midnight_indigo", "Midnight Indigo", 0xFF161B33, "Accent"),
        CuratedColor("dark_cypress", "Dark Cypress", 0xFF13221B, "Accent"),
        CuratedColor("burnt_ember", "Burnt Ember", 0xFF2A150A, "Accent")
    )

    val All = Neutrals + Calm + Accents

    fun findByHex(hex: Long): CuratedColor? = All.find { it.hexValue == hex }

    fun parseHexColor(hexString: String): Long? {
        val cleanHex = hexString.trim().removePrefix("#")
        return try {
            when (cleanHex.length) {
                6 -> 0xFF000000L or cleanHex.toLong(16)
                8 -> cleanHex.toLong(16)
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
}
