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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Tune
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
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.raedghazal.kotlinx_datetime_ext.now
import dataLayer.repository.DishStore.store
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.SmallSpacer
import domain.user.IdentityVM
import domain.user.NutritionVM
import domain.user.model.UserStoreDetailsDTO
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import modules.utils.FormatUtils.toPriceDisplay
import modules.utils.closingTodayRange
import modules.utils.dateOnly
import modules.utils.display


@Composable
fun StoreList(identityVM: IdentityVM, nutritionVM: NutritionVM) {
    val store = identityVM.currentProfile
    if (store != null) {
        val totalMinus = store.currentWeight.toFloat() - store.targetWeight.toFloat()

        val restDate = store.weightLossCycle - LocalDate.now()
            .daysUntil(store.startDate.plus(store.weightLossCycle, DateTimeUnit.DAY))
        val currentWeight =
            store.currentWeight.toFloat() - restDate / store.weightLossCycle.toFloat() * totalMinus
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically // Vertically align elements
        ) {

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.inverseSurface
                ), shape = MaterialTheme.shapes.extraSmall
            ) {
                Text(
                    text = "今日目标",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                )
            }
            SmallSpacer()
            Text(
                currentWeight.toBigDecimal(decimalMode = DecimalMode.US_CURRENCY)
                    .toPlainString() + "kg🏋️‍",
                style = MaterialTheme.typography.bodyMedium,
            )
            GrowSpacer()
            SmallSpacer()
            if (nutritionVM.currentDateRange == closingTodayRange())
                Text(
                    "剩余${(store.weightLossCycle - restDate)}天",
                    style = MaterialTheme.typography.bodySmall
                )
            else
                Text(
                    nutritionVM.currentDateRange.display(),
                    style = MaterialTheme.typography.bodySmall
                )
            SmallSpacer()
            IconButton(
                onClick = { nutritionVM.showDateDialog = true },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Apps,
                    contentDescription = null
                )
            }

        }
    }
}
