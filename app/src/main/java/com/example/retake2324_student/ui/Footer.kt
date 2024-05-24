package com.example.retake2324_student.ui

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun Footer(studentId: Int) {
    val context = LocalContext.current
    BottomAppBar(
        content = {
            BottomNavigationItem(
                icon = { Icon(imageVector = Icons.Default.AccountBox, contentDescription = "Profile") },
                label = { Text("Profile") },
                selected = false,
                onClick = {
                    val intent = Intent(context, ProfileActivity::class.java)
                    intent.putExtra("studentId", studentId)
                    context.startActivity(intent) }
            )
            BottomNavigationItem(
                icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Dashboard") },
                label = { Text("Dashboard") },
                selected = true,
                onClick = {
                    val intent = Intent(context, DashboardActivity::class.java)
                    intent.putExtra("studentId", studentId)
                    context.startActivity(intent) }
            )
            BottomNavigationItem(
                icon = { Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout") },
                label = { Text("Logout") },
                selected = false,
                onClick = {}
            )
        },
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp)
    )
}