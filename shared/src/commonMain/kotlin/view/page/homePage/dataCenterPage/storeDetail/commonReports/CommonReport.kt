package view.page.homePage.dataCenterPage.storeDetail.commonReports

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class CommonReport(
    val name: String,
    val icon: ImageVector
)

// Common reports section
val commonReports = listOf(
    CommonReport("销售报表", Icons.Filled.BarChart),
    CommonReport("订单报表", Icons.AutoMirrored.Filled.ReceiptLong),
    CommonReport("顾客报表", Icons.Filled.People),
    CommonReport("库存报表", Icons.Filled.Inventory),
    CommonReport("支付报表", Icons.Filled.Payment),
    CommonReport("员工报表", Icons.Filled.Person),
    CommonReport("产品报表", Icons.Filled.RestaurantMenu),
    CommonReport("会员报表", Icons.Filled.CardMembership),
    CommonReport("营销报表", Icons.Filled.Campaign),
    CommonReport("成本报表", Icons.Filled.AttachMoney),
    CommonReport("利润报表", Icons.AutoMirrored.Filled.TrendingUp),
    CommonReport("评价报表", Icons.Filled.Star),
    CommonReport("流量报表", Icons.Filled.Traffic),
    CommonReport("预订报表", Icons.Filled.Event)
    // Add more common reports as needed
)

@Composable
fun CommonReportItem(
    report: CommonReport,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = report.icon,
                contentDescription = report.name,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = report.name)
        }
    }
}