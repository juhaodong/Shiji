package domain.food.service

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.size
import androidx.compose.ui.unit.width
import domain.composable.basic.layout.SmallSpacer


enum class PlateSize(val displayName: String, val approximateDiameterCm: Int) {
    NONE("无餐盘", 0),
    SMALL_BOWL("小碗", 9),
    LARGE_BOWL("大碗", 15),
    SMALL("小号餐盘", 18),
    MEDIUM("中号餐盘", 24),
    LARGE("大号餐盘", 32),
    EXTRA_LARGE("超大号餐盘", 40)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlateSizeSelector(
    initialSize: PlateSize = PlateSize.MEDIUM,
    onSizeSelected: (PlateSize) -> Unit
) {
    val (selectedSize, setSelectedSize) = remember { mutableStateOf(initialSize) }

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(PlateSize.entries) { size ->
            val cardColor = if (size == selectedSize)
                MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surface
            Card(
                modifier = Modifier
                    .selectable(
                        selected = (size == selectedSize),
                        onClick = {
                            setSelectedSize(size)
                            onSizeSelected(size)
                        }
                    ),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardColor,
                    contentColor = contentColorFor(cardColor)
                ),
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.size(56.dp), contentAlignment = Alignment.Center) {
                        PlateSizeIcon(size = size)
                    }
                    SmallSpacer()
                    Text(
                        text = size.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )
                    if (size.approximateDiameterCm != 0) {
                        Text(
                            text = "Ø${size.approximateDiameterCm} cm",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    } else {
                        Text(
                            text = "--",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun PlateSizeIcon(size: PlateSize) {
    val iconSize = when (size) {
        PlateSize.SMALL -> 20.dp
        PlateSize.MEDIUM -> 24.dp
        PlateSize.LARGE -> 28.dp
        PlateSize.EXTRA_LARGE -> 32.dp
        PlateSize.NONE -> 24.dp
        PlateSize.SMALL_BOWL -> 20.dp
        PlateSize.LARGE_BOWL -> 30.dp
    }
    val iconColor = LocalContentColor.current.copy(0.7f)
    Card(
        modifier = Modifier.size(iconSize),
        shape = RoundedCornerShape(iconSize / 2),
        colors = CardDefaults.cardColors(
            containerColor = if (size != PlateSize.NONE && size != PlateSize.SMALL_BOWL && size != PlateSize.LARGE_BOWL)
                iconColor.copy(alpha = 0.2f)
            else Color.Transparent
        ),
    ) {
        when (size) {
            PlateSize.SMALL_BOWL, PlateSize.LARGE_BOWL -> {
                BowlIcon(iconColor = iconColor, iconSize = iconSize)
            }

            PlateSize.SMALL, PlateSize.MEDIUM, PlateSize.LARGE, PlateSize.EXTRA_LARGE -> {
                PlateIcon(iconColor = iconColor, iconSize = iconSize)
            }

            else -> {
                Box(
                    modifier = Modifier
                        .size(iconSize)
                        .drawBehind {
                            val strokeWidth = 2.dp.toPx()
                            val center =
                                Offset(iconSize.toPx() / 2, iconSize.toPx() / 2)
                            val radius = iconSize.toPx() / 2
                            drawCircle(
                                color = iconColor,
                                center = center,
                                radius = radius,
                                style = Stroke(strokeWidth)
                            )
                            drawLine(
                                color = iconColor,
                                start = Offset(0f, 0f),
                                end = Offset(iconSize.toPx(), iconSize.toPx()),
                                strokeWidth = strokeWidth
                            )
                        }
                )
            }
        }


    }
}

@Composable
fun BowlIcon(iconColor: Color, iconSize: Dp) {
    Box(
        modifier = Modifier
            .size(iconSize)
            .drawBehind {
                val strokeWidth = 2.dp.toPx()
                val path = Path().apply {
                    val startX = 0f
                    val startY = iconSize.toPx() / 2
                    moveTo(startX, startY)

                    // Adjusted control points for the bowl shape
                    val controlX1 = iconSize.toPx() * 0.2f
                    val controlY1 = iconSize.toPx() * 0.8f
                    val endX1 = iconSize.toPx() * 0.8f
                    val endY1 = iconSize.toPx() * 0.8f


                    cubicTo(controlX1, controlY1, endX1, endY1, iconSize.toPx(), startY)

                    val controlX2 = iconSize.toPx() * 0.8f
                    val controlY2 = iconSize.toPx() * 0.2f
                    val endX2 = iconSize.toPx() * 0.2f
                    val endY2 = iconSize.toPx() * 0.2f


                    cubicTo(endX1, controlY2, endX2, endY2, startX, startY)


                    // Add the bowl bottom using a line
                    lineTo(startX, iconSize.toPx())
                    lineTo(iconSize.toPx(), iconSize.toPx())

                    lineTo(iconSize.toPx(), startY)
                }
                drawPath(
                    path = path,
                    color = iconColor.copy(alpha = 0.3f), // fill the bowl with a lighter color
                )
                drawPath(
                    path = path,
                    color = iconColor,
                    style = Stroke(strokeWidth)
                )
            }
    )
}

@Composable
fun PlateIcon(iconColor: Color, iconSize: Dp) {
    Box(
        modifier = Modifier
            .size(iconSize)
            .drawBehind {
                val strokeWidth = 2.dp.toPx()
                val center =
                    Offset(iconSize.toPx() / 2, iconSize.toPx() / 2)
                val radius = iconSize.toPx() / 2
                drawCircle(
                    color = iconColor,
                    center = center,
                    radius = radius,
                    style = Stroke(strokeWidth)
                )
            }
    )
}