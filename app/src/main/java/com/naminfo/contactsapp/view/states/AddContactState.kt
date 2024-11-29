package com.naminfo.contactsapp.view.states

import com.naminfo.contactsapp.model.data.ContactsAdd


sealed class AddContactState {
    data class Loading(var message:String) : AddContactState()
    data class Success(var listOfContacts: ContactsAdd) : AddContactState()
    data class Failure(var message:String) : AddContactState()

}