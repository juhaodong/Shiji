package view.page.homePage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.ui.unit.dp
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.raedghazal.kotlinx_datetime_ext.now
import domain.composable.basic.layout.BaseSurface
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.SmallSpacer
import domain.user.IdentityVM
import domain.user.NutritionVM
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import modules.utils.DateRangeMoveDirection
import modules.utils.closingToday
import modules.utils.closingTodayRange
import modules.utils.display
import modules.utils.move


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
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
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
            BaseSurface(
                onClick = {
                    nutritionVM.confirmDateRange(
                        nutritionVM.currentDateRange.move(
                            DateRangeMoveDirection.Backward
                        )
                    )

                }, color = MaterialTheme.colorScheme.surfaceContainer

            ) {

                Box(modifier = Modifier.padding(2.dp)) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = MaterialTheme.colorScheme.secondary

                    )
                }
            }
            Surface(onClick = {
                nutritionVM.showDateDialog = true
            }, color = Color.Transparent) {
                Box(modifier = Modifier.padding(2.dp)) {
                    if (nutritionVM.currentDateRange == closingTodayRange()) Text(
                        "剩余${(profile.weightLossCycle - restDate)}天",
                        style = MaterialTheme.typography.bodySmall
                    )
                    else Text(
                        nutritionVM.currentDateRange.display(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            BaseSurface(
                onClick = {
                    nutritionVM.confirmDateRange(
                        nutritionVM.currentDateRange.move(
                            DateRangeMoveDirection.Forward
                        )
                    )
                },
                enabled = nutritionVM.currentDateRange.second != closingToday(),
                color = if (nutritionVM.currentDateRange.second != closingToday())
                    MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.04f
                )
            ) {
                Box(modifier = Modifier.padding(2.dp)) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = if (nutritionVM.currentDateRange.second != closingToday()) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.12f
                        )
                    )
                }

            }


        }
    }
}
