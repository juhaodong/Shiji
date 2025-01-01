package dataLayer.serivce

import dataLayer.model.ServantModel
import dataLayer.model.SourceMark
import dataLayer.model.order.BuffetDishesModel
import dataLayer.model.order.BuffetSettingModel
import dataLayer.model.order.ConsumeTypeModel
import dataLayer.model.payment.PayMethodModel
import dataLayer.model.restaurant.EbonEnableModel
import dataLayer.model.restaurant.FreeInformationModel
import dataLayer.model.restaurant.RestaurantInfoEntity
import dataLayer.model.restaurant.SimpleTableModel
import dataLayer.model.restaurant.TableModel
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import modules.network.IKResponse


interface RestaurantInfoService {
    @GET("Restaurant.php?op=view")
    suspend fun getRestaurantInfo(): IKResponse<List<RestaurantInfoEntity>>

    @GET("PayMethod.php?op=byLang")
    suspend fun getPaymentMethod(@Query("lang") lang: String?):
            IKResponse<List<PayMethodModel>>


    @GET("Servant.php?op=checkServant")
    suspend fun checkServant(@Query("pw") password: String): IKResponse<String>

    @GET("Servant.php?op=checkBoss")
    suspend fun checkBoss(@Query("pw") password: String): IKResponse<String>


    @GET("ConsumeType.php?op=showAllBuffetSetting")
    suspend fun getBuffetSettingList(): IKResponse<List<BuffetSettingModel>>

    @GET("ConsumeType.php?op=showBuffetPriceDishToConsumeTypeByConsumeType")
    suspend fun getBuffetDishes(
        @Query("lang") lang: String,
        @Query("consumeTypeId") consumeTypeId: Int
    ): IKResponse<List<BuffetDishesModel>>

    @GET("ConsumeType.php?op=showCurrentConsumeType")
    suspend fun getCurrentConsumeType(
        @Query("lang") lang: String,
    ): IKResponse<List<ConsumeTypeModel>>

    @GET("Tables.php")
    suspend fun getTableState(@Query("name") name: String): IKResponse<List<TableModel>>


    @GET("FreeInformation.php?op=showAllByLang")
    suspend fun showAllFreeInformation(@Query("lang") lang: String): IKResponse<List<FreeInformationModel>>


    @FormUrlEncoded
    @POST("FreeInformation.php?op=sendFreeInformation")
    suspend fun sendFreeInformation(
        @Field("tableId") tableId: Int,
        @Field("freeInformationId") freeInformationId: Int
    ): IKResponse<Any>


    @GET("Tables.php?op=showAllTableWithCells")
    suspend fun showTableList(): IKResponse<List<TableModel>>

    @GET("Tables.php?op=getById")
    suspend fun getTableStateById(@Query("id") tableId: Int): IKResponse<List<SimpleTableModel>>

    @GET("Complex.php?op=checkAadenPoint")
    suspend fun getEBonEnable(): IKResponse<EbonEnableModel>

    @GET("Servant.php")
    suspend fun getServantList(): IKResponse<List<ServantModel>>

    @GET("AccessLog.php?op=deviceId")
    suspend fun getDeviceId(): IKResponse<String>

    @GET("Complex.php?op=showAllSourceMarkOptions")
    suspend fun getSourceMarks(): IKResponse<List<SourceMark>>

    @FormUrlEncoded
    @POST("Restaurant.php?op=forceGetSystemSetting")
    suspend fun forceGetSystemSetting(
        @Field("systemSetting") systemSetting: String
    ): IKResponse<String?>

}

