package domain.composable.display

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun GeneralInfoDisplay(
    icon: ImageVector,
    text: String,
    color: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    onClick: () -> Unit = {}
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            Icon(imageVector = icon, contentDescription = "")
            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
            Text(
                text = text,
                maxLines = 1
            )
        }
    }
}