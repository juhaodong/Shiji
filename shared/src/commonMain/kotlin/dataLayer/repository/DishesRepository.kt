package dataLayer.repository


import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.ImageBitmap
import com.kmpalette.loader.NetworkLoader
import dataLayer.model.dish.CategoryModel
import dataLayer.model.dish.DishesModel
import dataLayer.serivce.DishesService
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.http.Url
import me.tatarka.inject.annotations.Inject
import modules.GlobalSettingManager
import modules.network.AppScope
import modules.network.IKNetworkRequest
import modules.utils.FormatUtils.getExtraKeys

object ImageStore {
    private val store: MutableMap<String, ImageBitmap> = mutableMapOf()
    val loader = NetworkLoader(
        httpClient = HttpClient {
            install(HttpCache)
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 5)
                exponentialDelay()
            }
        },
    )

    suspend fun getOrLoad(url: String): ImageBitmap? {
        return if (store.contains(url)) {
            store[url]
        } else {
            if (url.endsWith('/') || url.endsWith("null")) {
                return null
            }
            try {
                val res = loader.load(Url(url))
                store[url] = res
                res
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

        }
    }

    fun get(url: String): ImageBitmap? {
        return store[url]
    }
}

object DishStore {
    val store: MutableMap<String, DishesModel> = mutableMapOf()
    fun get(code: String): DishesModel? {
        return store[code]
    }

    fun getOrPut(code: String, model: DishesModel): DishesModel {
        return store.getOrPut(code) { model }
    }

    fun set(code: String, model: DishesModel) {
        return store.set(code, model)
    }
}

@Inject
@AppScope
class DishesRepository(
    private val dishesService: DishesService,
    private val globalSettingManager: GlobalSettingManager
) {

    val dishes = mutableStateListOf<DishesModel>()
    val favoriteDishes = mutableStateListOf<DishesModel>()
    private val categoryCache = mutableMapOf<Int, List<CategoryModel>>()
    fun loadDishes(categoryList: List<CategoryModel>) {
        Napier.e("-->Load Dish START")
        val dishesTmp = getDishes(categoryList = categoryList)
        dishes.clear()
        dishes.addAll(dishesTmp)
        Napier.e("-->Load Dish Complete")
    }

    val extraKeyList = mutableListOf<String>()
    suspend fun prepareTheDishCache(
    ) {
        val refDishes =
            IKNetworkRequest.handleRequest { dishesService.getAllDishes(globalSettingManager.lang) }
                ?: listOf()
        refDishes.forEach { model ->
            DishStore.set(model.code.uppercase(), model)
        }
        extraKeyList.clear()
        extraKeyList.addAll(refDishes.map { it.code }
            .filter { it != "cashDeposit" && it != "cashWithdraw" }.getExtraKeys())
        favoriteDishes.clear()
        favoriteDishes.addAll(refDishes.filter { it.isFavorite == 1 })
        categoryCache[1] = getCategories(1, true)
        categoryCache[2] = getCategories(2, true)
    }

    fun findDish(code: String): DishesModel? {
        Napier.e("-->Start Find Dish")
        return DishStore.get(code.uppercase())
    }


    suspend fun getCategories(consumeTypeId: Int, force: Boolean = false): List<CategoryModel> {
        if (categoryCache.contains(consumeTypeId) && !force) {
            return categoryCache[consumeTypeId]!!
        } else {
            val categories =
                IKNetworkRequest.handleRequest {
                    dishesService.getCategories(
                        globalSettingManager.lang.uppercase(),
                        consumeTypeId = consumeTypeId
                    )
                } ?: listOf()
            val res = categories.filter { it.dishes.isNotEmpty() }
            return res
        }
    }

    fun getExtraKeys(): List<String> {
        return extraKeyList
    }


    private fun getDishes(categoryList: List<CategoryModel>): List<DishesModel> {
        val resultList = mutableStateListOf<DishesModel>()
        var counter = 0

        categoryList.forEach { categoryModel ->
            counter++
            categoryModel.dishes.forEach {
                val tmp = DishStore.getOrPut(it.code.uppercase(), it)
                tmp.isFree = it.isFree
                tmp.isActive = it.isActive
                tmp.name = it.name
                resultList.add(DishStore.getOrPut(it.code.uppercase(), it))
                counter++
            }
        }
        return resultList
    }
}