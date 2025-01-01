package domain.composable.basic.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import domain.composable.basic.layout.SmallSpacer

@Composable
fun ActionLeftMainButton(
    text: String,
    icon: ImageVector? = null,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    loading: Boolean = false,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = color
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
            } else {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    SmallSpacer()
                }

                Text(
                    text,
                    style = MaterialTheme.typography.titleMedium, maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

        }
    }
}


@Composable
fun MainButton(
    text: String,
    icon: ImageVector? = null,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    loading: Boolean = false,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = color
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
            } else {
                if (icon != null) {
                    SmallSpacer(16)
                }
                Text(
                    text,
                    style = MaterialTheme.typography.titleMedium, maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (icon != null) {
                    SmallSpacer()
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                    )

                }

            }

        }
    }
}

@Composable
fun RowScope.MainActionGrowButton(
    text: String,
    icon: ImageVector? = null,
    loading: Boolean = false,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = color,
        modifier = Modifier.weight(1f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
            } else {
                if (icon != null) {
                    Icon(imageVector = icon, contentDescription = null)
                    SmallSpacer()
                }

                Text(
                    text,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

        }
    }
}