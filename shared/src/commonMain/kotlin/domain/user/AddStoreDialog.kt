package domain.user

import LocalDialogManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Egg
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.Grain
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dataLayer.repository.DishStore.store
import domain.composable.basic.button.MainActionGrowButton
import domain.composable.basic.button.MainButton
import domain.composable.basic.layout.BaseSurface
import domain.composable.basic.layout.IkHeader
import domain.composable.basic.layout.SmallSpacer
import domain.composable.basic.wrapper.LoadingProvider
import domain.composable.dialog.basic.BeautifulDialog
import domain.food.user.AggregatedActualIntake
import domain.food.user.FoodLog
import domain.food.user.NutritionRecommendation
import modules.utils.timeToNow
import view.page.homePage.dataCenterPage.NutrientItem
import view.page.homePage.dataCenterPage.NutrientRow
import view.page.homePage.dataCenterPage.storeDetail.dashboard.TwoItemsPerRowGrid


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodLogDetailDialog(nutritionVM: NutritionVM) {
    val hapticFeedback = LocalHapticFeedback.current
    BeautifulDialog(nutritionVM.showFoodLogDetailDialog, onDismissRequest = {
        nutritionVM.showFoodLogDetailDialog = false
    }) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            val log = nutritionVM.selectedFoodLog
            val info = nutritionVM.info
            if (log != null && info != null) {
                AsyncImage(
                    log.imageUrl, null, modifier = Modifier.fillMaxWidth().clip(
                        MaterialTheme.shapes.large
                    ), contentScale = ContentScale.FillWidth
                )
                SmallSpacer(16)
                Text(log.createTimestamp.timeToNow(), style = MaterialTheme.typography.bodySmall)
                Text(log.foodDescription)
                SmallSpacer(24)
                Text(
                    "营养成分",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Black
                )
                SmallSpacer()
                FoodLogSummary(recommendation = info.nutritionRecommendation, actual = log)
                SmallSpacer(24)
                BaseSurface {
                    Text(
                        log.aiTips,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp).fillMaxWidth()
                    )
                }

                SmallSpacer(24)
                MainButton(
                    text = "删除",
                    loading = nutritionVM.foodLogLoading,
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    nutritionVM.deleteFoodLog(log)
                }
            }
        }

    }


}

@Composable
fun FoodLogSummary(
    recommendation: NutritionRecommendation, actual: FoodLog
) {
    val nutrientItems = listOf(
        NutrientItem(
            label = "卡路里",
            recommended = recommendation.recommendedCalories,
            actual = actual.calories,
            unit = "kcal",
            icon = Icons.Outlined.RestaurantMenu,
            color = Color(0xFFE91E63)
        ), NutrientItem(
            label = "蛋白质",
            recommended = recommendation.recommendedProtein,
            actual = actual.proteinGrams,
            unit = "g",
            icon = Icons.Outlined.Egg,
            color = Color(0xFF9C27B0)
        ), NutrientItem(
            label = "脂肪",
            recommended = recommendation.recommendedFat,
            actual = actual.fatGrams,
            unit = "g",
            icon = Icons.Outlined.Fastfood,
            color = Color(0xFF673AB7)
        ), NutrientItem(
            label = "碳水化合物",
            recommended = recommendation.recommendedCarbohydrates,
            actual = actual.carbohydrateGrams,
            unit = "g",
            icon = Icons.Outlined.Grain,
            color = Color(0xFF3F51B5)
        ), NutrientItem(
            label = "膳食纤维",
            recommended = recommendation.recommendedDietaryFiber,
            actual = actual.dietaryFiberGrams,
            unit = "g",
            icon = Icons.Filled.TrackChanges,
            color = Color(0xFF2196F3)
        ), NutrientItem(
            label = "蔬菜",
            recommended = recommendation.recommendedVegetables,
            actual = actual.totalVegetablesGrams,
            unit = "g",
            icon = Icons.Filled.Restaurant,
            color = Color(0xFF03A9F4)
        ), NutrientItem(
            label = "水果",
            recommended = recommendation.recommendedFruits,
            actual = actual.totalFruitsGrams,
            unit = "g",
            icon = Icons.Outlined.ShoppingCart,
            color = Color(0xFF00BCD4)
        ), NutrientItem(
            label = "饮水量",
            recommended = recommendation.recommendedWaterIntake,
            actual = actual.waterIntakeMl,
            unit = "ml",
            icon = Icons.Filled.WaterDrop,
            color = Color(0xFF009688)
        ), NutrientItem(
            label = "钠",
            recommended = recommendation.recommendedSodium,
            actual = actual.sodiumMg,
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