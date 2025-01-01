package view.page.loginPage

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

@Composable
fun DarkModeDisplay(toggleDarkMode: () -> Unit, darkMode: Boolean) {
    IconButton(onClick = { toggleDarkMode() }) {
        Icon(
            imageVector = if (darkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
            contentDescription = ""
        )
    }
}