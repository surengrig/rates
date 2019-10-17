package app.example.rates.ui.common

sealed class ViewState<T> {
    class Loading<T>(val data: T?) : ViewState<T>()
    class Success<T>(val data: T) : ViewState<T>()
    class Failure<T>(val data: T? = null, val error: Throwable?) : ViewState<T>()
}