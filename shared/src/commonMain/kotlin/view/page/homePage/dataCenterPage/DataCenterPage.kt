package view.page.homePage.dataCenterPage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Egg
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.Grain
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.materialkolor.ktx.harmonize
import domain.composable.basic.TwoItemsPerRowGrid
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.SmallSpacer
import domain.food.service.AggregatedActualIntake
import domain.food.service.NutritionRecommendation
import domain.user.IdentityVM
import domain.user.NutritionVM
import kotlinx.coroutines.delay
import modules.utils.FormatUtils.displayWithUnit
import view.page.homePage.AppToolbarFragment


@Composable
fun DataCenterPage(
    identityVM: IdentityVM, nutritionVM: NutritionVM, toStatisticCenter: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AppToolbarFragment(identityVM, nutritionVM)
        StatisticsFragment(
            identityVM = identityVM,
            nutritionVM = nutritionVM,
            toStatisticCenter = toStatisticCenter
        )

        LaunchedEffect(true) {
            delay(100)
            nutritionVM.showDataAtDate()
        }

    }
}

@Composable
fun NutritionSummary(
    recommendation: NutritionRecommendation, actual: AggregatedActualIntake
) {
    val nutrientItems = listOf(
        NutrientItem(
            label = "卡路里",
            recommended = recommendation.recommendedCalories,
            actual = actual.totalCalories,
            unit = "kcal",
            icon = Icons.Outlined.RestaurantMenu,
            color = Color(0xFFE91E63)
        ), NutrientItem(
            label = "蛋白质",
            recommended = recommendation.recommendedProtein,
            actual = actual.totalProtein,
            unit = "g",
            icon = Icons.Outlined.Egg,
            color = Color(0xFF9C27B0)
        ), NutrientItem(
            label = "脂肪",
            recommended = recommendation.recommendedFat,
            actual = actual.totalFat,
            unit = "g",
            icon = Icons.Outlined.Fastfood,
            color = Color(0xFF673AB7)
        ), NutrientItem(
            label = "碳水化合物",
            recommended = recommendation.recommendedCarbohydrates,
            actual = actual.totalCarbohydrates,
            unit = "g",
            icon = Icons.Outlined.Grain,
            color = Color(0xFF3F51B5)
        ), NutrientItem(
            label = "膳食纤维",
            recommended = recommendation.recommendedDietaryFiber,
            actual = actual.totalDietaryFiber,
            unit = "g",
            icon = Icons.Filled.TrackChanges,
            color = Color(0xFF2196F3)
        ), NutrientItem(
            label = "蔬菜",
            recommended = recommendation.recommendedVegetables,
            actual = actual.totalVegetables,
            unit = "g",
            icon = Icons.Filled.Restaurant,
            color = Color(0xFF03A9F4)
        ), NutrientItem(
            label = "水果",
            recommended = recommendation.recommendedFruits,
            actual = actual.totalFruits,
            unit = "g",
            icon = Icons.Outlined.ShoppingCart,
            color = Color(0xFF00BCD4)
        ), NutrientItem(
            label = "饮水量",
            recommended = recommendation.recommendedWaterIntake,
            actual = actual.totalWaterIntake,
            unit = "ml",
            icon = Icons.Filled.WaterDrop,
            color = Color(0xFF009688)
        ), NutrientItem(
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
        verticalSpacing = 24.dp
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
            Text(
                item.recommended.displayWithUnit().dropLast(3),
                style = MaterialTheme.typography.labelSmall,
                color = LocalContentColor.current.copy(0.4f)
            )

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
                item.actual.displayWithUnit(item.unit),
                style = MaterialTheme.typography.titleLarge,
                color = if (over) MaterialTheme.colorScheme.error else item.color
            )
        }
    }
}
