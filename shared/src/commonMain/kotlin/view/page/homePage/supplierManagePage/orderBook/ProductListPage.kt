@file:OptIn(ExperimentalMaterial3Api::class)

package view.page.homePage.supplierManagePage.orderBook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import domain.composable.basic.button.MainButton
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.pa
import domain.composable.basic.wrapper.GrowLoadingProvider
import domain.composable.filter.SearchTopBarProvider
import domain.supplier.OrderBookViewModel
import view.page.homePage.supplierManagePage.orderBook.composable.ProductImportDisplay

@Composable
fun ProductListPage(
    orderBookViewModel: OrderBookViewModel,
    toProductDetail: (Long) -> Unit,
    back: () -> Unit,
    save: () -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current
    Scaffold(topBar = {
        SearchTopBarProvider(
            searchText = orderBookViewModel.supplierProductSearchText,
            searching = orderBookViewModel.supplierProductSearching,
            onSearchTextChange = {
                orderBookViewModel.supplierProductSearchText = it
            }, dismiss = { orderBookViewModel.supplierProductSearching = false }) {
            TopAppBar(
                title = {
                    Text("产品列表", style = MaterialTheme.typography.bodyLarge)
                },
                navigationIcon = {
                    IconButton(onClick = { back() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { orderBookViewModel.startSearchSupplierProduct() }) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Localized description"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
            )
        }
    }) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            GrowLoadingProvider(
                loading = orderBookViewModel.supplierProductsLoading,
                haveContent = orderBookViewModel.filteredSupplierProduct().isNotEmpty()
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(orderBookViewModel.filteredSupplierProduct()) {
                        ProductImportDisplay(
                            imported = orderBookViewModel.productImportedMap[it.product.id]
                                ?: it.imported,
                            info = it.product,
                            onClick = { toProductDetail(it.product.id) }, toggle = {
                                hapticFeedback.performHapticFeedback(hapticFeedbackType = HapticFeedbackType.LongPress)
                                val newValue =
                                    !(orderBookViewModel.productImportedMap[it.product.id]
                                        ?: it.imported)
                                if (newValue == it.imported) {
                                    orderBookViewModel.productImportedMap.remove(it.product.id)
                                } else {
                                    orderBookViewModel.productImportedMap[it.product.id] = newValue
                                }


                            })
                    }
                }
            }
            if (orderBookViewModel.countDifferentValues() > 0) {
                BaseVCenterRow(modifier = Modifier.pa()) {
                    MainButton(text = "保存修改(${orderBookViewModel.countDifferentValues()})") {
                        save()
                    }
                }
            }
        }
    }
}