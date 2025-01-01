package domain.user

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import domain.supplier.model.ShopInfo
import domain.user.model.AcceptInviteRequestDTO
import domain.user.model.ChangeAuthRequestDTO
import domain.user.model.CreateInviteRequestDTO
import domain.user.model.InviteInfoDTO
import domain.user.model.UserShopUserDTO
import domain.user.model.UserStoreDetailsDTO
import kotlinx.serialization.Serializable
import modules.network.cloudUrl

private const val BASE_URL = "$cloudUrl/user-stores"

interface StoreService {



    @GET("$BASE_URL/details")
    suspend fun getStoreDetails(@Query("firebaseUid") firebaseUid: String): List<UserStoreDetailsDTO>

    @GET("$BASE_URL/verify/{activeCode}")
    suspend fun getStoreDetailsByOtp(
        @Path("activeCode") activeCode: String
    ): ShopInfo?

    @POST("$BASE_URL/unbind")
    suspend fun unbindStore(
        @Query("firebaseUid") firebaseUid: String,
        @Query("deviceId") deviceId: String
    ): String

    @Headers("Content-Type: application/json")
    @POST("$cloudUrl/api/fcm/update-token")
    suspend fun updateToken(
        @Body request: UpdateTokenRequest
    ): String

    @GET("$BASE_URL/users-by-device")
    suspend fun getUsersByDeviceId(@Query("deviceId") deviceId: String): List<UserShopUserDTO>

    @POST("$BASE_URL/bind-main-user/{bindingKey}/{userId}")
    suspend fun bindMainUser(
        @Path("bindingKey") bindingKey: String,
        @Path("userId") userId: String
    )

    @Headers("Content-Type: application/json")
    @POST("$BASE_URL/invite")
    suspend fun createInvite(
        @Body request: CreateInviteRequestDTO
    )

    @Headers("Content-Type: application/json")
    @POST("$BASE_URL/accept-invite")
    suspend fun acceptInvite(
        @Body request: AcceptInviteRequestDTO
    )

    @Headers("Content-Type: application/json")
    @POST("$BASE_URL/delete-invite")
    suspend fun deleteInvite(
        @Body request: InviteInfoDTO
    )

    @Headers("Content-Type: application/json")
    @POST("$BASE_URL/refresh-invite")
    suspend fun refreshInvite(
        @Body request: InviteInfoDTO
    )

    @Headers("Content-Type: application/json")
    @POST("$BASE_URL/change-auth")
    suspend fun changeAuth(
        @Body request: ChangeAuthRequestDTO
    )


    @GET("$BASE_URL/get-store-info-by-uuid/{uuid}")
    suspend fun getStoreInfoByUUID(@Path uuid: String): ShopInfo?
}


@Serializable
data class UpdateTokenRequest(
    val firebaseUid: String,
    val fcmToken: String
)