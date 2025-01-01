package domain.composable.filter

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ToggleFilter(onValueChange: (Boolean) -> Unit) {
    var value by remember { mutableStateOf(false) }
    FilterChip(selected = value,
        onClick = {
            value = !value
            onValueChange(value)
        },
        label = { Text("仅显示缺货产品") },
        leadingIcon = {
            if (value) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "已激活",
                    modifier = Modifier.size(16.dp)
                )
            }
        })
}