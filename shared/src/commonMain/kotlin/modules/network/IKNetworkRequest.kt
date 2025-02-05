package modules.network


import io.github.aakira.napier.Napier
import io.ktor.client.request.request
import kotlinx.serialization.Serializable
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


@Serializable
data class IKResponse<T>(val status: String, val content: T, val info: String)

@Serializable
data class NormalResponse<T>(val code: Int, val data: T)

object IKNetworkRequest {
    suspend fun <T> handleRequest(
        shouldThrow: Boolean = false,
        reportError: Boolean = true,
        request: suspend () -> IKResponse<T>,
    ): T? {
        return try {
            val response = request()
            IkResponseConvertor.fetchFromResponse(response)
        } catch (e: Exception) {
            if (shouldThrow) {
                throw e
            } else {
                if (reportError) {
                    Napier.e(("NETWORK ERROR" + e.message))
                    Napier.e(e.stackTraceToString())
                }
            }
            null
        }
    }

    suspend fun <T> handleRequestWithError(
        request: suspend () -> IKResponse<T>,
    ): T {
        return try {
            val response = request()
            IkResponseConvertor.fetchFromResponse(response)
        } catch (e: Exception) {
            throw e
        }
    }

}

suspend fun <T> (suspend () -> T).safe(
    shouldThrow: Boolean = false,
    reportError: Boolean = true,
): T? {
    return try {
        this()
    } catch (e: Exception) {
        if (shouldThrow) {
            throw e
        } else {
            if (reportError) {
                Napier.e(("NETWORK ERROR" + e.message))
                Napier.e(e.stackTraceToString())
            }
        }
        null
    }
}


object SafeRequestScope {
    suspend fun <T> handleRequest(
        shouldThrow: Boolean = false,
        reportError: Boolean = true,
        request: suspend () -> T,
    ): T? {
        return try {
            request()
        } catch (e: Exception) {
            if (shouldThrow) {
                throw e
            } else {
                if (reportError) {
                    Napier.e(("NETWORK ERROR" + e.message))
                    Napier.e(e.stackTraceToString())
                }
            }
            null
        }
    }


    suspend fun <T> handleRequestWithError(
        request: suspend () -> T,
    ): T {
        return try {
            request()
        } catch (e: Exception) {
            throw e
        }
    }
}