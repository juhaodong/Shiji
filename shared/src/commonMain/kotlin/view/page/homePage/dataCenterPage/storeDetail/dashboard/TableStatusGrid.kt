package view.page.homePage.dataCenterPage.storeDetail.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.dashboard.TableInfo
import kotlin.random.Random

val tables = (1..Random.nextInt(30, 51)).map { id ->
    Table(id = id, isOpen = Random.nextBoolean())
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TableStatusGrid(tables: List<TableInfo>, takeawayOrders: Int) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        tables.forEach { table ->
            Box(
                modifier = Modifier
                    .size(16.dp) // Adjust size as needed
                    .background(
                        if (table.usageStatus == 1) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = MaterialTheme.shapes.small
                    ),
            )

        }
        repeat(takeawayOrders) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        MaterialTheme.colorScheme.tertiary, // Different color for takeaway orders
                        shape = MaterialTheme.shapes.small
                    ),
                contentAlignment = Alignment.Center // Center the icon
            ) {
                Icon(
                    imageVector = Icons.Filled.DeliveryDining, // Takeaway icon
                    contentDescription = "Takeaway Order",
                    modifier = Modifier.size(12.dp), // Adjust icon size as needed
                    tint = contentColorFor(backgroundColor = MaterialTheme.colorScheme.tertiary) // Use appropriate tint color
                )
            }
        }
    }
}

data class Table(val id: Int, val isOpen: Boolean)