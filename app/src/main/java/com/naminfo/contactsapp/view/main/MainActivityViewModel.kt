package com.naminfo.contactsapp.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naminfo.contactsapp.TAG
import com.naminfo.contactsapp.model.Contacts
import com.naminfo.contactsapp.model.ContactsAdd
import com.naminfo.contactsapp.repository.ContactRepository
import com.naminfo.contactsapp.repository.ContactsApi
import com.naminfo.contactsapp.roomdatabase.AppDatabase
import com.naminfo.contactsapp.states.AddContactState
import com.naminfo.contactsapp.states.ContactStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val database: AppDatabase,
    private val contactsApi: ContactsApi,
    private val repo: ContactRepository
) :
    ViewModel() {

    private val _contactsRoom = MutableLiveData<List<Contacts>>()
    val contactsRoom: LiveData<List<Contacts>> get() = _contactsRoom

    private val _allContactsInfo = MutableLiveData<ContactStates>()
    val allContactsInfo: LiveData<ContactStates> get() = _allContactsInfo

    val _addContactState = MutableLiveData<AddContactState>()
    val addContactState: LiveData<AddContactState> = _addContactState
    private var addUserName: String = ""
    private var addPhoneNumber: String = ""

    fun getContactFromRoom() {
        viewModelScope.launch {
            val contactsLiveData = database.contactDao().getAllContacts()
            Log.d(TAG, "getContactFromRoom: offline mode")
            _allContactsInfo.postValue(ContactStates.Success(contactsLiveData))
        }
    }

    fun getContacts() {
        Log.d(TAG, "getContacts: ")
        viewModelScope.launch {
            // Show loading state if needed
            _allContactsInfo.postValue(ContactStates.Loading("Fetching contacts..."))

            repo.getContacts().enqueue(object : Callback<List<Contacts>> {
                override fun onResponse(
                    call: Call<List<Contacts>>,
                    response: Response<List<Contacts>>
                ) {
                    Log.d(
                        TAG,
                        "onResponse() called with: call = $call, response = ${response.raw()}, body = ${response.body()}"
                    )

                    if (response.isSuccessful && (response.code() == 200 || response.code() == 201)) {
                        val contacts = response.body()
                        if (contacts.isNullOrEmpty()) {
                            _allContactsInfo.postValue(ContactStates.Failure("No contacts found"))
                        } else {
                            try {
                                val entities = contacts.map {
                                    Contacts(
                                        name = it.name,
                                        phone = it.phone,
                                        email = it.email
                                    )
                                }
                                viewModelScope.launch {
                                    database.contactDao().clearContacts()
                                    database.contactDao().insertAll(entities)

                                }
                                _allContactsInfo.postValue(ContactStates.Success(entities))
                            } catch (e: Exception) {
                                viewModelScope.launch {
                                    _contactsRoom.postValue(
                                        database.contactDao().getAllContacts()
                                    )
                                }
                            }


                        }
                    } else {
                        _allContactsInfo.postValue(ContactStates.Failure("Error: ${response.message()}"))
                    }
                }

                override fun onFailure(call: Call<List<Contacts>>, t: Throwable) {
                    Log.e(TAG, "onFailure() called with: call = $call, error = ${t.message}")
                    _allContactsInfo.postValue(ContactStates.Failure("Error: ${t.message}"))
                }
            })
        }
    }

    fun addContacts(
        username: String?,
        phone: String?,
        email: String?,
        function: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            if (username != null && phone != null && email != null) {
                addUserName = username
                addPhoneNumber = phone
                if (isPhoneNumberValid(phone) && isUserNameValid(username) && isEmailValid(email)) {
                    repo.addContacts(
                        ContactsAdd(name = addUserName, phone = phone, email = email)
                    ).enqueue(object : Callback<ContactsAdd> {
                        override fun onResponse(
                            p0: Call<ContactsAdd>,
                            response: Response<ContactsAdd>
                        ) {
                            Log.d(
                                TAG,
                                "onResponse() addContacts = called with: p0 = $p0, p1 = $response"
                            )
                            if (response.isSuccessful && (response.code() == 200 || response.code() == 201)) {
                                val contacts = response.body()
                                if (contacts == null) {
                                    _addContactState.postValue(AddContactState.Failure("No contacts found"))
                                    function(false)
                                } else {
                                    _addContactState.postValue(contacts?.let {
                                        AddContactState.Success(
                                            it
                                        )
                                    })
                                    function(false)
                                }
                            } else {
                                _addContactState.postValue(AddContactState.Failure("Error: ${response.message()}"))
                                function(false)
                            }

                        }

                        override fun onFailure(p0: Call<ContactsAdd>, p1: Throwable) {
                            Log.d(TAG, "onFailure() called with: p0 = $p0, p1 = $p1")
                            _addContactState.postValue(AddContactState.Failure("Error: ${p1.message}"))
                            function(false)
                        }

                    })
                } else {
                    _addContactState.postValue(
                        AddContactState.Failure(
                            message = "Username and Phone number invalid"
                        )
                    )
                }
            }
        }

    }


    fun isPhoneNumberValid(phone: String): Boolean {
        // Remove spaces and +91 prefix if present
        val normalizedPhone = phone.replace(" ", "").removePrefix("+91")

        // Ensure the phone number has exactly 10 digits
        if (normalizedPhone.length == 10 && normalizedPhone.all { it.isDigit() }) {
            return true
        }

        return false
    }

    fun isUserNameValid(username: String): Boolean {
        return username.trim().length > 3
    }

    fun isEmailValid(email: String): Boolean {
        //starts with alphanumeric characters and optional special characters need @ symbol
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        return email.trim().matches(emailRegex.toRegex())
    }


    fun countryCodeAdd(phone: String): String {
        if (phone.trim().replace(" ", "").length > 10) {
            return phone.replace(" ", "").reversed().substring(0, 10).reversed()
        }
        //  addPhoneCountry = "+91$phone"
        return phone
    }

    fun deleteContacts(id: Int) {
       repo.deleteContacts(id)
    }


}