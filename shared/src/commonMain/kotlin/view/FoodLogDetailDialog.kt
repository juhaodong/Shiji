package view

import LocalDialogManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import domain.composable.basic.TwoItemsPerRowGrid
import domain.composable.basic.button.MainActionGrowButton
import domain.composable.basic.button.MainButton
import domain.composable.basic.layout.BaseSurface
import domain.composable.basic.layout.SmallSpacer
import domain.composable.dialog.basic.BeautifulDialog
import domain.food.service.FoodLog
import domain.food.service.NutritionRecommendation
import domain.user.NutritionVM
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import modules.share.MimeType
import modules.share.ShareFileModel
import modules.share.rememberShareManager
import modules.utils.imageWithProxy

import modules.utils.timeToNow
import modules.utils.toByteArray
import view.page.homePage.dataCenterPage.NutrientItem
import view.page.homePage.dataCenterPage.NutrientRow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
fun FoodLogDetailDialog(nutritionVM: NutritionVM) {

    BeautifulDialog(nutritionVM.showFoodLogDetailDialog, noPadding = true, onDismissRequest = {
        nutritionVM.showFoodLogDetailDialog = false
    }) {
        val graphicsLayer = rememberGraphicsLayer()
        val scope = rememberCoroutineScope()
        val shareManager = rememberShareManager()
        val dialogManager = LocalDialogManager.current
        var sharing by remember {
            mutableStateOf(false)
        }
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            val log = nutritionVM.selectedFoodLog
            val info = nutritionVM.info
            if (log != null && info != null) {
                Column(
                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
                        .drawWithContent {
                            graphicsLayer.record {

                                // draw the contents of the composable into the graphics layer
                                this@drawWithContent.drawContent()
                            }
                            // draw the graphics layer on the visible canvas
                            drawLayer(graphicsLayer)
                        }) {
                    Column(
                        modifier = Modifier.background(MaterialTheme.colorScheme.background)
                            .padding(16.dp)
                    ) {

                        AsyncImage(
                            model = log.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().clip(
                                MaterialTheme.shapes.large
                            ),
                            contentScale = ContentScale.FillWidth
                        )
                        SmallSpacer(16)
                        Text(
                            log.createTimestamp.timeToNow(),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(log.socialDescription)
                        SmallSpacer()

                        SmallSpacer(24)
                        val rating = log.qualityRating
                        val (comment, emoji) = when (rating) {
                            in 0..25 -> "糟糕至极" to "😞"
                            in 26..50 -> "略逊一筹" to "😕"
                            in 51..75 -> "还算不错" to "🙂"
                            in 76..100 -> "非常健康" to "😄"
                            else -> "未知" to "❓"
                        }

                        Text(
                            "食物健康评分",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Black
                        )
                        SmallSpacer()
                        Text(
                            "$comment $emoji($rating)",
                            style = MaterialTheme.typography.headlineLarge
                        )
                        SmallSpacer(24)
                        Text(
                            "食物内容",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Black
                        )
                        SmallSpacer()
                        Text(
                            log.foodContent,
                            style = MaterialTheme.typography.bodyMedium
                        )
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

                        SmallSpacer()
                    }

                }
                Row(modifier = Modifier.padding(16.dp)) {
                    MainActionGrowButton(
                        text = "删除",
                        loading = nutritionVM.foodLogLoading,
                        icon = Icons.Default.Delete,
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        nutritionVM.deleteFoodLog(log)
                    }
                    SmallSpacer()
                    MainActionGrowButton(
                        text = "分享",
                        loading = sharing,
                        color = MaterialTheme.colorScheme.primary,
                        icon = Icons.Default.IosShare
                    ) {
                        sharing = true
                        scope.launch {
                            val imageBitmap = graphicsLayer.toImageBitmap()
                            val result = shareManager.shareFile(
                                ShareFileModel(
                                    mime = MimeType.IMAGE,
                                    fileName = Uuid.random().toString() + ".png",
                                    bytes = imageBitmap.toByteArray()
                                )
                            )
                            result.onFailure {
                                dialogManager.confirmAnd(it.message ?: "-")
                                Napier.e(throwable = it, message = "分享失败")
                            }
                            sharing = false
                        }

                    }
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