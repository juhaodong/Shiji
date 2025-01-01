package domain.supplier

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.raedghazal.kotlinx_datetime_ext.now
import domain.composable.dialog.basic.DialogViewModel
import domain.composable.dialog.form.AsyncOptionFormField
import domain.composable.dialog.form.StorageAmountFormField
import domain.composable.dialog.form.TextFormField
import domain.composable.dialog.selection.SelectOption
import domain.inventory.InventoryRepository
import domain.inventory.model.OrderStatus
import domain.purchaseOrder.PurchaseOrderService
import domain.purchaseOrder.model.OrderChangeLog
import domain.purchaseOrder.model.OrderItemDTO
import domain.purchaseOrder.model.PurchaseOrder
import domain.purchaseOrder.model.PurchaseOrderDTO
import domain.supplier.model.ImportOrderBookProductInfoDTO
import domain.supplier.model.OrderBookCategory
import domain.supplier.model.OrderBookDTO
import domain.supplier.model.OrderBookEditModel
import domain.supplier.model.OrderBookItemInfo
import domain.supplier.model.OrderBookProductEditModel
import domain.supplier.model.OrderBookProductInfoDTO
import domain.supplier.model.ProductDetailInfo
import domain.supplier.model.ProductImportedDTO
import domain.supplier.model.ProductTransFormDTO
import domain.supplier.model.ProductTransFormEditModel
import domain.supplier.model.SupplierOrderBookDTO
import domain.supplier.model.SupplierProductCategory
import domain.user.IdentityVM
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import me.tatarka.inject.annotations.Inject
import modules.GlobalSettingManager
import modules.network.AppScope
import modules.network.SafeRequestScope
import modules.utils.FormatUtils.sumOfB
import modules.utils.dateOnly

@Inject
@AppScope
class OrderBookViewModel(
    val dialogViewModel: DialogViewModel,
    val supplierService: SupplierService,
    val orderService: PurchaseOrderService,
    val globalSettingManager: GlobalSettingManager,
    val inventoryRepository: InventoryRepository,
    val identityVM: IdentityVM,
) : ViewModel() {
    var selectedOrderBook by mutableStateOf<SupplierOrderBookDTO?>(null)

    var orderBooks = mutableStateListOf<SupplierOrderBookDTO>()
    var orderBooksLoading by mutableStateOf(false)


    fun loadOrderBooks() {
        viewModelScope.launch {
            orderBooksLoading = true
            orderBooks.clear()
            orderBooks.addAll(
                supplierService.getOrderBooksForShop(globalSettingManager.selectedDeviceId)
            )
            selectedOrderBook?.orderBook?.let { chooseOrderBook(it.id) }
            orderBooksLoading = false
        }
    }

    var bookCategoryList by mutableStateOf<List<OrderBookCategory>>(emptyList())
    var bookCategoryListLoading by mutableStateOf(false)

    var bookProductList by mutableStateOf<List<OrderBookItemInfo>>(emptyList())
    var bookProductListLoading by mutableStateOf(false)
    val categoryIdIndexMap = mutableStateMapOf<Int, Long>()
    var activeCategoryId by mutableStateOf<Long?>(null)

    fun chooseOrderBook(orderBookId: Long) {
        selectedOrderBook = orderBooks.find { it.orderBook.id == orderBookId }
        loadBookCategoryList(orderBookId)
        loadBookProductList(orderBookId)
        loadSupplierProductCategories()
        loadOrderBookOrderList(orderBookId)
        loadChangeLogs(orderBookId)
    }

    var changeLogList by mutableStateOf<List<OrderChangeLog>>(emptyList())
    var changeLogLoading by mutableStateOf(false)

    // ... (Existing methods)

    private fun loadChangeLogs(orderBookId: Long) {
        viewModelScope.launch {
            changeLogLoading = true
            changeLogList = orderService.getChangeLogsForOrderBook(orderBookId)
            changeLogLoading = false
        }
    }


    var orderList by mutableStateOf<List<PurchaseOrder>>(emptyList())
    var orderListLoading by mutableStateOf(false)


    // ... (Existing methods)

    fun loadOrderBookOrderList(orderBookId: Long? = null) {
        viewModelScope.launch {
            orderListLoading = true
            (orderBookId ?: selectedOrderBook?.orderBook?.id)?.let {
                orderList = orderService.getOrderListByOrderBook(
                    it, OrderStatus.Active
                )
            }!!
            orderListLoading = false
        }
    }

    private fun loadBookCategoryList(orderBookId: Long) {
        viewModelScope.launch {
            bookCategoryListLoading = true
            bookCategoryList = supplierService.findOrderBookCategoriesByOrderBookId(orderBookId)
            bookCategoryListLoading = false
        }
    }

    suspend fun saveBookCategoryRequest(
        name: String,
        id: Long? = null,
        oldName: String = ""
    ): OrderBookCategory? {
        return SafeRequestScope.handleRequest {
            supplierService.createOrUpdateOrderBookCategory(
                OrderBookCategory(
                    name = oldName.ifBlank { name },
                    id = id,
                    orderBookId = selectedOrderBook!!.orderBook.id,
                    displayName = name,
                    createdBy = identityVM.currentUser!!.email!!,
                    lastUpdate = LocalDateTime.now()
                )
            )
        }
    }

    fun saveBookCategory(name: String, id: Long? = null, oldName: String = "") {
        viewModelScope.launch {
            saveBookCategoryRequest(name, id, oldName)
            loadBookCategoryList(selectedOrderBook!!.orderBook.id)
        }
    }


    fun deleteBookCategory(id: Long) {
        viewModelScope.launch {
            SafeRequestScope.handleRequest {
                supplierService.deleteOrderBookCategory(id)
                loadBookCategoryList(selectedOrderBook!!.orderBook.id)
            }
        }
    }

    var orderBookProductSearching by mutableStateOf(false)
    var orderBookProductSearchText by mutableStateOf("")

    fun startSearchProduct() {
        orderBookProductSearchText = ""
        orderBookProductSearching = true
    }

    fun filteredProduct(): List<OrderBookItemInfo> {
        return bookProductList.filter {
            (!orderBookProductSearching || orderBookProductSearchText.isBlank()) || it.getRealName()
                .contains(
                    orderBookProductSearchText
                )
        }
    }


    private fun loadBookProductList(orderBookId: Long) {
        viewModelScope.launch {
            bookProductListLoading = true
            val list = supplierService.findOrderBookProductsByOrderBookId(orderBookId)
            bookProductList = list.sortedBy { it.orderBookCategory.id }
            categoryIdIndexMap.clear()
            bookProductList.forEachIndexed { index, item ->
                val categoryId = item.orderBookCategory.id!!
                if (!categoryIdIndexMap.containsValue(categoryId)) {
                    categoryIdIndexMap[index] = categoryId
                }
            }
            bookProductListLoading = false
        }
    }


    val orderDict = mutableStateMapOf<Long, Int>()

    fun addItemToOrder(itemId: Long, count: Int = 1) {
        if (orderDict.containsKey(itemId)) {
            val currentCount = orderDict[itemId]!!
            val newCount = currentCount + count
            if (newCount > 0) {
                orderDict[itemId] = newCount
            } else {
                orderDict.remove(itemId) // Remove if count becomes 0 or negative
            }
        } else {
            if (count > 0) {
                orderDict[itemId] = count
            } // Ignore negative count if item not in order
        }
    }

    fun setCountInOrder(itemId: Long, count: String) {
        val newCount = count.toIntOrNull() ?: 0
        if (newCount != 0) {
            orderDict[itemId] = newCount
        } else {
            orderDict.remove(itemId)
        }

    }

    fun hasItemInOrder(itemId: Long): Boolean {
        return orderDict.containsKey(itemId)
    }

    fun orderItemList(): List<OrderBookItemInfo> {
        return bookProductList.filter { orderDict.containsKey(it.id) }
    }

    fun totalCount(): Int {
        return orderDict.values.sumOf { it }
    }

    fun total(): BigDecimal {
        return orderItemList().sumOfB { it.price * orderDict[it.id]!! }
    }

    var estimateOrderDate by mutableStateOf(
        LocalDate.now().plus(1, DateTimeUnit.DAY).dateOnly()
    )
    var note by mutableStateOf("")
    var createOrderLoading by mutableStateOf(false)
    fun createOrder(onSuccess: () -> Unit) {
        createOrderLoading = true
        viewModelScope.launch {
            val dto = PurchaseOrderDTO(
                orderBookId = selectedOrderBook!!.orderBook.id,
                orderList = orderDict.map { OrderItemDTO(it.key, it.value.toBigDecimal()) },
                estimateArriveDateTime = LocalDate.parse(estimateOrderDate)
                    .atTime(LocalTime.now()),
                note = note
            )
            SafeRequestScope.handleRequest(shouldThrow = false) {
                orderService.createOrder(dto)
                note = ""
                orderDict.clear()
                chooseOrderBook(selectedOrderBook!!.orderBook.id)
                onSuccess()
            }
            createOrderLoading = false
        }


    }

    fun updateOrderBookDetail() {
        viewModelScope.launch {
            val orderBookDTO = selectedOrderBook
            if (orderBookDTO != null) {
                val info = dialogViewModel.showFormDialog<OrderBookEditModel>(
                    TextFormField(
                        keyName = "displayName",
                        label = "显示名称",
                        defaultValue = orderBookDTO.displayName()
                    ),
                    TextFormField(
                        keyName = "customerReference",
                        label = "客户参考",
                        defaultValue = orderBookDTO.orderBook.customerReference
                    ),
                    title = "请输入新的信息",
                    subtitle = "您可以设置改供应商针对您的显示名称和客户参考"
                )
                SafeRequestScope.handleRequest {
                    supplierService.updateOrderBookInfo(
                        OrderBookDTO(
                            displayName = info.displayName,
                            customerReference = info.customerReference,
                            id = orderBookDTO.orderBook.id
                        )
                    )
                }
                loadOrderBooks()


            }

        }

    }

    /*# region supplier product*/
    var supplierProducts by mutableStateOf<List<ProductImportedDTO>>(emptyList())

    var supplierProductsLoading by mutableStateOf(false)

    var selectedCategoryId by mutableStateOf<Long?>(null)
    var supplierProductCategories by mutableStateOf<List<SupplierProductCategory>>(emptyList())
    var supplierProductCategoriesLoading by mutableStateOf(false)


    fun chooseCategory(categoryId: Long?) {
        selectedCategoryId = categoryId
        loadSupplierProducts()
    }

    val productImportedMap = mutableStateMapOf<Long, Boolean>()
    var supplierProductSearching by mutableStateOf(false)
    var supplierProductSearchText by mutableStateOf("")

    fun startSearchSupplierProduct() {
        chooseCategory(null)
        supplierProductSearchText = ""
        supplierProductSearching = true
    }

    fun filteredSupplierProduct(): List<ProductImportedDTO> {
        return supplierProducts.filter {
            (!supplierProductSearching || supplierProductSearchText.isBlank()) || it.product.name.contains(
                supplierProductSearchText
            )
        }
    }

    private fun loadSupplierProducts() {
        if (selectedOrderBook == null) return
        val id = selectedCategoryId
        supplierProductsLoading = true
        viewModelScope.launch {
            SafeRequestScope.handleRequest {
                if (id != null) {
                    supplierProducts = supplierService.findProductsByOrderBookId(
                        selectedOrderBook!!.orderBook.id, id
                    )
                } else {
                    supplierProducts =
                        supplierService.findProductsByOrderBookId(selectedOrderBook!!.orderBook.id)
                }
            }
            supplierProductsLoading = false

        }
    }


    fun saveImportResult(): Job? {
        if (selectedOrderBook == null) return null
        return viewModelScope.launch {
            SafeRequestScope.handleRequest {
                supplierService.importOrderBookProducts(
                    selectedOrderBook!!.orderBook.id,
                    ImportOrderBookProductInfoDTO(productImportedMap)
                )
            }
            productImportedMap.clear()
            chooseOrderBook(selectedOrderBook!!.orderBook.id)
        }
    }

    fun countDifferentValues(): Int {

        return productImportedMap.size
    }

    private fun loadSupplierProductCategories() {
        if (selectedOrderBook == null) return
        viewModelScope.launch {
            supplierProductCategoriesLoading = true
            supplierProductCategories =
                supplierService.findProductCategoriesByOrderBookId(selectedOrderBook!!.orderBook.id)
            supplierProductCategoriesLoading = false
        }
    }

    /**# endregion*/

    fun importSingleProduct(productId: Long, import: Boolean = true) {
        viewModelScope.launch {
            productImportedMap[productId] = import
            saveImportResult()?.join()
            loadProductDetail(productId)
        }

    }

    var productDetail by mutableStateOf<ProductDetailInfo?>(null)
    var productDetailLoading by mutableStateOf(false)
    fun loadProductDetail(id: Long) {
        viewModelScope.launch {
            productDetailLoading = true
            productDetail = SafeRequestScope.handleRequest {
                supplierService.getProductInfoById(selectedOrderBook!!.orderBook.id, id)
            }
            productDetailLoading = false
        }
    }


    fun updateProductTransform() {
        viewModelScope.launch {
            val detail = productDetail
            val itemInfo = detail?.itemInfo
            if (detail != null && itemInfo != null) {
                val resource = dialogViewModel.showSelectDialog("请选择一个原料",
                    inventoryRepository.getStorageItemList()
                        .map { SelectOption(label = it.name, value = it) })
                val info = dialogViewModel.showFormDialog<ProductTransFormEditModel>(
                    StorageAmountFormField(
                        keyName = "transFormAmount", unitList = resource.units
                    ),
                    title = "请输入每一个产品将会转化的数量",
                    subtitle = detail.product.name + "/" + detail.product.purchaseUnitName
                )
                productDetailLoading = true
                SafeRequestScope.handleRequest {
                    supplierService.saveTransFormInfo(
                        ProductTransFormDTO(
                            id = itemInfo.id,
                            transFormTo = resource.id,
                            transFormResourceName = resource.name,
                            transFormAmount = info.transFormAmount,
                            transFormUnitDisplay = resource.unitDisplay(info.transFormAmount),
                        )
                    )
                }
                loadProductDetail(detail.product.id)
            }
        }
    }

    fun updateProductDetail() {
        viewModelScope.launch {
            val detail = productDetail
            val itemInfo = detail?.itemInfo
            if (detail != null && itemInfo != null) {
                val info = dialogViewModel.showFormDialog<OrderBookProductEditModel>(
                    AsyncOptionFormField(
                        keyName = "categoryId",
                        defaultValue = itemInfo.orderBookCategory.id,
                        loadOptions = {
                            (SafeRequestScope.handleRequest {
                                supplierService.findOrderBookCategoriesByOrderBookId(
                                    selectedOrderBook!!.orderBook.id
                                )
                            } ?: emptyList()).fastMap {
                                SelectOption(
                                    label = it.getRealName(), value = it.id!!
                                )
                            }
                        },
                        addNewOption = {
                            val name = dialogViewModel.showInput("请输入分类名称")
                            val new = saveBookCategoryRequest(name)!!
                            SelectOption(label = new.getRealName(), value = new.id!!)
                        },
                        asyncScope = dialogViewModel::runInScope
                    ), TextFormField.getNoteField(defaultValue = itemInfo.note), TextFormField(
                        keyName = "overrideName",
                        required = false,
                        label = "显示名称",
                        defaultValue = itemInfo.note
                    ), title = "更改产品备注", subtitle = detail.product.name
                )
                productDetailLoading = true
                SafeRequestScope.handleRequest {
                    supplierService.saveOrderBookProductInfo(
                        OrderBookProductInfoDTO(
                            orderBookId = itemInfo.orderBookId,
                            productId = detail.product.id,
                            overrideName = info?.overrideName ?: "",
                            note = info?.note ?: "",
                            price = itemInfo.price,
                            categoryId = info.categoryId,
                            id = itemInfo.id
                        )
                    )
                }
                loadProductDetail(detail.product.id)
            }


        }
    }

}

val updateMessages = listOf(
    "新产品上架：新鲜草莓，现已开放订购！",
    "价格调整：由于市场波动，部分蔬菜价格有所调整，请查看最新价格表。",
    "订购指南更新：为了提高效率，我们更新了订购指南，请仔细阅读。",
    "系统维护通知：系统将于今晚23:00至次日凌晨01:00进行维护，期间可能无法访问，敬请谅解。",
    "配送延迟通知：由于天气原因，部分地区的配送可能会延迟，我们会尽快安排配送，请耐心等待。",
    "促销活动：本周订购满500元，即可享受9折优惠！",
    "新品推荐：优质牛肉，肉质鲜美，欢迎订购！",
    "缺货通知：由于供货紧张，部分产品暂时缺货，我们会尽快补货，敬请谅解。",
    "订单状态更新：您的订单#123456已确认，预计明天送达。",
    "支付方式更新：现已支持微信支付和支付宝支付，方便快捷。",
    "客服电话变更：我们的客服电话已变更为123-456-7890，欢迎咨询。",
    "节日放假通知：中秋节期间，我们将暂停配送服务，祝您节日快乐！",
    "意见反馈：欢迎您对我们的服务提出宝贵意见，我们会不断改进。",
    "新功能上线：新增批量订购功能，方便您快速下单。",
    "安全提示：请保管好您的账户信息，谨防诈骗。",
    "版本更新：系统已更新至最新版本，请及时更新。",
    "合作招募：诚邀优质供应商合作，共创美好未来。",
    "感谢信：感谢您一直以来的支持，我们会继续努力为您提供更好的服务。",
    "温馨提示：请您在下单前仔细核对订单信息，避免出错。",
    "常见问题解答：我们整理了一些常见问题，方便您快速解决疑问。",
    "配送范围扩大：现已覆盖更多地区，欢迎新用户加入！",
    "环保倡议：让我们一起行动起来，减少塑料袋的使用，保护环境。",
    "食品安全：我们严格把控食品质量，确保您的食品安全。",
    "售后服务：如有任何问题，请及时联系客服，我们会竭诚为您服务。",
    "用户协议更新：请您仔细阅读最新的用户协议，了解您的权益。",
    "隐私政策更新：请您仔细阅读最新的隐私政策，了解您的信息保护。",
    "数据安全：我们采取严格措施保护您的数据安全，请放心使用。",
    "平台规则更新：请您遵守最新的平台规则，共同维护良好的交易环境。",
    "联系我们：如有任何疑问，欢迎随时联系我们。",
    "关于我们：了解更多关于我们的信息，请访问我们的官网。"
)