package domain.food.service

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import modules.network.cloudUrl

private const val BASE_URL = "$cloudUrl/api/food-logs"

interface FoodLogService {

    @Headers("Content-Type: application/json")
    @POST(BASE_URL)
    suspend fun saveFoodLog(@Body request: FoodLogRequest): FoodLog


    @DELETE("$BASE_URL/{id}")
    suspend fun deleteFoodLog(@Path("id") id: Long): Any

    @GET("$BASE_URL/search-by-uid")
    suspend fun findFoodLogsByUidAndDateRange(
        @Query("uid") uid: String,
        @Query("startDate") startDate: LocalDate,
        @Query("endDate") endDate: LocalDate
    ): List<FoodLog>
}


@Serializable
data class FoodLog(
    val imageUrl: String,
    val personCount: String,
    @Contextual
    val calories: BigDecimal,
    @Contextual
    val proteinGrams: BigDecimal,
    @Contextual
    val fatGrams: BigDecimal,
    @Contextual
    val carbohydrateGrams: BigDecimal,
    @Contextual
    val dietaryFiberGrams: BigDecimal,
    @Contextual
    val totalVegetablesGrams: BigDecimal,
    @Contextual
    val totalFruitsGrams: BigDecimal,
    @Contextual
    val waterIntakeMl: BigDecimal,
    @Contextual
    val sodiumMg: BigDecimal,
    val glycemicIndex: Int,
    val qualityRating: Int,
    val uid: String,
    val aiTips: String,
    val location: String,
    val note: String,
    val foodDescription: String,
    val id: String? = null,
    val createTimestamp: LocalDateTime
)

@Serializable
data class FoodLogRequest(
    val imageUrl: String,
    val personCount: String = "",
    val location: String = "",
    val note: String = "",
    val uid: String,
)