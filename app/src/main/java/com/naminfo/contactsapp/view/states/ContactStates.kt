package com.naminfo.contactsapp.view.states

import com.naminfo.contactsapp.model.data.Contacts

sealed class ContactStates {
    data class Loading(var message:String) : ContactStates()
    data class Success(var listOfContacts:List<Contacts>) : ContactStates()
    data class Messages(var status:String,var message:String) : ContactStates()
    data class Failure(var message:String) : ContactStates()
}

