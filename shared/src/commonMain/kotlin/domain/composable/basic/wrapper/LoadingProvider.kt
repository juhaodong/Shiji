package domain.composable.basic.wrapper

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LoadingProvider(loading: Boolean, content: @Composable () -> Unit) {
    if (loading) {
        LoadingIndicator()
    } else {
        content()
    }
}

@Composable
fun ColumnScope.GrowLoadingProvider(
    loading: Boolean,
    haveContent: Boolean = true,
    content: @Composable () -> Unit
) {
    if (loading || !haveContent) {
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            if (loading)
                LoadingIndicator()
            else
                NoContentColumnDisplay(title = "暂时没有内容")
        }

    } else {
        content()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColumnScope.PageLoadingProvider(
    loading: Boolean,
    refreshKey: Any? = true,
    onRefresh: () -> Unit,
    haveContent: Boolean = true,

    content: @Composable ColumnScope.() -> Unit
) {
    val state = rememberPullToRefreshState()
    LaunchedEffect(refreshKey) {
        onRefresh()
    }
    PullToRefreshBox(
        isRefreshing = loading,
        onRefresh = onRefresh,
        state = state,
        modifier = Modifier.weight(1f)
    ) {
        if (haveContent) {
            Column(modifier = Modifier.fillMaxSize()) {
                content()
            }

        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                NoContentColumnDisplay(title = "暂时没有内容")
            }
        }
    }
    LaunchedEffect(loading) {
        if (!loading) {
            state.animateToHidden()
        }
    }
}