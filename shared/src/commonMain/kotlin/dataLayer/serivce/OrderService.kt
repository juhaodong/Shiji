package dataLayer.serivce


import dataLayer.model.dish.OrderDishDTO
import dataLayer.model.oldOrder.OldOrderDTO
import dataLayer.model.order.CurrentOrderInfoDTO
import dataLayer.model.order.OrderInfoDTO
import dataLayer.model.order.OrderInfoModel
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import modules.network.IKResponse

interface OrderService {

    @GET("Tables.php?op=currentInfo")
    suspend fun getTableInfo(
        @Query("id") id: Int,
        @Query("sourceMark") sourceMark: String? = null
    ): IKResponse<CurrentOrderInfoDTO>

    @GET("Orders.php?op=showOne")
    suspend fun getOrderInfoByOrderId(
        @Query("id") orderId: Int,
        @Query("chaos") chaos: String,
    ): IKResponse<List<OrderInfoModel>>

    @FormUrlEncoded
    @POST("Complex.php?op=addDishesToTable")
    suspend fun addDishesToTable(
        @Field("tableId") tableId: Int,
        @Field("params") dishes: String,
        @Field("printingKitchenBon") printingKitchenBon: Int,
    ): IKResponse<String>


    @GET("Orders.php?op=printZwichenBonUseDishesList")
    suspend fun printTemporaryBonWithDishes(
        @Query("tableId") tableId: Int,
        @Query("dishes") dishes: String,
    )

    @FormUrlEncoded
    @POST("BackendData.php?op=reprintOrder")
    suspend fun printTemporaryBon(
        @Field("id") orderId: Int,
        @Field("withTitle") withTitle: Int = 0,
        @Field("printCount") printCount: Int = 1,
    )

    @GET("BackendData.php?op=mobileV3StatWithLang")
    suspend fun getOneDayStatData(
        @Query("lang") lang: String, @Query("date") date: String,
        @Query("pw") password: String,
    ): IKResponse<OldOrderDTO>

    @GET("Complex.php?op=dishesInTableWithoutBuffet")
    suspend fun getBillDishes(
        @Query("tableId") tableId: Int,
        @Query("lang") lang: String,
        @Query("sourceMark") sourceMark: String? = null
    ): IKResponse<List<OrderDishDTO>>


    @GET("Complex.php?op=resetTableCallStatus")
    suspend fun resetTable(
        @Query("tableId") tableId: Int,
    ): IKResponse<String>

    @GET("Complex.php?op=dishesInJpTable")
    suspend fun getBuffetOrderList(
        @Query("tableId") tableId: Int,
        @Query("lang") lang: String,
        @Query("sourceMark") sourceMark: String?
    ): IKResponse<List<OrderDishDTO>>

    @POST("Orders.php?op=updateMetaData")
    @FormUrlEncoded
    suspend fun updateMetaData(
        @Field("orderId") orderId: Int,
        @Field("metaData") metaData: String,
    ): IKResponse<String>


    @FormUrlEncoded
    @POST("Complex.php?op=checkOut")
    suspend fun checkOut(
        @Field("tableId") tableId: Int,
        @Field("withTitle") withTitle: Int = 0,
        @Field("printCount") printCount: Int = 1,
        @Field("pw") password: String,
        @Field("paymentLog") paymentLog: String,
        @Field("overrideCardTerminalIp") overrideCardTerminalIp: String,
    ): IKResponse<String>

    @FormUrlEncoded
    @POST("Complex.php?op=setDiscount")
    suspend fun setDiscount(
        @Field("tableId") tableId: Int,
        @Field("discountStr") discountStr: String
    ): IKResponse<String>


    @FormUrlEncoded
    @POST("Complex.php?op=splitOrder")
    suspend fun splitOrder(
        @Field("tableId") tableId: Int,
        @Field("withTitle") withTitle: Int = 0,
        @Field("printCount") printCount: Int = 1,
        @Field("pw") password: String,
        @Field("paymentLog") paymentLog: String,
        @Field("dishes") dishes: String,
        @Field("discountStr") discountStr: String,
        @Field("overrideCardTerminalIp") overrideCardTerminalIp: String,
    ): IKResponse<String>


    @FormUrlEncoded
    @POST("Complex.php?op=forceOpenJapanBuffetTableV2")
    suspend fun openBuffetTable(
        @Field("pw") password: String,
        @Field("tableName") tableName: String,
        @Field("buffetPriceDishes") buffetPriceDishes: String,
        @Field("extraJson") extraJson: String,
        @Field("metaData") metaData: String,
        @Field("overrideConsumeTypeId") consumeTypeId: Int?,
    ): IKResponse<OrderInfoDTO>

    @FormUrlEncoded
    @POST("Complex.php?op=openTakeawayTable")
    suspend fun openTakeawayTable(
        @Field("pw") password: String,
        @Field("metaData") metaData: String,
    ): IKResponse<OrderInfoDTO>

    @FormUrlEncoded
    @POST("Tables.php?op=change")
    suspend fun changeTable(
        @Field("oldTableName") oldTableName: String,
        @Field("newTableName") newTableName: String
    ): IKResponse<Int>

    @FormUrlEncoded
    @POST("Complex.php?op=dishesChangeTable")
    suspend fun dishesChangeTable(
        @Field("oldTableName") oldTableName: String,
        @Field("dishes") dishes: String,
        @Field("newTableName") newTableName: String
    ): IKResponse<Any>

    @FormUrlEncoded
    @POST("Complex.php?op=deleteDishes")
    suspend fun deleteDishes(
        @Field("dishes") dishes: String,
        @Field("tableId") tableId: Int,
        @Field("reason") reason: String
    ): IKResponse<Any>


    @FormUrlEncoded
    @POST("Dishes.php?op=setDiscountToDishes")
    suspend fun setDiscountToDishes(
        @Field("dishes") dishes: String,
        @Field("orderId") orderId: Int,
        @Field("discountStr") discountStr: String
    ): IKResponse<List<String?>>

    @FormUrlEncoded
    @POST("Tables.php?op=mergeTables")
    suspend fun mergeTables(
        @Field("oldTableName") oldTableName: String,
        @Field("newTableName") newTableName: String
    ): IKResponse<Int>

    @FormUrlEncoded
    @POST("Orders.php?op=acceptTakeawayOrder")
    suspend fun acceptOrder(
        @Field("tableId") tableId: Int,
        @Field("reason") reason: String
    ): IKResponse<String>

    @FormUrlEncoded
    @POST("Orders.php?op=rejectTakeAwayOrder")
    suspend fun rejectOrder(
        @Field("tableId") tableId: Int,
        @Field("reason") reason: String
    ): IKResponse<String>


    @Headers("Content-Type: application/json")
    @POST("https://sendinvoicemail-evhwqutgjq-ey.a.run.app")
    suspend fun sendInvoiceEmail(
        @Body sendDTO: String,
    )

}

