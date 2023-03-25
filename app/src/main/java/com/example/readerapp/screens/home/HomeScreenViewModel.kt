package com.example.readerapp.screens.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readerapp.data.DataOrException
import com.example.readerapp.model.MBook
import com.example.readerapp.repository.BookRepository
import com.example.readerapp.repository.FireRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val repository: FireRepository) : ViewModel() {

    val dataR: MutableState<DataOrException<List<MBook>, Boolean, java.lang.Exception>> =
        mutableStateOf(
            DataOrException(listOf(), true, Exception(""))

        )

    init {
        getAllBooksFromDatabase()
    }

    private fun getAllBooksFromDatabase() {
        viewModelScope.launch {
            dataR.value.loading = true
            dataR.value = repository.getAllBooksFromDatabase()
            if(!dataR.value.data.isNullOrEmpty()) dataR.value.loading = false
            Log.d("Get Firestore Books", "getAllBooksFromDatabase: ${dataR.value.data?.toList().toString()}")

        }
    }
}