package com.example.retake2324_student.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.retake2324_student.core.App
import com.example.retake2324_student.data.Role
import com.example.retake2324_student.data.Schemas
import com.example.retake2324_student.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import java.security.MessageDigest

class SignupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as App

        setContent {
            SignupLoader(app)
        }
    }

    private fun hashPassword(password: String): String {

        // Create a MessageDigest instance for SHA-512
        val digest = MessageDigest.getInstance("SHA-512")

        // Apply the hash to the input password bytes
        val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))

        // Convert the hash bytes to a hexadecimal string
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    private suspend fun fetchObjects(database: Database): Role{

        try {


            val role = withContext(Dispatchers.IO) {
                database.sequenceOf(Schemas.Roles).find { it.name eq "Student" }
            }
            return role ?: Role()
        }catch (e: Exception) {
            return Role()
        }
    }

    @Composable
    fun SignupLoader(app: App) {

        // MutableState to hold values
        var studentRole by remember { mutableStateOf<Role>(Role()) }
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            val database = app.getDatabase() // Reuse the existing database connection
            val fetchedStudentRole = fetchObjects(database)

            // Update the states
            studentRole = fetchedStudentRole
            isLoading = false
        }

        if (isLoading) {
            Text(text = "Loading...", modifier = Modifier.padding(16.dp))
        } else {
            SignupScreen(app, studentRole.id)
        }

    }


    @Composable
    fun SignupScreen(app: App, student_roleId: Int) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()

        var firstname by remember { mutableStateOf("") }
        var lastname by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var password2 by remember { mutableStateOf("") }
        var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
        var selectedFileBase64 by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(false) }

        val filePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            selectedFileUri = uri
            uri?.let {
                val contentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(uri)
                inputStream?.use { stream ->
                    val bytes = stream.readBytes()
                    selectedFileBase64 = Base64.encodeToString(bytes, Base64.DEFAULT)
                }
            }
        }

        LaunchedEffect(isLoading) {
            if (isLoading) {
                try {
                    val hashedPassword = hashPassword(password)

                    // Perform database operation on IO dispatcher
                    withContext(Dispatchers.IO) {
                        val database = app.getDatabase()
                        database.insert(Schemas.Users) {
                            set(it.roleId, student_roleId)
                            set(it.groupId, null)
                            set(it.moduleId, 1) // only 1 module
                            set(it.componentId, null) // only column used for tutors only
                            set(it.photo, selectedFileBase64)
                            set(it.firstName, firstname)
                            set(it.lastName, lastname)
                            set(it.email, email)
                            set(it.password, hashedPassword)
                        }
                    }

                    // Navigate to LoginActivity on success
                    val intent = Intent(context, LoginActivity::class.java).apply {
                        putExtra("email", email)
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Log.e("SUBMIT", "Error submitting file: ${e.message}")

                    // reset variables
                    firstname = ""
                    lastname = ""
                    email = ""
                    password = ""
                    password2 = ""
                    selectedFileUri = null
                    selectedFileBase64 = null


                } finally {
                    isLoading = false
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Signup", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

            OutlinedTextField(
                value = firstname,
                onValueChange = { firstname = it },
                label = { Text("Firstname") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = lastname,
                onValueChange = { lastname = it },
                label = { Text("Lastname") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                visualTransformation = PasswordVisualTransformation()
            )

            OutlinedTextField(
                value = password2,
                onValueChange = { password2 = it },
                label = { Text("Confirm password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                visualTransformation = PasswordVisualTransformation()
            )

            if (password != password2) {
                Text(text = "The 2 passwords don't match!", color = Color.Red)
            }

            Button(
                onClick = { filePickerLauncher.launch("*/*") },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Upload Photo")
            }

            selectedFileUri?.let {
                Text(text = "Selected Photo: ${it.path}", modifier = Modifier.padding(top = 8.dp))
            }

            Button(
                enabled = (
                        firstname.isNotEmpty() &&
                                lastname.isNotEmpty() &&
                                password.isNotEmpty() &&
                                password2.isNotEmpty() &&
                                password == password2 &&
                                selectedFileUri != null
                        ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                onClick = {
                    isLoading = true
                }
            ) {
                Text("Create account")
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            }
        }
    }

}