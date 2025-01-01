package view.page.homePage.supplierManagePage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import domain.composable.basic.layout.BaseSurface
import domain.composable.basic.layout.SmallSpacer
import domain.inventory.model.storageItem.getRealUrl
import domain.supplier.model.SupplierSetting

@Composable
fun SupplierDisplay(
    supplierSetting: SupplierSetting,
    overrideName: String = "",
    onClick: () -> Unit
) {
    BaseSurface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box( // Wrap the icon in a Box
                modifier = Modifier.size(48.dp) // Adjust size as needed
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), // Light background
                        shape = CircleShape
                    ).clip(CircleShape) // Clip to a circle
                    .wrapContentSize(Alignment.Center) // Center the icon
            ) {
                AsyncImage(
                    model = supplierSetting.imageUrl?.let { getRealUrl(it) },
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
            SmallSpacer(16)
            Column(modifier = Modifier.weight(1f)) { // Use Column for two lines of text
                Text(
                    overrideName.ifBlank { supplierSetting.name }, // Supplier type
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold // Make the type bold
                )
                SmallSpacer(4)
                Text(
                    supplierSetting.description, // Second line
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    color = Color.Gray // Use a lighter color for the second line
                )
            }
        }
    }
}
