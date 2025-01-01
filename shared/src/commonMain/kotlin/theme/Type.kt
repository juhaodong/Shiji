@file:OptIn(ExperimentalResourceApi::class)

package theme


import aadenadmin.shared.generated.resources.Res
import aadenadmin.shared.generated.resources.Satoshi_Black
import aadenadmin.shared.generated.resources.Satoshi_Bold
import aadenadmin.shared.generated.resources.Satoshi_Light
import aadenadmin.shared.generated.resources.Satoshi_Medium
import aadenadmin.shared.generated.resources.Satoshi_Regular
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font


@Composable
fun getType(): Typography {
    val nippo = FontFamily(
        Font(
            resource = Res.font.Satoshi_Medium,
            weight = FontWeight.Normal,
        ),
        Font(
            resource = Res.font.Satoshi_Black,
            weight = FontWeight.Bold,
        ),
        Font(
            resource = Res.font.Satoshi_Black,
            weight = FontWeight.Black,
        ),
        Font(
            resource = Res.font.Satoshi_Light,
            weight = FontWeight.ExtraLight,
        ),
        Font(
            resource = Res.font.Satoshi_Regular,
            weight = FontWeight.Light,
        ),
        Font(
            resource = Res.font.Satoshi_Bold,
            weight = FontWeight.Medium,
        ),
    )

    return Typography(
        displayLarge = TextStyle(
            fontFamily = nippo,
            fontWeight = FontWeight.Light,//Light
            fontSize = 57.sp,
            lineHeight = 64.sp,
            letterSpacing = 0.sp
        ),
        displayMedium = TextStyle(
            fontFamily = nippo,
            fontWeight = FontWeight.Light,//Light
            fontSize = 45.sp,
            lineHeight = 52.sp,
            letterSpacing = 0.sp
        ),
        displaySmall = TextStyle(
            fontFamily = nippo,
            fontWeight = FontWeight.Normal,//Normal
            fontSize = 36.sp,
            lineHeight = 44.sp,
            letterSpacing = 0.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = nippo,
            fontWeight = FontWeight.Bold,//Bold
            fontSize = 32.sp,
            lineHeight = 40.sp,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = nippo,
            fontWeight = FontWeight.Bold,//Bold
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = nippo,
            fontWeight = FontWeight.Bold,//Bold
            fontSize = 24.sp,
            lineHeight = 32.sp,
            letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = nippo,
            fontWeight = FontWeight.Bold,//Bold
            fontSize = 22.sp,//22
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = nippo,
            fontWeight = FontWeight.Bold,//Bold
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily = nippo,
            fontWeight = FontWeight.Bold,//Bold
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = nippo,
            fontWeight = FontWeight.Normal,//Normal
            fontSize = 16.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.15.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = nippo,
            fontWeight = FontWeight.Medium,//Medium
            fontSize = 14.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = nippo,
            fontWeight = FontWeight.SemiBold,//Meduim
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp
        ),
        labelLarge = TextStyle(
            fontFamily = nippo,
            fontWeight = FontWeight.Normal,//Normal
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = nippo,
            fontWeight = FontWeight.Normal,//Normal
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = nippo,
            fontWeight = FontWeight.Medium,//Medium
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        )
    )
}
