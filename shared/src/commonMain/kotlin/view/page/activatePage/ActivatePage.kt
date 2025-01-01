@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package view.page.activatePage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.user.IdentityVM
import view.UserProfileFragment

@Composable
fun ActivatePage(identityVM: IdentityVM, profileUpdated: () -> Unit) {

    Scaffold(
    ) { innerPadding ->
        Column(
            modifier = Modifier.consumeWindowInsets(innerPadding).padding(innerPadding)
                .padding(16.dp).fillMaxSize(),
        ) {
            UserProfileFragment(identityVM, profileUpdated)

        }

    }
}

