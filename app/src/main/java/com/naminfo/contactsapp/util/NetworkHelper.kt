package com.naminfo.contactsapp.util

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "==>>NetworkHelper"
@Singleton
class NetworkHelper @Inject constructor(@ApplicationContext private val context: Context) {
    @OptIn(DelicateCoroutinesApi::class)
    fun isNetworkConnected(callback: (Any) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {

            try {
                val process = Runtime.getRuntime().exec("/system/bin/ping -c 1 8.8.8.8")
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                val output = StringBuffer()
                var line: String?

                while (reader.readLine().also { line = it } != null) output.append(line)

                reader.close()
                process.waitFor()
                callback(process.exitValue() == 0)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d(TAG, "isNetworkConnected: error: ${e.message}")
                withContext(Dispatchers.Main){
                    callback(false)
                }

            }

        }

    }
}