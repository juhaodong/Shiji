package domain.composable.basic.button

import LocalDialogManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun BaseIconButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    color: Color? = null,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(icon, null, tint = color ?: LocalContentColor.current)
    }
}

@Composable
fun BaseTonalIconButton(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    FilledTonalIconButton(
        onClick = onClick,
        modifier = modifier,
        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = color)
    ) {
        Icon(icon, null)
    }
}

@Composable
fun BaseOutlinedIconButton(icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedIconButton(onClick = onClick, modifier = modifier) {
        Icon(icon, null)
    }
}


@Composable
fun DeleteIconButton(name: String = "", onDelete: () -> Unit) {
    val manager = LocalDialogManager.current
    BaseIconButton(icon = Icons.Default.Delete, onClick = {
        manager.confirmDelete(name) {
            onDelete()
        }
    })
}