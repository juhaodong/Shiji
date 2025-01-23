package modules

import AppBase
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.materialkolor.PaletteStyle
import com.russhwolf.settings.Settings
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Inject
import modules.network.AppScope
import modules.network.NetModule
import theme.colorsSets
import kotlin.reflect.KProperty

@Component
abstract class ApplicationComponent : NetModule() {
    abstract val home: AppBase
}

class StringPD(
    private val defaultValue: String,
    private val manager: GlobalSettingManager
) {

    private var currentValue by mutableStateOf(defaultValue)

    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        currentValue = manager.stringGetter(property.name, defaultValue)
        return currentValue
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        manager.stringSetter(property.name, value)
    }
}

class IntPD(
    private val defaultValue: Int,
    private val manager: GlobalSettingManager
) {

    private var currentValue by mutableStateOf(defaultValue)

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        currentValue = manager.stringGetter(property.name, defaultValue.toString()).toInt()
        return currentValue
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        manager.stringSetter(property.name, value.toString())
    }
}


class BooleanPD(
    private val defaultValue: Boolean, private val manager: GlobalSettingManager
) {
    private var currentValue by mutableStateOf(defaultValue)

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        currentValue = manager.booleanGetter(property.name, defaultValue)
        return currentValue
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        manager.booleanSetter(property.name, value)
    }
}


@AppScope
@Inject
class GlobalSettingManager {

    private val defaultValue = "192.168.168.1"
    var ip by StringPD(defaultValue, this)

    var lang: String by StringPD("ZH", this)
    var selectedDeviceId: String by StringPD("", this)


    var darkMode: Boolean by BooleanPD(false, this)


    var currentColorSchemeId: String by StringPD(colorsSets[0].value.toString(), this)


    var dishFallBackLanguage: String by StringPD("EN", this)
    var currentPaletteStyle: String by StringPD(
        PaletteStyle.Rainbow.toString(),
        this
    )
    var showHealthAdvice: Boolean by BooleanPD(true, this)

    private fun getUrl(): String {
        return "http://$ip/"
    }


    fun getImgUrl(): String {
        return getResourceUrl() + "dishImg/"
    }

    fun getResourceUrl(): String {
        return getUrl() + "Resource/"
    }


    private val dataStore: Settings = Settings()

    fun stringGetter(key: String, defaultValue: String): String {

        return dataStore.getString(key, defaultValue)


    }

    fun booleanGetter(key: String, defaultValue: Boolean): Boolean {
        return dataStore.getBoolean(key, defaultValue)
    }

    fun stringSetter(key: String, v: String) {
        dataStore.putString(key, v)

    }

    fun booleanSetter(key: String, v: Boolean) {
        dataStore.putBoolean(key, v)
    }

}