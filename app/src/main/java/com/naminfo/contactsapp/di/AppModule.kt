package com.naminfo.contactsapp.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.naminfo.contactsapp.model.repository.remote.ContactRepository
import com.naminfo.contactsapp.model.repository.remote.ContactsApi
import com.naminfo.contactsapp.model.repository.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val BASE_URL = "https://contacts-app-g7le.onrender.com"
    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Singleton
    @Provides
    fun provideContactsAPI(retrofit: Retrofit): ContactsApi =
        retrofit.create(ContactsApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "contacts_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideContactDao(database: AppDatabase) = database.contactDao()

    @Provides
    fun provideGetContext(@ApplicationContext context: Context): Context = context
    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder().setLenient().create()

    @Singleton
    @Provides
    fun provideContactsRepo(gson: Gson, @ApplicationContext context: Context,contactsApi: ContactsApi): ContactRepository = ContactRepository(gson,context,contactsApi)
}