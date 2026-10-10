package com.thelazybattley.dinkspace.core.domain

sealed interface DataError : Error {
    enum class Remote : DataError {
        NO_INTERNET,
        UNAUTHORIZED,
        NOT_FOUND,
        SERVER,
        UNKNOWN,
    }

    enum class Local : DataError {
        DISK_FULL,
        UNKNOWN,
    }
}
