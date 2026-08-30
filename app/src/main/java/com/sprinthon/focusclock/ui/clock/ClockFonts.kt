package com.sprinthon.focusclock.ui.clock

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.sprinthon.focusclock.R

val GoogleFontsProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

/**
 * Top tall, condensed fonts specifically chosen for high-legibility clock and focus displays.
 */
enum class ClockFont(
    val id: String,
    val displayName: String,
    val tagLine: String,
    val googleFontName: String,
    val defaultWeight: FontWeight = FontWeight.Normal,
    val letterSpacing: TextUnit = 0.sp
) {
    BEBAS_NEUE(
        id = "bebas_neue",
        displayName = "Bebas Neue",
        tagLine = "Ultra-tall condensed · Bold & modern",
        googleFontName = "Bebas Neue",
        defaultWeight = FontWeight.Normal,
        letterSpacing = 1.0.sp
    ),
    STAATLICHES(
        id = "staatliches",
        displayName = "Staatliches",
        tagLine = "Clean proportioned tall display",
        googleFontName = "Staatliches",
        defaultWeight = FontWeight.Normal,
        letterSpacing = 1.0.sp
    ),
    LEAGUE_GOTHIC(
        id = "league_gothic",
        displayName = "League Gothic",
        tagLine = "Classic American gothic · Bold impact",
        googleFontName = "League Gothic",
        defaultWeight = FontWeight.Normal,
        letterSpacing = 0.5.sp
    ),
    TEKO(
        id = "teko",
        displayName = "Teko",
        tagLine = "Tall rectangular condensed · Geometric",
        googleFontName = "Teko",
        defaultWeight = FontWeight.Medium,
        letterSpacing = 0.5.sp
    ),
    BIG_SHOULDERS(
        id = "big_shoulders",
        displayName = "Big Shoulders",
        tagLine = "Architectural tall display · Industrial",
        googleFontName = "Big Shoulders Display",
        defaultWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp
    ),
    ALLERTA_STENCIL(
        id = "allerta_stencil",
        displayName = "Allerta Stencil",
        tagLine = "Modern stencil cutout font",
        googleFontName = "Allerta Stencil",
        defaultWeight = FontWeight.Normal,
        letterSpacing = 0.5.sp
    ),
    AVERIA_SERIF_LIBRE(
        id = "averia_serif_libre",
        displayName = "Averia Serif Libre",
        tagLine = "Organic soft serif display",
        googleFontName = "Averia Serif Libre",
        defaultWeight = FontWeight.Normal,
        letterSpacing = 0.0.sp
    ),
    CALISTOGA(
        id = "calistoga",
        displayName = "Calistoga",
        tagLine = "Warm rounded slab serif",
        googleFontName = "Calistoga",
        defaultWeight = FontWeight.Normal,
        letterSpacing = 0.0.sp
    ),
    CARAMEL(
        id = "caramel",
        displayName = "Caramel",
        tagLine = "Flowing calligraphic script",
        googleFontName = "Caramel",
        defaultWeight = FontWeight.Normal,
        letterSpacing = 0.0.sp
    ),
    DOSIS(
        id = "dosis",
        displayName = "Dosis",
        tagLine = "Soft rounded geometric sans",
        googleFontName = "Dosis",
        defaultWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp
    ),
    EXPLETUS_SANS(
        id = "expletus_sans",
        displayName = "Expletus Sans",
        tagLine = "Architectural futuristic display",
        googleFontName = "Expletus Sans",
        defaultWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp
    ),
    KNEWAVE(
        id = "knewave",
        displayName = "Knewave",
        tagLine = "Bold painted display impact",
        googleFontName = "Knewave",
        defaultWeight = FontWeight.Normal,
        letterSpacing = 0.5.sp
    ),
    MANROPE(
        id = "manrope",
        displayName = "Manrope",
        tagLine = "Modern clean geometric sans",
        googleFontName = "Manrope",
        defaultWeight = FontWeight.SemiBold,
        letterSpacing = 0.0.sp
    ),
    MONTSERRAT(
        id = "montserrat",
        displayName = "Montserrat",
        tagLine = "Classic geometric display",
        googleFontName = "Montserrat",
        defaultWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp
    ),
    ORBITRON(
        id = "orbitron",
        displayName = "Orbitron",
        tagLine = "Sci-fi digital display",
        googleFontName = "Orbitron",
        defaultWeight = FontWeight.Bold,
        letterSpacing = 1.0.sp
    ),
    OXANIUM(
        id = "oxanium",
        displayName = "Oxanium",
        tagLine = "Cyberpunk square sans",
        googleFontName = "Oxanium",
        defaultWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp
    ),
    POLLER_ONE(
        id = "poller_one",
        displayName = "Poller One",
        tagLine = "High-contrast poster display",
        googleFontName = "Poller One",
        defaultWeight = FontWeight.Normal,
        letterSpacing = 0.5.sp
    ),
    RUBIK(
        id = "rubik",
        displayName = "Rubik",
        tagLine = "Rounded geometric sans",
        googleFontName = "Rubik",
        defaultWeight = FontWeight.Medium,
        letterSpacing = 0.0.sp
    ),
    RUSSO_ONE(
        id = "russo_one",
        displayName = "Russo One",
        tagLine = "Heavy bold impact display",
        googleFontName = "Russo One",
        defaultWeight = FontWeight.Normal,
        letterSpacing = 0.5.sp
    ),
    UNIFRAKTURCOOK(
        id = "unifrakturcook",
        displayName = "UnifrakturCook",
        tagLine = "Gothic Fraktur calligraphic",
        googleFontName = "UnifrakturCook",
        defaultWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp
    ),
    BARLOW_CONDENSED(
        id = "barlow_condensed",
        displayName = "Barlow Condensed",
        tagLine = "Sleek narrow grotesque · Minimalist",
        googleFontName = "Barlow Condensed",
        defaultWeight = FontWeight.Normal,
        letterSpacing = (-0.5).sp
    ),
    ANTONIO(
        id = "antonio",
        displayName = "Antonio",
        tagLine = "Futuristic tall display · Crisp lines",
        googleFontName = "Antonio",
        defaultWeight = FontWeight.Normal,
        letterSpacing = 0.0.sp
    ),
    DEFAULT_SANS(
        id = "default_sans",
        displayName = "Default Sans",
        tagLine = "Standard clean geometric sans-serif",
        googleFontName = "Roboto",
        defaultWeight = FontWeight.Light,
        letterSpacing = (-1.0).sp
    );

    val fontFamily: FontFamily
        get() {
            if (this == DEFAULT_SANS) {
                return FontFamily.SansSerif
            }
            return try {
                val gFont = GoogleFont(googleFontName)
                FontFamily(
                    Font(
                        googleFont = gFont,
                        fontProvider = GoogleFontsProvider,
                        weight = defaultWeight
                    )
                )
            } catch (e: Exception) {
                FontFamily.SansSerif
            }
        }
}
