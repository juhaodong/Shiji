package domain.purchaseOrder

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import domain.composable.dialog.basic.DialogViewModel
import domain.inventory.InventoryRepository
import domain.inventory.model.OrderStatus
import domain.purchaseOrder.model.OrderAction
import domain.purchaseOrder.model.OrderChangeBasicDTO
import domain.purchaseOrder.model.ProductOrderSignDTO
import domain.purchaseOrder.model.PurchaseOrder
import domain.purchaseOrder.model.PurchaseOrderDetailDTO
import domain.purchaseOrder.model.TransformInfo
import domain.supplier.OrderBookViewModel
import domain.supplier.SupplierRepository
import domain.supplier.SupplierService
import domain.supplier.model.OrderBookItemInfo
import domain.supplier.model.OrderBookProductInfoDTO
import domain.user.IdentityVM
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import modules.GlobalSettingManager
import modules.network.AppScope
import modules.network.SafeRequestScope
import modules.utils.toByteArray

@AppScope
@Inject
class PurchaseOrderVM(
    val purchaseOrderService: PurchaseOrderService,
    val globalSettingManager: GlobalSettingManager,
    val inventoryRepository: InventoryRepository,
    val identityVM: IdentityVM,
    val dialogViewModel: DialogViewModel,
    val supplierService: SupplierService,
    val orderBookViewModel: OrderBookViewModel,
) : ViewModel() {
    var orderList by mutableStateOf(listOf<PurchaseOrder>())
    var orderListLoading by mutableStateOf(false)
    var selectedStatus by mutableStateOf(OrderStatus.Active)

    var orderCheckDict = mutableStateMapOf<Long, BigDecimal>()

    fun loadOrderList() {
        viewModelScope.launch {
            orderListLoading = true
            SafeRequestScope.handleRequest {
                orderList =
                    purchaseOrderService.getOrderList(
                        globalSettingManager.selectedDeviceId.toLong(),
                        selectedStatus
                    )
                orderCheckDict.clear()
            }

            orderListLoading = false
        }
    }

    var selectedOrderId by mutableStateOf(5L)
    var orderDetail by mutableStateOf<PurchaseOrderDetailDTO?>(null)
    var orderBookProductMap by mutableStateOf(mapOf<Long, OrderBookItemInfo>())
    var orderDetailLoading by mutableStateOf(false)

    fun setAmountForProduct(orderBookProductId: Long, amount: BigDecimal) {
        orderCheckDict[orderBookProductId] = amount
    }

    var signLoading by mutableStateOf(false)
    fun signOrder(imageBitmap: ImageBitmap): Job {
        signLoading = true
        val job = viewModelScope.launch {
            SafeRequestScope.handleRequest {
                val imageUrl = inventoryRepository.uploadFile(imageBitmap.toByteArray())!!
                val dto = ProductOrderSignDTO(
                    orderId = selectedOrderId,
                    imageUrl = imageUrl,
                    note = "",
                    signedBy = identityVM.currentUser!!.email!!,
                    itemAmountCheckDesc = orderCheckDict
                )
                purchaseOrderService.signOrder(dto)
                orderBookViewModel.loadOrderBookOrderList()
            }
            signLoading = false
        }
        return job
    }

    fun cancelOrder(orderId: Long) {
        viewModelScope.launch {
            SafeRequestScope.handleRequest {
                val note = dialogViewModel.showInput("请输入取消订单的理由")
                purchaseOrderService.orderAction(
                    OrderChangeBasicDTO(orderId = orderId, note = note),
                    OrderAction.Cancel
                )
                orderBookViewModel.loadOrderBookOrderList()
                chooseOrder()
            }
        }
    }

    fun archiveOrder(orderId: Long) {
        viewModelScope.launch {
            SafeRequestScope.handleRequest {
                purchaseOrderService.orderAction(
                    OrderChangeBasicDTO(orderId = orderId),
                    OrderAction.Archive
                )
                orderBookViewModel.loadOrderBookOrderList()
                chooseOrder()
            }
        }
    }

    fun chooseOrder() {
        viewModelScope.launch {
            orderDetailLoading = true
            SafeRequestScope.handleRequest {
                orderDetail = purchaseOrderService.getOrderDetail(selectedOrderId)
                orderBookProductMap =
                    supplierService.findOrderBookProductsByOrderBookId(orderDetail!!.orderBook.id)
                        .associateBy { it.id }
            }
            orderDetailLoading = false
        }
    }

    fun getTransformInfo(orderBookProductId: Long?, amount: BigDecimal): TransformInfo? {
        return orderBookProductId?.let {
            orderBookProductMap.get(it)
        }?.let {
            it.transFormTo?.let { _ ->
                TransformInfo(
                    it.transFormResourceName.ifBlank { it.getRealName() },
                    it.transFormUnitDisplay.ifBlank { it.product.purchaseUnitName },
                    amount
                )
            } ?: TransformInfo(it.getRealName(), it.product.purchaseUnitName, amount)
        }
    }


}