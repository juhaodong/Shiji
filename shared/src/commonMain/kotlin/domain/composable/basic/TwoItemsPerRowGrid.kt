package domain.composable.basic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun <T> TwoItemsPerRowGrid(
    items: List<T>,
    contentPadding: PaddingValues = PaddingValues(
        16.dp,
        vertical = 8.dp
    ), // Add contentPadding parameter
    horizontalSpacing: Dp = 12.dp, // Add horizontalSpacing parameter
    verticalSpacing: Dp = 12.dp, // Add verticalSpacing parameter，
    itemContent: @Composable (T) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(contentPadding), // Apply contentPadding
        verticalArrangement = Arrangement.spacedBy(verticalSpacing) // Apply verticalSpacing
    ) {
        val chunkedItems = items.chunked(2)
        for (row in chunkedItems) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(horizontalSpacing) // Apply horizontalSpacing
            ) {
                for (item in row) {
                    Box(modifier = Modifier.weight(1f)) {
                        itemContent(item)
                    }
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun Float.getRatioColor(): Color {
    val consumeRatio = this
    return when {
        consumeRatio <= 0.25f -> Color(0xFFF5B7B1) // Light red (most severe shortage)
        consumeRatio <= 0.5f -> Color(0xFFFAD7A0) // Light orange
        consumeRatio <= 0.75f -> Color(0xFFFFFDD0) // Light yellow
        else -> MaterialTheme.colorScheme.primaryContainer.copy(0.6f)
    }
}