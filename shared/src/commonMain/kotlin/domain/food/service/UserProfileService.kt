package domain.food.service

import com.raedghazal.kotlinx_datetime_ext.now
import de.jensklingenberg.ktorfit.http.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import modules.network.cloudUrl


private const val BASE_URL = "$cloudUrl/api/user-profile"


interface UserProfileService {

    @POST("$cloudUrl/uploadFile")
    suspend fun uploadFile(@Body map: MultiPartFormDataContent): String

    @GET("$BASE_URL/{uid}")
    suspend fun getUserProfile(
        @Path("uid") uid: String
    ): UserProfile?

    @Headers("Content-Type: application/json")
    @POST(BASE_URL)
    suspend fun createOrUpdateUserProfile(
        @Body request: UserProfileRequest
    ): UserProfile

    @DELETE("$BASE_URL/{uid}")
    suspend fun deleteUserProfile(
        @Path("uid") uid: String
    )

    @GET(BASE_URL)
    suspend fun getAllUserProfiles(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): List<UserProfile>
}

@Serializable
data class UserProfile(
    val uid: String,  // 用户唯一标识
    val nickname: String,  // 昵称
    val birthDate: LocalDate?,  // 生日
    val exerciseIntensity: Int, // 运动强度等级（枚举值：1-轻度、2-中度、3-高强度）
    val height: String,  // 身高（单位：cm）
    val currentWeight: String,  // 当前体重（单位：kg）
    val targetWeight: String,  // 目标体重（单位：kg）
    val weightLossCycle: Int,  // 减重周期（单位：天）
    val startDate: LocalDate,
)


@Serializable
data class UserProfileEditDTO(
    val nickname: String,  // 昵称
    val birthDate: LocalDate?,  // 生日
    val exerciseIntensity: String, // 运动强度等级（枚举值：1-轻度、2-中度、3-高强度）
    val height: String,  // 身高（单位：cm）
    val currentWeight: String,  // 当前体重（单位：kg）
    val targetWeight: String,  // 目标体重（单位：kg）
    val weightLossCycle: String,  // 减重周期（单位：天）
) {

    fun toUserProfile(uid: String): UserProfileRequest {
        return UserProfileRequest(
            uid = uid,
            nickname = this.nickname,
            birthDate = this.birthDate,
            exerciseIntensity = this.exerciseIntensity.toIntOrNull() ?: 1,
            height = this.height.toDouble(),
            currentWeight = this.currentWeight.toDouble(),
            targetWeight = this.targetWeight.toDouble(),
            weightLossCycle = this.weightLossCycle.toIntOrNull() ?: 0,
            startDate = LocalDate.now()
        )
    }
}


@Serializable
data class UserProfileRequest(
    val uid: String,
    val nickname: String,
    val birthDate: LocalDate?,
    val height: Double,
    val currentWeight: Double,
    val targetWeight: Double,
    val weightLossCycle: Int,
    val startDate: LocalDate,

    val exerciseIntensity: Int
)