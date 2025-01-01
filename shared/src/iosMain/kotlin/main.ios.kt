import com.moriatsushi.insetsx.WindowInsetsUIViewController
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier


actual fun getPlatformName(): String = "iOS"
fun debugBuild() {
    Napier.base(DebugAntilog())
}

fun MainViewController() = WindowInsetsUIViewController {
    App()
}

