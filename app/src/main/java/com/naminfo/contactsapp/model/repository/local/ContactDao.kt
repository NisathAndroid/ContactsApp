package com.naminfo.contactsapp.model.repository.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.naminfo.contactsapp.model.data.Contacts

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts")
    suspend fun getAllContacts(): List<Contacts>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contacts: List<Contacts>)

    @Query("DELETE FROM contacts")
    suspend fun clearContacts()
}