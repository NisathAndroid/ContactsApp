package com.naminfo.contactsapp.view.main

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.naminfo.contactsapp.R
import com.naminfo.contactsapp.databinding.ActivityMainBinding
import com.naminfo.contactsapp.databinding.DialogAddcontactsBinding
import com.naminfo.contactsapp.model.data.Contacts
import com.naminfo.contactsapp.view.states.AddContactState
import com.naminfo.contactsapp.view.states.ConstantsMessage
import com.naminfo.contactsapp.view.states.ContactStates
import com.naminfo.contactsapp.util.NetworkHelper
import com.naminfo.contactsapp.util.PickContactContract
import com.naminfo.contactsapp.util.snackMessage
import com.naminfo.contactsapp.view.adapter.ContactListAdapter
import com.naminfo.contactsapp.view.states.ConstantsMessage.SUCCESS
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "==>>MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var contactAdapter: ContactListAdapter
    private var contactList = mutableListOf<Contacts>()
    private val viewModel: MainActivityViewModel by viewModels()
    private val dispatcherMain = Dispatchers.Main
    private val dispatcherIO = Dispatchers.IO
    private lateinit var addDialogBinding: DialogAddcontactsBinding
    private lateinit var pickContactLauncher: ActivityResultLauncher<Intent>
    private var userName: String = ""
    private var phoneNumber: String = ""
    private var emailId: String = ""

    @Inject
    lateinit var network: NetworkHelper
    private fun chooseContactLauncher() {
        pickContactLauncher =
            registerForActivityResult(PickContactContract(applicationContext)) { result ->
                result?.let { (name, number) ->
                    addDialogBinding.apply {
                        Log.d(TAG, "chooseContactLauncher: ")
                        phoneNumber = viewModel.countryCodeAdd(number.trim().replace(" ", ""))
                        userName = name.trim().replace(" ", "")
                        emailId = emailId.trim().replace(" ", "")
                        addContactNameTIE.setText(userName)
                        addContactNumberTIE.setText(phoneNumber)
                        addEmailTIE.setText(emailId)
                    }
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()

    }

    private fun initUI() {
        fetchContacts()
        livedataObserver()
        setupRecyclerView()
        refreshScreen()
        chooseContactLauncher()
        uiOnClickListener()
    }

    private fun uiOnClickListener() {
        showAddContactDialog()
    }

    private fun refreshScreen() {
        binding.apply {
            swipeRefreshLayout.setDistanceToTriggerSync(200) // 200 pixels
            swipeRefreshLayout.setProgressViewEndTarget(false, 300)
            swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorAccent,
                R.color.red
            )
            swipeRefreshLayout.setOnRefreshListener {
                fetchContacts()
                swipeRefreshLayout.isRefreshing = false

            }
        }
    }

    private fun fetchContacts() {
        network.isNetworkConnected { isAvailableNetwork ->
            if (isAvailableNetwork as Boolean) lifecycleScope.launch(dispatcherMain) { viewModel.getContacts() }
            else {
                lifecycleScope.launch(dispatcherMain) { viewModel.getContactFromRoom() }
            }
        }
    }

    private fun livedataObserver() {
        binding.apply {
            viewModel.addContactState.observe(this@MainActivity) {
                when (it) {
                    is AddContactState.Loading -> {
                        progressAI.visibility = View.VISIBLE
                        Log.d(TAG, "livedataObserver: Loading")
                    }

                    is AddContactState.Success -> {
                        progressAI.visibility = View.GONE
                        Log.d(TAG, "livedataObserver: Success")
                        binding.snackMessage("Successfully added contact...")
                        fetchContacts()
                    }

                    is AddContactState.Failure -> {
                        progressAI.visibility = View.GONE
                        binding.snackMessage(it.message)
                        Log.d(TAG, "livedataObserver: Failure ${it.message}")
                    }
                }
            }
            viewModel.allContactsInfo.observe(this@MainActivity) {
                when (it) {
                    is ContactStates.Loading -> {
                        progressAI.visibility = View.VISIBLE
                        Log.d(TAG, "livedataObserver: LOADING....")
                    }

                    is ContactStates.Success -> {
                        Log.d(TAG, "livedataObserver: SUCCESS....")
                        contactList.clear()
                        if (it.listOfContacts.isEmpty()) {
                            binding.emptyContactsIMV.visibility = View.VISIBLE
                            binding.snackMessage("Contacts is empty...")
                        } else {
                            binding.emptyContactsIMV.visibility = View.GONE
                            contactList = it.listOfContacts.toMutableList()

                        }
                        progressAI.visibility = View.GONE
                    }

                    is ContactStates.Failure -> {
                        Log.d(TAG, "livedataObserver: FAILURE....")

                        progressAI.visibility = View.GONE
                    }

                    is ContactStates.Messages -> {
                        Log.d(TAG, "livedataObserver: FAILURE....")
                        progressAI.visibility = View.GONE
                        if (it.status == ConstantsMessage.EMPTY) {
                            binding.emptyContactsIMV.visibility = View.VISIBLE
                            Toast.makeText(
                                this@MainActivity,
                                it.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                contactAdapter.notify(contactList)
            }
        }

    }

    private fun setupRecyclerView() {
        // Initialize recyclerView
        contactAdapter = ContactListAdapter(contactList) { contacts: Contacts, i: Int ->
            Log.d(TAG, "setupRecyclerView: ${contacts.id}")
            network.isNetworkConnected { isConnected ->
                Log.d(TAG, "setupRecyclerView: $isConnected")
                lifecycleScope.launch {
                    if (isConnected as Boolean) {
                        viewModel.deleteContacts(contacts._id.toString()) { status, toast ->

                            fetchContacts()
                            Toast.makeText(
                                this@MainActivity,
                                toast,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            ConstantsMessage.NETWORK_FAILED,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        binding.recyclerContactListRV.adapter = contactAdapter
        binding.apply {
            recyclerContactListRV.layoutManager = LinearLayoutManager(this@MainActivity)
            recyclerContactListRV.adapter = contactAdapter
        }
    }

    private fun showAddContactDialog() {
        binding.apply {
            addContactFABBtn.setOnClickListener {
                val addDialog = Dialog(this@MainActivity, R.style.RoundedDialogTheme)
                addDialogBinding = DialogAddcontactsBinding.inflate(layoutInflater)
                addDialog.setContentView(addDialogBinding.root)

              /*  addDialogBinding.apply {
                    addContactNameTIE.setOnFocusChangeListener { _, hasFocus ->
                        if (hasFocus && addContactNameTIE.text.toString() == addContactNumberTIE.text.toString()) {
                            addContactNameTIE.text?.clear() // Clear if it's auto-filled with the phone number
                        }
                    }
                    addContactNameTIE.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                            if (s.toString() == addContactNumberTIE.text.toString()) {
                                addContactNameTIE.text?.clear() // Clear if it matches the phone number
                            }
                        }

                        override fun afterTextChanged(s: Editable?) {}
                    })
                    cancelBtn.setOnClickListener {
                        addDialog.dismiss()
                    }
                    saveBtn.setOnClickListener {
                        network.isNetworkConnected { isConnected ->
                            if (isConnected as Boolean) {
                                lifecycleScope.launch {
                                    userName = addContactNameTIE.text?.trim().toString()
                                    phoneNumber = addContactNumberTIE.text?.trim().toString()
                                    emailId = addEmailTIE.text?.trim().toString()

                                    if (!viewModel.isUserNameValid(userName))
                                        addDialogBinding.addContactNameContainerTIL.setError(
                                            ConstantsMessage.ERROR_NAME
                                        )
                                    if (!viewModel.isPhoneNumberValid(phoneNumber))
                                        addDialogBinding.addContactNumberContainerTIL.setError(
                                            ConstantsMessage.ERROR_PHONE
                                        )
                                    if (!viewModel.isEmailValid(emailId)) {
                                        addDialogBinding.addEmailContainerTIL.setError(
                                            ConstantsMessage.ERROR_EMAIL
                                        )

                                    } else {
                                        viewModel.addContacts(
                                            username = userName,
                                            phone = phoneNumber,
                                            email = emailId
                                        )
                                        {
                                            addDialog.dismiss()
                                        }
                                    }
                                }
                            } else {
                                lifecycleScope.launch {
                                    Toast.makeText(
                                        this@MainActivity,
                                        ConstantsMessage.NETWORK_FAILED,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                        }

                    }
                    pickBtn.setOnClickListener {
                        pickContactLauncher.launch(Intent())
                    }
                }*/

                addDialog.show()
            }
        }
    }
}