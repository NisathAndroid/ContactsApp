package com.naminfo.contactsapp.repository

import android.content.Context
import com.google.gson.Gson
import com.naminfo.contactsapp.Contacts
import com.naminfo.contactsapp.ContactsAdd
import retrofit2.Call
import javax.inject.Inject

class ContactRepository @Inject constructor(
   private val gson: Gson,
   private val  context: Context,
   private val  contactsApi: ContactsApi
) {
    suspend fun addContacts(contacts: ContactsAdd):Call<ContactsAdd> {
      return  contactsApi.addContacts(contacts)
    }
    suspend fun getContacts(): Call<List<Contacts>> {
       return contactsApi.getContacts()
    }
}