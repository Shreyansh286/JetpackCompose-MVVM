package com.example.myjetpack1.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.myjetpack1.model.User
import com.example.myjetpack1.ui.components.UserItem
import com.example.myjetpack1.viewmodel.UserViewModel
import java.net.UnknownHostException

//  this function to get a user-friendly error message
private fun displayErrorMessage(throwable: Throwable): String {
    return if (throwable is UnknownHostException) {
        "No data found. Please check your internet connection."
    } else {
        throwable.localizedMessage ?: "An unknown error occurred."
    }
}

@OptIn(ExperimentalMaterial3Api::class) // For Scaffold and potentially other M3 components
@Composable
fun UserListScreen(
    viewModel: UserViewModel = hiltViewModel(),
    onUserClick: (User) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val lazyUserItems = viewModel.users.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(lazyUserItems.loadState.refresh) {
        val refreshState = lazyUserItems.loadState.refresh
        // Show Snackbar for refresh errors only if there are items currently displayed.
        // If itemCount is 0, the main content area will handle showing the error or empty state.
        if (refreshState is LoadState.Error && lazyUserItems.itemCount > 0) {
            val friendlyMessage = displayErrorMessage(refreshState.error)
            val result = snackbarHostState.showSnackbar(
                message = "Could not refresh: $friendlyMessage",
                actionLabel = "Retry",
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) {
                lazyUserItems.retry()
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from Scaffold
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { query -> viewModel.searchUsers(query) },
                label = { Text("Search Users") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            val refreshLoadState = lazyUserItems.loadState.refresh

            // Case 1: Initial load is in progress, and no items are yet available.
            if (refreshLoadState is LoadState.Loading && lazyUserItems.itemCount == 0) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            // Case 2: No items found for an active search query (online or offline results).
            // This takes precedence over a general network error if a search was performed.
            else if (lazyUserItems.itemCount == 0 && searchQuery.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No users found for '$searchQuery'.")
                }
            }
            // Case 3: Initial load failed, no items available, and no active search query.
            // This is for general errors (e.g., first launch, no cache, no network, no search attempted).
            else if (refreshLoadState is LoadState.Error && lazyUserItems.itemCount == 0 && searchQuery.isEmpty()) {
                val friendlyMessage = displayErrorMessage(refreshLoadState.error)
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Error: $friendlyMessage",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { lazyUserItems.retry() }) {
                        Text("Retry")
                    }
                }
            }
            // Case 4: Data is available, or it's an empty state with no search query (and no error for initial load).
            else {
                if (lazyUserItems.itemCount == 0) { // searchQuery must be empty here if this block is reached.
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Search for users to see results.")
                    }
                } else {
                    // Data is available, show the list.
                    // If refreshLoadState is Loading or Error here, but itemCount > 0, it means a background refresh is happening
                    // or a Snackbar is shown for the error.
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(
                            count = lazyUserItems.itemCount,
                            key = lazyUserItems.itemKey { user -> user.uuid }
                        ) { index ->
                            val user = lazyUserItems[index]
                            user?.let {
                                UserItem(user = it, onClick = { onUserClick(it) })
                            }
                        }

                        val appendLoadState = lazyUserItems.loadState.append
                        when (appendLoadState) {
                            is LoadState.Loading -> {
                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                            is LoadState.Error -> {
                                val friendlyMessage = displayErrorMessage(appendLoadState.error)
                                item {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            "Error loading more: $friendlyMessage",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                        Button(onClick = { lazyUserItems.retry() }) {
                                            Text("Retry")
                                        }
                                    }
                                }
                            }
                            is LoadState.NotLoading -> {
                                if (appendLoadState.endOfPaginationReached && lazyUserItems.itemCount > 0) {
                                    item {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text("You've reached the end!")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
