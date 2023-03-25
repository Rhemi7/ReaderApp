package com.example.readerapp.di

import com.example.readerapp.network.BooksAPI
import com.example.readerapp.repository.BookRepository
import com.example.readerapp.repository.FireRepository
import com.example.readerapp.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideBookRepository(api: BooksAPI) = BookRepository(api = api)

    @Singleton
    @Provides
    fun provideBookApi(): BooksAPI {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BooksAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideFireBookRepository() = FireRepository(queryBook = FirebaseFirestore.getInstance().collection("books"))

}