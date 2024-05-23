package com.example.retake2324_student.core

import android.app.Application
import com.example.retake2324_student.connectDatabase
import com.example.retake2324_student.data.Component
import com.example.retake2324_student.data.Module
import com.example.retake2324_student.data.Skill
import com.example.retake2324_student.data.User
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.ktorm.database.Database

class App : Application() {

    private val _exceptionFlow = MutableStateFlow<Exception?>(null)
    val exceptionFlow = _exceptionFlow.asStateFlow()

    // connections
    private val _databaseDeferred = CompletableDeferred<Database>()
    val databaseDeferred = _databaseDeferred

    // global state
    // NB: we use this instead of relying on the `putExtra` logic when creating an Intent to switch activity
    var currentUser: User? = null
    var currentModule: Module? = null
    var currentComponent: Component? = null
    var currentSkill: Skill? = null

    override fun onCreate() {
        super.onCreate()
        // Initialize the database connection lazily
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = connectDatabase()
                _databaseDeferred.complete(database)
            } catch (e: Exception) {
                displayException(e)
                // NB:commented out as we don't want to propagate the exception
                // we're catching it in a centralized way through the `exceptionFlow`
                // the centralized handler is in the `MainActivity`
                //_databaseDeferred.completeExceptionally(e)
            }
        }
    }

    suspend fun getDatabase(): Database {
        return _databaseDeferred.await()
    }

    // NB: this method doesn't directly show the exception but registers it to the `exceptionFlow`
    // it's then `BaseActivity` that has the code to display it to the user
    // each Activity can override how exceptions are routed and displayed
    fun displayException(exception: Exception) {
        _exceptionFlow.value = exception
        exception.printStackTrace()
    }
}