@file:OptIn(ExperimentalMaterial3Api::class)

package view.page.homePage.inventoryPage.resource

import LocalDialogManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Outbond
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.materialkolor.ktx.darken
import domain.composable.basic.button.ActionLeftMainButton
import domain.composable.basic.button.BaseIconButton
import domain.composable.basic.button.DeleteIconButton
import domain.composable.basic.cards.BaseCardHeader
import domain.composable.basic.layout.BaseSurface
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.IconWithLabel
import domain.composable.basic.layout.SmallSpacer
import domain.composable.basic.layout.StartValueRow
import domain.composable.basic.layout.pa
import domain.composable.basic.wrapper.LoadingProvider
import domain.composable.basic.wrapper.NoContentProvider
import domain.composable.basic.wrapper.PageLoadingProvider
import domain.composable.chart.BarData
import domain.composable.chart.ChartDisplay
import domain.composable.chart.ChartType
import domain.composable.dialog.basic.BeautifulDialog
import domain.composable.dialog.basic.DialogViewModel
import domain.composable.dialog.selection.SelectOption
import domain.composable.filter.SearchTopBarProvider
import domain.inventory.InventoryViewModel
import domain.inventory.model.StorageOperationType
import domain.inventory.model.change.OperationType
import domain.inventory.model.change.OperatorType
import domain.inventory.model.storageItem.StorageItemModel
import domain.inventory.model.storageItem.getRatioColor
import modules.utils.FormatUtils.sumOfB
import modules.utils.FormatUtils.toPriceDisplay
import modules.utils.beautify
import modules.utils.toMinuteDisplay
import modules.utils.toPercentageDisplay
import view.page.homePage.dataCenterPage.storeDetail.dashboard.TwoItemsPerRowGrid
import view.page.homePage.inventoryPage.resource.list.OutboundStatistic
import view.page.homePage.inventoryPage.resource.list.ProductInventoryItem
import view.page.homePage.inventoryPage.resource.list.RecentChangesDisplay


@Composable
fun StorageItemListPage(
    dialogViewModel: DialogViewModel, inventoryViewModel: InventoryViewModel, back: () -> Unit
) {
    LaunchedEffect(true) {
        inventoryViewModel.loadResourceCategories()
    }
    val manager = LocalDialogManager.current
    fun onItemClick(model: StorageItemModel, longPress: Boolean) {
        if (!longPress && inventoryViewModel.storageOperationType == null) {
            inventoryViewModel.showResourceDetail(model.id)
        } else {
            dialogViewModel.runInScope {
                val action = if (inventoryViewModel.storageOperationType == null) {
                    dialogViewModel.showSelectDialog(
                        title = "请选择要进行的操作", StorageItemAction.entries.map {
                            SelectOption(it.label, it)
                        }, twoRow = true
                    )
                } else {
                    when (inventoryViewModel.storageOperationType) {
                        StorageOperationType.Enter -> StorageItemAction.StockIn
                        StorageOperationType.Out -> StorageItemAction.StockOut
                        StorageOperationType.Check -> StorageItemAction.Adjust
                        StorageOperationType.Loss -> StorageItemAction.Loss
                        null -> StorageItemAction.StockIn
                    }
                }

                when (action) {
                    StorageItemAction.Edit -> {
                        inventoryViewModel.editStorageItem(model)
                    }

                    StorageItemAction.Delete -> {
                        manager.confirmDelete(model.name) {
                            inventoryViewModel.deleteStorageItem(model.id)
                        }
                    }

                    StorageItemAction.StockIn -> {
                        inventoryViewModel.enter(model)
                    }

                    StorageItemAction.StockOut -> {
                        inventoryViewModel.exit(model, false)
                    }

                    StorageItemAction.Loss -> {
                        inventoryViewModel.exit(model, true)
                    }

                    StorageItemAction.Adjust -> {
                        inventoryViewModel.change(model)
                    }

                    StorageItemAction.Record -> {
                        inventoryViewModel.showRecentChangesDialog(model.id)
                    }
                }

            }
        }
    }
    Scaffold(topBar = {
        SearchTopBarProvider(searching = inventoryViewModel.searching,
            searchText = inventoryViewModel.searchText,
            onSearchTextChange = {
                inventoryViewModel.searchText = it
            },
            dismiss = {
                inventoryViewModel.searching = false
            },
            content = {
                TopAppBar(
                    title = {
                        Text("库存列表", style = MaterialTheme.typography.titleSmall)
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            back()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            inventoryViewModel.startSearch()
                        }) {
                            Icon(Icons.Default.Search, contentDescription = null)
                        }
                        IconButton(onClick = {
                            inventoryViewModel.showRecentChangesDialog(null)
                        }) {
                            Icon(Icons.Default.History, contentDescription = null)
                        }
                        IconButton(onClick = {
                            inventoryViewModel.editStorageItem()
                        }) {
                            Icon(Icons.Default.AddCircle, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                )
            })
    }) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (inventoryViewModel.resourceCategoryLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            if (!inventoryViewModel.searching) {
                Row(
                    modifier = Modifier.fillMaxWidth().background(
                        MaterialTheme.colorScheme.surfaceContainerLow
                    )
                ) {
                    ScrollableTabRow(
                        inventoryViewModel.activeCategoryIndex(),
                        divider = {},
                        edgePadding = 8.dp,
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ) {
                        Tab(selected = inventoryViewModel.selectedCategoryId == null,
                            onClick = { inventoryViewModel.changeSelectedCategoryId(null) },
                            text = {
                                Text("全部")
                            })
                        inventoryViewModel.resourceCategoryList.forEach { resourceCategoryModel ->
                            Tab(selected = resourceCategoryModel.id == inventoryViewModel.selectedCategoryId,
                                onClick = {
                                    inventoryViewModel.changeSelectedCategoryId(
                                        resourceCategoryModel.id
                                    )
                                },
                                text = { Text(resourceCategoryModel.name) })
                        }
                    }
                    BaseIconButton(
                        icon = Icons.Default.ExpandMore,
                    ) {
                        inventoryViewModel.categoryManageDialog = true
                    }

                }
                HorizontalDivider(modifier = Modifier)
            }

            PageLoadingProvider(inventoryViewModel.storageItemLoading, onRefresh = {
                inventoryViewModel.loadStorageItemList()
            }, haveContent = inventoryViewModel.filteredItemList().isNotEmpty()) {
                LazyVerticalGrid(
                    contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    columns = GridCells.Fixed(2)
                ) {
                    if (inventoryViewModel.selectedCategoryId == null && !inventoryViewModel.searching) {
                        val grouped = inventoryViewModel.filteredItemList()
                            .groupBy { it.storageItemCategory.icon + it.storageItemCategory.name }
                            .filterValues { it.isNotEmpty() }
                        grouped.forEach { (key, list) ->
                            item(key = key, span = { GridItemSpan(2) }) {
                                SmallSpacer(16)
                                Text(key, style = MaterialTheme.typography.titleMedium)
                                SmallSpacer()
                            }
                            items(list) { model ->
                                ProductInventoryItem(model) {
                                    onItemClick(model, it)
                                }
                            }

                        }

                    } else {
                        items(inventoryViewModel.filteredItemList()) { model ->
                            ProductInventoryItem(model) {
                                onItemClick(model, it)
                            }
                        }
                    }
                }
            }
            BeautifulDialog(inventoryViewModel.recentChangesDialog, {
                inventoryViewModel.recentChangesDialog = false
            }) {
                BaseCardHeader(
                    inventoryViewModel.recentChangeTitle + "最近的变动",
                    "最近两个销售周期内的变动",
                    icon = Icons.Default.Category,
                    noPadding = true
                )
                SmallSpacer()
                LoadingProvider(inventoryViewModel.recentChangesLoading) {
                    RecentChangesDisplay(list = inventoryViewModel.recentChanges)
                }

            }
            BeautifulDialog(show = inventoryViewModel.showResourceDetailDialog, onDismissRequest = {
                inventoryViewModel.showResourceDetailDialog = false
            }, noPadding = true) {
                val detail = inventoryViewModel.resourceDetail
                var selectedTab by remember { mutableStateOf(ResourceDetailTab.BasicInformation) }

                if (detail != null) {
                    val storageItem = detail.storageItem
                    BaseCardHeader(
                        storageItem.name, "最近一个周期的详细数据", icon = Icons.Default.Link,
                    )
                    Column(
                        modifier = Modifier.pa().weight(1f, false),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        when (selectedTab) {
                            ResourceDetailTab.BasicInformation -> {
                                BaseSurface(color = MaterialTheme.colorScheme.surfaceContainerLowest) {
                                    Column(modifier = Modifier.pa()) {
                                        BaseVCenterRow {
                                            Text(
                                                "存量水平",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            GrowSpacer()
                                            Text(
                                                storageItem.getShortageLabel() + "(可用~" + storageItem.safeDays()
                                                    .toPlainString() + "天)",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = storageItem.getShortageColor().darken(2f)
                                            )
                                        }
                                        SmallSpacer()
                                        Row(
                                            modifier = Modifier.height(12.dp).fillMaxWidth()

                                                .clip(
                                                    MaterialTheme.shapes.medium
                                                ).clipToBounds()
                                                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                                        ) {
                                            Surface(color = storageItem.getShortageColor()) {
                                                Box(
                                                    modifier = Modifier.fillMaxHeight()
                                                        .fillMaxWidth(storageItem.consumeRatio())
                                                )
                                            }

                                        }
                                        SmallSpacer()
                                        Row {
                                            IconWithLabel(
                                                Icons.Filled.Inventory, storageItem.unitDisplay
                                            ) // Replace with Inventory icon
                                            SmallSpacer()
                                            IconWithLabel(
                                                Icons.Filled.Outbond,
                                                storageItem.periodOutUnitDisplay + "/" + storageItem.inventoryPeriodDays + "天"
                                            ) // Replace with

                                        }
                                    }
                                }

                                BaseSurface(color = MaterialTheme.colorScheme.surfaceContainerLowest) {
                                    Column(modifier = Modifier.pa()) {
                                        val inbound =
                                            detail.recentRecords.filter { it.operationType == OperationType.Enter }
                                        BaseVCenterRow {
                                            Text(
                                                "入库总计",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            GrowSpacer()
                                            Text(
                                                storageItem.unitDisplay(inbound.sumOfB { it.amount }),
                                                style = MaterialTheme.typography.bodyMedium,
                                            )
                                        }
                                        SmallSpacer()
                                        if (inbound.isNotEmpty()) {
                                            ChartDisplay(chartType = ChartType.Line,
                                                modifier = Modifier.fillMaxWidth().height(120.dp),
                                                sparkChart = true,
                                                outData = inbound.map {
                                                    BarData(
                                                        (it.priceLiteral * it.storageItem.maxLevelFactor).floatValue(
                                                            false
                                                        ), it.createTimestamp.beautify()
                                                    )
                                                })
                                            Text(
                                                "入库价格变动",
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }
                                    }
                                }

                                BaseSurface(color = MaterialTheme.colorScheme.surfaceContainerLowest) {
                                    Column(modifier = Modifier.pa()) {
                                        BaseVCenterRow {
                                            Text(
                                                "出库总计",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            GrowSpacer()
                                            Text(
                                                detail.totalOutUnitDisplay,
                                                style = MaterialTheme.typography.bodyMedium,
                                            )
                                        }

                                        SmallSpacer(16)
                                        val rotation = try {
                                            storageItem.periodOutAmount.divide(
                                                storageItem.currentCount * 2 + detail.totalOutAmount,
                                                decimalMode = DecimalMode.US_CURRENCY
                                            )

                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            BigDecimal.ZERO
                                        }
                                        val outboundStatistics = listOf(
                                            OutboundStatistic(
                                                "销量",
                                                storageItem.periodOutUnitDisplay,
                                                storageItem.periodOutAmount
                                            ),
                                            OutboundStatistic(
                                                "损耗",
                                                detail.lossUnitDisplay,
                                                detail.lossAmount
                                            ),
                                            OutboundStatistic(
                                                "其他",
                                                detail.otherUnitDisplay,
                                                detail.otherAmount
                                            ),
                                            OutboundStatistic(
                                                "周转率",
                                                rotation.toPercentageDisplay(),
                                                rotation
                                            ) // Assuming rotation is BigDecimal
                                        )
                                        TwoItemsPerRowGrid(
                                            contentPadding = PaddingValues(0.dp),
                                            items = outboundStatistics
                                        ) {
                                            BaseSurface(color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                                                Box() {
                                                    val ratio = if (it.label != "周转率") {
                                                        try {
                                                            it.amount.divide(
                                                                detail.totalOutAmount,
                                                                DecimalMode.US_CURRENCY
                                                            ).floatValue(false).coerceIn(0f, 100f)
                                                        } catch (e: Exception) {
                                                            BigDecimal.ZERO.floatValue(false)
                                                        }
                                                    } else {
                                                        it.amount.floatValue(false)
                                                            .coerceIn(0f, 100f)
                                                    }
                                                    Box(modifier = Modifier.matchParentSize()) {
                                                        Box(
                                                            modifier = Modifier.fillMaxHeight()
                                                                .fillMaxWidth(ratio)
                                                                .background(ratio.getRatioColor())
                                                        )
                                                    }
                                                    Row(
                                                        modifier = Modifier.pa().fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        Text(
                                                            it.label,
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                        GrowSpacer()
                                                        Text(
                                                            it.value,
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                    }
                                                }

                                            }
                                        }

                                    }
                                }

                            }

                            ResourceDetailTab.InventoryBatches -> {
                                LazyColumn() {
                                    items(detail.batches) {
                                        BaseSurface {
                                            BaseVCenterRow(modifier = Modifier.pa()) {
                                                Text("单价：" + (it.unitPrice * storageItem.maxLevelFactor).toPriceDisplay())
                                                GrowSpacer()
                                                Text(it.unitDisplay)
                                            }
                                        }
                                    }
                                }
                            }

                            ResourceDetailTab.RecentChanges -> {

                                RecentChangesDisplay(list = detail.recentRecords)

                            }
                        }
                    }




                    PrimaryTabRow(
                        selectedTabIndex = selectedTab.ordinal,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ) { // Get ordinal for PrimaryTabRow
                        ResourceDetailTab.entries.forEach { tab ->
                            Tab(selected = selectedTab == tab,
                                onClick = { selectedTab = tab },
                                text = {
                                    Text(
                                        when (tab) {
                                            ResourceDetailTab.BasicInformation -> "基本信息"
                                            ResourceDetailTab.InventoryBatches -> "库存批次"
                                            ResourceDetailTab.RecentChanges -> "最近变动"
                                        }
                                    )
                                })
                        }
                    }
                }

            }
            BeautifulDialog(show = inventoryViewModel.categoryManageDialog,
                onDismissRequest = { inventoryViewModel.categoryManageDialog = false }) {
                BaseCardHeader(
                    "分类管理", "添加或修改分类", icon = Icons.Default.Category, noPadding = true
                )
                SmallSpacer()
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(inventoryViewModel.resourceCategoryList) {
                        BaseSurface(onClick = {
                            inventoryViewModel.changeSelectedCategoryId(it.id)
                            inventoryViewModel.categoryManageDialog = false
                        }) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Text(it.name)
                                GrowSpacer()
                                BaseIconButton(icon = Icons.Default.Edit) {
                                    dialogViewModel.runInScope {
                                        val newName =
                                            dialogViewModel.showInput("修改分类名称", it.name)
                                        inventoryViewModel.saveResourceCategory(newName, it.id)
                                    }
                                }
                                DeleteIconButton(it.name) {
                                    inventoryViewModel.deleteResourceCategory(it.id!!)
                                }
                            }
                        }

                    }
                }
                SmallSpacer(16)
                ActionLeftMainButton("新建") {
                    dialogViewModel.runInScope {
                        val name = dialogViewModel.showInput("请输入分类名称")
                        inventoryViewModel.saveResourceCategory(name)
                    }
                }
            }
        }
    }
}

enum class StorageItemAction(val label: String) {


    StockIn("⬆️ 入库"), StockOut("⬇️ 出库"), Loss("💔 报损"), Adjust("🔄 校正"), Record("📑 记录"), Edit(
        "📝 编辑"
    ),
    Delete("🗑️ 删除"),
}

// Define the enum for tab states
enum class ResourceDetailTab {
    BasicInformation, // Added Basic Information tab
    InventoryBatches, RecentChanges
}