@file:OptIn(ExperimentalMaterial3Api::class)

package view.page.homePage.dataCenterPage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.composable.basic.button.BaseIconButton
import domain.composable.basic.cards.BaseCardHeader
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.SmallSpacer
import domain.user.IdentityVM
import domain.user.NutritionVM
import kotlinx.coroutines.launch
import modules.utils.FormatUtils.displayWithUnit
import modules.utils.timeToNow


@Composable
fun StatisticsFragment(identityVM: IdentityVM, nutritionVM: NutritionVM, toStatisticCenter: () -> Unit) {
    val pullRefreshState = rememberPullToRefreshState()

    val scope = rememberCoroutineScope()
    val profile = identityVM.currentProfile
    val info = nutritionVM.info

    PullToRefreshBox(
        isRefreshing = nutritionVM.loading,
        state = pullRefreshState,
        modifier = Modifier.fillMaxSize(),
        onRefresh = {

            scope.launch {
                pullRefreshState.animateToHidden()
            }
        }) {
        if (profile != null && info != null) {
            val actual = info.actualNutrition
            val recommendation = info.nutritionRecommendation
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    .verticalScroll(state = rememberScrollState())
            ) {
                SmallSpacer(16)

                Column {
                    val over = actual.totalCalories > recommendation.recommendedCalories
                    Text(
                        "本日摄入卡路里",
                        style = MaterialTheme.typography.bodySmall,
                        color = LocalContentColor.current.copy(alpha = 0.6f)
                    )
                    SmallSpacer(2)

                    BaseVCenterRow {
                        Text(
                            actual.totalCalories.displayWithUnit("kcal"),
                            style = MaterialTheme.typography.headlineMedium,
                            color = if (over) MaterialTheme.colorScheme.error else LocalContentColor.current
                        )

                    }
                    val max = maxOf(
                        info.actualNutrition.totalCalories,
                        info.nutritionRecommendation.recommendedCalories
                    )

                    SmallSpacer(4)
                    LinearProgressIndicator(
                        progress = {
                            actual.totalCalories.floatValue(false) / max.floatValue(false)
                                .coerceAtLeast(1f)
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        color = if (over) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.inverseSurface,
                        trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                }
                SmallSpacer(16)

                Column(modifier = Modifier) {
                    val over = actual.averageQualityRating < recommendation.minimumQualityRating
                    Text(
                        "食物质量评分",
                        style = MaterialTheme.typography.bodySmall,
                        color = LocalContentColor.current.copy(alpha = 0.6f)
                    )
                    SmallSpacer(2)
                    BaseVCenterRow {
                        Text(
                            actual.averageQualityRating.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = if (over) MaterialTheme.colorScheme.error else LocalContentColor.current
                        )
                        Text(
                            "/" + recommendation.minimumQualityRating.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = LocalContentColor.current.copy(alpha = 0.6f)
                        )
                    }
                }
                SmallSpacer(16)
                Column(modifier = Modifier) {
                    Text(
                        "上次用餐",
                        style = MaterialTheme.typography.bodySmall,
                        color = LocalContentColor.current.copy(alpha = 0.6f)
                    )
                    SmallSpacer(2)

                    Text(
                        nutritionVM.lastLog()?.createTimestamp?.timeToNow() ?: "尚未用餐",
                        style = MaterialTheme.typography.headlineSmall,
                    )

                }







                SmallSpacer(16)
                Row(modifier = Modifier) {
                    BaseCardHeader("更多的详细数据", "继续浏览", noPadding = true, large = true) {
                        BaseIconButton(icon = Icons.Default.ArrowForward) {

                        }
                    }
                }
                SmallSpacer(24)

                NutritionSummary(
                    nutritionVM.info!!.nutritionRecommendation, nutritionVM.info!!.actualNutrition
                )

                SmallSpacer(16)


            }
        }

    }




}

