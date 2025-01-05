package view.page.homePage

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.Equalizer
import androidx.compose.material.icons.rounded.Inventory
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavigationItem(val icon: ImageVector, val label: String) {
    DataCenter(Icons.Rounded.Equalizer, "营养概况"),
    Inventory(Icons.Rounded.Inventory, "食记"),
    Supplier(Icons.AutoMirrored.Rounded.Chat, "健康建议"),
    Workbench(Icons.Rounded.Widgets, "我的")
}