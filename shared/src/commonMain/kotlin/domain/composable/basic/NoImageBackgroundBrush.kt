package domain.composable.basic

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.materialkolor.ktx.lighten


@Composable
fun PageBackgroundBrush(): Brush {
    return Brush.linearGradient(
        colors = listOf(
            Color.Gray.lighten(1.3f),
            MaterialTheme.colorScheme.surfaceContainerLow
        )
    )
}