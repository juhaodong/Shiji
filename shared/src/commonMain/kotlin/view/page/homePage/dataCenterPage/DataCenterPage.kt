package view.page.homePage.dataCenterPage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import domain.user.IdentityVM
import domain.user.StoreVM
import view.page.homePage.dataCenterPage.storeDetail.StoreDetails
import view.page.homePage.dataCenterPage.storeList.StoreList


@Composable
fun DataCenterPage(identityVM: IdentityVM, storeVM: StoreVM, toStatisticCenter: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize()


    ) {
        StoreList(identityVM = identityVM)

        StoreDetails(
            identityVM = identityVM,
            storeVM = storeVM,
            toStatisticCenter = toStatisticCenter
        )

    }
}



