package dataLayer.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import me.tatarka.inject.annotations.Inject
import modules.network.AppScope

@AppScope
@Inject
class TimerRepository {

    val refreshTimerFlow = flow {
        var counter = 0
        while (true) {
            emit(counter++)
            delay(5000)
        }
    }
}