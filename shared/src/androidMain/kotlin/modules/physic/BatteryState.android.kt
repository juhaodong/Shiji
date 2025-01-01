package modules.physic

import android.content.Context
import android.os.BatteryManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.Locale
import androidx.core.os.LocaleListCompat
import com.materialkolor.ktx.hasEnoughContrast
import currentContext


actual fun getCurrentBatteryState(): BatteryState {
    val ctx = currentContext
    if (ctx != null) {
        with(ctx) {
            val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            val percentage = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            return BatteryState(percentage = percentage, charging = batteryManager.isCharging)
        }
    }
    return defaultBatteryState
}

actual fun changeLocale(locale: Locale) {
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(locale.toLanguageTag()))
}

@Composable
actual fun PlatformColors(darkMode: Boolean) {
    val context = LocalContext.current as ComponentActivity
    val backgroundColor = android.graphics.Color.TRANSPARENT
    val dark = darkMode
    LaunchedEffect(dark) {
        context.enableEdgeToEdge(
            statusBarStyle = if (dark) SystemBarStyle.dark(backgroundColor) else SystemBarStyle.light(
                backgroundColor, backgroundColor
            ),
            navigationBarStyle = if (dark) SystemBarStyle.dark(backgroundColor) else SystemBarStyle.light(
                backgroundColor, backgroundColor
            )
        )
    }

}