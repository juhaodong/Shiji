@file:OptIn(ExperimentalMaterial3Api::class)

package view.page.homePage.supplierManagePage.orderBook

import LocalDialogManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import domain.composable.basic.button.MainButton
import domain.composable.basic.layout.BaseSurface
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.pa
import domain.composable.basic.wrapper.GrowLoadingProvider
import domain.supplier.OrderBookViewModel

@Composable
fun ProductImportPage(
    orderBookViewModel: OrderBookViewModel,
    chooseCategory: (Long?) -> Unit,
    back: () -> Unit,
    save: () -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current
    val dialogManager = LocalDialogManager.current
    Scaffold(topBar = {

        TopAppBar(
            title = {
                Text("浏览类别", style = MaterialTheme.typography.bodyLarge)
            },
            navigationIcon = {
                IconButton(onClick = {
                    dialogManager.confirmAnd(
                        title = "您确定要返回吗？",
                        content = "您现在所做的更改将被丢弃",
                        shouldConfirm = orderBookViewModel.productImportedMap.size > 0
                    ) {
                        orderBookViewModel.productImportedMap.clear()
                        back()
                    }

                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Localized description"
                    )
                }
            },
            actions = {
                IconButton(onClick = {
                    orderBookViewModel.startSearchSupplierProduct()
                    chooseCategory(null)
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Localized description"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
        )

    }) { paddingValues ->

        Column(modifier = Modifier.padding(paddingValues)) {
            GrowLoadingProvider(
                loading = orderBookViewModel.supplierProductCategoriesLoading,
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(orderBookViewModel.supplierProductCategories) {
                        BaseSurface(color = MaterialTheme.colorScheme.surfaceContainer, onClick = {
                            hapticFeedback.performHapticFeedback(hapticFeedbackType = HapticFeedbackType.LongPress)
                            chooseCategory(it.id)
                        }) {
                            BaseVCenterRow(modifier = Modifier.fillMaxWidth().pa()) {
                                Text(
                                    it.name, maxLines = 2, overflow = TextOverflow.Ellipsis,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
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