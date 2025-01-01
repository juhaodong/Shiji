package view.page.homePage.supplierManagePage.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import domain.composable.basic.button.BaseIconButton
import domain.composable.basic.button.MainButton
import domain.composable.basic.cards.LabelText
import domain.composable.basic.cards.LabelValuePair
import domain.composable.basic.layout.BaseSurface
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.SmallSpacer
import domain.composable.basic.layout.pa
import domain.composable.basic.wrapper.PageLoadingProvider
import domain.inventory.model.OrderStatus
import domain.inventory.model.getOrderStatusColor
import domain.inventory.model.storageItem.getRealUrl
import domain.inventory.model.storageItem.imageWithProxy
import domain.purchaseOrder.PurchaseOrderVM
import domain.purchaseOrder.model.OrderChangeLog
import domain.purchaseOrder.model.OrderItem
import modules.utils.FormatUtils
import modules.utils.FormatUtils.toPriceDisplay
import modules.utils.beautify
import modules.utils.dateOnly
import modules.utils.timeToNow

private enum class TabOptions {
    Products, TimeLine, Details,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsPage(
    purchaseOrderVM: PurchaseOrderVM,
    toOrderSignPage: () -> Unit,
    back: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { back() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                title = {
                    Text(
                        "订单详情", style = MaterialTheme.typography.bodyLarge
                    )
                },
                actions = {
                    BaseIconButton(icon = Icons.Default.MoreVert) {

                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) {
            PageLoadingProvider(loading = purchaseOrderVM.orderDetailLoading, onRefresh = {
                purchaseOrderVM.chooseOrder()
            }, haveContent = purchaseOrderVM.orderDetail != null) {
                val orderDetail = purchaseOrderVM.orderDetail ?: return@PageLoadingProvider
                val order = orderDetail.purchaseOrder
                val statusColor = getOrderStatusColor(order.status)

                var selectedTabIndex by remember {
                    mutableStateOf(TabOptions.Products)
                }



                TabRow(
                    selectedTabIndex = TabOptions.entries.indexOf(selectedTabIndex),
                    containerColor = Color.Transparent
                ) {
                    TabOptions.entries.forEach {
                        Tab(selected = it == selectedTabIndex, onClick = {
                            selectedTabIndex = it
                        }, text = {
                            Text(it.name)
                        })
                    }
                }
                when (selectedTabIndex) {
                    TabOptions.Products -> LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(orderDetail.orderContents) {
                            OrderSignDisplay(it)
                        }
                        item {
                            Column {
                                SmallSpacer()
                                BaseVCenterRow {
                                    Text("预计订单总额(含税)")
                                    GrowSpacer()
                                    Text(order.totalPrice.toPriceDisplay())
                                }
                                SmallSpacer()
                                Text(
                                    "请注意，此处仅仅为根据供应商上次更新的价格提供的预计总额，Aaden POS不保证订单总额的准确性，也不对订单总额中出现的错误负责",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = LocalContentColor.current.copy(alpha = 0.6f)
                                )
                                SmallSpacer(16)

                                LabelValuePair("订单备注", order.note)
                            }
                        }
                    }


                    TabOptions.TimeLine -> {

                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(orderDetail.orderChangeLog) {
                                OrderChangeLogDisplay(it)
                            }
                        }

                    }

                    TabOptions.Details -> {
                        Column(
                            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                                .pa(), verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OrderListItem(order) {}
                            LabelValuePair("订单号", order.orderUUID)
                            Row {
                                LabelValuePair(
                                    "客户编号",
                                    orderDetail.orderBook.customerReference,
                                    modifier = Modifier.weight(1f)
                                )
                                LabelValuePair(
                                    "订购门店",
                                    orderDetail.shop.contactInfo.legalName,
                                    modifier = Modifier.weight(1f)
                                )

                            }
                            LabelValuePair("订单备注", order.note)
                            LabelValuePair(
                                "送货地址", orderDetail.shop.contactInfo.toNormalString()
                            )
                            if (order.completeTime != null) {
                                BaseSurface(color = MaterialTheme.colorScheme.surfaceContainerLow) {
                                    Column {
                                        BaseVCenterRow(modifier = Modifier.pa().fillMaxWidth()) {
                                            Icon(
                                                Icons.Default.CheckCircle,
                                                contentDescription = null,
                                            )
                                            SmallSpacer(16)
                                            Text(
                                                "已签收",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            GrowSpacer()
                                            if (order.signedBy.isNotBlank()) {
                                                SmallSpacer()
                                                Text(
                                                    "签收人:${order.signedBy}",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        }
                                        BaseSurface(color = MaterialTheme.colorScheme.surfaceContainer) {
                                            Column(modifier = Modifier.pa().fillMaxWidth()) {
                                                Row() {
                                                    AsyncImage(
                                                        model = order.signImageUrl,
                                                        contentDescription = null
                                                    )
                                                }
                                                SmallSpacer()
                                                LabelText("签收于:" + order.completeTime?.beautify())
                                            }
                                        }

                                    }
                                }
                            }
                            LabelValuePair(
                                "账单状态",
                                if (order.billUrl.isNullOrBlank()) "未就绪" else "可下载"
                            )

                        }
                    }
                }
                val currentMainAction = when (order.status) {
                    OrderStatus.Active -> ClientOrderAction.Sign
                    OrderStatus.Created -> ClientOrderAction.Cancel
                    OrderStatus.Confirmed -> ClientOrderAction.Sign
                    OrderStatus.Delivering -> ClientOrderAction.Sign
                    OrderStatus.Hold -> ClientOrderAction.None
                    OrderStatus.Completed -> ClientOrderAction.Archive
                    OrderStatus.Cancel -> ClientOrderAction.Reorder
                    OrderStatus.Rejected -> ClientOrderAction.Reorder
                    OrderStatus.Archive -> if (!order.billUrl.isNullOrBlank()) ClientOrderAction.DownloadBill else ClientOrderAction.None
                }
                when (currentMainAction) {
                    ClientOrderAction.Sign, ClientOrderAction.Archive, ClientOrderAction.Cancel -> {
                        BaseVCenterRow(modifier = Modifier.pa()) {
                            MainButton(
                                currentMainAction.name,
                                icon = getClientOrderActionIcon(currentMainAction)
                            ) {
                                when (currentMainAction) {
                                    ClientOrderAction.Cancel -> {
                                        purchaseOrderVM.cancelOrder(order.id)
                                    }

                                    ClientOrderAction.Sign -> {
                                        toOrderSignPage()
                                    }

                                    else -> {
                                        purchaseOrderVM.archiveOrder(order.id)
                                    }
                                }

                            }
                        }
                    }

                    ClientOrderAction.DownloadBill -> {
                        val handler = LocalUriHandler.current
                        BaseVCenterRow(modifier = Modifier.pa()) {
                            MainButton(
                                currentMainAction.name,
                                icon = getClientOrderActionIcon(currentMainAction)
                            ) {
                                handler.openUri(order.billUrl!!)
                            }
                        }
                    }

                    ClientOrderAction.Reorder -> {

                    }

                    ClientOrderAction.None -> {

                    }
                }


            }
        }
    }
}

fun getClientOrderActionIcon(action: ClientOrderAction): ImageVector? {
    return when (action) {
        ClientOrderAction.Cancel -> Icons.Filled.Cancel
        ClientOrderAction.Sign -> Icons.Filled.CheckCircle
        ClientOrderAction.DownloadBill -> Icons.Filled.Download
        ClientOrderAction.Reorder -> Icons.Filled.Refresh
        ClientOrderAction.None -> null // Or a default icon if needed
        ClientOrderAction.Archive -> Icons.Default.MoveToInbox
    }
}

enum class ClientOrderAction {
    Cancel,
    Sign,
    DownloadBill,
    None,
    Reorder,
    Archive,
}

@Composable
private fun OrderSignDisplay(model: OrderItem) {
    BaseSurface(color = MaterialTheme.colorScheme.surfaceContainer) {
        BaseVCenterRow(modifier = Modifier.pa().fillMaxWidth()) {
            Text(
                model.amount.toPlainString() + FormatUtils.times,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.width(40.dp),
                color = MaterialTheme.colorScheme.primary
            )
            if (model.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = model.imageUrl.imageWithProxy(),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp).clip(MaterialTheme.shapes.medium)
                )
                SmallSpacer(16)
            }
            Column() {
                Text(model.name, style = MaterialTheme.typography.bodyMedium)
                SmallSpacer(4)
                LabelText(
                    model.purchaseUnit + "/" + model.price.toPriceDisplay(), secondary = true
                )
            }
        }
    }
}

@Composable
private fun OrderChangeLogDisplay(model: OrderChangeLog) {
    Column {
        Row(modifier = Modifier.pa(8)) {
            LabelText(model.createTimestamp.timeToNow(), secondary = true)
        }

        BaseSurface(color = MaterialTheme.colorScheme.surfaceContainerLow) {
            Column(modifier = Modifier.pa().fillMaxWidth()) {
                BaseVCenterRow() {
                    Column {
                        BaseVCenterRow {
                            Text(
                                if (model.previousStatus != model.status) {
                                    "${model.previousStatus.name} -> ${model.status.name}"
                                } else {
                                    model.status.name
                                },
                                style = MaterialTheme.typography.bodyMedium
                            )
                            GrowSpacer()
                            Text(model.operator, style = MaterialTheme.typography.bodyMedium)
                        }
                        SmallSpacer(4)
                        LabelText("预计到达日期:" + model.estimateArriveDateTime.dateOnly())
                    }
                }
                if (model.imageUrl.isNotBlank()) {
                    SmallSpacer()
                    AsyncImage(
                        model = getRealUrl(model.imageUrl),
                        contentDescription = null,
                    )
                    SmallSpacer()
                }
                SmallSpacer(4)
                if (model.note.isNotBlank()) {
                    Text(
                        "备注:" + model.note,
                        style = MaterialTheme.typography.labelSmall,
                        color = LocalContentColor.current.copy(alpha = 0.6f)
                    )
                }
                if (model.records.isNotEmpty()) {
                    Column {
                        LabelText("订单内容变动:")
                        model.records.forEach {
                            Text(
                                it.name + ":" + it.oldAmount + "->" + it.newAmount,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

        }
    }

}