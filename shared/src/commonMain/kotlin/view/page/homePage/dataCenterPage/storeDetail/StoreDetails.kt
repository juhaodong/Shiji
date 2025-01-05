@file:OptIn(ExperimentalMaterial3Api::class)

package view.page.homePage.dataCenterPage.storeDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlusOne
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.SystemFontFamily
import androidx.compose.ui.unit.dp
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.raedghazal.kotlinx_datetime_ext.now
import domain.composable.basic.button.BaseIconButton
import domain.composable.basic.cards.BaseCardHeader
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.SmallSpacer
import domain.composable.basic.wrapper.NoContentProvider
import domain.user.IdentityVM
import domain.user.NutritionVM
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import modules.utils.FormatUtils.displayWithUnit
import modules.utils.dateOnly
import view.page.homePage.dataCenterPage.NutritionSummary
import view.page.homePage.dataCenterPage.storeDetail.commonReports.CommonReportItem
import view.page.homePage.dataCenterPage.storeDetail.commonReports.commonReports
import view.page.homePage.dataCenterPage.storeDetail.dashboard.DashboardCardList
import view.page.homePage.dataCenterPage.storeDetail.dashboard.TwoItemsPerRowGrid


@Composable
fun StoreDetails(identityVM: IdentityVM, nutritionVM: NutritionVM, toStatisticCenter: () -> Unit) {
    val pullRefreshState = rememberPullToRefreshState()
    var selectedDate by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    val store = identityVM.currentProfile
    val info = nutritionVM.info

    PullToRefreshBox(
        isRefreshing = nutritionVM.loading,
        state = pullRefreshState,
        modifier = Modifier.fillMaxSize(),
        onRefresh = {
            nutritionVM.showDataForIndex(selectedDate)
            scope.launch {
                pullRefreshState.animateToHidden()
            }
        }) {
        if (store != null && info != null) {
            val actual = info.actualNutrition
            val recommendation = info.nutritionRecommendation
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    .verticalScroll(state = rememberScrollState())
            ) {
                Column {
                    val totalMinus = store.currentWeight.toFloat() - store.targetWeight.toFloat()

                    val restDate = store.weightLossCycle - LocalDate.now()
                        .daysUntil(store.startDate.plus(store.weightLossCycle, DateTimeUnit.DAY))
                    val currentWeight =
                        store.currentWeight.toFloat() - restDate / store.weightLossCycle.toFloat() * totalMinus

                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(vertical = 8.dp),
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
                        Text(
                            "剩余${(store.weightLossCycle - restDate)}天",
                            style = MaterialTheme.typography.bodySmall
                        )
                        SmallSpacer()
                        IconButton(
                            onClick = { identityVM.toggleProfileDialog() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Apps,
                                contentDescription = null
                            )
                        }

                    }
                }

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
                        GrowSpacer()
                        Text(
                            recommendation.recommendedCalories.displayWithUnit("kcal"),
                            style = MaterialTheme.typography.bodyMedium,
                            color = LocalContentColor.current.copy(alpha = 0.6f)
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
                        color = if (over) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    )
                }
                SmallSpacer(16)
                BaseVCenterRow {
                    Column(modifier = Modifier.weight(1f)) {
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "上次用餐",
                            style = MaterialTheme.typography.bodySmall,
                            color = LocalContentColor.current.copy(alpha = 0.6f)
                        )
                        SmallSpacer(2)
                        BaseVCenterRow {
                            Text(
                                "两小时前😓",
                                style = MaterialTheme.typography.headlineSmall,
                            )
                            Text(
                                "(有点能吃",
                                style = MaterialTheme.typography.labelSmall,
                                color = LocalContentColor.current.copy(alpha = 0.6f)
                            )

                        }
                    }
                }





                SmallSpacer(48)
                Row(modifier = Modifier) {
                    BaseCardHeader("更多的详细数据", "继续浏览", noPadding = true, large = true) {
                        BaseIconButton(icon = Icons.Default.ArrowForward) {
                            toStatisticCenter()
                        }
                    }
                }
                SmallSpacer(16)

                NutritionSummary(
                    nutritionVM.info!!.nutritionRecommendation, nutritionVM.info!!.actualNutrition
                )

                SmallSpacer(16)


            }
        }

    }

    LaunchedEffect(true) {
        nutritionVM.showDataForIndex(0)
    }


}

