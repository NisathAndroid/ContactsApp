package com.naminfo.contactsapp.repository

import com.naminfo.contactsapp.model.data.Contacts
import com.naminfo.contactsapp.model.data.ContactsAdd
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ContactsApi {
    @GET("/contacts")
    fun getContacts(): Call<List<Contacts>>

    @POST("/contacts")
    fun addContacts(@Body contact: ContactsAdd): Call<ContactsAdd>

    @PUT("/contacts/{id}")
    fun updateContact(@Path("id") id: String, @Body contact: Contacts): Call<Contacts>

    @DELETE("/contacts/{id}")
    fun deleteContact(@Path("id") id: String): Call<Void>
}