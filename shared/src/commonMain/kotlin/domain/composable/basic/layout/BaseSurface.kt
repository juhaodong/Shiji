@file:OptIn(ExperimentalFoundationApi::class)

package domain.composable.basic.layout

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BaseSurface(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        color = color,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 3.dp
    ) {
        content()
    }
}


@Composable
fun BaseSurface(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
    enabled: Boolean = true,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        onClick = onClick,
        color = color,
        enabled = enabled,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 3.dp
    ) {
        content()
    }
}

@Composable
fun LongPressBaseSurface(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
    enabled: Boolean = true,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        color = color,
        modifier = modifier.combinedClickable(
            enabled,
            onClick = onClick,
            onLongClick = onLongPress
        ),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 3.dp
    ) {
        content()
    }
}