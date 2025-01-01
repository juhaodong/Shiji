package domain.composable.basic.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import domain.composable.basic.layout.BaseSurface
import domain.composable.basic.layout.SmallSpacer
import domain.composable.basic.wrapper.PageLoadingProvider


@Composable
fun ColumnScope.BaseCardList(
    loading: Boolean = false,
    refreshKey: Any? = null,
    onRefresh: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    PageLoadingProvider(loading = loading, refreshKey = refreshKey, onRefresh = onRefresh) {
        Column(
            modifier = Modifier.fillMaxHeight().padding(horizontal = 16.dp).verticalScroll(
                rememberScrollState()
            ), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            content()
            SmallSpacer(0)
        }
    }

}


@Composable
fun LabelWith(
    labelText: String = "", content: @Composable ColumnScope.() -> Unit
) {

    Column(
        horizontalAlignment = Alignment.Start
    ) {
        LabelText(labelText)
        SmallSpacer(4)
        content()
    }

}

@Composable
fun LabelValuePair(
    labelText: String, valueText: String, modifier: Modifier = Modifier, center: Boolean = false
) {

    Column(
        modifier,
        horizontalAlignment = if (center) Alignment.CenterHorizontally else Alignment.Start
    ) {
        LabelText(labelText)
        SmallSpacer(4)
        BigText(valueText.ifBlank { "-" })
    }

}

@Composable
fun LabelText(text: String, modifier: Modifier = Modifier, secondary: Boolean = false) {
    Text(
        text,
        style = MaterialTheme.typography.labelMedium,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        color = LocalContentColor.current.copy(alpha = if (secondary) 0.7f else 1f),
    )
}

@Composable
fun BigText(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
    )
}

@Composable
fun ShowAllButton(
    text: String = "查看详情",
    icon: ImageVector = Icons.AutoMirrored.Filled.ArrowForward,
    onClick: () -> Unit
) {
    BaseSurface(onClick = onClick) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Icon(
                imageVector = icon, contentDescription = text
            )
            SmallSpacer()
            Text(text)
        }
    }
}


@Composable
fun BaseContentCard(
    title: String = "",
    subtitle: String = "",
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
    noPadding: Boolean = false,

    content: (@Composable ColumnScope.() -> Unit)? = null
) {
    BaseSurface(color = color, modifier = Modifier.fillMaxWidth()) {
        Column(modifier) {
            if (title.isNotBlank()) {
                BaseCardHeader(title, subtitle, icon)
            }
            if (content != null) {
                Column(
                    Modifier.padding(horizontal = if (noPadding) 0.dp else 16.dp).fillMaxWidth()
                        .padding(bottom = if (noPadding) 0.dp else 16.dp)
                ) {
                    content()
                }
            }


        }
    }
}


@Composable
fun NoBackgroundContentCard(
    title: String = "",
    subtitle: String = "",
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    content: (@Composable ColumnScope.() -> Unit)? = null
) {
    Column(modifier.padding(8.dp)) {
        if (title.isNotBlank()) {
            BaseCardHeader(title, subtitle, icon, noPadding = true, large = true)
        }
        SmallSpacer(16)
        if (content != null) {
            Column(
                Modifier.fillMaxWidth()
            ) {
                content()
            }
        }


    }

}