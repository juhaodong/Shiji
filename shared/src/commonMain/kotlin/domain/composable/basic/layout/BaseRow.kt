package domain.composable.basic.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import domain.composable.basic.cards.LabelValuePair


fun Modifier.py(dp: Int = 16) = padding(vertical = dp.dp)
fun Modifier.px(dp: Int = 16) = padding(horizontal = dp.dp)
fun Modifier.pa(dp: Int = 16) = padding(dp.dp)

@Composable
fun BaseVCenterRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}


@Composable
fun CenterValueRow(
    modifier: Modifier = Modifier,
    verticalPadding: Int = 16,
    horizontalPadding: Int = 16,
    values: List<Pair<String, String>>
) {
    Row(
        modifier = modifier.padding(
            vertical = verticalPadding.dp,
            horizontal = horizontalPadding.dp
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        values.forEach {
            LabelValuePair(
                labelText = it.first,
                valueText = it.second,
                modifier = Modifier.weight(1f),
                center = true
            )
        }
    }
}

@Composable
fun StartValueRow(
    modifier: Modifier = Modifier,
    values: List<Pair<String, String>>
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        values.forEach {
            LabelValuePair(
                labelText = it.first,
                valueText = it.second,
                modifier = Modifier.weight(1f),
                center = false
            )
        }
    }
}

@Composable
fun IconWithLabel(icon: ImageVector, label: String) {
    Row { // Use a Row to arrange icon and label horizontally
        Icon(icon, contentDescription = null)
        SmallSpacer() // Add some spacing between icon and label
        Text(label)
    }
}