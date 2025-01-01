package view.page.homePage.dataCenterPage.storeDetail.industryInsight

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import domain.composable.basic.button.MainActionGrowButton
import domain.composable.basic.layout.SmallSpacer

@Composable
fun IndustryInsightDetails(insight: IndustryInsight, close: () -> Unit) {
    Surface( // Wrap the entire content with Surface
        color = MaterialTheme.colorScheme.surfaceContainerHigh, // Set background color of the dialog
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column() { // Add padding to the Column
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = insight.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "${insight.source} - ${insight.date}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = insight.content,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), // Add top padding for spacing
            ) {
                MainActionGrowButton(
                    text = "没用处",
                    icon = Icons.Filled.ThumbDown,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest
                ) {
                    close()
                }
                SmallSpacer()
                MainActionGrowButton(
                    text = "有用处",
                    icon = Icons.Filled.ThumbUp,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    close()
                }
            }


        }
    }
}