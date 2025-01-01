package view.page.homePage.supplierManagePage.orderBook.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import domain.composable.basic.button.BaseOutlinedIconButton
import domain.composable.basic.button.BaseTonalIconButton
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.SmallSpacer
import domain.composable.basic.layout.px
import domain.composable.basic.layout.py
import domain.composable.dialog.form.generateKeyboardType
import domain.supplier.OrderBookViewModel
import domain.supplier.model.OrderBookItemInfo
import modules.utils.FormatUtils.toPriceDisplay

@Composable
fun OrderBookProductDisplay(
    toProductDetail: (Long) -> Unit,
    info: OrderBookItemInfo,
    orderBookViewModel: OrderBookViewModel,
    hapticFeedback: HapticFeedback
) {
    Surface(onClick = {
        toProductDetail(info.product.id)
    }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                if (info.product.imageUrl.isNotBlank()) {
                    AsyncImage(
                        info.product.realImageUrl(),
                        contentDescription = null,
                        modifier = Modifier.size(84.dp)
                    )
                    SmallSpacer(16)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Row {
                        Text(
                            info.getRealName(),
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        SmallSpacer()
                        Text(
                            "~" + info.price.toPriceDisplay(),
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        info.product.description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 3,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            SmallSpacer()
            BaseVCenterRow {
                OutlinedCard(shape = MaterialTheme.shapes.extraSmall) {
                    BaseVCenterRow(modifier = Modifier.px(12).py(8)) {
                        BasicTextField(
                            modifier = Modifier.width(84.dp)
                                .align(alignment = Alignment.CenterVertically),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                textAlign = TextAlign.Center,
                                color = if (orderBookViewModel.hasItemInOrder(info.id)) MaterialTheme.colorScheme.primary else Color.Unspecified
                            ),
                            value = orderBookViewModel.orderDict[info.id]?.toString()
                                ?: "0",
                            keyboardOptions = generateKeyboardType(
                                isLastOne = true, keyboardType = KeyboardType.Number
                            ),
                            onValueChange = { change ->
                                orderBookViewModel.setCountInOrder(
                                    itemId = info.id, count = change
                                )
                            },
                        )
                        VerticalDivider(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                            Text(
                                info.product.purchaseUnitName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (orderBookViewModel.hasItemInOrder(info.id)) MaterialTheme.colorScheme.primary else Color.Unspecified

                            )

                        }
                    }
                }
                GrowSpacer()
                if (orderBookViewModel.orderDict[info.id] != null) {
                    BaseOutlinedIconButton(
                        icon = Icons.Default.Remove,
                        modifier = Modifier.size(32.dp)
                    ) {
                        orderBookViewModel.addItemToOrder(info.id, -1)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    SmallSpacer()
                }

                BaseTonalIconButton(icon = Icons.Default.Add, modifier = Modifier.size(32.dp)) {
                    orderBookViewModel.addItemToOrder(info.id, 1)
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            }
        }
    }
    HorizontalDivider()
}