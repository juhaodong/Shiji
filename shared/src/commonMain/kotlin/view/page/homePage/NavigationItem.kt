package view.page.homePage

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.Equalizer
import androidx.compose.material.icons.rounded.Inventory
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavigationItem(val icon: ImageVector, val label: String) {
    DataCenter(Icons.Rounded.Equalizer, "营养概况"),
    DailyRecord(Icons.Rounded.Inventory, "食记"),
    Workbench(Icons.Rounded.Widgets, "我的")
}