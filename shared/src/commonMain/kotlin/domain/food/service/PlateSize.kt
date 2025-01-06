package domain.food.service

import androidx.compose.animation.core.copy
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.S
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import domain.composable.basic.layout.SmallSpacer


enum class PlateSize(val displayName: String, val approximateDiameterCm: Int) {
    SMALL("小号餐盘", 20),
    MEDIUM("中号餐盘", 25),
    LARGE("大号餐盘", 30),
    EXTRA_LARGE("超大号餐盘", 35)
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
                    containerColor = if (size == selectedSize) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                ),
                border = BorderStroke(
                    1.dp,
                    if (size == selectedSize) MaterialTheme.colorScheme.primary else Color.Gray.copy(
                        alpha = 0.3f
                    )
                )
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
                        color = if (size == selectedSize) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Ø${size.approximateDiameterCm} cm",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (size == selectedSize) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.7f
                        )
                    )
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
    }
    val iconColor = MaterialTheme.colorScheme.primary
    Card(
        modifier = Modifier.size(iconSize),
        shape = RoundedCornerShape(iconSize / 2),
        colors = CardDefaults.cardColors(
            containerColor = iconColor.copy(alpha = 0.2f)
        ),
    ) {
        Spacer(modifier = Modifier.size(iconSize))
    }


}
