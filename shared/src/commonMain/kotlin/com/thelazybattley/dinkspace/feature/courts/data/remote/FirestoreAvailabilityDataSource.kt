package com.thelazybattley.dinkspace.feature.courts.data.remote

import com.thelazybattley.dinkspace.core.data.firebase.firestoreCall
import com.thelazybattley.dinkspace.core.domain.DataError
import com.thelazybattley.dinkspace.core.domain.Result
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.datetime.LocalDate

class FirestoreAvailabilityDataSource(
    private val firestore: FirebaseFirestore,
) : AvailabilityRemoteDataSource {

    override suspend fun fetchAvailability(date: LocalDate): Result<List<AvailabilityDto>, DataError.Remote> =
        firestoreCall {
            firestore.collection(COLLECTION)
                .where { "date" equalTo date.toString() }
                .get()
                .documents
                .map { it.data<AvailabilityDto>() }
        }

    private companion object {
        const val COLLECTION = "availability"
    }
}
