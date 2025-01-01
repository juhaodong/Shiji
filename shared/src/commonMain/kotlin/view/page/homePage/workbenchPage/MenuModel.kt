package view.page.homePage.workbenchPage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

sealed class MenuModel(val name: String, val description: String? = null) {
    class ActionMenuItem(name: String, description: String? = null, val action: () -> Unit) :
        MenuModel(name, description)

    class SubMenuItem(name: String, description: String? = null, val subMenus: List<MenuModel>) :
        MenuModel(name, description)
}


@Composable
fun MenuList(
    menuItems: List<MenuModel>,
) {
    var currentMenu by remember { mutableStateOf(menuItems) }
    var parentMenu: List<MenuModel>? by remember { mutableStateOf(null) }
    LazyColumn {
        item {
            if (parentMenu != null) {
                ListItem(
                    headlineContent = { Text("返回") },
                    leadingContent = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) },
                    modifier = Modifier.clickable {
                        currentMenu = parentMenu ?: menuItems
                        parentMenu = null
                    }
                )
            }
        }
        items(currentMenu) { item ->
            MenuItem(item) { clickedItem ->
                if (clickedItem is MenuModel.SubMenuItem) {
                    parentMenu = currentMenu
                    currentMenu = clickedItem.subMenus
                } else if (clickedItem is MenuModel.ActionMenuItem) {
                    clickedItem.action.invoke()
                }
            }
        }
    }
}

@Composable
fun MenuItem(item: MenuModel, onMenuItemClick: (MenuModel) -> Unit) {
    when (item) {
        is MenuModel.ActionMenuItem -> {
            ListItem(
                headlineContent = { Text(item.name) },
                supportingContent = item.description?.let { { Text(it) } },
                trailingContent = {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null
                    )
                },
                modifier = Modifier.clickable { item.action.invoke() }
            )
        }

        is MenuModel.SubMenuItem -> {
            ListItem(
                headlineContent = { Text(item.name) },
                supportingContent = item.description?.let { { Text(it) } },
                trailingContent = { Icon(Icons.Filled.ExpandMore, contentDescription = null) },
                modifier = Modifier.clickable { onMenuItemClick(item) }
            )
        }
    }
}