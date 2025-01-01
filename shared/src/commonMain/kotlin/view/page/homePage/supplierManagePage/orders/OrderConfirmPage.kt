@file:OptIn(ExperimentalMaterial3Api::class)

package view.page.homePage.supplierManagePage.orders

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.raedghazal.kotlinx_datetime_ext.now
import domain.composable.basic.button.MainButton
import domain.composable.basic.cards.LabelText
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.SmallSpacer
import domain.composable.basic.layout.pa
import domain.supplier.OrderBookViewModel
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import modules.utils.FormatUtils
import modules.utils.FormatUtils.toPriceDisplay
import modules.utils.dateOnly

@Composable
fun OrderConfirmPage(
    orderBookViewModel: OrderBookViewModel,
    toOrderSuccess: () -> Unit,
    back: () -> Unit,
) {
    val selectedOrderBook = orderBookViewModel.selectedOrderBook ?: return
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("订单摘要", style = MaterialTheme.typography.bodyLarge)
                },
                navigationIcon = {
                    IconButton(onClick = { back() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                modifier = Modifier.weight(1f).padding(16.dp).verticalScroll(rememberScrollState())
            ) {
                TextField(
                    value = selectedOrderBook.supplier.name,
                    onValueChange = {},
                    label = { Text("订单接收方") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    readOnly = true,
                )
                SmallSpacer(16)
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = orderBookViewModel.estimateOrderDate,
                        onValueChange = {},
                        label = { Text("期望交付时间") },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable)
                            .fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {

                        val today = LocalDate.now()
                        (1..30).map { today.plus(it, DateTimeUnit.DAY) }.forEach {
                            DropdownMenuItem(text = {
                                Text(it.dateOnly())

                            }, onClick = {
                                orderBookViewModel.estimateOrderDate = it.dateOnly()
                                expanded = false
                            })
                        }

                    }
                }

                SmallSpacer(16)
                TextField(
                    value = orderBookViewModel.note,
                    onValueChange = {
                        orderBookViewModel.note = it
                    },
                    label = { Text("备注") },
                    modifier = Modifier.height(96.dp).fillMaxWidth()
                )
                SmallSpacer(16)
                HorizontalDivider()
                SmallSpacer(16)
                Text("产品列表", style = MaterialTheme.typography.titleSmall)
                SmallSpacer()
                orderBookViewModel.orderItemList().forEach {
                    Surface(color = MaterialTheme.colorScheme.surfaceContainerHigh) {

                        ListItem(tonalElevation = 3.dp, leadingContent = {
                            Text(
                                orderBookViewModel.orderDict[it.id].toString() + FormatUtils.times,
                                fontWeight = FontWeight.Black
                            )
                        }, headlineContent = {
                            Text(it.getRealName())
                        }, supportingContent = {
                            Text(
                                it.product.purchaseUnitName,
                                color = LocalContentColor.current.copy(alpha = 0.6f)
                            )
                        }, trailingContent = {
                            Text((it.price * orderBookViewModel.orderDict[it.id]!!).toPriceDisplay())
                        })

                    }
                }
                SmallSpacer()

                LabelText("预计订单总额(税前)")
                SmallSpacer(4)
                Text(
                    orderBookViewModel.total().toPriceDisplay(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black
                )


                SmallSpacer(16)
                HorizontalDivider()
                SmallSpacer(16)
                Text("下单门店地址信息", style = MaterialTheme.typography.titleSmall)
                LabelText("如果地址有误，请在Aaden POS中台中进行更新", secondary = true)
                SmallSpacer()
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceContainer
                ) {
                    BaseVCenterRow(modifier = Modifier.pa()) {
                        Text(selectedOrderBook.shopInfo.contactInfo.displayString())
                    }
                }


            }
            BaseVCenterRow(modifier = Modifier.pa()) {
                MainButton(
                    "确认订单",
                    icon = Icons.Default.Done,
                    loading = orderBookViewModel.createOrderLoading
                ) {

                    orderBookViewModel.createOrder(toOrderSuccess)


                }
            }
        }

    }
}