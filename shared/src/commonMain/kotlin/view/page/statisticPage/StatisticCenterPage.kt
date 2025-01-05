@file:OptIn(ExperimentalMaterial3Api::class)

package view.page.statisticPage

import LargeDashboardCardView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SyncLock
import androidx.compose.material.icons.rounded.Diversity3
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.raedghazal.kotlinx_datetime_ext.now
import domain.composable.basic.button.ActionLeftMainButton
import domain.composable.basic.cards.BaseCardHeader
import domain.composable.basic.cards.ShowAllButton
import domain.composable.basic.layout.BaseSurface
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.SmallSpacer
import domain.composable.basic.layout.CenterValueRow
import domain.composable.basic.wrapper.NoContentProvider
import domain.composable.chart.BarData
import domain.composable.chart.ChartDisplay
import domain.composable.dialog.basic.BeautifulDialog
import domain.dashboard.DishStatisticCard
import domain.user.IdentityVM
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import modules.utils.DateRangeMoveDirection
import modules.utils.FormatUtils.sumOfB
import modules.utils.FormatUtils.toPriceDisplay
import modules.utils.closingToday
import modules.utils.closingTodayRange
import modules.utils.dateOnly
import modules.utils.display
import modules.utils.easyDivide
import modules.utils.generateLast120Months
import modules.utils.generateYearsSince1970
import modules.utils.move
import modules.utils.toLocalDate
import modules.utils.toPercentageDisplay
import modules.utils.withFinanceDisplay

enum class StatisticType {
    Overall, Tax, Payment, Product
}

fun getLabelForStatisticType(statisticType: StatisticType): String {
    return when (statisticType) {
        StatisticType.Overall -> "营业统计"
        StatisticType.Tax -> "税务"
        StatisticType.Payment -> "收款"
        StatisticType.Product -> "产品"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticCenterPage(statisticVM: StatisticVM, identityVM: IdentityVM, backToHome: () -> Unit) {
    val tabs = StatisticType.entries.toTypedArray()
    var selectedTab by remember { mutableStateOf(tabs[0]) }
    LaunchedEffect(true) {
        statisticVM.confirmDateRange(closingTodayRange())
    }
    Scaffold() { innerPadding ->
        Column(
            modifier = Modifier.consumeWindowInsets(innerPadding).padding(innerPadding)
                .fillMaxSize(),
        ) {
            Box(
                Modifier.padding(4.dp).fillMaxWidth(),
            ) {
                IconButton(onClick = {
                    backToHome()
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }

                SmallSpacer()
                Text(
                    "营业报告",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )
            }
            Surface {
                PrimaryTabRow(selectedTabIndex = tabs.indexOf(selectedTab)) {
                    tabs.forEach {
                        Tab(selectedTab == it, onClick = { selectedTab = it }, text = {
                            Text(
                                getLabelForStatisticType(it),
                                style = MaterialTheme.typography.bodySmall
                            )
                        })
                    }
                }
            }

            PullToRefreshBox(
                modifier = Modifier.fillMaxWidth().weight(1f),
                isRefreshing = statisticVM.loading,
                state = rememberPullToRefreshState(),
                onRefresh = {
                    statisticVM.showDataAtDate()
                }) {

                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                ) {
                    Text(
                        "查询时间范围：" + statisticVM.currentDateRange.withFinanceDisplay(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    SmallSpacer(8)
                    if (!statisticVM.loading) {
                        when (selectedTab) {

                            StatisticType.Overall -> {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    BaseSurface(color = MaterialTheme.colorScheme.surfaceContainer) {
                                        Column {
                                            BaseCardHeader(
                                                "时段统计",
                                                "查看您最为繁忙的时段",
                                                icon = Icons.Default.SyncLock,
                                            )
                                            SmallSpacer()

                                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                                ChartDisplay(
                                                    modifier = Modifier.height(200.dp),
                                                    outData = statisticVM.hourlyReports
                                                        .map {
                                                            BarData(
                                                                it.total.floatValue(false),
                                                                it.hour.toString() + ":00"
                                                            )
                                                        })
                                            }


                                        }
                                    }
                                    statisticVM.dashboardData.forEach {
                                        LargeDashboardCardView(it)
                                    }
                                }
                            }

                            StatisticType.Tax -> {
                                BaseSurface(color = MaterialTheme.colorScheme.primary) {
                                    Column(modifier = Modifier.padding(4.dp).fillMaxWidth()) {
                                        Column(
                                            modifier = Modifier.padding(
                                                horizontal = 16.dp, vertical = 8.dp
                                            )
                                        ) {
                                            Text(
                                                text = "总收入",
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                            SmallSpacer()
                                            Text(
                                                text = statisticVM.totalIncome().toPriceDisplay(),
                                                style = MaterialTheme.typography.headlineMedium
                                            )
                                        }

                                        BaseSurface {
                                            Column(
                                                modifier = Modifier.padding(horizontal = 16.dp)
                                                    .padding(bottom = 8.dp),

                                                ) {
                                                statisticVM.taxInfoList.forEach {
                                                    if (it.taxName == "Total") {
                                                        SmallSpacer(16)
                                                        Column {
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.SpaceBetween
                                                            ) {
                                                                Text(
                                                                    text = it.taxRate.toPercentageDisplay() + "总计",
                                                                    style = MaterialTheme.typography.labelMedium
                                                                )
                                                                Text(
                                                                    text = "税前/税额",
                                                                    style = MaterialTheme.typography.labelMedium
                                                                )
                                                            }

                                                            SmallSpacer()

                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Text(
                                                                    text = it.total.toPriceDisplay(),
                                                                    fontWeight = FontWeight.Black
                                                                )
                                                                GrowSpacer()

                                                                Text(
                                                                    text = (it.total - it.tax).toPriceDisplay()
                                                                )
                                                                Text(
                                                                    text = "/" + it.tax.toPriceDisplay(),
                                                                    style = MaterialTheme.typography.bodyMedium
                                                                )
                                                            }
                                                        }
                                                    } else {
                                                        Column {
                                                            Text(
                                                                it.taxName,
                                                                style = MaterialTheme.typography.labelMedium
                                                            )
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.SpaceBetween
                                                            ) {
                                                                Text(
                                                                    text = it.total.toPriceDisplay(),
                                                                    style = MaterialTheme.typography.labelMedium
                                                                )
                                                                GrowSpacer()
                                                                Text(
                                                                    text = (it.total - it.tax).toPriceDisplay(),
                                                                    style = MaterialTheme.typography.labelMedium
                                                                )
                                                                Text(
                                                                    "/",
                                                                    style = MaterialTheme.typography.labelMedium
                                                                )
                                                                Text(
                                                                    text = it.tax.toPriceDisplay(),
                                                                    style = MaterialTheme.typography.labelMedium
                                                                )
                                                            }

                                                        }
                                                    }
                                                    SmallSpacer()

                                                }

                                            }
                                        }
                                    }

                                }
                                SmallSpacer(16)
                                BaseSurface(color = MaterialTheme.colorScheme.surfaceContainer) {
                                    Column(modifier = Modifier) {
                                        BaseCardHeader(
                                            title = "收入种类占比",
                                            subtitle = "堂食和外卖的收入占比",
                                            icon = Icons.Default.Book
                                        )
                                        SmallSpacer()
                                        val total = statisticVM.totalIncome()
                                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                            Row {
                                                Text(
                                                    "堂食/非堂食",
                                                    style = MaterialTheme.typography.labelMedium
                                                )
                                                GrowSpacer()
                                                Text(
                                                    "按已经结算的订单计算",
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                            val inHouseRatio =
                                                (statisticVM.inHouseTotalIncome()
                                                    .easyDivide(total)).floatValue(false)
                                            SmallSpacer()
                                            Row(
                                                modifier = Modifier.height(12.dp).fillMaxWidth()

                                                    .clip(
                                                        MaterialTheme.shapes.medium
                                                    ).clipToBounds()
                                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                            ) {
                                                Surface(color = MaterialTheme.colorScheme.primary) {
                                                    Box(
                                                        modifier = Modifier.fillMaxHeight()
                                                            .fillMaxWidth(inHouseRatio)
                                                    )
                                                }

                                            }
                                            SmallSpacer()
                                            Row(verticalAlignment = Alignment.Bottom) {
                                                Icon(
                                                    Icons.Default.Circle,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Text(
                                                    "堂食:" + inHouseRatio.toBigDecimal()
                                                        .toPercentageDisplay(),
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                                SmallSpacer()
                                                Icon(
                                                    Icons.Default.Circle,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primaryContainer,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Text(
                                                    "非堂食:" + (1 - inHouseRatio).toBigDecimal()
                                                        .toPercentageDisplay(),
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                            SmallSpacer(16)
                                            Row(verticalAlignment = Alignment.Bottom) {
                                                Text(
                                                    "总收入",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                GrowSpacer()
                                                Text(
                                                    total.toPriceDisplay(),
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                            SmallSpacer()
                                            Row(verticalAlignment = Alignment.Bottom) {
                                                Text(
                                                    "堂食",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                GrowSpacer()
                                                Text(
                                                    statisticVM.inHouseTotalIncome()
                                                        .toPriceDisplay(),
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                            SmallSpacer()
                                            Row(verticalAlignment = Alignment.Bottom) {
                                                Text(
                                                    "非堂食",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                GrowSpacer()
                                                Text(
                                                    statisticVM.takeawayTotalIncome()
                                                        .toPriceDisplay(),
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                            SmallSpacer(16)
                                        }
                                    }
                                }
                            }

                            StatisticType.Payment -> {
                                BaseSurface(color = MaterialTheme.colorScheme.surfaceContainer) {
                                    Column(modifier = Modifier) {
                                        BaseCardHeader(
                                            title = "现金情况",
                                            subtitle = "现金收入占比",
                                            icon = Icons.Default.Payment
                                        )
                                        SmallSpacer()
                                        val (cash, noCash) = statisticVM.paymentInfoList.partition { it.payMethodId == 1 }
                                        val (tip, normalNoCash) = noCash.partition { it.payMethodId == 9 }
                                        val total = statisticVM.paymentInfoList.sumOfB { it.total }
                                            .coerceAtLeast(1.toBigDecimal())
                                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                            Row {
                                                Text(
                                                    "现金/非现金",
                                                    style = MaterialTheme.typography.labelMedium
                                                )
                                                GrowSpacer()
                                                Text(
                                                    "按已经结算的订单计算",
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            }
                                            val cashRatio = (cash.sumOfB { it.total }.divide(
                                                total, decimalMode = DecimalMode(
                                                    decimalPrecision = 30,
                                                    roundingMode = RoundingMode.ROUND_HALF_TOWARDS_ZERO,
                                                    4
                                                )
                                            )).floatValue(false)
                                            SmallSpacer()
                                            Row(
                                                modifier = Modifier.height(12.dp).fillMaxWidth()

                                                    .clip(
                                                        MaterialTheme.shapes.medium
                                                    ).clipToBounds()
                                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                            ) {
                                                Surface(color = MaterialTheme.colorScheme.primary) {
                                                    Box(
                                                        modifier = Modifier.fillMaxHeight()
                                                            .fillMaxWidth(cashRatio)
                                                    )
                                                }

                                            }
                                            SmallSpacer()
                                            Row(verticalAlignment = Alignment.Bottom) {
                                                Icon(
                                                    Icons.Default.Circle,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Text(
                                                    "现金:" + cashRatio.toBigDecimal()
                                                        .toPercentageDisplay(),
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                                SmallSpacer()
                                                Icon(
                                                    Icons.Default.Circle,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primaryContainer,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Text(
                                                    "非现金:" + (1 - cashRatio).toBigDecimal()
                                                        .toPercentageDisplay(),
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                            SmallSpacer(16)
                                            Row(verticalAlignment = Alignment.Bottom) {
                                                Text(
                                                    "总收入",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                GrowSpacer()
                                                Text(
                                                    total.toPriceDisplay() + "(" + (normalNoCash + cash)
                                                    .sumOf { it.count } + ")",
                                                    style = MaterialTheme.typography.bodyMedium)
                                            }
                                            SmallSpacer()
                                            Row(verticalAlignment = Alignment.Bottom) {
                                                Text(
                                                    "现金",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                GrowSpacer()
                                                Text(cash.sumOfB { it.total }
                                                    .toPriceDisplay() + "(" + cash.sumOf { it.count } + ")",
                                                    style = MaterialTheme.typography.bodyMedium)
                                            }
                                            SmallSpacer()
                                            Row(verticalAlignment = Alignment.Bottom) {
                                                Text(
                                                    "非现金",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                GrowSpacer()
                                                Text(noCash.sumOfB { it.total }
                                                    .toPriceDisplay() + "(" + normalNoCash.sumOf { it.count } + ")",
                                                    style = MaterialTheme.typography.bodyMedium)
                                            }
                                            SmallSpacer(16)
                                        }
                                    }
                                }
                                SmallSpacer(16)
                                BaseSurface(color = MaterialTheme.colorScheme.surfaceContainer) {
                                    Column(modifier = Modifier) {
                                        BaseCardHeader(
                                            title = "收入统计",
                                            subtitle = "详细收入情况",
                                            icon = Icons.AutoMirrored.Filled.List
                                        )
                                        SmallSpacer()
                                        Column(
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            statisticVM.paymentInfoList.sortedByDescending { it.total }
                                                .forEach {
                                                    Row(verticalAlignment = Alignment.Bottom) {
                                                        Text(
                                                            it.name,
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                        GrowSpacer()
                                                        Text(
                                                            it.total.toPriceDisplay() + "(" + it.count + ")",
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                    }
                                                }

                                        }
                                        SmallSpacer(16)
                                    }
                                }
                                SmallSpacer(16)
                                BaseSurface(color = MaterialTheme.colorScheme.surfaceContainer) {
                                    Column(modifier = Modifier) {
                                        BaseCardHeader(
                                            title = "员工收入",
                                            subtitle = "每个员工的收入情况",
                                            icon = Icons.Rounded.Diversity3
                                        )
                                        SmallSpacer()
                                        NoContentProvider(statisticVM.servantInfoList.isNotEmpty()) {
                                            Column(
                                                modifier = Modifier.padding(horizontal = 16.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                statisticVM.servantInfoList.sortedByDescending { it.total }
                                                    .forEachIndexed { index, servantPayment ->
                                                        Row(verticalAlignment = Alignment.Bottom) {

                                                            Text(
                                                                (index + 1).toString(),
                                                                color = if (index < 3) MaterialTheme.colorScheme.primary else Color.Unspecified,
                                                                modifier = Modifier.alignByBaseline(),
                                                                fontWeight = FontWeight.Black
                                                            )
                                                            SmallSpacer(16)

                                                            Text(
                                                                servantPayment.name,
                                                                style = MaterialTheme.typography.bodyMedium
                                                            )
                                                            GrowSpacer()
                                                            Text(
                                                                servantPayment.total.toPriceDisplay() + "(" + servantPayment.orderCount + ")",
                                                                style = MaterialTheme.typography.bodyMedium
                                                            )
                                                        }
                                                    }

                                            }
                                        }

                                        SmallSpacer(16)
                                    }

                                }
                            }

                            StatisticType.Product -> {
                                var showAll by remember { mutableStateOf(false) }
                                var filter: Int? by remember { mutableStateOf(null) }
                                BaseSurface(color = MaterialTheme.colorScheme.primary) {
                                    CenterValueRow(
                                        values = listOf(
                                            "总销量" to
                                                    statisticVM.dishStatistics.sumOf { it.totalCount }
                                                        .toString(),
                                            "产品销量" to
                                                    statisticVM.dishStatistics.filter { it.categoryTypeId != 9 }
                                                        .sumOf { it.totalCount }.toString(),
                                            "酒水销量" to
                                                    statisticVM.dishStatistics.filter { it.categoryTypeId == 9 }
                                                        .sumOf { it.totalCount }.toString(),
                                        )
                                    )
                                }
                                SmallSpacer(16)
                                Text(
                                    "数据说明：同名产品已经被合并统计，只统计正常结账的订单中的产品。",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                SmallSpacer(16)
                                BaseSurface(color = MaterialTheme.colorScheme.surfaceContainer) {
                                    Column {
                                        BaseCardHeader(
                                            title = "畅销排行",
                                            icon = Icons.Default.Restaurant,
                                            subtitle = "产品名称/销售数据"
                                        )
                                        statisticVM.dishStatistics.take(5)
                                            .forEachIndexed { index, it ->
                                                DishStatisticCard(it, index)
                                            }
                                        ShowAllButton {
                                            filter = null
                                            showAll = true
                                        }
                                    }
                                }
                                SmallSpacer(16)
                                BaseSurface(color = MaterialTheme.colorScheme.surfaceContainer) {
                                    Column {
                                        BaseCardHeader(
                                            title = "产品排行",
                                            icon = Icons.Default.Restaurant,
                                            subtitle = "产品名称/销售数据"
                                        )
                                        statisticVM.dishStatistics.filter { it.categoryTypeId != 9 }
                                            .take(5)
                                            .forEachIndexed { index, it ->
                                                DishStatisticCard(it, index)
                                            }
                                        ShowAllButton {
                                            filter = 10
                                            showAll = true
                                        }
                                    }
                                }
                                SmallSpacer(16)
                                BaseSurface(color = MaterialTheme.colorScheme.surfaceContainer) {
                                    Column {
                                        BaseCardHeader(
                                            title = "酒水",
                                            icon = Icons.Default.Restaurant,
                                            subtitle = "产品名称/销售数据"
                                        )
                                        statisticVM.dishStatistics.filter { it.categoryTypeId == 9 }
                                            .take(5)
                                            .forEachIndexed { index, it ->
                                                DishStatisticCard(it, index)
                                            }
                                        ShowAllButton {
                                            filter = 9
                                            showAll = true
                                        }
                                    }
                                }
                                SmallSpacer(16)
                                BaseSurface(color = MaterialTheme.colorScheme.surfaceContainer) {
                                    Column(modifier = Modifier.padding(4.dp).fillMaxWidth()) {
                                        Column(
                                            modifier = Modifier.padding(
                                                horizontal = 16.dp, vertical = 8.dp
                                            )
                                        ) {
                                            Text(
                                                text = "未售出产品数量",
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                            SmallSpacer()
                                            Text(
                                                text = statisticVM.dishStatistics.filter { it.totalCount == 0 }.size.toString(),
                                                style = MaterialTheme.typography.headlineMedium
                                            )
                                        }

                                        BaseSurface {
                                            Column(
                                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                NoContentProvider(statisticVM.dishStatistics.filter { it.totalCount == 0 }
                                                    .isNotEmpty()) {
                                                    statisticVM.dishStatistics.filter { it.totalCount == 0 }
                                                        .forEach { dishStatisticModel ->
                                                            DishStatisticCard(
                                                                dishStatisticModel,
                                                            )
                                                        }
                                                }


                                            }
                                        }
                                    }

                                }
                                BeautifulDialog(
                                    show = showAll,
                                    onDismissRequest = { showAll = false },
                                    noPadding = true,
                                    useCloseButton = true
                                ) {
                                    BaseCardHeader(
                                        title = "畅销产品排行",
                                        icon = Icons.Default.Restaurant,
                                        subtitle = "产品名称/销售数据"
                                    )
                                    LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                                        itemsIndexed(
                                            statisticVM.dishStatistics.filter {
                                                filter == null || if (filter == 9) it.categoryTypeId == 9 else it.categoryTypeId != 9
                                            },
                                            key = { index, item -> item.dishId }) { index, item ->
                                            DishStatisticCard(item, index)
                                        }
                                    }
                                    Row(modifier = Modifier.padding(16.dp)) {
                                        ActionLeftMainButton(
                                            text = "返回", icon = Icons.Default.Close
                                        ) {
                                            showAll = false
                                        }
                                    }
                                }
                            }
                        }
                    }


                }


            }
            Surface(color = MaterialTheme.colorScheme.surfaceContainerLow) {
                Row(
                    modifier = Modifier.clickable { statisticVM.showDateDialog = true }
                        .padding(vertical = 12.dp, horizontal = 16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("统计范围", style = MaterialTheme.typography.labelMedium)
                        SmallSpacer(4)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                statisticVM.currentDateRange.display(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    GrowSpacer()

                    BaseSurface(
                        onClick = {
                            statisticVM.confirmDateRange(
                                statisticVM.currentDateRange.move(
                                    DateRangeMoveDirection.Backward
                                )
                            )

                        }, color = MaterialTheme.colorScheme.secondaryContainer

                    ) {

                        Box(modifier = Modifier.padding(8.dp)) {
                            Icon(
                                Icons.Default.ChevronLeft,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.secondary

                            )
                        }
                    }
                    SmallSpacer()

                    BaseSurface(
                        onClick = {
                            statisticVM.confirmDateRange(
                                statisticVM.currentDateRange.move(
                                    DateRangeMoveDirection.Forward
                                )
                            )
                        },
                        enabled = statisticVM.currentDateRange.second != closingToday(),
                        color = if (statisticVM.currentDateRange.second != closingToday()) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.04f
                        )
                    ) {
                        Box(modifier = Modifier.padding(8.dp)) {
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = if (statisticVM.currentDateRange.second != closingToday()) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.12f
                                )
                            )
                        }

                    }

                }
            }

            BeautifulDialog(
                show = statisticVM.showDateDialog,
                onDismissRequest = { statisticVM.showDateDialog = false },
                useCloseButton = false,
                noPadding = true
            ) {
                val timeOption = listOf("日", "月", "年", "其他")
                var selectTab by remember { mutableStateOf(timeOption[0]) }
                var dateRange by remember { mutableStateOf(closingTodayRange()) }
                LaunchedEffect(true) {
                    dateRange = closingTodayRange()
                }
                PrimaryTabRow(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    selectedTabIndex = timeOption.indexOf(selectTab)
                ) {
                    timeOption.forEach { s ->
                        Tab(
                            s == selectTab,
                            onClick = { selectTab = s },
                            text = { Text(s, style = MaterialTheme.typography.bodyMedium) })
                    }
                }
                when (selectTab) {
                    "日" -> {
                        val datePickerState =
                            rememberDatePickerState(
                                initialSelectedDateMillis = Clock.System.now()
                                    .toEpochMilliseconds(),
                                selectableDates = object : SelectableDates {
                                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                                        return Instant.fromEpochMilliseconds(utcTimeMillis)
                                            .toLocalDateTime(
                                                TimeZone.currentSystemDefault()
                                            ) <= LocalDateTime.now()
                                    }
                                })
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh
                        ) { SmallSpacer(16) }

                        DatePicker(
                            state = datePickerState, showModeToggle = false, title = null
                        )
                        LaunchedEffect(datePickerState.selectedDateMillis) {
                            val date = datePickerState.selectedDateMillis.toLocalDate()
                            if (date != null) {
                                dateRange = date to date
                            }
                        }
                    }

                    "月" -> {
                        val monthList = generateLast120Months()
                        var selectedDateRange by remember { mutableStateOf(monthList[0]) }
                        LaunchedEffect(true) {
                            selectedDateRange = monthList[0]
                        }
                        LazyColumn(
                            modifier = Modifier.height(400.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
                        ) {
                            items(monthList) {
                                Surface(
                                    shape = MaterialTheme.shapes.medium,
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        selectedDateRange = it
                                    },
                                    color = if (selectedDateRange == it) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        Text(
                                            it.display()
                                        )
                                        GrowSpacer()
                                        Text(
                                            it.first.dateOnly() + " - " + it.second.dateOnly(),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
                        LaunchedEffect(selectedDateRange) {
                            dateRange = selectedDateRange
                        }

                    }

                    "年" -> {
                        val yearList = generateYearsSince1970()
                        var selectedDateRange by remember { mutableStateOf(yearList[0]) }
                        LaunchedEffect(true) {
                            selectedDateRange = yearList[0]
                        }
                        LazyColumn(
                            modifier = Modifier.height(400.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
                        ) {
                            items(yearList) {
                                Surface(
                                    shape = MaterialTheme.shapes.medium,
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        selectedDateRange = it
                                    },
                                    color = if (selectedDateRange == it) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface

                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        Text(
                                            it.display()
                                        )
                                        GrowSpacer()
                                        Text(
                                            it.first.dateOnly() + " - " + it.second.dateOnly(),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
                        LaunchedEffect(selectedDateRange) {
                            dateRange = selectedDateRange
                        }
                    }

                    "其他" -> {
                        val datePickerState = rememberDateRangePickerState(
                            initialSelectedStartDateMillis = Clock.System.now()
                                .toEpochMilliseconds(),
                            initialSelectedEndDateMillis = Clock.System.now().toEpochMilliseconds(),
                            selectableDates = object : SelectableDates {
                                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                                    return Instant.fromEpochMilliseconds(utcTimeMillis)
                                        .toLocalDateTime(
                                            TimeZone.currentSystemDefault()
                                        ) <= LocalDateTime.now()
                                }
                            })
                        DateRangePicker(
                            state = datePickerState,
                            showModeToggle = false,
                            title = {},
                            modifier = Modifier.weight(1f)
                        )
                        LaunchedEffect(datePickerState.selectedStartDateMillis to datePickerState.selectedEndDateMillis) {

                            val dateStart = datePickerState.selectedStartDateMillis.toLocalDate()
                            val dateEnd = datePickerState.selectedEndDateMillis.toLocalDate()
                            if (dateStart != null && dateEnd != null) {
                                dateRange = dateStart to dateEnd
                            }

                        }
                    }
                }

                Row(modifier = Modifier.padding(16.dp)) {
                    ActionLeftMainButton(text = "确认", icon = Icons.Default.Done) {
                        statisticVM.confirmDateRange(dateRange)
                    }
                }


            }
        }
    }

}