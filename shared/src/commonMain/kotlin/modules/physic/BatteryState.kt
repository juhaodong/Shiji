package modules.physic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.intl.Locale
import kotlinx.coroutines.delay

data class BatteryState(val percentage: Int, val charging: Boolean)

val defaultBatteryState = BatteryState(100, false)

expect fun getCurrentBatteryState(): BatteryState

@Composable
fun batteryState(): State<BatteryState> {
    return produceState(initialValue = defaultBatteryState, producer = {
        while (true) {
            value = getCurrentBatteryState()
            delay(3 * 1000)
        }
    })
}


expect fun changeLocale(locale: Locale)

@Composable
expect fun PlatformColors(darkMode: Boolean)

