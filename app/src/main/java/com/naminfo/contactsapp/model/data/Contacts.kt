package com.naminfo.contactsapp.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "contacts")
data class Contacts(@PrimaryKey(autoGenerate = true)
                    val id: Int = 0,
                    @SerializedName("_id")
                    val _id: String? = null, // Retrofit's _id field
                    val name: String,
                    val phone: String,
                    val email: String)

data class ContactsAdd(val name:String,val phone:String,val email:String)
