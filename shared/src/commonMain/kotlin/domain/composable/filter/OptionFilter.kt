package domain.composable.filter

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import domain.composable.dialog.basic.DialogViewModel
import domain.composable.dialog.selection.SelectOption
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

@Composable
inline fun <reified T> OptionFilter(
    dialogViewModel: DialogViewModel,
    title: String,
    options: List<SelectOption<T>>,
    crossinline onOptionSelected: (T?) -> Unit
) {
    val scope = rememberCoroutineScope()
    var filterValue: T? by remember { mutableStateOf(null) }
    LaunchedEffect(true) {
        onOptionSelected(filterValue)
    }

    FilterChip(
        selected = filterValue != null,
        onClick = {
            if (filterValue != null) {
                filterValue = null
            } else {
                scope.launch {

                    filterValue = dialogViewModel.showSelectDialog("请选择" + title, options)
                    onOptionSelected(filterValue)
                    Napier.e { filterValue.toString() }

                }
            }
        },
        label = { Text(options.find { it.value == filterValue }?.label ?: title) },
        trailingIcon = {
            if (filterValue != null) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "清除",
                    tint = LocalContentColor.current.copy(alpha = 0.6f)
                )
            }
        }
    )
}