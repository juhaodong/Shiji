package view.page.homePage.dataCenterPage.storeList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.SmallSpacer
import domain.user.IdentityVM
import domain.user.model.UserStoreDetailsDTO
import modules.utils.FormatUtils.toPriceDisplay


@Composable
fun StoreCard(
    store: UserStoreDetailsDTO,
    isSelected: Boolean,
    loading: Boolean,
    ratio: BigDecimal = BigDecimal.ZERO,
    onClick: () -> Unit
) {
    val backgroundColor =
        if (ratio == BigDecimal.ZERO)
            MaterialTheme.colorScheme.surfaceContainerHigh else
            MaterialTheme.colorScheme.primary.copy(alpha = ratio.floatValue(false))
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        color = backgroundColor,
        contentColor = if (ratio < 0.5f) Color.Black else Color.White,
        shape = MaterialTheme.shapes.small,
    ) {
        Column(modifier = Modifier.padding(8.dp)) {

            Text(
                text = store.storeName,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 2,  // Ensure the text occupies up to two lines
                overflow = TextOverflow.Ellipsis,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                lineHeight = 11.sp,
                modifier = Modifier.height(26.dp)
            )
            if (loading) {
                Text(
                    text = "数据更新中...",
                    style = MaterialTheme.typography.labelSmall,
                )
            } else {
                BaseVCenterRow {
                    Icon(
                        Icons.Default.Wallet,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp)
                    )
                    SmallSpacer(4)
                    Text(
                        text = store.salesToday.toPriceDisplay(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                    )
                }

            }

        }
    }
}

@Composable
fun StoreList(identityVM: IdentityVM) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween, // Arrange elements with space between
            verticalAlignment = Alignment.CenterVertically // Vertically align elements
        ) {

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.inverseSurface
                ), shape = MaterialTheme.shapes.extraSmall
            ) {
                Text(
                    text = "门店",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                )
            }

            Text(
                text = " ${identityVM.currentStore?.storeName?.split("<BR>")?.first() ?: "-"}",
                style = MaterialTheme.typography.bodySmall,
            )
            GrowSpacer()
            Card(
                modifier = Modifier, // Reduced padding
                colors = CardDefaults.cardColors(
                    containerColor = if (identityVM.currentStore?.ngrokOnline == true) MaterialTheme.colorScheme.inverseSurface
                    else MaterialTheme.colorScheme.error
                ), shape = MaterialTheme.shapes.extraSmall
            ) {
                Text(
                    text = if (identityVM.currentStore?.ngrokOnline == true) "实时数据在线" else "离线/实时数据不可用",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold, // Make "当前门店" bold
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                )
            }

            SmallSpacer()
            IconButton(
                onClick = { identityVM.toggleStoreList() }, modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Apps,
                    contentDescription = null
                )
            }
        }

    }
}
