package com.naminfo.contactsapp.roomdatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.naminfo.contactsapp.model.data.Contacts

@Database(entities = [Contacts::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
}