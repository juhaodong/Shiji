package domain.composable.basic.button

import aadenadmin.shared.generated.resources.Res
import aadenadmin.shared.generated.resources._Back
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource

@Composable
fun RowScope.GrowBackButton(back: () -> Unit) {
    MainActionGrowButton(
        text = stringResource(Res.string._Back),
        icon = Icons.AutoMirrored.Filled.ArrowBack
    ) {
        back()

    }
}