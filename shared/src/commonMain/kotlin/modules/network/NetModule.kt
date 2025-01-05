@file:OptIn(ExperimentalUuidApi::class)

package modules.network


import RouteName
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import dataLayer.serivce.ActivationService
import dataLayer.serivce.DishesService
import dataLayer.serivce.OrderService
import dataLayer.serivce.ReservationService
import dataLayer.serivce.RestaurantInfoService
import dataLayer.serivce.createActivationService
import dataLayer.serivce.createDishesService
import dataLayer.serivce.createOrderService
import dataLayer.serivce.createReservationService
import dataLayer.serivce.createRestaurantInfoService
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.ktorfit
import domain.dashboard.DashboardReportService
import domain.dashboard.createDashboardReportService
import domain.food.user.FoodLogService
import domain.food.user.NutritionService
import domain.food.user.UserProfileService
import domain.inventory.InventoryService
import domain.inventory.createInventoryService
import domain.purchaseOrder.PurchaseOrderService
import domain.purchaseOrder.createPurchaseOrderService
import domain.supplier.SupplierService
import domain.supplier.createSupplierService
import domain.user.StoreService
import domain.user.createStoreService
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

//const val cloudUrl = "https://cloud-v2.aaden.io"
const val cloudUrl = "http://192.168.178.41"

var startRoute = RouteName.HOME


@AppScope
@Component
abstract class NetModule {
    @AppScope
    @Provides
    protected fun orderService(ktorfit: Ktorfit): OrderService = ktorfit.createOrderService()

    @AppScope
    @Provides
    protected fun restaurantService(ktorfit: Ktorfit): RestaurantInfoService =
        ktorfit.createRestaurantInfoService()

    @AppScope
    @Provides
    protected fun dishService(ktorfit: Ktorfit): DishesService = ktorfit.createDishesService()

    @AppScope
    @Provides
    protected fun reservationService(ktorfit: Ktorfit): ReservationService =
        ktorfit.createReservationService()

    @AppScope
    @Provides
    protected fun userProfileService(ktorfit: Ktorfit): UserProfileService =
        ktorfit.create()

    @AppScope
    @Provides
    protected fun foodLogService(ktorfit: Ktorfit): FoodLogService =
        ktorfit.create()

    @AppScope
    @Provides
    protected fun nutritionService(ktorfit: Ktorfit): NutritionService =
        ktorfit.create()

    @AppScope
    @Provides
    protected fun service(ktorfit: Ktorfit): ActivationService = ktorfit.createActivationService()

    @AppScope
    @Provides
    protected fun storeService(ktorfit: Ktorfit): StoreService = ktorfit.createStoreService()

    @AppScope
    @Provides
    protected fun inventoryService(ktorfit: Ktorfit): InventoryService =
        ktorfit.createInventoryService()

    @AppScope
    @Provides
    protected fun supplierService(ktorfit: Ktorfit): SupplierService =
        ktorfit.createSupplierService()

    @AppScope
    @Provides
    protected fun purchaseOrderService(ktorfit: Ktorfit): PurchaseOrderService =
        ktorfit.createPurchaseOrderService()

    @AppScope
    @Provides
    protected fun dashboardReportService(ktorfit: Ktorfit): DashboardReportService =
        ktorfit.createDashboardReportService()

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
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(json, ContentType.Any)
            }
        })
    }
}
