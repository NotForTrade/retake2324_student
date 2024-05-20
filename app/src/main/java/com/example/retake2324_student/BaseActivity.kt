package com.example.retake2324_student

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Centralized exception handling
        val app = application as App
        lifecycleScope.launch {
            app.exceptionFlow.collect { exception ->
                exception?.let {
                    handleException(it)
                }
            }
        }
    }

    // Code to control how exceptions are shown to the app user
    // We can filter out some exceptions and rename their messages if we want
    // Each activity can override this function to to custom routing (though it's better to have everything here)
    private fun handleException(exception: Exception) {
        when (exception) {
            is java.sql.SQLException -> displayException(DbAccessException("Couldn't connect to DB"))
            else -> displayException(exception)
        }
    }

    // Each activity can override how an exception gets displayed
    private fun displayException(exception: Exception) {
        Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
    }
}