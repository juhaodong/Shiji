package view.page.homePage.supplierManagePage.supplierlistpage.findSupplierPage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.composable.basic.button.BaseIconButton
import domain.composable.basic.wrapper.PageLoadingProvider
import domain.composable.filter.SearchTopBarProvider
import domain.supplier.SupplierViewModel
import domain.supplier.model.SupplierSetting
import view.page.homePage.supplierManagePage.SupplierDisplay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindSupplierPage(
    supplierViewModel: SupplierViewModel,
    chooseSupplier: (SupplierSetting) -> Unit,
    back: () -> Unit,
) {
    Scaffold(topBar = {
        SearchTopBarProvider(
            supplierViewModel.searching,
            searchText = supplierViewModel.searchText,
            onSearchTextChange = {
                supplierViewModel.searchText = it
            },
            dismiss = {
                supplierViewModel.searching = false
            }) {
            TopAppBar(
                navigationIcon = {
                    BaseIconButton(icon = Icons.Default.ArrowBack) {
                        back()
                    }
                },
                title = {
                    Text("搜索供应商", style = MaterialTheme.typography.bodyMedium)
                },
                actions = {
                    BaseIconButton(icon = Icons.Default.Search) {
                        supplierViewModel.searching = true
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            )
        }
    }) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            PageLoadingProvider(supplierViewModel.supplierLoading, onRefresh = {
                supplierViewModel.loadSuppliers()
            }, haveContent = supplierViewModel.filteredSupplier().isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    items(supplierViewModel.filteredSupplier()) {
                        SupplierDisplay(it) {
                            chooseSupplier(it)
                        }
                    }
                }
            }

        }
    }

}
