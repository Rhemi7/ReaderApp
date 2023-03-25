package com.example.readerapp.repository

import com.example.readerapp.data.DataOrException
import com.example.readerapp.model.MBook
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FireRepository @Inject constructor(private val queryBook: Query) {

    suspend fun getAllBooksFromDatabase(): DataOrException<List<MBook>, Boolean, java.lang.Exception> {
        val dataOrException = DataOrException<List<MBook>, Boolean, java.lang.Exception>()

        try {

            dataOrException.loading = true
            dataOrException.data = queryBook.get().await().documents.map { documentSnapshot ->
                documentSnapshot.toObject(MBook::class.java)!!
            }

            if (!dataOrException.data.isNullOrEmpty()) dataOrException.loading = false
        } catch (e: java.lang.Exception) {
            dataOrException.e = e
        }

        return  dataOrException
    }
}