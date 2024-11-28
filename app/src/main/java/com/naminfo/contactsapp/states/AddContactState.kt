package com.naminfo.contactsapp.states

import com.naminfo.contactsapp.Contacts
import com.naminfo.contactsapp.ContactsAdd


sealed class AddContactState {
    data class Loading(var message:String) : AddContactState()
    data class Success(var listOfContacts:ContactsAdd) : AddContactState()
    data class Failure(var message:String) : AddContactState()

}