package dataLayer.model.restaurant

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantInfoEntity(
    val adress1: String,
    val adress2: String,
    val bonLogo: String?,
    val buffetLogo: String?,
    val callColor: String,
    val childRoundDishCount: Int,
    val city: String,
    val emailAddress: String,
    val id: String,
    val inhalterLastName: String,
    val inhalterName: String,
    val inhalterTitle: String,
    val latitude: String,
    val logoUrl: String,
    val longitude: String,
    val maxDineTime: Int,
    val name: String,
    val postCode: String,
    val roundDishCount: Int,
    val roundTime: Int,
    val state: String,
    val tableColor: String,
    val takeawayPriceModification: String,
    val taxNumber: String,
    val telephone: String,
    val totalRound: Int,
    val buffetAnnouncementHead: String,
    val buffetAnnouncementBody: String,
    val s3LogoUrl: String?,
) {
    fun getAddressString(): String {
        return listOf(adress1, adress2, "$postCode $city", emailAddress).filter { it.isNotBlank() }
            .joinToString("\n")
    }
}