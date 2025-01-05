package view.page.homePage.dataCenterPage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Egg
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.Grain
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.materialkolor.ktx.harmonize
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.SmallSpacer
import domain.food.user.AggregatedActualIntake
import domain.food.user.NutritionRecommendation
import domain.user.IdentityVM
import domain.user.NutritionVM
import modules.utils.FormatUtils.displayWithUnit
import modules.utils.FormatUtils.toPriceDisplay
import view.page.homePage.dataCenterPage.storeDetail.StoreDetails
import view.page.homePage.dataCenterPage.storeDetail.dashboard.TwoItemsPerRowGrid


@Composable
fun DataCenterPage(
    identityVM: IdentityVM,
    nutritionVM: NutritionVM,
    toStatisticCenter: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        StoreDetails(
            identityVM = identityVM,
            nutritionVM = nutritionVM,
            toStatisticCenter = toStatisticCenter
        )

    }
}

@Composable
fun NutritionSummary(
    recommendation: NutritionRecommendation,
    actual: AggregatedActualIntake
) {
    val nutrientItems = listOf(
        NutrientItem(
            label = "卡路里",
            recommended = recommendation.recommendedCalories,
            actual = actual.totalCalories,
            unit = "kcal",
            icon = Icons.Outlined.RestaurantMenu,
            color = Color(0xFFE91E63)
        ),
        NutrientItem(
            label = "蛋白质",
            recommended = recommendation.recommendedProtein,
            actual = actual.totalProtein,
            unit = "g",
            icon = Icons.Outlined.Egg,
            color = Color(0xFF9C27B0)
        ),
        NutrientItem(
            label = "脂肪",
            recommended = recommendation.recommendedFat,
            actual = actual.totalFat,
            unit = "g",
            icon = Icons.Outlined.Fastfood,
            color = Color(0xFF673AB7)
        ),
        NutrientItem(
            label = "碳水化合物",
            recommended = recommendation.recommendedCarbohydrates,
            actual = actual.totalCarbohydrates,
            unit = "g",
            icon = Icons.Outlined.Grain,
            color = Color(0xFF3F51B5)
        ),
        NutrientItem(
            label = "膳食纤维",
            recommended = recommendation.recommendedDietaryFiber,
            actual = actual.totalDietaryFiber,
            unit = "g",
            icon = Icons.Filled.TrackChanges,
            color = Color(0xFF2196F3)
        ),
        NutrientItem(
            label = "蔬菜",
            recommended = recommendation.recommendedVegetables,
            actual = actual.totalVegetables,
            unit = "g",
            icon = Icons.Filled.Restaurant,
            color = Color(0xFF03A9F4)
        ),
        NutrientItem(
            label = "水果",
            recommended = recommendation.recommendedFruits,
            actual = actual.totalFruits,
            unit = "g",
            icon = Icons.Outlined.ShoppingCart,
            color = Color(0xFF00BCD4)
        ),
        NutrientItem(
            label = "饮水量",
            recommended = recommendation.recommendedWaterIntake,
            actual = actual.totalWaterIntake,
            unit = "ml",
            icon = Icons.Filled.WaterDrop,
            color = Color(0xFF009688)
        ),
        NutrientItem(
            label = "钠",
            recommended = recommendation.recommendedSodium,
            actual = actual.totalSodium,
            unit = "mg",
            icon = Icons.Filled.Science,
            color = Color(0xFF4CAF50)
        )
    )
    TwoItemsPerRowGrid(
        items = nutrientItems,
        horizontalSpacing = 16.dp,
        contentPadding = PaddingValues(0.dp),
        verticalSpacing = 16.dp
    ) { item ->

        NutrientRow(item = item)

    }
}

data class NutrientItem(
    val label: String,
    val recommended: BigDecimal,
    val actual: BigDecimal,
    val unit: String,
    val color: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
)


@Composable
fun NutrientRow(item: NutrientItem) {
    Column(
    ) {
        val max = maxOf(item.recommended, item.actual)
        val over = item.actual > item.recommended
        BaseVCenterRow(
        ) {
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodyMedium,
            )
            GrowSpacer()
            Text(item.unit, style = MaterialTheme.typography.bodyMedium)

        }
        SmallSpacer()
        Row {
            LinearProgressIndicator(
                progress = {
                    item.actual.floatValue(false) / max.floatValue(false).coerceAtLeast(1f)
                },
                modifier = Modifier.fillMaxWidth().height(12.dp),
                color = if (over) MaterialTheme.colorScheme.error else item.color,
                trackColor = item.color.harmonize(MaterialTheme.colorScheme.primary).copy(0.2f)
            )
        }
        Row {
            Text(
                item.actual.displayWithUnit(),
                style = MaterialTheme.typography.titleLarge,
                color = if (over) MaterialTheme.colorScheme.error else item.color
            )
        }
        Text(
            item.recommended.displayWithUnit(""),
            style = MaterialTheme.typography.labelSmall
        )
    }
}
