package view.page.homePage

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.raedghazal.kotlinx_datetime_ext.now
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.SmallSpacer
import domain.user.IdentityVM
import domain.user.NutritionVM
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import modules.utils.closingTodayRange
import modules.utils.display


@Composable
fun AppToolbarFragment(identityVM: IdentityVM, nutritionVM: NutritionVM) {
    val profile = identityVM.currentProfile
    if (profile != null) {
        val totalMinus = profile.currentWeight.toFloat() - profile.targetWeight.toFloat()

        val restDate = profile.weightLossCycle - LocalDate.now()
            .daysUntil(profile.startDate.plus(profile.weightLossCycle, DateTimeUnit.DAY))
        val currentWeight =
            profile.currentWeight.toFloat() - restDate / profile.weightLossCycle.toFloat() * totalMinus
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
                    "剩余${(profile.weightLossCycle - restDate)}天",
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
