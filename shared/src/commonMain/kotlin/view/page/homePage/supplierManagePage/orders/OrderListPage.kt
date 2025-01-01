package view.page.homePage.supplierManagePage.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import domain.composable.basic.cards.LabelText
import domain.composable.basic.layout.BaseSurface
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.SmallSpacer
import domain.composable.basic.layout.pa
import domain.composable.basic.layout.px
import domain.composable.basic.layout.py
import domain.composable.basic.wrapper.PageLoadingProvider
import domain.inventory.model.OrderStatus
import domain.inventory.model.getOrderStatusColor
import domain.purchaseOrder.PurchaseOrderVM
import domain.purchaseOrder.model.PurchaseOrder
import modules.utils.FormatUtils.toPriceDisplay
import modules.utils.dateOnly

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListPage(
    purchaseOrderVM: PurchaseOrderVM,
    toOrderDetail: (Long) -> Unit,
    back: () -> Unit
) {
    Scaffold(topBar = {
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
                    "订单列表", style = MaterialTheme.typography.bodyLarge
                )
            },
            actions = {},
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) {
            ScrollableTabRow(
                OrderStatus.entries.indexOf(purchaseOrderVM.selectedStatus), edgePadding = 16.dp
            ) {
                OrderStatus.entries.forEach {
                    Tab(
                        selected = purchaseOrderVM.selectedStatus == it,
                        onClick = { purchaseOrderVM.selectedStatus = it },
                        text = {
                            Text(text = it.name)
                        },
                        selectedContentColor = getOrderStatusColor(it),
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            PageLoadingProvider(
                loading = purchaseOrderVM.orderListLoading,
                haveContent = purchaseOrderVM.orderList.isNotEmpty(),
                onRefresh = {
                    purchaseOrderVM.loadOrderList()
                },
                refreshKey = purchaseOrderVM.selectedStatus
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(purchaseOrderVM.orderList) {
                        OrderListItem(it) {
                            toOrderDetail(it.id)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderListItem(order: PurchaseOrder, onClick: () -> Unit) {
    val statusMainColor = getOrderStatusColor(order.status)

    BaseSurface(onClick = onClick, color = MaterialTheme.colorScheme.surfaceContainer) {
        Column() {
            BaseVCenterRow(
                modifier = Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainerLow).pa()
            ) {
                AsyncImage(
                    order.supplierImageUrl,
                    contentDescription = null,
                    modifier = Modifier.width(18.dp).aspectRatio(1f)
                        .clip(MaterialTheme.shapes.medium)
                )
                SmallSpacer()
                Text(
                    order.supplierName,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(modifier = Modifier.pa(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row {
                    Text(
                        order.status.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = statusMainColor,
                    )
                }

                Row() {
                    Column() {
                        LabelText("订购日期", secondary = true)
                        SmallSpacer(4)
                        Text(
                            order.createTimestamp.dateOnly(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    GrowSpacer()
                    Column(horizontalAlignment = Alignment.End) {
                        LabelText("预计送达日期", secondary = true)
                        SmallSpacer(4)
                        Text(
                            order.estimateArriveDateTime.dateOnly(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                BaseVCenterRow(modifier = Modifier.padding(top = 8.dp)) {
                    Surface(
                        color = statusMainColor,
                        shape = MaterialTheme.shapes.extraSmall,
                        contentColor = Color.White
                    ) {
                        Box(
                            modifier = Modifier.px(8).py(4)
                        ) {
                            LabelText(order.totalPrice.toPriceDisplay())
                        }

                    }
                    GrowSpacer()
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }


        }
    }
}