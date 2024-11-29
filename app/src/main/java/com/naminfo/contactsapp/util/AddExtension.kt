package com.naminfo.contactsapp.util

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar

fun AppCompatActivity.toastMessage(message: String){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun ViewBinding.snackMessage(message: String){
    Snackbar.make(this.root,message,Toast.LENGTH_SHORT).show()
}