@file:OptIn(ExperimentalMaterial3Api::class)

package domain.composable.basic.wrapper

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.rounded.SentimentNeutral
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import domain.composable.basic.button.ActionLeftMainButton
import domain.composable.basic.layout.SmallSpacer

@Composable
fun ActiveWrapper(
    active: Boolean,
    activate: () -> Unit,
    checkActive: () -> Unit,
    loading: Boolean = false,
    modifier: Modifier = Modifier,
    haveSubscription: Boolean = true,
    title: String = "您尚未激活本功能",
    content: @Composable () -> Unit
) {
    LaunchedEffect(true) {
        checkActive()
    }
    Column(modifier = modifier.fillMaxSize()) {
        GrowLoadingProvider(loading) {
            if (!active) {
                Column(
                    modifier.fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                ) {
                    Column(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Rounded.SentimentNeutral,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp)
                        )
                        SmallSpacer()
                        if (haveSubscription) {
                            Text(
                                title,
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "不要担心，请直接点击下面的按钮激活。",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                            )
                        } else {
                            Text(
                                "您尚未订阅本功能",
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "请去Shiji App完成订阅后开通本功能。",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                            )
                        }
                    }
                    if (haveSubscription) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            ActionLeftMainButton(
                                "现在激活",
                                color = MaterialTheme.colorScheme.primary,
                                icon = Icons.Default.Done
                            ) {
                                activate()
                            }
                        }

                    }


                }
            } else {

                content()


            }
        }

    }


}