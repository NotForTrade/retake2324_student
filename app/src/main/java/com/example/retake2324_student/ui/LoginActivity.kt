package com.example.retake2324_student.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.retake2324_student.core.App
import com.example.retake2324_student.data.Schemas
import com.example.retake2324_student.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.ktorm.dsl.eq
import org.ktorm.dsl.insert
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import java.security.MessageDigest

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as App
        setContent {
            LoginScreen(app)
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

    @Composable
    fun LoginScreen(app: App) {
        val context = LocalContext.current

        var studentId by remember { mutableStateOf(-1) }
        var success by remember { mutableStateOf(false) }


        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Login", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                visualTransformation = PasswordVisualTransformation()
            )

            Button(
                onClick = {


                    // Handle submit action here
                    CoroutineScope(Dispatchers.IO).launch {
                        try {


                            val hashedPassword = hashPassword(password)

                            val database = app.getDatabase()

                            // email is a unique key and used as "username"
                            val student = database.sequenceOf(Schemas.Users).find { it.Mail eq email } !!

                            if (student != User()) {
                                if ( student.password == hashedPassword) {
                                    studentId = student.id
                                    success = true
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("SUBMIT", "Error submitting file: ${e.message}")

                        }
                    }
                    if (success) {
                        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                        intent.putExtra("studentId", studentId)
                        startActivity(intent)
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clickable { (email != "") and (password != "") }
            ) {
                Text("Login")
            }

            TextButton(
                onClick = {
                    val intent = Intent(this@LoginActivity, SignupActivity::class.java)
                    startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Signup")
            }
        }
    }
}