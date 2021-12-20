package com.tare.mechat.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.tare.mechat.messages.LatestMessages
import com.tare.mechat.R

class LoginActivity : AppCompatActivity() {
    private lateinit var email : EditText
    private lateinit var password : EditText
    private lateinit var button : Button
    private lateinit var register : TextView
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //-----------------------_VARS_--------------------//
        email = findViewById(R.id.ETemail_login)
        password = findViewById(R.id.ETpassword_login)
        button  = findViewById(R.id.BTLogin)
        register = findViewById(R.id.TVRegister)
        auth = FirebaseAuth.getInstance()
        //------------------------------------------------//

        button.setOnClickListener {
            if(email.editableText.isEmpty() or password.editableText.isEmpty())
            {
                Toast.makeText(this, "Please enter email or password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            loginUser(email.editableText.toString(), password.editableText.toString())
        }
        register.setOnClickListener {
            finish()
        }

    }

    private fun loginUser(email : String, password : String)
    {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(it.isSuccessful)
                {
                    Log.d("LoginActivity", "Login Successful with uid: ${auth.currentUser?.uid}")
                    val intent = Intent(this, LatestMessages::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }else
                {
                    Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                    Log.w("LoginActivity", "Login failed with :", it.exception)
                }
            }
    }
}