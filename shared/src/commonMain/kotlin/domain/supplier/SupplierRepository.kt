package domain.supplier

import domain.supplier.model.SupplierSetting
import me.tatarka.inject.annotations.Inject
import modules.GlobalSettingManager
import modules.network.AppScope
import modules.network.SafeRequestScope

@Inject
@AppScope
class SupplierRepository(
    private val service: SupplierService,
    val globalSettingManager: GlobalSettingManager
) {
    suspend fun getSuppliers(query: String): List<SupplierSetting> =
        SafeRequestScope.handleRequest {
            service.getSuppliers(
                query,
                globalSettingManager.selectedDeviceId
            )
        } ?: emptyList()

}