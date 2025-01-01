import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.S
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.materialkolor.ktx.harmonize
import domain.composable.basic.button.ActionLeftMainButton
import domain.composable.basic.cards.LabelText
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.GrowSpacer
import domain.composable.basic.layout.SmallSpacer
import domain.composable.chart.BarData
import domain.composable.chart.ChartDisplay
import domain.composable.dialog.basic.BeautifulDialog
import theme.successColor
import view.page.homePage.dataCenterPage.storeDetail.dashboard.DashboardCard
import view.page.homePage.dataCenterPage.storeDetail.dashboard.DashboardCardType
import view.page.homePage.dataCenterPage.storeDetail.dashboard.formatDashboardCardValue


@Composable
fun ChangeArrow(value: BigDecimal, contentColor: Color) {
    val changeColor = if (value >= BigDecimal.ZERO) {
        successColor(contentColor)
    } else {
        MaterialTheme.colorScheme.error.harmonize(contentColor)
    }
    Box(
        modifier = Modifier.size(18.dp) // Adjust size as needed
            .background(
                changeColor.copy(alpha = 0.2f), CircleShape
            ) // Circle background
            .clip(CircleShape), // Clip to circle shape
        contentAlignment = Alignment.Center // Center the icon
    ) {
        Icon(
            imageVector = if (value >= BigDecimal.ZERO) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
            contentDescription = null,
            tint = changeColor, // Use the same color for the icon
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
fun ChangePercentageDisplay(percentageValue: BigDecimal, contentColor: Color, label: String = "") {
    val changeColor = if (percentageValue >= BigDecimal.ZERO) {
        successColor(contentColor)
    } else {
        MaterialTheme.colorScheme.error.harmonize(contentColor)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (label.isNotBlank()) {
            Text(label, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
            SmallSpacer(4)
        }
        ChangeArrow(percentageValue, contentColor)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "${percentageValue.toPlainString()}%",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = changeColor
        )
    }
}

@Composable
fun DashboardCardView(dashboardCard: DashboardCard) {
    var showChart by remember { mutableStateOf(false) }
    val cardColor = Color.Transparent

    val contentColor = contentColorFor(cardColor)
    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
        color = cardColor,
        onClick = {
            showChart = true
        }) {
        BaseVCenterRow(
            modifier = Modifier.padding(8.dp, vertical = 8.dp).fillMaxWidth(),
        ) {
            Column() {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = dashboardCard.name,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Normal,
                    )
                    GrowSpacer()
                    if (dashboardCard.description.isNotEmpty()) {

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = "Help",
                            modifier = Modifier.size(12.dp)
                        )

                    }
                }
                SmallSpacer(4)
                val valueText =
                    formatDashboardCardValue(dashboardCard.currentValue, dashboardCard.type)
                Text(
                    text = valueText,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black
                )
                SmallSpacer(4)
                (dashboardCard.lastWeekChange
                    ?: dashboardCard.previousPeriodChange)?.let { change ->
                    ChangePercentageDisplay(change, contentColor)
                }
            }


        }
    }
    BeautifulDialog(showChart, onDismissRequest = {
        showChart = false
    }) {
        DetailDashboardCardView(dashboardCard, 200)
        SmallSpacer()

        ActionLeftMainButton(text = "关闭", color = MaterialTheme.colorScheme.secondaryContainer) {
            showChart = false
        }


    }
}

@Composable
fun LargeDashboardCardView(dashboardCard: DashboardCard, chartHeight: Int = 100) {
    val cardColor = MaterialTheme.colorScheme.surfaceContainer
    val contentColor = contentColorFor(cardColor)
    Surface(
        shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth(), color = cardColor
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = dashboardCard.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Normal,
                    )
                    Text(
                        text = dashboardCard.description,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Normal,
                    )

                }
                SmallSpacer()
                if (dashboardCard.lastWeekChange == null) {
                    dashboardCard.previousPeriodChange?.let { change ->
                        ChangePercentageDisplay(
                            change,
                            LocalContentColor.current,
                        )
                    }
                }

            }
            SmallSpacer(4)
            val valueText = formatDashboardCardValue(dashboardCard.currentValue, dashboardCard.type)
            if (dashboardCard.lastWeekValue == null) {

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = valueText,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        "/" + dashboardCard.previousPeriodValue?.let {
                            formatDashboardCardValue(
                                it, dashboardCard.type
                            )
                        },
                        style = MaterialTheme.typography.titleSmall,
                        color = contentColor.copy(alpha = 0.6f)
                    )
                }

            } else {
                Row() {
                    Text(
                        text = valueText,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                SmallSpacer(4)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "上周：" + dashboardCard.lastWeekValue.let {
                            formatDashboardCardValue(
                                it, dashboardCard.type
                            )
                        },
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Normal
                    )
                    dashboardCard.lastWeekChange?.let { change ->
                        GrowSpacer()
                        ChangePercentageDisplay(change, LocalContentColor.current)
                    }
                }
                SmallSpacer(2)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "昨日：" + dashboardCard.previousPeriodValue?.let {
                            formatDashboardCardValue(
                                it, dashboardCard.type
                            )
                        },
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Normal
                    )
                    dashboardCard.previousPeriodChange?.let { change ->
                        GrowSpacer()
                        ChangePercentageDisplay(change, LocalContentColor.current)
                    }
                }
            }

            SmallSpacer(4)
            if (dashboardCard.changesInTime.size > 4) {
                ChartDisplay(
                    modifier = Modifier.fillMaxWidth().height(chartHeight.dp),
                    outData = dashboardCard.changesInTime.map {
                        BarData(
                            it.currentValue.floatValue(false), it.getLabel()
                        )
                    }.sortedBy { it.label },
                    sparkChart = true,
                )
            }


        }
    }
}

@Composable
fun DetailDashboardCardView(dashboardCard: DashboardCard, chartHeight: Int = 150) {

    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = dashboardCard.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Normal,
                    )
                    Text(
                        text = dashboardCard.description,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Normal,
                    )

                }
                SmallSpacer()
            }
            SmallSpacer()
            val valueText = formatDashboardCardValue(dashboardCard.currentValue, dashboardCard.type)
            Row() {
                Text(
                    text = valueText,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            SmallSpacer(4)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "上周：" + dashboardCard.lastWeekValue?.let {
                        formatDashboardCardValue(
                            it, dashboardCard.type
                        )
                    },
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Normal
                )
                dashboardCard.lastWeekChange?.let { change ->
                    GrowSpacer()
                    ChangePercentageDisplay(change, LocalContentColor.current)
                }
            }
            SmallSpacer(2)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "昨日：" + dashboardCard.previousPeriodValue?.let {
                        formatDashboardCardValue(
                            it, dashboardCard.type
                        )
                    },
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Normal
                )
                dashboardCard.previousPeriodChange?.let { change ->
                    GrowSpacer()
                    ChangePercentageDisplay(change, LocalContentColor.current)
                }
            }



            SmallSpacer(32)
            if (dashboardCard.changesInTime.size > 4) {
                ChartDisplay(
                    modifier = Modifier.fillMaxWidth().height(chartHeight.dp),
                    outData = dashboardCard.changesInTime.map {
                        BarData(
                            it.currentValue.floatValue(false), it.getLabel()
                        )
                    }.sortedBy { it.label },
                    sparkChart = true,
                )
            }


        }
    }
}