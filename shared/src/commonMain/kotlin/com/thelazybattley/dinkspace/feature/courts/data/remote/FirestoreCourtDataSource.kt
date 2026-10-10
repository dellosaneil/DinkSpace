package com.thelazybattley.dinkspace.feature.courts.data.remote

import com.thelazybattley.dinkspace.core.data.firebase.firestoreCall
import com.thelazybattley.dinkspace.core.domain.DataError
import com.thelazybattley.dinkspace.core.domain.Result
import dev.gitlive.firebase.firestore.FirebaseFirestore

class FirestoreCourtDataSource(
    private val firestore: FirebaseFirestore,
) : CourtRemoteDataSource {

    override suspend fun fetchCourts(): Result<List<CourtDto>, DataError.Remote> = firestoreCall {
        firestore.collection(COLLECTION).get().documents.map { document ->
            document.data<CourtDto>().copy(id = document.id)
        }
    }

    private companion object {
        const val COLLECTION = "courts"
    }
}
