package com.peterchege.statussaver.domain.models

sealed class SaveResult<T : Any> {
    class Success<T: Any>(val msg: T):SaveResult<T>()

    class Failure<T: Any>(val msg: T):SaveResult<T>()
}