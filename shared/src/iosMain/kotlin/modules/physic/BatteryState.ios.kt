package modules.physic


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.intl.Locale
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceBatteryState
import platform.UIKit.UINavigationBar
import platform.UIKit.UIScreen
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.UIView
import platform.UIKit.UIWindow
import platform.UIKit.setStatusBarStyle


actual fun getCurrentBatteryState(): BatteryState {
    val currentUIDevice = UIDevice.currentDevice
    currentUIDevice.batteryMonitoringEnabled = true
    return BatteryState(
        percentage = (currentUIDevice.batteryLevel * 100).toInt(),
        charging = currentUIDevice.batteryState === UIDeviceBatteryState.UIDeviceBatteryStateCharging
    )

}

actual fun changeLocale(locale: Locale) {}


@Composable
private fun statusBarView() = remember {
}


@Composable
actual fun PlatformColors(
    darkMode: Boolean,
) {

    val backgroundColor = MaterialTheme.colorScheme.background.toUIColor()
    UIApplication.sharedApplication.setStatusBarStyle(if(darkMode) UIStatusBarStyleDarkContent else UIStatusBarStyleLightContent)
    SideEffect {
        UINavigationBar.appearance().backgroundColor = backgroundColor
    }
}


private fun Color.toUIColor(): UIColor = UIColor(
    red = this.red.toDouble(),
    green = this.green.toDouble(),
    blue = this.blue.toDouble(),
    alpha = this.alpha.toDouble()
)