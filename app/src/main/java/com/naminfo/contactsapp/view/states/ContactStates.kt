package com.naminfo.contactsapp.states

import com.naminfo.contactsapp.model.data.Contacts

sealed class ContactStates {
    data class Loading(var message:String) : ContactStates()
    data class Success(var listOfContacts:List<Contacts>) : ContactStates()
    data class Failure(var message:String) : ContactStates()
}

