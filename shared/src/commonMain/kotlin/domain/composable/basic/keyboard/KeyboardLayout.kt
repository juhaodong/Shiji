package domain.composable.basic.keyboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import modules.utils.FormatUtils

data class IkKey(
    val name: String,
    val id: Int = -1,
    val textStyle: TextStyle? = null,
    val label: String? = null,
    val color: Color? = null,
    val halfHeight: Boolean = false,
)

var keyboardLock = false

fun List<Any>.toKeyList(): List<IkKey> {
    return map {
        if (it is String) IkKey(it)
        else it as IkKey
    }
}

@Composable
fun ACKey(): IkKey {
    return IkKey("AC", color = MaterialTheme.colorScheme.errorContainer)
}

@Composable
fun CloseKey(): IkKey {
    return IkKey(FormatUtils.close, color = MaterialTheme.colorScheme.errorContainer)
}

@Composable
fun KeyboardLayout(
    ikKeys: List<IkKey>, columnCount: Int = 4, onInput: (String, Int) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    LazyVerticalGrid(
        columns = GridCells.Fixed(columnCount),
        modifier = Modifier.wrapContentSize()
    ) {
        items(ikKeys) {
            KeyItem(
                ikKey = it,
            ) {
                if (!keyboardLock) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    keyboardLock = true
                    onInput(it.name, it.id)
                    keyboardLock = false
                }
            }
        }
    }
    LaunchedEffect(keyboardLock) {
        if (keyboardLock == true) {
            delay(5000)
            keyboardLock = false
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KeyItem(
    ikKey: IkKey,
    onClick: () -> Unit,
) {
    val color = ikKey.color ?: MaterialTheme.colorScheme.surface
    val textStyle =
        if (ikKey.halfHeight) MaterialTheme.typography.titleMedium
            .copy(fontWeight = FontWeight.Black)
        else MaterialTheme.typography.headlineSmall
    val haptics = LocalHapticFeedback.current
    Surface(
        modifier = Modifier.height(if (ikKey.halfHeight) 48.dp else 72.dp).padding(4.dp)
            .fillMaxWidth(),
        color = color,
        onClick = onClick,
        tonalElevation = 3.dp,
        shape = if (ikKey.halfHeight) MaterialTheme.shapes.small else MaterialTheme.shapes.medium
    ) {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Text(
                text = ikKey.label ?: ikKey.name,
                style = textStyle.copy(textAlign = TextAlign.Center)
            )
        }

    }
}

