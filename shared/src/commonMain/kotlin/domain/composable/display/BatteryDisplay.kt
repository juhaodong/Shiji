package domain.composable.display

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Battery0Bar
import androidx.compose.material.icons.filled.Battery1Bar
import androidx.compose.material.icons.filled.Battery2Bar
import androidx.compose.material.icons.filled.Battery3Bar
import androidx.compose.material.icons.filled.Battery4Bar
import androidx.compose.material.icons.filled.Battery5Bar
import androidx.compose.material.icons.filled.Battery6Bar
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import modules.physic.batteryState


@Composable
fun BatteryDisplay() {
    val batteryLevel by batteryState()

    val icon = if (batteryLevel.charging) {
        Icons.Default.BatteryChargingFull
    } else {
        when (batteryLevel.percentage) {
            in 0..15 -> Icons.Default.Battery0Bar
            in 16..24 -> Icons.Default.Battery1Bar
            in 25..35 -> Icons.Default.Battery2Bar
            in 35..50 -> Icons.Default.Battery3Bar
            in 50..74 -> Icons.Default.Battery4Bar
            in 75..89 -> Icons.Default.Battery5Bar
            in 90..99 -> Icons.Default.Battery6Bar
            else -> Icons.Default.BatteryFull
        }
    }

    Row(
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "",
            modifier = Modifier.rotate(90f)
        )
    }


}