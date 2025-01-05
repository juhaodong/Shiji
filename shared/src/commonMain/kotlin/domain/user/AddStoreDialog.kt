package domain.user

import shijiapp.shared.generated.resources.Res
import shijiapp.shared.generated.resources._PleaseEnterActivationCode
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import domain.composable.basic.button.MainActionGrowButton
import domain.composable.basic.button.MainButton
import domain.composable.basic.cards.BaseCardHeader
import domain.composable.basic.keyboard.OTPLayout
import domain.composable.basic.layout.IkHeader
import domain.composable.basic.layout.SmallSpacer
import domain.composable.basic.layout.pa
import domain.composable.basic.wrapper.LoadingProvider
import domain.composable.basic.wrapper.NoContentColumnDisplay
import domain.composable.dialog.basic.BeautifulDialog
import io.github.aakira.napier.Napier
import org.jetbrains.compose.resources.stringResource
import qrscanner.CameraLens
import qrscanner.QrScanner

enum class AddStoreTab {
    QrCode,
    ActivationCode,

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStoreDialog(identityVM: IdentityVM) {
    val hapticFeedback = LocalHapticFeedback.current
    BeautifulDialog(identityVM.updateUserProfileDialog, noPadding = true, onDismissRequest = {
        identityVM.updateUserProfileDialog = false
    }) {
        var selectedTab by remember { mutableStateOf(AddStoreTab.QrCode) }
        PrimaryTabRow(
            selectedTabIndex = selectedTab.ordinal, // Get the ordinal value for PrimaryTabRow
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            AddStoreTab.entries.forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    icon = {
                        when (tab) {
                            AddStoreTab.ActivationCode -> Icon(
                                Icons.Filled.VpnKey,
                                contentDescription = "激活码"
                            )

                            AddStoreTab.QrCode -> Icon(
                                Icons.Filled.QrCode,
                                contentDescription = "扫码"
                            )
                        }
                    },
                    onClick = { selectedTab = tab },
                    text = {
                        Text(
                            when (tab) {
                                AddStoreTab.ActivationCode -> "输入邀请码"
                                AddStoreTab.QrCode -> "扫描设备二维码"
                            }
                        )
                    }
                )
            }
        }
        when (selectedTab) {
            AddStoreTab.ActivationCode -> {
                Column(modifier = Modifier.padding(16.dp)) {
                    OTPLayout(stringResource(Res.string._PleaseEnterActivationCode), checkOTP = {
                        identityVM.checkOpt(it)
                    }) {
                        identityVM.updateUserProfileDialog = false
                    }
                }
            }

            AddStoreTab.QrCode -> {
                LaunchedEffect(identityVM.updateUserProfileDialog) {
                    if (identityVM.updateUserProfileDialog) {
                        identityVM.qrHaveResult = false
                    }
                }
                Column(modifier = Modifier.pa()) {
                    if (!identityVM.qrHaveResult) {
                        BaseCardHeader(
                            "扫描二维码来绑定门店",
                            "请注意，每个门店二维码都只能被一个账户绑定。",
                            icon = Icons.Default.QrCodeScanner,
                            noPadding = true
                        )
                        SmallSpacer(16)
                        Surface(
                            shape = MaterialTheme.shapes.large,
                            shadowElevation = 3.dp,
                        ) {

                            Box(
                                modifier = Modifier.aspectRatio(1f).fillMaxWidth()
                                    .clip(MaterialTheme.shapes.large)
                                    .clipToBounds()
                            ) {
                                QrScanner(
                                    modifier = Modifier.fillMaxSize(),
                                    flashlightOn = false,
                                    cameraLens = CameraLens.Back,
                                    openImagePicker = false,
                                    onCompletion = {
                                        Napier.d("onScanned: $it")
                                        identityVM.checkQrCode(it)
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    },
                                    imagePickerHandler = { },
                                    onFailure = {},
                                )
                            }

                        }
                    } else {
                        BaseCardHeader(
                            "扫描结果",
                            identityVM.scanResult,
                            icon = Icons.Default.QrCode,
                            noPadding = true
                        )
                        val shopInfo = identityVM.shopInfo
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (shopInfo != null) {
                                Text("门店名称: ${shopInfo.contactInfo.legalName}")
                                Text("门店编号: ${shopInfo.shopId}")
                                Text("绑定状态: ${if (shopInfo.canBind()) "可以绑定" else "不可绑定"}")
                                if (shopInfo.canBind()) {
                                    MainButton("现在绑定", loading = identityVM.bindLoading) {
                                        identityVM.bindMainDevice()
                                    }
                                }
                            } else {
                                NoContentColumnDisplay(title = "没有找到对应的门店")
                            }
                        }

                    }

                }

            }
        }
    }

    BeautifulDialog(identityVM.addingStore != null, onDismissRequest = {
        identityVM.addingStore = null
    }) {
        val store = identityVM.addingStore!!
        Column {
            LoadingProvider(identityVM.bindingStore) {
                IkHeader(
                    icon = Icons.Default.Feedback,
                    title = "您是否确定绑定此门店?",
                    subtitle = "门店名称: ${store.contactInfo.legalName} 设备ID: ${store.shopId}"
                )
                SmallSpacer(16)

                Row {
                    MainActionGrowButton(
                        icon = Icons.Default.Cancel,
                        text = "取消",
                        color = MaterialTheme.colorScheme.surfaceContainer
                    ) {
                        identityVM.addingStore = null
                    }
                    SmallSpacer()
                    MainActionGrowButton(text = "好的", icon = Icons.Default.Done) {
                        identityVM.bindStore()
                    }
                }
            }

        }
    }


}