package view.page.homePage.dataCenterPage.storeDetail.dashboard

import DashboardCardView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DashboardCardList(dashboardCards: List<DashboardCard>) {
    TwoItemsPerRowGrid(dashboardCards, contentPadding = PaddingValues(0.dp)) {
        DashboardCardView(it)
    }

}

@Composable
fun <T> TwoItemsPerRowGrid(
    items: List<T>,
    contentPadding: PaddingValues = PaddingValues(
        16.dp,
        vertical = 8.dp
    ), // Add contentPadding parameter
    horizontalSpacing: Dp = 12.dp, // Add horizontalSpacing parameter
    verticalSpacing: Dp = 12.dp, // Add verticalSpacing parameter，
    itemContent: @Composable (T) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(contentPadding), // Apply contentPadding
        verticalArrangement = Arrangement.spacedBy(verticalSpacing) // Apply verticalSpacing
    ) {
        val chunkedItems = items.chunked(2)
        for (row in chunkedItems) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(horizontalSpacing) // Apply horizontalSpacing
            ) {
                for (item in row) {
                    Box(modifier = Modifier.weight(1f)) {
                        itemContent(item)
                    }
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}