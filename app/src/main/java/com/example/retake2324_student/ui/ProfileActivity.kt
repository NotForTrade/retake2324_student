package com.example.retake2324_student.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import android.text.util.Linkify
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.retake2324_student.EmptyDbListResultException
import com.example.retake2324_student.R
import com.example.retake2324_student.core.App
import com.example.retake2324_student.data.Schemas


class ProfileActivity : BaseActivity() {

    private lateinit var errorTextView: TextView
    private var userID = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as App

        // Start the coroutine to call the database related instructions
        CoroutineScope(Dispatchers.Main).launch {
            val database = app.getDatabase() // Reuse the existing database connection
            loadAndDisplayComponents(database, userID)
        }

    }

    private suspend fun loadAndDisplayComponents(database: Database, userID: Int) {
        val app = application as App

        try {

            // Fetch user data
            val user = withContext(Dispatchers.IO) {
                database.sequenceOf(Schemas.Users).find { it.Id eq userID }
            }

            if (user != null) {

                // bind the field's id to a variable
                val textRole: TextView = findViewById(R.id.textRole)
                val textName: TextView = findViewById(R.id.textName)
                val textEmail: TextView = findViewById(R.id.textEmail)
                val imagePhoto: ImageView = findViewById(R.id.imagePhoto)

                // Set the values from the fetched object to the
                textRole.text = user!!.role.name
                textName.text = user!!.firstName + " " + user!!.lastName
                textEmail.text = user!!.email.toString()

                // Load the photo
                // imagePhoto.setImageBitmap(base64ToBitmap(user.photo))

                // Add the hypertext link on the email address
                Linkify.addLinks(textEmail, Linkify.EMAIL_ADDRESSES)

            }
            else {
                app.displayException(EmptyDbListResultException("No components found"))
            }

        } catch (e: Exception) {
            app.displayException(e)
        }
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_profile
    }

    private fun displayError(message: String) {
        errorTextView.text = message
        errorTextView.visibility = View.VISIBLE
    }


    private fun base64ToByteArray(base64Str: String): ByteArray {
        return Base64.decode(base64Str, Base64.DEFAULT)
    }
    private fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
    private fun base64ToBitmap(base64Str: String): Bitmap {
        val byteArray = base64ToByteArray(base64Str)
        return byteArrayToBitmap(byteArray)
    }

}