package com.naminfo.contactsapp

import android.app.Application
import androidx.room.Room
import com.naminfo.contactsapp.roomdatabase.AppDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp :Application(){
    //lateinit var database: AppDatabase
    override fun onCreate() {
        super.onCreate()
     /*   database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "contacts_db"
        ).build()*/
    }
}