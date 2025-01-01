@file:OptIn(ExperimentalMaterial3Api::class)

package view.page.homePage.supplierManagePage.orderBook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import domain.composable.basic.button.ActionLeftMainButton
import domain.composable.basic.button.BaseIconButton
import domain.composable.basic.button.DeleteIconButton
import domain.composable.basic.button.MainButton
import domain.composable.basic.cards.BaseCardHeader
import domain.composable.basic.layout.BaseSurface
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.SmallSpacer
import domain.composable.basic.layout.pa
import domain.composable.basic.wrapper.GrowLoadingProvider
import domain.composable.dialog.basic.BeautifulDialog
import domain.composable.dialog.basic.DialogViewModel
import domain.composable.filter.SearchTopBarProvider
import domain.supplier.OrderBookViewModel
import kotlinx.coroutines.launch
import view.page.homePage.supplierManagePage.orderBook.composable.OrderBookDisplay
import view.page.homePage.supplierManagePage.orderBook.composable.OrderBookProductDisplay

@Composable
fun OrderMenuPage(
    orderBookViewModel: OrderBookViewModel,
    dialogViewModel: DialogViewModel,
    back: () -> Unit,
    toProductDetail: (productId: Long) -> Unit,
    toProductImport: () -> Unit,
    toOrderConfirm: () -> Unit
) {
    val selectedOrderBook = orderBookViewModel.selectedOrderBook ?: return
    val hapticFeedback = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            SearchTopBarProvider(
                searchText = orderBookViewModel.orderBookProductSearchText,
                searching = orderBookViewModel.orderBookProductSearching,
                onSearchTextChange = {
                    orderBookViewModel.orderBookProductSearchText = it
                }, dismiss = { orderBookViewModel.orderBookProductSearching = false }) {
                TopAppBar(
                    title = {
                        OrderBookDisplay(selectedOrderBook)
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
                        IconButton(onClick = { orderBookViewModel.startSearchProduct() }) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = "Localized description"
                            )
                        }
                        var expanded by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                            IconButton(onClick = { expanded = true }) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = "Localized description"
                                )
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }) {
                                DropdownMenuItem(
                                    text = { Text("浏览供应商的其他产品") },
                                    onClick = {
                                        expanded = false
                                        toProductImport()
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.AutoMirrored.Outlined.MenuBook,
                                            contentDescription = null
                                        )
                                    })
                                DropdownMenuItem(text = { Text("编辑订购指南分类") },
                                    onClick = {
                                        expanded = false

                                        showDialog = true
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Outlined.EditNote, contentDescription = null
                                        )
                                    })
                                DropdownMenuItem(text = { Text("创建新的分类") },
                                    onClick = {
                                        expanded = false
                                        dialogViewModel.runInScope {
                                            val newName =
                                                dialogViewModel.showInput("请输入分类名称")
                                            orderBookViewModel.saveBookCategory(newName)
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Outlined.CreateNewFolder,
                                            contentDescription = null
                                        )
                                    })
                            }
                        }

                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
                )
            }
        },
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            val listState = rememberLazyListState()
            if (!orderBookViewModel.orderBookProductSearching) {
                val tabState = rememberLazyListState()
                LaunchedEffect(listState.firstVisibleItemIndex) {
                    val activeId =
                        orderBookViewModel.categoryIdIndexMap[listState.firstVisibleItemIndex - 1]
                    if (activeId != null) {
                        orderBookViewModel.activeCategoryId = activeId
                        val index =
                            orderBookViewModel.bookCategoryList.indexOfFirst { it.id == activeId }
                        if (index != -1) {
                            tabState.scrollToItem(index)
                        }
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shadowElevation = 12.dp
                ) {
                    LazyRow(
                        state = tabState,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(orderBookViewModel.bookCategoryList) { category ->
                            FilterChip(selected = orderBookViewModel.activeCategoryId == category.id,
                                onClick = {
                                    orderBookViewModel.categoryIdIndexMap.entries.find { it.value == category.id }?.key?.let {
                                        scope.launch {
                                            listState.scrollToItem(
                                                it + 1
                                            )
                                        }
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                border = null,
                                label = { Text(category.getRealName()) })
                        }
                    }
                }

            }
            GrowLoadingProvider(
                loading = orderBookViewModel.bookProductListLoading,
                haveContent = orderBookViewModel.filteredProduct().isNotEmpty()
            ) {
                LazyColumn(modifier = Modifier.weight(1f), state = listState) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceContainerLow
                        ) {
                            BaseVCenterRow(modifier = Modifier.pa()) {
                                Text(
                                    "*请注意，供应商可能会随时调整价格，这里的报价仅供参考。订单会经过供应商的二次确认，请以供应商的最终报价为准。",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                        HorizontalDivider()
                    }
                    items(orderBookViewModel.filteredProduct()) { info ->
                        OrderBookProductDisplay(
                            toProductDetail = toProductDetail,
                            info = info,
                            orderBookViewModel = orderBookViewModel,
                            hapticFeedback = hapticFeedback
                        )
                    }
                }
            }

            if (orderBookViewModel.totalCount() > 0) {
                BaseVCenterRow(modifier = Modifier.pa()) {
                    MainButton("查看订单", icon = Icons.AutoMirrored.Filled.ArrowForward) {
                        toOrderConfirm()
                    }
                }
            }
            BeautifulDialog(show = showDialog,
                onDismissRequest = { showDialog = false }) {
                BaseCardHeader(
                    "分类管理", "添加或修改分类", icon = Icons.Default.Category, noPadding = true
                )
                SmallSpacer()
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(orderBookViewModel.bookCategoryList) { category ->
                        BaseSurface(onClick = {
                            orderBookViewModel.categoryIdIndexMap.entries.find { it.value == category.id }?.key?.let {
                                scope.launch {
                                    listState.scrollToItem(
                                        it + 1
                                    )
                                }
                            }
                            showDialog = false
                        }) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Text(category.getRealName())
                                GrowSpacer()
                                BaseIconButton(icon = Icons.Default.Edit) {
                                    dialogViewModel.runInScope {
                                        val newName =
                                            dialogViewModel.showInput(
                                                "修改分类名称",
                                                category.getRealName()
                                            )
                                        orderBookViewModel.saveBookCategory(
                                            newName,
                                            category.id,
                                            category.name
                                        )
                                    }
                                }
                                DeleteIconButton(category.name) {
                                    orderBookViewModel.deleteBookCategory(category.id!!)
                                }
                            }
                        }

                    }
                }
                SmallSpacer(16)
                ActionLeftMainButton("新建") {
                    dialogViewModel.runInScope {
                        val name = dialogViewModel.showInput("请输入分类名称")
                        orderBookViewModel.saveBookCategory(name)
                    }
                }
            }

        }
    }
}

