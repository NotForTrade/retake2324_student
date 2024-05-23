package com.example.retake2324_student.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.retake2324_student.R

class SignUpActivity : AppCompatActivity() {

    private lateinit var editFirstName: EditText
    private lateinit var editLastName: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var buttonSelectPhoto: Button
    private lateinit var imageProfile: ImageView
    private lateinit var buttonSignUp: Button

    private val PICK_IMAGE = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        editFirstName = findViewById(R.id.editFirstName)
        editLastName = findViewById(R.id.editLastName)
        editEmail = findViewById(R.id.editEmail)
        editPassword = findViewById(R.id.editPassword)
        buttonSelectPhoto = findViewById(R.id.buttonSelectPhoto)
        imageProfile = findViewById(R.id.imageProfile)
        buttonSignUp = findViewById(R.id.buttonSignUp)

        buttonSelectPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE)
        }

        buttonSignUp.setOnClickListener {
            val firstName = editFirstName.text.toString()
            val lastName = editLastName.text.toString()
            val email = editEmail.text.toString()
            val password = editPassword.text.toString()

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || imageUri == null) {
                Toast.makeText(this, "Please fill in all fields and select a photo", Toast.LENGTH_SHORT).show()
            } else {
                // TODO: push data to database & redirect to a new activity (either logged-in or successful account creation)
                Toast.makeText(this, "Sign-Up Successful", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            imageProfile.setImageURI(imageUri)
        }
    }
}
