package domain.composable.chart


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.materialkolor.ktx.darken
import com.materialkolor.ktx.lighten
import domain.composable.basic.wrapper.NoContentProvider


class BarData(val value: Float, val label: String)

private fun getBarIndexAtOffset(data: List<BarData>, canvasSize: Size, offset: Offset): Int? {
    val availableWidth = canvasSize.width
    val totalBarsWidth = availableWidth * 0.8f
    val barWidth = totalBarsWidth / data.size
    val barSpacing = (availableWidth - totalBarsWidth) / (data.size + 1)

    data.forEachIndexed { index, _ ->
        val x = barSpacing + index * (barWidth + barSpacing)
        val barRect = Rect(x, 0f, x + barWidth, canvasSize.height)

        if (barRect.contains(offset)) {
            return index
        }
    }

    return null
}

enum class ChartType {
    Bar,
    Line
}

@Composable
fun ChartDisplay(
    modifier: Modifier = Modifier,
    outData: List<BarData>,
    color: Color = MaterialTheme.colorScheme.primary,
    xLabel: String = "日期",
    sparkChart: Boolean = false,
    chartType: ChartType? = null
) {
    val hoverColor by remember {
        derivedStateOf {
            color.copy(alpha = 0.2f)
        }
    }
    var realChartType by remember { mutableStateOf(chartType ?: ChartType.Bar) }
    val data = outData.map { BarData(it.value.coerceAtLeast(0f), it.label) }

    LaunchedEffect(chartType) {
        realChartType = chartType ?: ChartType.Bar
    }

    val textMeasurer = rememberTextMeasurer()
    var currentTouchingX by remember { mutableStateOf<Float?>(null) }
    val interactionSource = remember { MutableInteractionSource() }
    val contentColor = MaterialTheme.colorScheme.onSurface
    val surfaceColor = MaterialTheme.colorScheme.surfaceContainer
    val neutralColor = MaterialTheme.colorScheme.surfaceContainerHighest
    val inverseContentColor = MaterialTheme.colorScheme.inverseOnSurface
    val hapticFeedback = LocalHapticFeedback.current
    var lastIndex by remember { mutableStateOf(-1) }
    fun vibWithIndex(index: Int) {
        if (index != lastIndex) {
            lastIndex = index
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)

        }
    }

    val highLightColor = MaterialTheme.colorScheme.inversePrimary
    Box(modifier = modifier) {
        NoContentProvider(data.isNotEmpty()) {
            Canvas(modifier = Modifier.fillMaxSize().pointerInput(interactionSource) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        currentTouchingX = event.changes.first().position.x

                        if (event.type == PointerEventType.Release) {
                            currentTouchingX = null
                        }
                    }
                }
            })
            {
                inset(16f) {
                    val availableWidth = size.width
                    val totalBarsWidth = availableWidth * 0.8f // Use 80% of width for bars
                    val barWidth = totalBarsWidth / data.size
                    val barSpacing =
                        (availableWidth - totalBarsWidth) / (data.size + 1) // Distribute remaining space for spacing

                    val maxValue = data.maxOf { it.value }.coerceAtLeast(10f)

                    val labelIndices =
                        calculateLabelIndices(data, size, textMeasurer, barWidth, barSpacing)
                    val availableHeight = if (sparkChart) size.height else size.height - 64f
                    val chartHeightFactor = 0.9f

                    when (realChartType) {
                        ChartType.Bar -> {
                            // Draw bars
                            data.forEachIndexed { index, item ->
                                val barHeight =
                                    ((item.value / maxValue) * availableHeight * chartHeightFactor).coerceAtLeast(
                                        8.0f
                                    )
                                val x =
                                    barSpacing + index * (barWidth + barSpacing) // Start with spacing
                                val y = availableHeight - barHeight

                                drawRoundRect(
                                    color = if (item.value == maxValue) highLightColor else if (barHeight == 8f) neutralColor else color.copy(
                                        alpha = 0.8f
                                    ),
                                    topLeft = Offset(x, y),
                                    size = Size(barWidth, barHeight),
                                    cornerRadius = CornerRadius(4f, 4f)
                                )
                            }
                        }

                        ChartType.Line -> {
                            // Draw line chart
                            // Draw line chart
                            val points = data.mapIndexed { index, item ->
                                var x = barSpacing + index * (barWidth + barSpacing) + barWidth / 2
                                if (index == 0) {
                                    x = 0f
                                } else if (index == data.lastIndex) {
                                    x = availableWidth
                                }

                                val y =
                                    availableHeight - (item.value / maxValue) * availableHeight * chartHeightFactor
                                Offset(x, y)
                            }
                            val path = Path().apply {
                                moveTo(points.first().x, points.first().y)
                                points.drop(1).forEach {
                                    lineTo(it.x, it.y)
                                }
                            }

                            // Apply rounded corner effect
                            val cornerRadius = 28f // Adjust the radius for desired smoothness
                            val roundedPathEffect = PathEffect.cornerPathEffect(cornerRadius)

                            drawPath(
                                path = path,
                                color = color.copy(alpha = 0.8f),
                                style = Stroke(width = 10f, pathEffect = roundedPathEffect)
                            )
                            // Create gradient path
                            val gradientPath = Path().apply {
                                moveTo(points.first().x, points.first().y)
                                points.drop(1).forEach {
                                    lineTo(it.x, it.y)
                                }
                                lineTo(points.last().x, availableHeight) // Bottom right corner
                                lineTo(points.first().x, availableHeight) // Bottom left corner
                                close() // Close the path
                            }
                            points.forEach { point ->
                                drawCircle(
                                    color = color,
                                    radius = 8f, // Adjust radius as needed
                                    center = point
                                )
                            }
                            // Draw gradient fill
                            drawPath(
                                path = gradientPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(color.copy(alpha = 0.15f), Color.Transparent),
                                    startY = 0f,
                                    endY = availableHeight
                                )
                            )
                        }
                    }



                    currentTouchingX?.let {

                        val barIndex = getBarIndexAtOffset(data, size, Offset(it, 0f))
                        if (barIndex != null) {
                            vibWithIndex(barIndex)
                            val x = barSpacing + barIndex * (barWidth + barSpacing)
                            drawRect(
                                color = hoverColor,
                                topLeft = Offset(x, 0f),
                                size = Size(barWidth * 1.2f, size.height)
                            )
                            val dateText = xLabel + "：" + data[barIndex].label
                            val valueText = "数值：" + data[barIndex].value.toString()
                            val dateTextLayoutResult = textMeasurer.measure(
                                text = AnnotatedString(dateText), style = TextStyle(
                                    color = contentColor,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Black
                                )
                            )
                            val valueTextLayoutResult = textMeasurer.measure(
                                text = AnnotatedString(valueText), style = TextStyle(
                                    color = contentColor,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Black
                                )
                            )
                            val tooltipWidth =
                                maxOf(
                                    dateTextLayoutResult.size.width,
                                    valueTextLayoutResult.size.width
                                ) + 32f
                            val tooltipHeight =
                                dateTextLayoutResult.size.height + valueTextLayoutResult.size.height + 32f
                            val tooltipX =
                                (x + barWidth / 2 - tooltipWidth / 2).coerceAtLeast(0f)
                                    .coerceAtMost(
                                        size.width - tooltipWidth
                                    )
                            val tooltipY = size.height * 0.3f - tooltipHeight / 2

                            // Draw tooltip background
                            drawRoundRect(
                                color = surfaceColor.copy(alpha = 0.8f),
                                topLeft = Offset(tooltipX, tooltipY),
                                size = Size(tooltipWidth, tooltipHeight),
                                cornerRadius = CornerRadius(8f, 8f)
                            )

                            // Draw tooltip text
                            drawText(
                                textLayoutResult = dateTextLayoutResult, topLeft = Offset(
                                    tooltipX + 16f,
                                    tooltipY + 8f
                                )
                            )
                            drawText(
                                textLayoutResult = valueTextLayoutResult, topLeft = Offset(
                                    tooltipX + 16f,
                                    tooltipY + 8f + dateTextLayoutResult.size.height
                                )
                            )

                        }
                    }

                    if (!sparkChart) {
                        labelIndices
                            .forEach { index ->
                                val item = data[index]
                                val x =
                                    barSpacing + index * (barWidth + barSpacing) // Start with spacing

                                val textLayoutResult = textMeasurer.measure(
                                    text = AnnotatedString(item.label.toString()),
                                    style = TextStyle(
                                        color = contentColor,
                                        fontSize = 8.sp,
                                        textAlign = TextAlign.Center
                                    ),
                                )
                                val labelX = when (index) {
                                    0 -> barSpacing // Align first label with left boundary
                                    data.lastIndex -> size.width - barSpacing - textLayoutResult.size.width // Align last label with right boundary
                                    else -> x + barWidth / 2 - textLayoutResult.size.width / 2 // Center other labels below bars
                                }
                                drawText(
                                    textLayoutResult = textLayoutResult,
                                    topLeft = Offset(
                                        labelX,
                                        availableHeight + 16f
                                    ) // Label position below the bar
                                )

                            }
                        val gridLineValues = calculateGridLineValues(maxValue)
                        gridLineValues.forEach { value ->
                            val y =
                                availableHeight - (value / maxValue) * availableHeight * chartHeightFactor
                            val labelText = value.toString()
                            val textLayoutResult = textMeasurer.measure(
                                text = AnnotatedString(labelText), style = TextStyle(
                                    color = contentColor,
                                    fontSize = 8.sp,
                                    textAlign = TextAlign.Left,
                                    fontWeight = FontWeight.Black
                                )
                            )
                            // Draw dashed line
                            if (value > 0.0) {
                                val dashPathEffect =
                                    PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                drawLine(
                                    color = contentColor.lighten(2.5f),
                                    start = Offset(textLayoutResult.size.width.toFloat(), y),
                                    end = Offset(size.width, y),
                                    pathEffect = dashPathEffect
                                )
                            } else {
                                drawLine(
                                    color = contentColor.lighten(2.5f),
                                    start = Offset(textLayoutResult.size.width.toFloat(), y),
                                    end = Offset(size.width, y),
                                )
                            }



                            drawText(
                                textLayoutResult = textLayoutResult,
                                topLeft = Offset(0f, y - textLayoutResult.size.height / 2),
                                shadow = Shadow(
                                    color = inverseContentColor.copy(alpha = 0.9f), // Shadow color
                                    offset = Offset(1f, 1f), // Shadow offset
                                    blurRadius = 2f // Shadow blur radius
                                )
                            )
                        }
                    }
                    // Draw grid lines and labels

                }
            }
            if (chartType == null) {
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                    FilledTonalIconButton(
                        onClick = {
                            realChartType =
                                if (realChartType == ChartType.Line) ChartType.Bar else ChartType.Line
                        },
                        modifier = Modifier.size(24.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = color.copy(
                                alpha = 0.2f
                            )
                        )

                    ) {
                        Icon(
                            imageVector = if (realChartType == ChartType.Bar) Icons.Filled.BarChart else Icons.AutoMirrored.Filled.ShowChart,
                            contentDescription = if (realChartType == ChartType.Bar) "Bar Chart" else "Line Chart",
                            modifier = Modifier.size(16.dp),
                            tint = color.darken(2f)
                        )
                    }
                }

            }
        }

    }

    // Calculate bar width and spacing based on available width


}

private fun calculateLabelIndices(
    data: List<BarData>,
    canvasSize: Size,
    textMeasurer: TextMeasurer,
    barWidth: Float,
    barSpacing: Float
): List<Int> {
    // Calculate available width for labels
    val availableWidthForLabels = canvasSize.width - (barWidth + barSpacing) * data.size

    // Determine the number of labels that can fit
    var numLabelsToShow = 0
    var totalLabelWidth = 0f
    for (i in data.indices) {
        val labelWidth = textMeasurer.measure(
            text = AnnotatedString(data[i].label.toString()), style = TextStyle(
                color = Color.Black, fontSize = 8.sp, textAlign = TextAlign.Center
            )
        ).size.width

        if (totalLabelWidth + labelWidth <= availableWidthForLabels) {
            totalLabelWidth += labelWidth
            numLabelsToShow++
        } else {
            break // Stop if labels exceed available width
        }
    }

    // Adjust numLabelsToShow to ensure at least first and last labels are shown
    if (numLabelsToShow < 2 && data.size >= 2) {
        numLabelsToShow = 2
    }

    // Calculate step for label indices
    val step = if (numLabelsToShow < data.size) {
        data.size / numLabelsToShow.coerceAtLeast(1)
    } else {
        1
    }

    // Generate label indices
    val labelIndices = mutableListOf<Int>()
    for (i in data.indices) {
        if (i % step == 0 || i == data.size - 1) {
            labelIndices.add(i)
        }
    }

    return labelIndices
}

private fun calculateGridLineValues(maxValue: Float): List<Float> {
    val roundedMaxValue = (maxValue + 99).toInt() / 100 * 100f // Round up to nearest 100
    return listOf(
        roundedMaxValue,
        roundedMaxValue * 0.75f,
        roundedMaxValue * 0.5f,
        roundedMaxValue * 0.25f,
        roundedMaxValue * 0.0f
    ).map { it.toInt().toFloat() } // Convert to Int and back to Float to ensure divisibility by 100
}