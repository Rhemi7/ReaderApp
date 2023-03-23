package com.example.readerapp.screens.search

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readerapp.data.DataOrException
import com.example.readerapp.data.Resource
import com.example.readerapp.model.Item
import com.example.readerapp.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: BookRepository) : ViewModel(){
    var listOfBooks: MutableState<DataOrException<List<Item>, Boolean, java.lang.Exception>> =
        mutableStateOf(DataOrException(null, true, Exception("")))

    var list: List<Item> by mutableStateOf(listOf())
    var isLoading: Boolean by mutableStateOf(false)

    init {
        loadBooks()
    }

    private fun loadBooks() {
        searchBooks("android")
    }

    fun searchBooks(query: String) {
        viewModelScope.launch {
            isLoading = true
            if(query.isEmpty()) {
                return@launch
            }
            try {
                when (val response = repository.getBooks2(query)) {
                    is Resource.Success -> {
                        list = response.data!!
                        if(list.isNotEmpty()) isLoading = false
                    }
                    is Resource.Error -> {
                        Log.e("Books Error", "searchBooks: Failed Getting Books", )
                    }
                    else -> {isLoading = false}
                }
            } catch (ex: java.lang.Exception) {
                Log.e("Books Error", "searchBooks: ${ex.message.toString()}", )
                isLoading = false
            }
        }
    }

}