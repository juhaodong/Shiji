package view.page.homePage

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.Equalizer
import androidx.compose.material.icons.rounded.Inventory
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavigationItem(val icon: ImageVector, val label: String) {
    DataCenter(Icons.Rounded.Equalizer, "数据中心"),
    Inventory(Icons.Rounded.Inventory, "库存状况"),
    Supplier(Icons.AutoMirrored.Rounded.Chat, "供应商"),
    Workbench(Icons.Rounded.Widgets, "工作台")
}