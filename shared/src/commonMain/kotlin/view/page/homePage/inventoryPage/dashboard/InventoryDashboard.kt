package view.page.homePage.inventoryPage.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.materialkolor.ktx.harmonize
import domain.composable.basic.cards.BaseCardList
import domain.composable.basic.cards.BaseContentCard
import domain.composable.basic.cards.LabelText
import domain.composable.basic.cards.ShowAllButton
import domain.composable.basic.layout.BaseSurface
import domain.composable.basic.layout.SmallSpacer
import domain.composable.basic.layout.CenterValueRow
import domain.composable.basic.wrapper.ActiveWrapper
import domain.composable.chart.BarData
import domain.composable.chart.ChartDisplay
import domain.composable.chart.ChartType
import domain.inventory.InventoryViewModel
import domain.inventory.model.OrderStatus
import domain.inventory.model.StorageOperationType
import domain.inventory.model.getStorageOperationLabelAndIcon
import domain.user.IdentityVM
import modules.utils.FormatUtils.toPriceDisplay
import modules.utils.toPercentageDisplay
import view.page.homePage.dataCenterPage.storeDetail.dashboard.TwoItemsPerRowGrid
import view.page.homePage.dataCenterPage.storeList.StoreList


@Composable
fun InventoryDashboard(
    identityVM: IdentityVM,
    inventoryViewModel: InventoryViewModel,
    toStorageItemList: () -> Unit,
    toOrderList: (OrderStatus?) -> Unit,
    toStorageOperation: (StorageOperationType) -> Unit,
) {
    ActiveWrapper(
        inventoryViewModel.currentInventorySetting != null,
        loading = inventoryViewModel.activateLoading,
        haveSubscription = inventoryViewModel.subscriptionStatus,
        activate = {
            inventoryViewModel.saveSetting()
        },
        checkActive = { inventoryViewModel.refreshInventoryStatus() },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            StoreList(identityVM = identityVM)

            BaseCardList(
                loading = inventoryViewModel.dashboardLoading,
                refreshKey = identityVM.currentProfile,
                onRefresh = {
                    inventoryViewModel.loadDashboardData()
                }) {
                val data = inventoryViewModel.dashboardData
                if (data != null) {
                    BaseContentCard(
                        "库存状况",
                        "库存商品总值的实时变化状况",
                        icon = Icons.Default.Inventory,
                        noPadding = true
                    ) {

                        ChartDisplay(
                            modifier = Modifier.height(144.dp),
                            outData = data.last24HourList.map {
                                BarData(
                                    value = it.value.floatValue(
                                        false
                                    ), label = it.hour
                                )
                            },
                            sparkChart = true,
                            chartType = ChartType.Line,
                            xLabel = "时间"
                        )
                        SmallSpacer(8)
                        CenterValueRow(
                            values = listOf(
                                "库存总值" to
                                        data.currentValue.toPriceDisplay(),
                                "缺货商品" to data.notEnoughResourceCount.toString(),
                                "周转率" to data.rotationRate.toPercentageDisplay(),
                            )
                        )
                        ShowAllButton {
                            toStorageItemList()
                        }
                    }
                    BaseContentCard(
                        "我的订单",
                        "订单状况总览",
                        icon = Icons.Default.Dashboard,
                        noPadding = true
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            LabelText("已确认/运输中/已签收")
                            SmallSpacer()
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OrderStatusItem(
                                Icons.Filled.CheckCircle,
                                "已确认",
                                MaterialTheme.colorScheme.surfaceColorAtElevation(24.dp)
                                    .harmonize(Color.Magenta)
                            ) {
                                toOrderList(OrderStatus.Confirmed)
                            }
                            OrderStatusItem(
                                Icons.Filled.LocalShipping,
                                "已发货",
                                MaterialTheme.colorScheme.surfaceColorAtElevation(24.dp)
                                    .harmonize(Color.Blue)
                            ) {
                                toOrderList(OrderStatus.Delivering)
                            }
                            OrderStatusItem(
                                Icons.AutoMirrored.Filled.ReceiptLong,
                                "已签收",
                                MaterialTheme.colorScheme.surfaceColorAtElevation(24.dp)
                                    .harmonize(Color.Green)
                            ) {
                                toOrderList(OrderStatus.Completed)
                            }

                        }
                        SmallSpacer(16)
                        ShowAllButton(text = "订货", icon = Icons.Default.RestartAlt) {
                            toOrderList(null)
                        }
                    }
                    BaseContentCard(
                        "库存操作",
                        "快捷库存操作",
                        icon = Icons.Default.MoreVert,
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TwoItemsPerRowGrid(
                                StorageOperationType.entries, contentPadding = PaddingValues(0.dp)
                            ) {
                                BaseSurface(modifier = Modifier.fillMaxWidth(), onClick = {
                                    toStorageOperation(it)
                                }) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val (label, icon) = getStorageOperationLabelAndIcon(it)
                                        Icon(
                                            icon,
                                            contentDescription = label,
                                            modifier = Modifier.size(18.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        SmallSpacer()
                                        Text(
                                            label,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Black
                                        )
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.OrderStatusItem(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    BaseSurface(modifier = Modifier.weight(1f), color = color, onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

}