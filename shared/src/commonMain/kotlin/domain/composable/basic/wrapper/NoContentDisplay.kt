package domain.composable.basic.wrapper

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SentimentNeutral
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import domain.composable.basic.layout.SmallSpacer

@Composable
fun NoContentColumnDisplay(modifier: Modifier = Modifier, minHeight: Int = 200, title: String) {
    Column(
        modifier = modifier.fillMaxWidth().requiredHeightIn(min = minHeight.dp).padding(36.dp),
        verticalArrangement = Arrangement.Center,
        Alignment.CenterHorizontally
    ) {
        Icon(Icons.Rounded.SentimentNeutral, contentDescription = null)
        SmallSpacer()
        Text(
            title, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center
        )
    }
}


@Composable
fun NoContentProvider(haveContent: Boolean, minHeight: Int = 200, content: @Composable () -> Unit) {
    if (haveContent) {
        content()
    } else {
        NoContentColumnDisplay(title = "暂时没有内容呢", minHeight = minHeight)
    }
}


