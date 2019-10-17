package app.example.rates.helpers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response

fun <T : Any> Response<T>.getResult(): Result<T> {
    return if (this.isSuccessful) {
        val body = this.body()

        if (body == null) {
            Result.Failure(Exception("no result"))
        } else {
            Result.Success(body)
        }
    } else {
        Result.Failure(HttpException(this))
    }
}

sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Failure(val error: Throwable?) : Result<Nothing>()
}

/**
 * [CoroutineScope] extension launching [block] every [msec]
 */
fun CoroutineScope.repeatDelayed(
    msec: Long,
    block: suspend CoroutineScope.() -> Unit
) = launch(this.coroutineContext) {
    while (true) {
        block.invoke(this)
        delay(msec)
    }
}