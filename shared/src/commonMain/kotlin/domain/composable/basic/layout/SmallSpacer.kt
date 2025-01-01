package domain.composable.basic.layout

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SmallSpacer(size: Int = 8, modifier: Modifier = Modifier.size(size.dp)) {
    Spacer(modifier = modifier)
}

@Composable
fun BigSpacer(size: Int = 36, modifier: Modifier = Modifier.size(size.dp)) {
    Spacer(modifier = modifier)
}


@Composable
fun ColumnScope.GrowSpacer(modifier: Modifier = Modifier.weight(1f)) {
    Spacer(modifier = modifier)
}

@Composable
fun RowScope.GrowSpacer(modifier: Modifier = Modifier.weight(1f)) {
    Spacer(modifier = modifier)
}