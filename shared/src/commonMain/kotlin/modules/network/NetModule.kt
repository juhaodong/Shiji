@file:OptIn(ExperimentalUuidApi::class)

package modules.network


import RouteName
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.ktorfit
import domain.food.service.FoodLogService
import domain.food.service.NutritionService
import domain.food.service.UserProfileService
import domain.food.service.createFoodLogService
import domain.food.service.createNutritionService
import domain.food.service.createUserProfileService
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope
import modules.utils.BigDecimalSerializer
import modules.utils.UUIDSerializer
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Scope
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
annotation class AppScope

const val cloudUrl = "https://cloud-v2.aaden.io"
//const val cloudUrl = "http://192.168.178.41"

var startRoute = RouteName.HOME


@AppScope
@Component
abstract class NetModule {


    @AppScope
    @Provides
    protected fun userProfileService(ktorfit: Ktorfit): UserProfileService =
        ktorfit.createUserProfileService()

    @AppScope
    @Provides
    protected fun foodLogService(ktorfit: Ktorfit): FoodLogService =
        ktorfit.createFoodLogService()

    @AppScope
    @Provides
    protected fun nutritionService(ktorfit: Ktorfit): NutritionService =
        ktorfit.createNutritionService()


    @AppScope
    @Provides
    protected fun json(): Json = Json {
        coerceInputValues = true
        isLenient = true
        ignoreUnknownKeys = true
        encodeDefaults = true
        serializersModule = SerializersModule {
            contextual(BigDecimal::class, BigDecimalSerializer)
            contextual(Uuid::class, UUIDSerializer)
        }
    }


    @AppScope
    @Provides
    protected fun ktorfit(json: Json): Ktorfit = ktorfit {
        httpClient(HttpClient {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Napier.e(message)
                    }
                }
                level = LogLevel.INFO
            }
            install(ContentNegotiation) {
                json(json, ContentType.Any)
            }
        })
    }
}
