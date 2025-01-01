import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.materialkolor.PaletteStyle
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.KmpComponentCreate
import modules.network.NetModule
import modules.utils.DialogManager
import theme.CurrentTheme
import theme.colorsSets


data class UrlProvider(
    val resourceUrl: String = "",
    val dishImgUrl: String = "",
    var useGridView: Boolean = false
)

val LocalUrl = compositionLocalOf { UrlProvider() }
val LocalTheme =
    compositionLocalOf {
        CurrentTheme(
            darkMode = false,
            currentColor = colorsSets[0],
            currentStyle = PaletteStyle.Rainbow
        )
    }


val LocalDialogManager = compositionLocalOf { DialogManager() }

@Component
abstract class ApplicationComponent : NetModule() {
    abstract val home: AppBase
}

@KmpComponentCreate
expect fun createKmp(): ApplicationComponent

@Composable
fun App() {
    var restarting by remember { mutableStateOf(false) }
    if (!restarting) {
        Napier.base(DebugAntilog())

        Napier.e("TestTest")
        val applicationComponent = createKmp()
        applicationComponent.home()
    } else {
        Box(modifier = Modifier.fillMaxSize().imePadding()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

}

expect fun getPlatformName(): String