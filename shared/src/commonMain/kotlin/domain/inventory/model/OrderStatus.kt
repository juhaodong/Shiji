package domain.inventory.model

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.ktx.harmonize
import theme.successColor

enum class OrderStatus {
    Active,
    Created,
    Confirmed,
    Delivering,
    Hold,
    Completed,
    Cancel,
    Rejected,
    Archive,

}

@Composable
fun getOrderStatusColor(status: OrderStatus): Color {
    return when (status) {
        OrderStatus.Active -> MaterialTheme.colorScheme.primary // Teal Green
        OrderStatus.Created -> MaterialTheme.colorScheme.secondary // Blue
        OrderStatus.Confirmed -> MaterialTheme.colorScheme.primary // Amber
        OrderStatus.Delivering -> Color(0xFFFF9800) // Orange
        OrderStatus.Hold -> Color(0xFF9E9E9E) // Gray
        OrderStatus.Completed -> successColor() // Teal Green (same as Active)
        OrderStatus.Cancel -> MaterialTheme.colorScheme.error // Red
        OrderStatus.Rejected -> MaterialTheme.colorScheme.error // Red (same as Cancel)
        OrderStatus.Archive -> Color(0xFF607D8B) // Blue Gray
    }.harmonize(MaterialTheme.colorScheme.primary)
}