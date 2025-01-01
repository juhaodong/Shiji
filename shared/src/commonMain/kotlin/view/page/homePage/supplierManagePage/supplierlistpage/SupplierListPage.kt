@file:OptIn(ExperimentalMaterial3Api::class)

package view.page.homePage.supplierManagePage.supplierlistpage


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.automirrored.twotone.HelpCenter
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.rounded.Agriculture
import androidx.compose.material.icons.rounded.LocalDrink
import androidx.compose.material.icons.rounded.Sailing
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material.icons.twotone.LocalMall
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import domain.composable.basic.button.BaseIconButton
import domain.composable.basic.button.BaseTonalIconButton
import domain.composable.basic.cards.BaseCardHeader
import domain.composable.basic.layout.BaseSurface
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.SmallSpacer
import domain.composable.basic.wrapper.PageLoadingProvider
import domain.composable.dialog.basic.BeautifulDialog
import domain.inventory.InventoryViewModel
import domain.inventory.model.storageItem.imageWithProxy
import domain.supplier.OrderBookViewModel
import domain.supplier.model.SupplierOrderBookDTO
import theme.dashedBorder
import view.page.homePage.supplierManagePage.SupplierDisplay

@Composable
fun SupplierListPage(
    inventoryViewModel: InventoryViewModel,
    orderBookViewModel: OrderBookViewModel,
    toFindSupplierPage: () -> Unit,
    toOrderListPage: () -> Unit,
    enterSupplierDetail: (SupplierOrderBookDTO) -> Unit
) {
    var showHelpingDialog by mutableStateOf(false)
    Scaffold(topBar = {
        TopAppBar(
            navigationIcon = {
                BaseIconButton(Icons.TwoTone.LocalMall) {
                }
            },
            title = {
                Column {
                    Text(
                        "货品供应", style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "随时随地为门店订货", style = MaterialTheme.typography.labelSmall
                    )
                }

            },
            actions = {
                BaseIconButton(
                    Icons.AutoMirrored.Outlined.ReceiptLong,
                ) {
                    toOrderListPage()
                }
                BaseIconButton(
                    Icons.AutoMirrored.Filled.Help,
                ) {
                    showHelpingDialog = true
                }
            },
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) {
            PageLoadingProvider(loading = orderBookViewModel.orderBooksLoading, onRefresh = {
                orderBookViewModel.loadOrderBooks()
            }) {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                ) {
                    BaseSurface(color = MaterialTheme.colorScheme.surfaceContainerLow) {
                        Row(
                            modifier = Modifier.clickable {
                                toFindSupplierPage()
                            }.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "添加供应商",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Black
                            )
                            GrowSpacer()
                            BaseTonalIconButton(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                modifier = Modifier.size(32.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer

                            ) {
                                toFindSupplierPage()
                            }

                        }
                    }
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        orderBookViewModel.orderBooks.forEach {
                            SupplierDisplay(
                                supplierSetting = it.supplier, overrideName = it.displayName()
                            ) {
                                enterSupplierDetail(it)
                            }
                        }
                        if (!orderBookViewModel.orderBooksLoading) {
                            listOf(
                                SupplierType("一般", Icons.Rounded.Store),
                                SupplierType("农产品", Icons.Rounded.Agriculture),
                                SupplierType("鱼类", Icons.Rounded.Sailing),
                                SupplierType("饮料", Icons.Rounded.LocalDrink)
                            ).drop(
                                (orderBookViewModel.orderBooks.size - 1).coerceAtMost(4)
                                    .coerceAtLeast(0)
                            ).forEach { supplierType ->
                                SupplierTypeDisplay(
                                    onClick = { toFindSupplierPage() }, supplierType
                                )
                            }
                        }
                    }
                }
            }
        }

        BeautifulDialog(showHelpingDialog, onDismissRequest = {
            showHelpingDialog = false
        }) {
            BaseCardHeader(
                "您是否需要帮助?",
                "下面是您所有供应商的联系方式，请直接联系供应商来获取帮助",
                icon = Icons.AutoMirrored.TwoTone.HelpCenter
            )
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                val uriHandler = LocalUriHandler.current
                orderBookViewModel.orderBooks.forEach {
                    ListItem(leadingContent = {
                        AsyncImage(
                            it.supplier.imageUrl?.imageWithProxy(),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp).clip(MaterialTheme.shapes.medium)
                        )
                    }, headlineContent = {
                        Text(it.supplier.name, style = MaterialTheme.typography.bodyMedium)
                    }, supportingContent = {
                        Text(it.supplier.telephone)
                    }, trailingContent = {
                        BaseIconButton(icon = Icons.Default.Call) {
                            uriHandler.openUri("tel:${it.supplier.telephone}")
                        }
                    })
                }
            }
        }
    }
}

data class SupplierType(val name: String, val icon: ImageVector)


@Composable
fun SupplierTypeDisplay(onClick: () -> Unit, supplierType: SupplierType) {
    BaseSurface(
        modifier = Modifier.fillMaxWidth().dashedBorder(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            shape = MaterialTheme.shapes.medium
        ), color = Color.Transparent, onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Box( // Wrap the icon in a Box
                modifier = Modifier.size(48.dp) // Adjust size as needed
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), // Light background
                        shape = CircleShape
                    ).clip(CircleShape) // Clip to a circle
                    .wrapContentSize(Alignment.Center) // Center the icon
            ) {
                Icon(
                    supplierType.icon,
                    contentDescription = supplierType.name,
                    modifier = Modifier.size(24.dp)
                )
            }
            SmallSpacer(16)
            Column { // Use Column for two lines of text
                Text(
                    supplierType.name, // Supplier type
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold // Make the type bold
                )
                Text(
                    "添加此类型的供应商", // Second line
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray // Use a lighter color for the second line
                )
            }
            GrowSpacer()
            Icon(
                Icons.Filled.Add, // Add icon
                contentDescription = "添加",
                tint = MaterialTheme.colorScheme.primary // Use primary color for the icon
            )
        }
    }
}
