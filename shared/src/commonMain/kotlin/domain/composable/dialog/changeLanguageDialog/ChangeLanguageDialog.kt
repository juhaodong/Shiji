@file:OptIn(ExperimentalResourceApi::class)

package domain.composable.dialog.changeLanguageDialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import currentLocales
import domain.composable.dialog.basic.BeautifulDialog
import org.jetbrains.compose.resources.ExperimentalResourceApi

@Composable
fun ChangeLanguageDialog(
    show: Boolean,
    onDismissRequest: () -> Unit,
    changeLanguage: (String) -> Unit

) {
    val locales = currentLocales
    BeautifulDialog(show, useCloseButton = false, onDismissRequest = { onDismissRequest() }) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.heightIn(300.dp, 600.dp)
        ) {
            items(locales) {
                Surface(onClick = {
                    changeLanguage(it.language)
                }) {
                    ListItem(headlineContent = {
                        Text(text = it.localizedName)
                    }, supportingContent = {
                        Text(text = it.name)
                    })
                }
            }
        }
    }


}