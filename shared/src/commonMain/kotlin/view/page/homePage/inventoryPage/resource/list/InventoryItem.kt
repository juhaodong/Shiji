package view.page.homePage.inventoryPage.resource.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import domain.composable.basic.layout.BaseSurface
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.LongPressBaseSurface
import domain.composable.basic.layout.SmallSpacer
import domain.composable.basic.layout.px
import domain.composable.basic.layout.py
import domain.composable.basic.wrapper.NoContentProvider
import domain.inventory.model.change.InventoryChangeLogModel
import domain.inventory.model.change.OperationType
import domain.inventory.model.change.OperatorType
import domain.inventory.model.storageItem.StorageItemModel
import modules.utils.toHourDisplay
import modules.utils.toMinuteDisplay


@Composable
fun ProductInventoryItem(product: StorageItemModel, onClick: (long: Boolean) -> Unit) {
    val isOutOfStock = product.currentCount < product.periodOutAmount
    val isEmpty = product.consumeRatio() < 0.1f && product.periodOutAmount > 0
    val haptics = LocalHapticFeedback.current
    LongPressBaseSurface(
        onClick = { onClick(false) },
        onLongPress = {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick(true)
        },
        modifier = Modifier.height(72.dp),
        color = if (isEmpty) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Box {
            Box(
                modifier = Modifier.align(Alignment.TopStart).fillMaxHeight()
                    .fillMaxWidth(product.consumeRatio())
                    .background(product.getShortageColor())
            ) {

            }
            BaseVCenterRow(modifier = Modifier.fillMaxSize().px(16).py(12)) {
                if (product.imageUrl.isNotBlank()) {
                    AsyncImage(
                        product.realImageUrl(),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp).clip(MaterialTheme.shapes.medium),
                    )
                    SmallSpacer()
                }
                Column(
                    modifier = Modifier.weight(1f),
                ) {

                    Row {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    GrowSpacer()
                    Text(
                        text = product.unitDisplay,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isOutOfStock) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,

                        fontWeight = FontWeight.Black
                    )

                }
            }
        }


    }
}

@Composable
fun RecentChangesItem(it: InventoryChangeLogModel) {
    BaseSurface(color = MaterialTheme.colorScheme.surfaceContainerHigh) {
        Column(
            modifier = Modifier.padding(
                horizontal = 12.dp, vertical = 8.dp
            ).fillMaxWidth().height(56.dp),
        ) {
            val color = when (it.operationType) {
                OperationType.Enter -> MaterialTheme.colorScheme.primary // Example color for StockIn
                OperationType.Out -> MaterialTheme.colorScheme.error // Example color for StockOut
            }
            Text(
                it.storageItem.name,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2
            )
            GrowSpacer()
            Row {
                Text(
                    it.unitDisplay,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Black,
                    color = color
                )
                if (it.operatorType == OperatorType.Loss) {
                    SmallSpacer()
                    Icon(
                        Icons.Rounded.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(end = 4.dp)
                            .size(14.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecentChangesDisplay(list: List<InventoryChangeLogModel>) {
    val grouped = list.groupBy { it.createTimestamp.toHourDisplay() }.entries.toList()
    NoContentProvider(list.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(108.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            grouped.forEach {
                item(it.key, span = {
                    GridItemSpan(maxLineSpan)
                }) {
                    Text(
                        "${it.key}:00", style = MaterialTheme.typography.titleMedium
                    )
                }
                val models = list.groupBy { it.storageItem.id }.map {
                    it.value.reduce { acc, inventoryChangeLogModel ->
                        acc.amount += inventoryChangeLogModel.amount
                        acc.unitDisplay = acc.storageItem.unitDisplay(acc.amount)
                        acc
                    }
                }
                items(models) {
                    RecentChangesItem(it)
                }
                item(span = {
                    GridItemSpan(currentLineSpan = maxCurrentLineSpan)
                }) {}
            }

        }
    }

}

class OutboundStatistic(
    val label: String,
    val value: String,
    val amount: BigDecimal
)