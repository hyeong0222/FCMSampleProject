package com.example.fcmsampleproject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.iid.FirebaseInstanceId

class MainActivity : AppCompatActivity() {

    private lateinit var button: Button
    private lateinit var text: TextView
    private lateinit var messageReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.btn_retrieve_token)
        text = findViewById(R.id.tv_main)

        messageReceiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                text.text = intent?.extras?.getString("message")
            }
        }

        button.setOnClickListener {
            if (checkGooglePlayServices()) {
                FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("", "getInstanceId Failed", task.exception)
                        return@addOnCompleteListener
                    }

                    val token = task.result?.token

                    val msg = "My token is: $token"
                    Log.d("", msg)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(baseContext, "Device does not have google play services", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, IntentFilter("MyData"))
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)
    }

    private fun checkGooglePlayServices(): Boolean {
        val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        return status == ConnectionResult.SUCCESS
    }
}