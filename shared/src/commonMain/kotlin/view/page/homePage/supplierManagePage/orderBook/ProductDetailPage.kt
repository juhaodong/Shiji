@file:OptIn(ExperimentalMaterial3Api::class)

package view.page.homePage.supplierManagePage.orderBook

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Transform
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import domain.composable.basic.button.BaseIconButton
import domain.composable.basic.button.DeleteIconButton
import domain.composable.basic.button.MainButton
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.SmallSpacer
import domain.composable.basic.layout.pa
import domain.composable.basic.wrapper.NoContentColumnDisplay
import domain.supplier.OrderBookViewModel
import modules.utils.FormatUtils.toPriceDisplay
import modules.utils.toPercentageDisplay

@Composable
fun ProductDetailPage(orderBookViewModel: OrderBookViewModel, back: () -> Unit) {
    val selectedOrderBook = orderBookViewModel.selectedOrderBook ?: return
    val productInfo = orderBookViewModel.productDetail ?: return
    val hapticFeedback = LocalHapticFeedback.current
    val itemInfo = productInfo.itemInfo
    var selectedTabIndex by remember { mutableStateOf(0) }
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text("产品详情", style = MaterialTheme.typography.bodyLarge)
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
                if (itemInfo != null) {
                    BaseIconButton(icon = Icons.Rounded.Settings) {
                        orderBookViewModel.updateProductTransform()
                    }
                    DeleteIconButton(productInfo.product.name) {
                        orderBookViewModel.importSingleProduct(productInfo.product.id, false)
                    }
                }

            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).fillMaxHeight()

                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                AsyncImage(
                    productInfo.product.realImageUrl(),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth()
                )
                PrimaryTabRow(
                    selectedTabIndex = selectedTabIndex,
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("基本信息") }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("详细信息") }
                    )
                    if (itemInfo?.transFormTo != null) {
                        Tab(
                            selected = selectedTabIndex == 2,
                            onClick = { selectedTabIndex = 2 },
                            text = { Text("转换信息") }
                        )
                    }
                }
                Column(modifier = Modifier.padding(16.dp)) {
                    when (selectedTabIndex) {
                        0 -> {
                            ProductDetailItem("产品编号", productInfo.product.sku)
                            ProductDetailItem("产品名称", productInfo.product.name)
                            ProductDetailItem("产品描述", productInfo.product.description)
                            ProductDetailItem("包装大小", productInfo.product.purchaseUnitName)
                            ProductDetailItem(
                                "税率类型",
                                productInfo.product.taxRate.toPercentageDisplay()
                            )
                            ProductDetailItem(
                                "所属供应商分类",
                                productInfo.product.category.name
                            )
                        }

                        1 -> {
                            if (itemInfo == null) {
                                NoContentColumnDisplay(title = "您尚未导入此商品")

                            } else {
                                ProductDetailItem("备注名称", itemInfo.overrideName)
                                ProductDetailItem("备注", itemInfo.note)
                                ProductDetailItem("所属分类", itemInfo.orderBookCategory.name)
                                ProductDetailItem("专属报价", itemInfo.price.toPriceDisplay())
                            }
                        }

                        2 -> {
                            if (itemInfo != null) {
                                ProductDetailItem("转化原料名称", itemInfo.transFormResourceName)
                                ProductDetailItem("转化原料数量", itemInfo.transFormUnitDisplay)
                            }

                        }
                    }
                }
            }
            BaseVCenterRow(modifier = Modifier.pa()) {
                if (itemInfo == null) {
                    MainButton(
                        "导入商品",
                        icon = Icons.AutoMirrored.Rounded.ArrowForward,
                        loading = orderBookViewModel.productDetailLoading
                    ) {
                        orderBookViewModel.importSingleProduct(productInfo.product.id)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                } else {
                    if (selectedTabIndex != 2) {
                        MainButton(
                            "编辑信息",
                            icon = Icons.Rounded.EditNote,
                            loading = orderBookViewModel.productDetailLoading
                        ) {
                            orderBookViewModel.updateProductDetail()
                        }
                    } else {
                        MainButton(
                            "编辑产品转化信息",
                            icon = Icons.Rounded.Transform,
                            loading = orderBookViewModel.productDetailLoading
                        ) {
                            orderBookViewModel.updateProductTransform()
                        }
                    }

                }

            }


        }

    }
}

@Composable
fun ProductDetailItem(label: String, value: String) {
    OutlinedCard(shape = RectangleShape) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = LocalContentColor.current.copy(0.6f)
            )
            SmallSpacer(4)
            Text(value.ifBlank { "-" }, fontWeight = FontWeight.Medium)
        }
    }

}
