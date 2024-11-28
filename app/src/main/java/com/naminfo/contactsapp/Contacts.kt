package com.naminfo.contactsapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contacts(@PrimaryKey(autoGenerate = true) val id: Int = 0,
                    val name: String,
                    val phone: String,
                    val email: String)

data class ContactsAdd(val name:String,val phone:String,val email:String)
