package view.page.homePage.supplierManagePage.orderBook.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import domain.composable.basic.layout.BaseVCenterRow
import domain.composable.basic.layout.SmallSpacer
import domain.inventory.model.storageItem.getRealUrl
import domain.supplier.model.SupplierOrderBookDTO

@Composable
fun OrderBookDisplay(model: SupplierOrderBookDTO) {
    BaseVCenterRow {
        AsyncImage(
            model = model.supplier.imageUrl?.let { getRealUrl(it) },
            contentDescription = "Supplier Avatar",
            modifier = Modifier
                .size(36.dp) // Adjust size as needed
                .clip(MaterialTheme.shapes.medium) // Make it circular
        )
        SmallSpacer()
        Column {
            Text(
                "订购指南",
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Black
            )
            Text(
                model.displayName(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}