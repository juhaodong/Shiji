@file:OptIn(ExperimentalMaterial3Api::class)

package view.page.homePage.supplierManagePage.orderBook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import domain.composable.basic.button.MainButton
import domain.composable.basic.cards.BaseCardHeader
import domain.composable.basic.cards.LabelText
import domain.composable.basic.layout.BaseSurface
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.SmallSpacer
import domain.composable.basic.layout.pa
import domain.composable.basic.layout.px
import domain.composable.basic.layout.py
import domain.composable.basic.wrapper.NoContentProvider
import domain.inventory.model.OrderStatus
import domain.inventory.model.getOrderStatusColor
import domain.purchaseOrder.PurchaseOrderService
import domain.purchaseOrder.PurchaseOrderVM
import domain.supplier.OrderBookViewModel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import modules.utils.FormatUtils.toPriceDisplay
import modules.utils.dateOnly
import nl.jacobras.humanreadable.HumanReadable
import view.page.homePage.supplierManagePage.orderBook.composable.OrderBookDisplay

@Composable
fun OrderBookDetailPage(
    orderBookViewModel: OrderBookViewModel,
    purchaseOrderVM: PurchaseOrderVM,
    back: () -> Unit,
    toOrderProductPage: () -> Unit,
    toOrderDetailPage: (Long) -> Unit,
    toOrderListPage: (OrderStatus) -> Unit,
    toOrderSignPage: (Long) -> Unit,
) {

    val selectedOrderBook = orderBookViewModel.selectedOrderBook ?: return
    Scaffold(
        topBar = {
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
                    IconButton(onClick = { orderBookViewModel.updateOrderBookDetail() }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Localized description"
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            )
        },
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp).verticalScroll(
                    rememberScrollState()
                ), verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SmallSpacer()
                Column {
                    BaseCardHeader(
                        "最近订单",
                        "此处仅显示活跃的订单",
                        icon = Icons.AutoMirrored.Filled.ReceiptLong,
                        noPadding = true
                    )
                    SmallSpacer(16)
                    NoContentProvider(orderBookViewModel.orderList.isNotEmpty()) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(orderBookViewModel.orderList) {
                                BaseSurface(color = MaterialTheme.colorScheme.surfaceContainerLow) {
                                    val statusColor = getOrderStatusColor(it.status)

                                    Column(modifier = Modifier.width(266.dp)) {
                                        BaseVCenterRow(modifier = Modifier.pa()) {
                                            Column() {
                                                Text(
                                                    "#" + it.getDisplayId(),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                SmallSpacer(4)
                                                LabelText(
                                                    HumanReadable.timeAgo(
                                                        it.createTimestamp.toInstant(
                                                            TimeZone.currentSystemDefault()
                                                        )
                                                    )
                                                )
                                            }
                                            GrowSpacer()
                                            BaseSurface(
                                                color = statusColor.copy(alpha = 0.1f),
                                            ) {
                                                Box(modifier = Modifier.padding(8.dp)) {
                                                    Text(
                                                        it.status.name,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = statusColor
                                                    )
                                                }
                                            }
                                        }
                                        BaseSurface(modifier = Modifier.fillMaxWidth(),
                                            color = MaterialTheme.colorScheme.surfaceContainer,
                                            onClick = {
                                                toOrderDetailPage(it.id)
                                            }) {
                                            Column(modifier = Modifier) {
                                                Column(modifier = Modifier.py(8)) {
                                                    LabelRow(
                                                        text = it.estimateArriveDateTime.dateOnly(),
                                                        label = "预计配送日期"
                                                    )
                                                    LabelRow(
                                                        text = it.totalPrice.toPriceDisplay(),
                                                        label = "订单总额"
                                                    )
                                                    LabelRow(
                                                        text = it.note.ifBlank { "-" },
                                                        label = "备注"
                                                    )
                                                }

                                                HorizontalDivider()
                                                BaseVCenterRow {
                                                    if (it.status == OrderStatus.Created) {
                                                        TextButton(
                                                            onClick = {
                                                                purchaseOrderVM.cancelOrder(it.id)
                                                            }) {
                                                            Text("取消订单")
                                                        }
                                                    }
                                                    if (it.status == OrderStatus.Completed) {
                                                        TextButton(
                                                            onClick = {
                                                                purchaseOrderVM.archiveOrder(it.id)
                                                            }) {
                                                            Text("归档")
                                                        }
                                                    }
                                                    TextButton(
                                                        enabled = listOf(
                                                            OrderStatus.Delivering,
                                                            OrderStatus.Confirmed,
                                                        ).contains(it.status), onClick = {
                                                            toOrderSignPage(it.id)
                                                        }) {
                                                        Text("签收")
                                                    }
                                                    val handler = LocalUriHandler.current
                                                    TextButton(
                                                        onClick = {
                                                            handler.openUri(it.billUrl!!)
                                                        },
                                                        enabled = it.billUrl != null
                                                    ) {
                                                        Text("下载账单")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }


                            }
                        }
                    }

                    TextButton(onClick = {
                        toOrderListPage(OrderStatus.Active)
                    }) {
                        Icon(Icons.Default.History, contentDescription = null)
                        SmallSpacer()
                        Text("查看全部订单")
                    }
                }

            }

            BaseVCenterRow(modifier = Modifier.pa()) {
                MainButton("开始订购", icon = Icons.AutoMirrored.Filled.ArrowForward) {
                    toOrderProductPage()
                }
            }
        }
    }

}


@Composable
private fun LabelRow(text: String, label: String) {
    BaseVCenterRow(modifier = Modifier.px(16).py(8)) {
        LabelText(label)
        GrowSpacer()
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
