import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.runtime.Composable


@SuppressLint("StaticFieldLeak")
var currentContext: Activity? = null


@Composable
fun MainView() = App()
