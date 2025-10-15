package com.example.myjetpack1.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myjetpack1.model.User
import com.google.gson.Gson

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter") // Add if Scaffold padding is handled
@OptIn(ExperimentalMaterial3Api::class) // For TopAppBar
@Composable
fun UserDetailScreen(
    userJson: String?, // Nullable in case argument is missing
    onNavigateBack: () -> Unit
) {
    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(userJson) {
        if (!userJson.isNullOrBlank()) {
            try {
                user = Gson().fromJson(userJson, User::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                user = null
            }
        } else {
            user = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(user?.name?.let { "${it.first} ${it.last}" } ?: "User Detail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues -> // paddingValues should be used by the content
        if (user != null) {
            Column(
                modifier = Modifier
                    .padding(paddingValues) // Apply padding
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()) // Make content scrollable
                    .padding(16.dp), // Padding for content inside Column
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp) // Spacing between items
            ) {
                AsyncImage(
                    model = user!!.picture.large,
                    contentDescription = "User's large picture",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "${user!!.name.first} ${user!!.name.last}",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Email: ${user!!.email}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else {
            // Show a loading indicator or an error message if the user object is null
            Box(
                modifier = Modifier
                    .padding(paddingValues) // Apply padding
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (userJson.isNullOrBlank()) {
                    Text("User data not provided.")
                } else if (user == null) { // if userJson was provided but parsing failed or is in progress
                    Text("Error loading user details or invalid data.")
                } else { // Default case, could be initial load before LaunchedEffect
                    CircularProgressIndicator()
                }
            }
        }
    }
}