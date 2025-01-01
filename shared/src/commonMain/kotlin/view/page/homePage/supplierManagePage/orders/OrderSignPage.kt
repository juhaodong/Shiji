package view.page.homePage.supplierManagePage.orders

import LocalDialogManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import domain.composable.basic.button.BaseIconButton
import domain.composable.basic.button.BaseTonalIconButton
import domain.composable.basic.button.MainButton
import domain.composable.basic.cards.BaseCardHeader
import domain.composable.basic.cards.LabelText
import domain.composable.basic.cards.LabelValuePair
import domain.composable.basic.layout.BaseSurface
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.SmallSpacer
import domain.composable.basic.layout.pa
import domain.composable.basic.layout.px
import domain.composable.basic.layout.py
import domain.composable.basic.wrapper.GrowLoadingProvider
import domain.composable.dialog.basic.BeautifulDialog
import domain.composable.dialog.basic.DialogViewModel
import domain.inventory.model.storageItem.imageWithProxy
import domain.purchaseOrder.PurchaseOrderVM
import domain.purchaseOrder.model.OrderItem
import io.github.joelkanyi.sain.Sain
import io.github.joelkanyi.sain.SignatureAction
import io.github.joelkanyi.sain.SignatureState
import modules.utils.FormatUtils
import modules.utils.globalDialogManager
import theme.dashedBorder


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSignPage(
    purchaseOrderVM: PurchaseOrderVM,
    dialogViewModel: DialogViewModel,
    back: () -> Unit
) {
    LaunchedEffect(Unit) {
        purchaseOrderVM.chooseOrder()
    }
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
                        "签收订单", style = MaterialTheme.typography.bodyLarge
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
        var showSign by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) {

            GrowLoadingProvider(
                loading = purchaseOrderVM.orderDetailLoading,
                haveContent = purchaseOrderVM.orderDetail != null
            ) {
                val orderDetail = purchaseOrderVM.orderDetail ?: return@GrowLoadingProvider
                val order = orderDetail.purchaseOrder
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(orderDetail.orderContents) {
                        OrderSignDisplay(
                            model = it,
                            checkedAmount = purchaseOrderVM.orderCheckDict[it.orderBookProductId],
                            amountCorrect = {
                                if (it.orderBookProductId != null) {
                                    purchaseOrderVM.setAmountForProduct(
                                        it.orderBookProductId,
                                        it.amount
                                    )
                                }
                            },
                            amountWrong = {
                                if (it.orderBookProductId != null) {
                                    dialogViewModel.runInScope {
                                        val amount = dialogViewModel.showInput(
                                            title = "请输入正确的数量",
                                            type = KeyboardType.Number
                                        )

                                        amount.toIntOrNull()?.toBigDecimal()?.let { it1 ->
                                            purchaseOrderVM.setAmountForProduct(
                                                it.orderBookProductId,
                                                it1
                                            )
                                        }?: globalDialogManager.confirmAnd("您输入的数字不可读","请重新输入")

                                    }
                                }
                            })
                    }
                    item {
                        Column {
                            SmallSpacer(16)
                            LabelValuePair("订单备注", order.note)
                            SmallSpacer(32)
                            BaseCardHeader(
                                title = "签收预览",
                                "签收后的入库预览",
                                noPadding = true,
                                icon = Icons.Default.Summarize
                            )
                        }
                    }
                    items(orderDetail.orderContents.mapNotNull {
                        purchaseOrderVM.getTransformInfo(
                            it.orderBookProductId,
                            it.amount
                        )
                    }) {
                        BaseSurface {
                            BaseVCenterRow(modifier = Modifier.px(16).py(8)) {
                                Text(it.name)
                                GrowSpacer()
                                Text(it.unit)
                                SmallSpacer()
                                Text(
                                    "${FormatUtils.times}${it.amount.toPlainString()}",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                    }
                }


            }
            val dialogManager = LocalDialogManager.current
            BeautifulDialog(showSign, onDismissRequest = {
                showSign = false
            }) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    BaseCardHeader(
                        title = "请在此处签字确认",
                        subtitle = "请使用正式字体签名，认真书写",
                        icon = Icons.Default.Brush,
                        noPadding = true
                    )
                    SmallSpacer(16)
                    var imageBitmap: ImageBitmap? by remember {
                        mutableStateOf(null)
                    }

                    val state = remember {
                        SignatureState()
                    }

                    Sain(
                        state = state,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .dashedBorder(
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.medium
                            ),
                        onComplete = { signatureBitmap ->
                            if (signatureBitmap != null) {
                                showSign = false
                                imageBitmap = signatureBitmap
                                dialogViewModel.runInScope {
                                    purchaseOrderVM.signOrder(imageBitmap!!).join()
                                    dialogManager.successAnd {
                                        back()
                                    }

                                }


                            } else {
                                dialogManager.confirmAnd(
                                    "您根本没签名",
                                    "这样子可不行，必须要签名！🤯"
                                ) { }
                            }
                        },
                    ) { action ->
                        Row(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            OutlinedButton(
                                enabled = state.signatureLines.isNotEmpty(),
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    imageBitmap = null
                                    action(SignatureAction.CLEAR)
                                }) {
                                Text("重新签名")
                            }
                            FilledTonalButton(
                                enabled = state.signatureLines.isNotEmpty(),
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    action(SignatureAction.COMPLETE)
                                }) {
                                Text("确认签收")
                            }
                        }


                    }
                }
            }

            val orderDetail = purchaseOrderVM.orderDetail ?: return@Column

            BaseVCenterRow(modifier = Modifier.fillMaxWidth().pa()) {

                MainButton(text = "确认签收") {
                    val haveNotConfirmed =
                        purchaseOrderVM.orderCheckDict.size != (orderDetail.orderContents.size
                            ?: -1)

                    dialogManager.confirmAnd(
                        "请注意！",
                        "您还有一些未确认的商品，是否跳过确认步骤，直接签收？",
                        shouldConfirm = haveNotConfirmed
                    ) {

                        orderDetail.orderContents.forEach {
                            if (it.orderBookProductId != null &&
                                !purchaseOrderVM.orderCheckDict.containsKey(
                                    it.orderBookProductId
                                )
                            ) {
                                purchaseOrderVM.setAmountForProduct(
                                    it.orderBookProductId,
                                    it.amount
                                )
                            }
                        }

                        showSign = true
                    }


                }
            }

        }

    }
}


@Composable
private fun OrderSignDisplay(
    model: OrderItem,
    checkedAmount: BigDecimal?,
    amountCorrect: () -> Unit,
    amountWrong: () -> Unit
) {
    BaseSurface(color = MaterialTheme.colorScheme.surfaceContainerLow) {
        BaseVCenterRow(modifier = Modifier.pa().fillMaxWidth()) {

            if (model.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = model.imageUrl.imageWithProxy(),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp).clip(MaterialTheme.shapes.medium)
                )
                SmallSpacer(16)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(model.name, style = MaterialTheme.typography.bodyMedium)
                SmallSpacer(4)
                LabelText(
                    model.purchaseUnit, secondary = true
                )
            }
            Text(
                FormatUtils.times + " " + model.amount.toPlainString(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.width(40.dp),
                color = MaterialTheme.colorScheme.primary
            )
            if (checkedAmount == null) {
                BaseTonalIconButton(
                    icon = Icons.Outlined.ThumbDown,
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    amountWrong()
                }
                BaseTonalIconButton(
                    icon = Icons.Rounded.ThumbUp,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    amountCorrect()
                }
            } else {
                if (checkedAmount == model.amount) {
                    BaseTonalIconButton(Icons.Default.Check) {
                        amountWrong()
                    }
                } else {
                    BaseSurface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        onClick = { amountWrong() }) {
                        Box(modifier = Modifier.px(12).py(8)) {
                            Text(checkedAmount.toPlainString())
                        }
                    }
                }
            }

        }
    }
}
