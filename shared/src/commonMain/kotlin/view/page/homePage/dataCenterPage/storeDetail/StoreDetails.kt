@file:OptIn(ExperimentalMaterial3Api::class)

package view.page.homePage.dataCenterPage.storeDetail

import ChangePercentageDisplay
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import domain.composable.basic.button.BaseIconButton
import domain.composable.basic.cards.BaseCardHeader
import domain.composable.basic.cards.BaseContentCard
import domain.composable.basic.cards.NoBackgroundContentCard
import domain.composable.basic.layout.BaseSurface
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.SmallSpacer
import domain.composable.basic.layout.pa
import domain.composable.basic.layout.px
import domain.composable.basic.layout.py
import domain.composable.basic.wrapper.NoContentProvider
import domain.composable.chart.BarData
import domain.composable.chart.ChartDisplay
import domain.composable.chart.ChartType
import domain.user.IdentityVM
import domain.user.StoreVM
import kotlinx.coroutines.launch
import modules.utils.FormatUtils.sumOfB
import modules.utils.FormatUtils.toPriceDisplay
import modules.utils.toPercentageDisplay
import qrgenerator.qrkitpainter.text
import view.page.homePage.dataCenterPage.storeDetail.commonReports.CommonReportItem
import view.page.homePage.dataCenterPage.storeDetail.commonReports.commonReports
import view.page.homePage.dataCenterPage.storeDetail.dashboard.DashboardCardList
import view.page.homePage.dataCenterPage.storeDetail.dashboard.DashboardCardType
import view.page.homePage.dataCenterPage.storeDetail.dashboard.TableStatusGrid
import view.page.homePage.dataCenterPage.storeDetail.dashboard.TwoItemsPerRowGrid


@Composable
fun StoreDetails(identityVM: IdentityVM, storeVM: StoreVM, toStatisticCenter: () -> Unit) {
    val pullRefreshState = rememberPullToRefreshState()
    var selectedDate by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    val store = identityVM.currentStore

    PullToRefreshBox(
        isRefreshing = storeVM.loading,
        state = pullRefreshState,
        modifier = Modifier.fillMaxSize(),
        onRefresh = {
            storeVM.showDataForIndex(selectedDate)
            scope.launch {
                pullRefreshState.animateToHidden()
            }
        }) {
        if (store != null) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    .verticalScroll(state = rememberScrollState())
            ) {
                SmallSpacer(36)
                val unpaid = storeVM.tableList.sumOfB {
                    it.totalPrice ?: BigDecimal.ZERO
                }
                val sales =
                    (storeVM.dashboardData.find { card -> card.type == DashboardCardType.TOTAL_REVENUE }?.currentValue
                        ?: BigDecimal.ZERO) + (if (selectedDate == 0) unpaid else BigDecimal.ZERO)

                val text = if (selectedDate == 0) "预期营业额" else "营业额"
                Column() {
                    BaseVCenterRow(modifier = Modifier.pa(8).clickable {
                        toStatisticCenter()
                    }) {
                        Column(modifier = Modifier) {
                            BaseVCenterRow {
                                Text(
                                    text = text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = LocalContentColor.current.copy(alpha = 0.6f)
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.Bottom
                            ) {
                                if (storeVM.loading) {
                                    Text(
                                        "...", style = MaterialTheme.typography.headlineLarge
                                    )

                                } else {
                                    Text(
                                        text = sales.toPriceDisplay(),
                                        style = MaterialTheme.typography.headlineLarge
                                    )
                                }
                            }
                            SmallSpacer()
                            val sales =
                                (storeVM.dashboardData.find { card -> card.type == DashboardCardType.TOTAL_REVENUE }?.previousPeriodChange
                                    ?: BigDecimal.ZERO)
                            ChangePercentageDisplay(
                                sales,
                                contentColor = LocalContentColor.current
                            )

                        }
                        GrowSpacer()
                        val dataOptions = listOf("实时数据", "昨日数据")
                        val selectedData = dataOptions[selectedDate]
                        BaseSurface(onClick = {
                            selectedDate = (selectedDate + 1) % dataOptions.size
                        }, color = Color.Transparent) {
                            BaseVCenterRow(modifier = Modifier.px(4).py(8)) {
                                SmallSpacer(4)
                                Text(selectedData, style = MaterialTheme.typography.bodySmall)
                                SmallSpacer(2)
                                Icon(imageVector = Icons.Default.ArrowDropDown, null)
                            }
                        }
                    }
                    val list =
                        storeVM.dashboardData.find { card -> card.type == DashboardCardType.TOTAL_REVENUE }?.changesInTime?.map { dTO ->
                            BarData(
                                dTO.currentValue.floatValue(false), dTO.getLabel()
                            )
                        }
                    if (list != null) {
                        ChartDisplay(
                            modifier = Modifier.fillMaxWidth().aspectRatio(16 / 9f),
                            outData = list,
                            chartType = ChartType.Line,
                            sparkChart = true
                        )

                    }

                }
                if (selectedDate == 0 && storeVM.tableList.isNotEmpty() && store.ngrokOnline && storeVM.tableList.size != 10) {
                    SmallSpacer(48)
                    NoBackgroundContentCard(
                        title = "店内实时营业情况",
                        subtitle = "此时此刻",
                    ) {
                        NoContentProvider(
                            storeVM.tableList.isNotEmpty() && store.ngrokOnline, minHeight = 100
                        ) {
                            Column(modifier = Modifier) {
                                TableStatusGrid(
                                    tables = storeVM.tableList.filter { it.sectionId != 6 },
                                    takeawayOrders = storeVM.tableList.filter { it.sectionId == 6 }.size
                                )
                            }
                            SmallSpacer(16)
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("待收款", style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        storeVM.tableList.sumOfB {
                                            it.totalPrice ?: BigDecimal.ZERO
                                        }.toPriceDisplay(),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Column {
                                    Text(
                                        "活跃订单数", style = MaterialTheme.typography.labelSmall
                                    )
                                    Text(
                                        storeVM.tableList.filter { it.usageStatus == 1 }.size.toString(),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Column {
                                    Text(
                                        "实时开台率", style = MaterialTheme.typography.labelSmall
                                    )
                                    Text(
                                        storeVM.tableList.filter { it.usageStatus == 1 && it.sectionId != 6 }.size.toBigDecimal()
                                            .divide(
                                                storeVM.tableList.filter { it.sectionId != 6 }.size.toBigDecimal()
                                                    .coerceAtLeast(
                                                        BigDecimal.ONE
                                                    ), DecimalMode.US_CURRENCY
                                            ).toPercentageDisplay(),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                        }
                    }
                }


                SmallSpacer(48)
                Row(modifier = Modifier.padding(start = 8.dp)) {
                    BaseCardHeader("更多的详细数据", "继续浏览", noPadding = true, large = true) {
                        BaseIconButton(icon = Icons.Default.ArrowForward) {
                            toStatisticCenter()
                        }
                    }
                }
                SmallSpacer()
                NoContentProvider(storeVM.dashboardData.isNotEmpty()) {
                    DashboardCardList(storeVM.dashboardData)
                }
                SmallSpacer(16)

                LaunchedEffect(storeVM.dashboardData) {
                    if (!storeVM.dashboardData.isEmpty()) {
                        if (storeVM.dashboardData.first().currentValue == BigDecimal.ZERO) {
                            selectedDate = 1
                        }
                    }

                }


            }
            LaunchedEffect(store.deviceId to selectedDate) {
                storeVM.showDataForIndex(selectedDate)
            }
        }

    }

}

@Composable
private fun CommonReportSection() {
    Surface(
        modifier = Modifier.padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 3.dp
    ) {
        Column(modifier = Modifier) {
            BaseCardHeader("常用报表", "查看常用报表", Icons.Filled.Report)
            TwoItemsPerRowGrid(items = commonReports) { report ->
                CommonReportItem(report = report) {}
            }
            SmallSpacer()
        }
    }
}

