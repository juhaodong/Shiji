package domain.composable.filter

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.composable.dialog.basic.DialogViewModel
import kotlinx.coroutines.launch

@Composable
fun DateFilter(
    dialogViewModel: DialogViewModel, title: String, onValueChange: (String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    LaunchedEffect(true) {
        onValueChange(searchText)
    }
    FilterChip(selected = searchText.isNotEmpty(), onClick = {
        if (searchText.isNotEmpty()) {
            searchText = ""
        } else {
            scope.launch {
                searchText = dialogViewModel.showDatePicker(title)
                onValueChange(searchText)
            }
        }
    }, leadingIcon = {
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = "搜索",
            modifier = Modifier.size(16.dp)
        )
    }, label = { Text(searchText.ifBlank { title }) }, trailingIcon = {
        if (searchText.isNotEmpty()) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "清除",
                tint = LocalContentColor.current.copy(alpha = 0.6f) // Adjust alpha value as needed
            )
        }
    })
}