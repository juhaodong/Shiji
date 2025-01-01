package view.page.homePage.inventoryPage.resource.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.composable.dialog.basic.DialogViewModel
import domain.composable.dialog.selection.SelectOption
import domain.composable.filter.OptionFilter
import domain.composable.filter.ToggleFilter
import domain.inventory.InventoryViewModel

@Composable
fun InventoryFilterSection(
    dialogViewModel: DialogViewModel,
    inventoryViewModel: InventoryViewModel
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var searchText by remember { mutableStateOf("") }
    var showLowStockOnly by remember { mutableStateOf(false) }
    var selectedSupplier by remember { mutableStateOf<String?>(null) }

    val suppliers = listOf("Supplier A", "Supplier B", "Supplier C")
    val categories = listOf("Category 1", "Category 2", "Category 3")
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            OptionFilter(
                dialogViewModel,
                title = "商品分类",
                options = categories.map { SelectOption(it, it) }) {
                selectedCategory = it
            }
        }
        item {
            OptionFilter(
                dialogViewModel,
                title = "供货商",
                options = suppliers.map { SelectOption(it, it) }) {
                selectedSupplier = it
            }
        }
        item {

        }
        item {
            ToggleFilter {
                showLowStockOnly = it
            }
        }
    }

}