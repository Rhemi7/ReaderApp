package com.example.readerapp.repository

import com.example.readerapp.data.DataOrException
import com.example.readerapp.model.Item
import com.example.readerapp.network.BooksAPI
import javax.inject.Inject

class BookRepository @Inject constructor(private val  api: BooksAPI) {
    private  val dataOrException = DataOrException<List<Item>, Boolean, java.lang.Exception>()

    private  val bookInfoDataOrException = DataOrException<Item, Boolean, java.lang.Exception>()


    suspend fun getBooks(searchQuery: String): DataOrException<List<Item>, Boolean, java.lang.Exception> {
        try {
            dataOrException.loading = true
            dataOrException.data = api.getAllBooks(searchQuery).items
            if(dataOrException.data!!.isNotEmpty()) dataOrException.loading = false
        } catch (e: java.lang.Exception) {
            dataOrException.e = e
        }
        return  dataOrException
    }

    suspend fun getBookInfo(bookId: String): DataOrException<Item, Boolean, Exception> {

        val response = try {
            bookInfoDataOrException.loading = true
            bookInfoDataOrException.data =  api.getBookInfo(bookId = bookId)
                if (bookInfoDataOrException.data.toString().isNotEmpty()) bookInfoDataOrException.loading = false else {}
        } catch (e: java.lang.Exception) {
            bookInfoDataOrException.e = e
        }
        return  bookInfoDataOrException
    }
}