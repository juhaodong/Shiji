package theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.ktx.darken
import com.materialkolor.ktx.harmonize


val colorsSets = listOf(
    Color(0xff26d220),//0xffe82828
    Color(0xffd8a884),//0xffd8a884 light warm orange
    Color(0xff4c0013),//0xff84ced8 hexadecimal

     Color(0xff7853b2),
    Color(0xfff4d690),//0xfffedc5emm,0xfffedc00
    Color(0xff84b2d8),//0xffdedc49 blue
    Color(0xff84cbd8),//0xff84cbd8
    Color(0xffd884c7),//0xffd884c7
    Color(0xff4b4d57),//0xff84b2d8 red
    Color(0xffc084d8),//0xffc084d8 hexadecimal
    Color(0xff7e6278),//0xff7e6278 old lavender
    Color(0xffff6600),//0xff84d8b8 pure orange
    Color(0xff11396e),//0xffd88484 green
)


@Composable
fun successColor(overrideColor: Color? = null): Color {
    return Color.Green.darken(2f).harmonize(overrideColor?:MaterialTheme.colorScheme.primary)
}