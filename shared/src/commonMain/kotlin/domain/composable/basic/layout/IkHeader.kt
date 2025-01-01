package domain.composable.basic.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun IkHeader(modifier: Modifier=Modifier,icon: ImageVector, title: String, subtitle: String = "") {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleLarge)

            if (subtitle.isNotBlank()) {
                SmallSpacer(4)
                Text(subtitle, style = MaterialTheme.typography.bodyLarge)
            }
        }
        SmallSpacer()
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp))


    }
}