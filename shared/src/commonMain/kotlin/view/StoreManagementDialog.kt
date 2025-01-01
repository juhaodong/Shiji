package view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import domain.composable.basic.button.ActionLeftMainButton
import domain.composable.basic.button.MainButton
import domain.composable.basic.cards.BaseCardHeader
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.SmallSpacer
import domain.composable.basic.layout.pa
import domain.composable.dialog.basic.BeautifulDialog
import domain.user.IdentityVM
import modules.utils.FormatUtils.toPriceDisplay
import view.page.homePage.dataCenterPage.storeList.StoreCard

@Composable
fun StoreManagementDialog(identityVM: IdentityVM, onChoose: () -> Unit) {
    BeautifulDialog(identityVM.showStoreManagementDialog, onDismissRequest = {
        identityVM.showStoreManagementDialog = false
    }) {
        BaseCardHeader(
            title = "请选择您要查看数据的门店",
            subtitle = "您还可以添加更多的门店",
            icon = Icons.Default.Link
        )
        LazyVerticalGrid(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            columns = GridCells.Adaptive(96.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val maxToday = identityVM.userStoreList.maxOf { it.salesToday }
            items(identityVM.userStoreList) { store ->
                val ratio = store.salesToday.divide(
                    maxToday.coerceAtLeast(BigDecimal.ONE),
                    DecimalMode.US_CURRENCY
                )
                StoreCard(store = store,
                    ratio = ratio,
                    isSelected = store.deviceId == identityVM.currentStore?.deviceId,
                    loading = identityVM.storeListLoading,
                    onClick = {
                        identityVM.enterStore(store.deviceId)
                        identityVM.showStoreManagementDialog = false
                        onChoose()
                    })
            }
        }
        BaseVCenterRow(modifier = Modifier.pa()) {
            MainButton(
                "添加门店",
                Icons.Default.Add,
                color = MaterialTheme.colorScheme.primary
            ) {
                identityVM.addStoreOpt = true
            }
        }


    }

}


@Composable
fun ColumnScope.StoreManagementFragment(identityVM: IdentityVM, selectDeviceId: (String) -> Unit) {
    BaseCardHeader(
        icon = Icons.Rounded.CloudSync,
        title = "请选择想要查看的门店",
        subtitle = "如果您拥有多个门店，那么您可以在之后随时绑定和查看更多门店的数据",
        noPadding = true
    )
    SmallSpacer(16)
    if (identityVM.userStoreList.isEmpty()) {
        // Empty state
        Box(
            modifier = Modifier.fillMaxSize().weight(1f), // Take up remaining space
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Outlined.Store, // Or any relevant icon
                    contentDescription = "No stores",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "您还没有绑定任何门店",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)
        ) {
            items(identityVM.userStoreList) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        selectDeviceId(it.deviceId)
                    }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = it.storeName,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            SmallSpacer()
                            Text(
                                text = it.salesToday.toPriceDisplay(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Black
                            )
                        }
                        SmallSpacer()

                        Text(
                            text = "在线状态: ${if (it.isOnline) "在线" else "离线"}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "ACS 实时数据: ${if (it.ngrokOnline) "在线" else "离线"}",
                            style = MaterialTheme.typography.bodySmall
                        )

                    }
                }
            }
        }
    }
    SmallSpacer(16)

    MainButton("添加新的门店", icon = Icons.Default.Add) {
        identityVM.addStoreOpt = true
    }
    SmallSpacer(16)
    MainButton(
        "登出",
        icon = Icons.AutoMirrored.Filled.Logout,
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        identityVM.logout()
    }
}
