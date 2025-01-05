package domain.composable.basic.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import domain.composable.basic.layout.SmallSpacer

@Composable
fun BaseCardHeader(
    title: String,
    subtitle: String = "",
    icon: ImageVector? = null,
    noPadding: Boolean = false,
    large: Boolean = false,
    iconColor: Color = LocalContentColor.current
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(if (noPadding) 0.dp else 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween, // Arrange items with space between
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.Start, modifier = Modifier.weight(1f)
        ) { // Align text to the start
            if (large && subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalContentColor.current.copy(alpha = 0.6f)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = if (large) FontWeight.Black else FontWeight.Bold,
            )
            if (!large && subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalContentColor.current.copy(alpha = 0.6f)
                )
            }
        }
        if (icon != null) {
            SmallSpacer(16)
            Icon(
                imageVector = icon, contentDescription = title, modifier = Modifier.size(20.dp),
                tint = iconColor
            )
        }
    }
}

@Composable
fun BaseCardHeader(
    title: String,
    subtitle: String = "",
    noPadding: Boolean = false,
    large: Boolean = false,
    actions: @Composable RowScope.() -> Unit = {},

    ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(if (noPadding) 0.dp else 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween, // Arrange items with space between
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.Start, modifier = Modifier.weight(1f)
        ) { // Align text to the start
            if (large && subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalContentColor.current.copy(alpha = 0.6f)
                )
            }
            Text(
                text = title,
                style = if (large) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleMedium,
                fontWeight = if (large) FontWeight.Black else FontWeight.Bold,
            )
            if (!large && subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalContentColor.current.copy(alpha = 0.6f)
                )
            }
        }
        SmallSpacer()
        actions()
    }
}