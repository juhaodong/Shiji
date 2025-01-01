package domain.inventory.model

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import kotlinx.serialization.Serializable

@Serializable
class InventorySetting() {
    lateinit var name: String
    var inventoryPeriodDays: Long = 7
    lateinit var bossPassword: String
    lateinit var reportEmail: String
    var enableReport: Boolean = true
    var shopId: Long? = null
}

@Serializable
class ContactInfo(
    val legalName: String,
    val addressLine1: String,
    val addressLine2: String,
    val city: String,
    val postalCode: String,
    val country: String,
    val taxNumber: String,
) {
    fun displayString(): AnnotatedString {
        return buildAnnotatedString {
            if (legalName.isNotBlank()) {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(legalName)
                }
                append("\n") // Add a newline after legalName if it's not blank
            }
            if (addressLine1.isNotBlank()) {
                append("${addressLine1}\n")
            }
            if (addressLine2.isNotBlank()) {
                append("${addressLine2}\n")
            }
            if (city.isNotBlank() || postalCode.isNotBlank()) {
                append(
                    "${city.ifBlank { "" }}, ${postalCode.ifBlank { "" }}".trim()
                        .replace(", $", "") + "\n"
                )
            }
            if (country.isNotBlank()) {
                append("${country}\n")
            }
            if (taxNumber.isNotBlank()) {
                append(taxNumber)
            }
        }
    }

    fun toNormalString(): String {
        return "$addressLine1,$city $postalCode,${country.ifBlank { "DE" }}"
    }
}

@Serializable
class BankInfo(
    val bankName: String,
    val bic: String,
    val iban: String
)

@Serializable
data class InventorySettingDTO(
    val id: Long? = null,
    val name: String,
    val inventoryPeriodDays: Long = 7,
    val bossPassword: String = "",
    val enableReport: Boolean = true,
    val shopId: Long,
)