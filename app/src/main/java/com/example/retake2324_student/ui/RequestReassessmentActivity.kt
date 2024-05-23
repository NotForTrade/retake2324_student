package com.example.retake2324_student.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.retake2324_student.R

class RequestReassessmentActivity : AppCompatActivity() {

    private lateinit var selectedFileUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_request_reassessment)

        // Set window insets listener
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Spinner for components
        val spinnerComponent: Spinner = findViewById(R.id.spinner_component)
        ArrayAdapter.createFromResource(
            this,
            R.array.component_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerComponent.adapter = adapter
        }

        // Spinner for skills
        val spinnerSkill: Spinner = findViewById(R.id.spinner_skill)
        ArrayAdapter.createFromResource(
            this,
            R.array.skill_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSkill.adapter = adapter
        }


        // Find the button
        val buttonSelectFile: Button = findViewById(R.id.button_select_file)

        // Set up an ActivityResultLauncher to handle the file selection result
        val selectFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Handle the selected file
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    selectedFileUri = uri
                    // Do something with the selected file URI (e.g., upload it or display its name)
                }
            }
        }

        // Set click listener on the button to open the file chooser
        buttonSelectFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"  // You can specify a more specific MIME type if needed
            }
            selectFileLauncher.launch(intent)
        }



    }


}