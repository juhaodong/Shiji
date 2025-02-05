package domain.user

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import kotlinx.serialization.Serializable
import modules.network.cloudUrl

private const val BASE_URL = "$cloudUrl/user"



interface CloudUserService {

    @Headers("Content-Type: application/json")
    @POST("$BASE_URL/sendOTP/{email}")
    suspend fun sendOTP(
        @Path("email") email: String
    )

    @Headers("Content-Type: application/json")
    @POST("$BASE_URL/loginUsingOTP")
    suspend fun loginUsingOTP(
        @Body request: OTPLoginRequest
    ): SaTokenInfo

    @Headers("Content-Type: application/json")
    @GET("$BASE_URL/logout/{token}")
    suspend fun logout(@Path("token") token: String)

    @Headers("Content-Type: application/json")
    @GET("$BASE_URL/info/{token}")
    suspend fun getUserInfoByToken(@Path("token") token: String): CloudUser

}

@Serializable
class SaTokenInfo(
    val tokenValue: String
)

@Serializable
class CloudUser(
    val email: String,
    val id: String
)

@Serializable
class OTPLoginRequest(val email: String, val otp: String)
