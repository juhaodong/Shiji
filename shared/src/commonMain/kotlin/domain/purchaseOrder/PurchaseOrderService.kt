package domain.purchaseOrder

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import domain.inventory.model.OrderStatus
import domain.purchaseOrder.model.OrderAction
import domain.purchaseOrder.model.OrderChangeBasicDTO
import domain.purchaseOrder.model.OrderChangeLog
import domain.purchaseOrder.model.ProductOrderSignDTO
import domain.purchaseOrder.model.PurchaseOrder
import domain.purchaseOrder.model.PurchaseOrderDTO
import domain.purchaseOrder.model.PurchaseOrderDetailDTO
import modules.network.cloudUrl

private const val BASE_URL = "$cloudUrl/inventory/productOrder"

interface PurchaseOrderService {

    @GET("$BASE_URL/list/{shopId}/Active")
    suspend fun activeShopOrderList(
        @Path("shopId") shopId: Long,
    ): List<PurchaseOrder>

    @GET("$BASE_URL/list/{shopId}/{status}")
    suspend fun getOrderList(
        @Path("shopId") shopId: Long,
        @Path("status") status: OrderStatus,
    ): List<PurchaseOrder>

    @GET("$BASE_URL/detail/{orderId}")
    suspend fun getOrderDetail(@Path("orderId") orderId: Long): PurchaseOrderDetailDTO

    @GET("$BASE_URL/changeLog/{orderBookId}")
    suspend fun getChangeLogsForOrderBook(
        @Path("orderBookId") orderBookId: Long,
    ): List<OrderChangeLog>


    @GET("$BASE_URL/byOrderBook/{orderBookId}/{status}")
    suspend fun getOrderListByOrderBook(
        @Path("orderBookId") orderBookId: Long,
        @Path("status") status: OrderStatus
    ): List<PurchaseOrder>

    @Headers("Content-Type: application/json")
    @POST("$BASE_URL/save")
    suspend fun createOrder(
        @Body dto: PurchaseOrderDTO,
    )

    @Headers("Content-Type: application/json")
    @POST("$BASE_URL/action/{orderAction}")
    suspend fun orderAction(
        @Body dto: OrderChangeBasicDTO,
        @Path("orderAction") orderAction: OrderAction,
    )

    @Headers("Content-Type: application/json")
    @POST("$BASE_URL/sign")
    suspend fun signOrder(
        @Body dto: ProductOrderSignDTO
    )
}

