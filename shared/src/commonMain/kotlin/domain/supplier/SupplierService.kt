package domain.supplier

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import domain.supplier.model.ImportOrderBookProductInfoDTO
import domain.supplier.model.OrderBookCategory
import domain.supplier.model.OrderBookDTO
import domain.supplier.model.OrderBookItemInfo
import domain.supplier.model.OrderBookProductInfoDTO
import domain.supplier.model.ProductDetailInfo
import domain.supplier.model.ProductImportedDTO
import domain.supplier.model.ProductTransFormDTO
import domain.supplier.model.SupplierOrderBookDTO
import domain.supplier.model.SupplierProductCategory
import domain.supplier.model.SupplierSetting
import modules.network.cloudUrl

private const val BASE_URL = cloudUrl // Or your actual base URL
private const val ORDER_BOOK_CATEGORIES_URL = "$BASE_URL/orderBooks/categories" // New URL for
private const val ORDER_BOOK_PRODUCTS_URL = "$BASE_URL/orderBooks/products" // New URL for

interface SupplierService {
    @GET("$BASE_URL/suppliers/search/{shopId}")
    suspend fun getSuppliers(
        @Query("query") query: String,
        @Path("shopId") shopId: String
    ): List<SupplierSetting>


    @GET("$BASE_URL/supplier/{id}")
    suspend fun getSupplier(@Path("id") id: Long): SupplierSetting

    @POST("$BASE_URL/orderBooks/shop/{shopId}/supplier/{supplierId}")
    suspend fun bindSupplier(@Path("shopId") shopId: String, @Path("supplierId") supplierId: Long)

    @GET("$BASE_URL/orderBooks/shop/{shopId}")
    suspend fun getOrderBooksForShop(@Path("shopId") shopId: String): List<SupplierOrderBookDTO>


    @Headers("Content-Type: application/json")
    @POST("${BASE_URL}/orderBooks/save")
    suspend fun updateOrderBookInfo(@Body dto: OrderBookDTO)

    @Headers("Content-Type: application/json")
    @POST(ORDER_BOOK_CATEGORIES_URL)
    suspend fun createOrUpdateOrderBookCategory(@Body category: OrderBookCategory): OrderBookCategory

    @POST("$ORDER_BOOK_CATEGORIES_URL/delete/{id}")
    suspend fun deleteOrderBookCategory(@Path("id") id: Long)

    @GET("$ORDER_BOOK_CATEGORIES_URL/{id}")
    suspend fun findOrderBookCategoryById(@Path("id") id: Long): OrderBookCategory?


    @GET("$ORDER_BOOK_CATEGORIES_URL/orderBook/{orderBookId}")
    suspend fun findOrderBookCategoriesByOrderBookId(@Path("orderBookId") orderBookId: Long): List<OrderBookCategory>

    @Headers("Content-Type: application/json")
    @POST(ORDER_BOOK_PRODUCTS_URL)
    suspend fun saveOrderBookProductInfo(@Body dto: OrderBookProductInfoDTO): OrderBookItemInfo

    @POST("$ORDER_BOOK_PRODUCTS_URL/delete/{id}")
    suspend fun deleteOrderBookProductInfo(@Path("id") id: Long)

    @Headers("Content-Type: application/json")
    @POST("$ORDER_BOOK_PRODUCTS_URL/importProduct/{orderBookId}")
    suspend fun importOrderBookProducts(
        @Path("orderBookId") orderBookId: Long,
        @Body dto: ImportOrderBookProductInfoDTO
    )

    @GET("$ORDER_BOOK_PRODUCTS_URL/orderBook/{orderBookId}")
    suspend fun findOrderBookProductsByOrderBookId(@Path("orderBookId") orderBookId: Long): List<OrderBookItemInfo>


    @GET("$ORDER_BOOK_PRODUCTS_URL/byId/{orderBookId}/{id}")
    suspend fun getProductInfoById(
        @Path("orderBookId") orderBookId: Long,
        @Path("id") id: Long
    ): ProductDetailInfo

    @GET("$ORDER_BOOK_PRODUCTS_URL/category/{categoryId}")
    suspend fun findOrderBookProductsByCategoryId(@Path("categoryId") categoryId: Long): List<OrderBookItemInfo>


    @GET("$BASE_URL/supplier/supplierProducts/orderBooks/{orderBookId}/{categoryId}")
    suspend fun findProductsByOrderBookId(
        @Path("orderBookId") orderBookId: Long,
        @Path("categoryId") categoryId: Long
    ): List<ProductImportedDTO>

    @GET("$BASE_URL/supplier/supplierProducts/orderBooks/{orderBookId}")
    suspend fun findProductsByOrderBookId(
        @Path("orderBookId") orderBookId: Long,
    ): List<ProductImportedDTO>


    @GET("$BASE_URL/supplier/supplierProductCategories/orderBooks/{orderBookId}") // Assuming this is the correct URL
    suspend fun findProductCategoriesByOrderBookId(@Path("orderBookId") orderBookId: Long): List<SupplierProductCategory>

    @Headers("Content-Type: application/json")
    @POST("$ORDER_BOOK_PRODUCTS_URL/transform")
    suspend fun saveTransFormInfo(@Body dto: ProductTransFormDTO): OrderBookItemInfo

}

