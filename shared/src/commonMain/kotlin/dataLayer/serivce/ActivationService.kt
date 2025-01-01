package dataLayer.serivce

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import kotlinx.serialization.Serializable
import modules.network.cloudUrl


interface ActivationService {


    @GET("$cloudUrl/subscriptions/portalLink/{firebaseUid}")
    suspend fun getPortalLink(@Path("firebaseUid") firebaseUid: String): String?


    @Headers("Content-Type: application/json")
    @POST("$cloudUrl/api/frontend-logs/save")
    suspend fun reportToCloud(@Body frontendLogDTO: FrontendLogDTO)
}


@Serializable
data class FrontendLogDTO(
    val name: String,
    val ip: String,
    val uuid: String,
    val version: String,
    val frontendType: String,
    val deviceId: String,
    val timestamp: Long
)
