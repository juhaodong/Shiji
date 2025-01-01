package domain.dashboard

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import domain.composable.basic.layout.SmallSpacer
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import modules.utils.BigDecimalSerializer
import modules.utils.FormatUtils.toPriceDisplay

@Serializable
class DishStatisticModel(
    val deviceId: String,
    val date: LocalDate,
    val lastUpdate: LocalDateTime,
    val code: String,
    val name: String,
    val dishId: Int,
    var totalCount: Int,
    @Serializable(with = BigDecimalSerializer::class) val price: BigDecimal,
    val categoryTypeId: Int,
    val categoryId: Int,
)


@Composable
fun DishStatisticCard(item: DishStatisticModel, index: Int? = null) {
    Row(modifier = Modifier.padding(16.dp)) {
        if (index != null) {
            Text(
                (index + 1).toString(),
                color = if (index < 3) MaterialTheme.colorScheme.primary else Color.Unspecified,
                modifier = Modifier.alignByBaseline(),
                fontWeight = FontWeight.Black
            )
            SmallSpacer(16)
        }
        Text(
            item.name, modifier = Modifier.weight(1f).alignByBaseline()
        )
        SmallSpacer()
        Text(
            item.price.times(item.totalCount).toPriceDisplay(),
            modifier = Modifier.alignByBaseline()
        )
        Text(
            '/' + item.totalCount.toString(),
            modifier = Modifier.alignByBaseline(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
