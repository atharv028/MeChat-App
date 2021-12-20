package com.tare.mechat.login

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.tare.mechat.messages.LatestMessages
import com.tare.mechat.R
import com.tare.mechat.entities.User
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class RegisterPage : AppCompatActivity() {
    private lateinit var username : EditText
    private lateinit var email : EditText
    private lateinit var password : EditText
    private lateinit var register : Button
    private lateinit var login : TextView
    private lateinit var photo : ExtendedFloatingActionButton
    private lateinit var auth : FirebaseAuth
    private lateinit var storage : FirebaseStorage
    private lateinit var db : FirebaseDatabase
    private lateinit var civImg : CircleImageView
    private var imgUri : Uri? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //-------------------------VARS----------------------//
        username = findViewById(R.id.ETusername)
        email = findViewById(R.id.ETemail)
        password = findViewById(R.id.ETpassword)
        register = findViewById(R.id.BTregister)
        login = findViewById(R.id.TVLogin)
        photo = findViewById(R.id.EFABImage)
        civImg = findViewById(R.id.CIVImage)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        db = FirebaseDatabase.getInstance()
        //--------------------------------------------------//


        register.setOnClickListener {
            if(username.editableText.isEmpty())
            {
                Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(email.editableText.isEmpty() or password.editableText.isEmpty())
            {
                Toast.makeText(this, "Please enter email or password", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            registerUser(email.editableText.toString(), password.editableText.toString())
        }

        login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        photo.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            launchSomeActivity.launch(intent)
        }

    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser != null)
        {
            val intent = Intent(this, LatestMessages::class.java)
            startActivity(intent)
            finish()
        }
    }

    private var launchSomeActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if(data != null)
            {
                imgUri = data.data
                try {
                    imgUri?.let {
                        if(Build.VERSION.SDK_INT < 28) {
                            val bitmap = MediaStore.Images.Media.getBitmap(
                                this.contentResolver,
                                imgUri
                            )
                            civImg.setImageBitmap(bitmap)
                            photo.alpha = 0f
                        } else {
                            val source = ImageDecoder.createSource(this.contentResolver, imgUri!!)
                            val bitmap = ImageDecoder.decodeBitmap(source)
                            civImg.setImageBitmap(bitmap)
                            photo.alpha = 0f
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    private fun registerUser(email : String, password : String)
    {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(it.isSuccessful)
                {
                    Log.d("Register", "Successfully Logged in the user")
                    uploadToStorage()
                }
                else
                {
                    Toast.makeText(this, "Registration Failed, Please Recheck the input", Toast.LENGTH_SHORT).show()
                    Log.w("Register", "Failed with", it.exception)
                }
            }
    }

    private fun uploadToStorage()
    {
        val filename = UUID.randomUUID().toString()
        val reference = storage.getReference("/images/$filename")
        imgUri?.let { reference.putFile(it).addOnSuccessListener {task ->
            Log.d("Register", "Image uploaded Successfully: ${task.metadata}")
            reference.downloadUrl.addOnSuccessListener { url ->
                Log.d("Register", "File Location: $url")
                saveUserToDB(url!!)
            }
        } }
    }

    private fun saveUserToDB(url : Uri)
    {
        val uid = auth.uid
        val reference = db.getReference("/users/$uid")
        val user = User(uid!!,username.editableText.toString(),url.toString())
        reference.setValue(user)
            .addOnSuccessListener {
                Log.d("Register", "User Saved to DB")
                val intent = Intent(this, LatestMessages::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
    }


}