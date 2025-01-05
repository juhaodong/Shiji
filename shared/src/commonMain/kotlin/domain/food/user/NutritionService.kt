package domain.food.user

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import modules.network.cloudUrl

private const val BASE_URL = "$cloudUrl/api/nutrition-recommendation"

interface NutritionService {



    @GET("$BASE_URL/with-actual")
    suspend fun getNutritionData(
        @Query("uid") uid: String,
        @Query("startDate") startDate: LocalDate,
        @Query("endDate") endDate: LocalDate
    ): RecommendationAndActualResponse
}

@Serializable
data class RecommendationAndActualResponse(
    val nutritionRecommendation: NutritionRecommendation,
    val actualNutrition: AggregatedActualIntake
)



@Serializable
data class AggregatedActualIntake(
    @Contextual
    val totalCalories: BigDecimal,
    @Contextual
    val totalProtein: BigDecimal,
    @Contextual
    val totalFat: BigDecimal,
    @Contextual
    val totalCarbohydrates: BigDecimal,
    @Contextual
    val totalDietaryFiber: BigDecimal,
    @Contextual
    val totalVegetables: BigDecimal,
    @Contextual
    val totalFruits: BigDecimal,
    @Contextual
    val totalWaterIntake: BigDecimal,
    @Contextual
    val totalSodium: BigDecimal,
    val averageGlycemicIndex: Int,
    val averageQualityRating: Int,
)

@Serializable
data class NutritionRecommendation(
    @Contextual
    val recommendedCalories: BigDecimal,
    @Contextual
    val recommendedProtein: BigDecimal,
    @Contextual
    val recommendedFat: BigDecimal,
    @Contextual
    val recommendedCarbohydrates: BigDecimal,
    @Contextual
    val recommendedDietaryFiber: BigDecimal,
    @Contextual
    val recommendedVegetables: BigDecimal,
    @Contextual
    val recommendedFruits: BigDecimal,
    @Contextual
    val recommendedWaterIntake: BigDecimal,
    @Contextual
    val recommendedSodium: BigDecimal,
    val minimumQualityRating: Int
)