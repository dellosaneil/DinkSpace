package com.thelazybattley.dinkspace.core.data.firebase

import com.thelazybattley.dinkspace.core.domain.DataError
import com.thelazybattley.dinkspace.core.domain.Result
import dev.gitlive.firebase.firestore.FirebaseFirestoreException
import dev.gitlive.firebase.firestore.FirestoreExceptionCode
import dev.gitlive.firebase.firestore.code
import kotlin.coroutines.cancellation.CancellationException

/** Runs a Firestore request and turns any thrown exception into a [DataError.Remote]. */
suspend inline fun <T> firestoreCall(block: suspend () -> T): Result<T, DataError.Remote> = try {
    Result.Success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: FirebaseFirestoreException) {
    Result.Failure(e.code.toDataError())
} catch (e: Exception) {
    Result.Failure(DataError.Remote.UNKNOWN)
}

fun FirestoreExceptionCode.toDataError(): DataError.Remote = when (this) {
    FirestoreExceptionCode.UNAVAILABLE,
    FirestoreExceptionCode.DEADLINE_EXCEEDED -> DataError.Remote.NO_INTERNET
    FirestoreExceptionCode.PERMISSION_DENIED,
    FirestoreExceptionCode.UNAUTHENTICATED -> DataError.Remote.UNAUTHORIZED
    FirestoreExceptionCode.NOT_FOUND -> DataError.Remote.NOT_FOUND
    FirestoreExceptionCode.INTERNAL,
    FirestoreExceptionCode.DATA_LOSS,
    FirestoreExceptionCode.RESOURCE_EXHAUSTED -> DataError.Remote.SERVER
    else -> DataError.Remote.UNKNOWN
}
