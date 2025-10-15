package com.example.myjetpack1.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myjetpack1.model.CountryDataItem
import com.example.myjetpack1.ui.NavRoutes
import com.example.myjetpack1.ui.NavRoutes.SELECTED_FIAT_KEY
import com.example.myjetpack1.ui.NavRoutes.SELECTED_UNIT_KEY

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavController, // Receive NavController to navigate
    selectedCountryData: CountryDataItem?, // Receive the nullable object
    onNavigateBack: () -> Unit
) {
    var selectedUnit by remember { mutableStateOf("SATS") }
    var selectedFiat by remember { mutableStateOf(selectedCountryData?.currencyCode) }

    // Observe the result from UnitListScreen
    val unitResult = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<String>(SELECTED_UNIT_KEY)

    // Observe the result from FiatListScreen
    val fiatResult = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<String>(SELECTED_FIAT_KEY)
    // Update the state when a new Unit value is received
    LaunchedEffect(unitResult) {
        unitResult?.let {
            selectedUnit = it // Update the state with the new value
            // Clean up the handle so it doesn't trigger again on configuration change
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>(SELECTED_UNIT_KEY)
        }
    }
    // Update the state when a new Fiat value is received
    LaunchedEffect(fiatResult) {
        fiatResult?.let {
            selectedFiat = it // Update the state with the new value
            // Clean up the handle so it doesn't trigger again on configuration change
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>(SELECTED_FIAT_KEY)
        }
    }
    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text("Advance settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Reusable cards based on the image
            SettingDropdownCard(
                title = "Unit",
                icon = Icons.Default.Star,
                selectedValue = selectedUnit,// Use the state variable
                onClick = {   // Navigate to the UnitListScreen
                    navController.navigate(NavRoutes.UNIT_LIST)
                }
            )

            SettingDropdownCard(
                title = "Fiat currency",
                icon = Icons.Default.MonetizationOn,
                // Use the preferred currency from the passed object, or "INR" as a fallback
                selectedValue = selectedFiat.toString(),
                onClick = {
                    /* Handle Fiat currency click */
                    navController.navigate(NavRoutes.FIAT_LIST)
                }
            )

            SettingNavigationCard(
                title = "Currency Conversation ",
                icon = Icons.Default.AccountCircle,
                onClick = { /* Handle Change account name click */ }
            )

            SettingNavigationCard(
                title = "Close account",
                icon = Icons.Default.Delete,
                onClick = { /* Handle Close account click */ }
            )
        }
    }
}

/**
 * A reusable card component for settings that have a dropdown selection.
 */
@Composable
fun SettingDropdownCard(
    title: String,
    icon: ImageVector,
    selectedValue: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)) // Dark grey color
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFF007AFF), // Blue icon tint
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                Text(text = title, color = Color.White, fontSize = 16.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = selectedValue,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    tint = Color.Gray
                )
            }
        }
    }
}

/**
 * A reusable card component for settings that navigate to another screen.
 */
@Composable
fun SettingNavigationCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)) // Dark grey color
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFF007AFF), // Blue icon tint
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                Text(text = title, color = Color.White, fontSize = 16.sp)
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = Color.Gray
            )
        }
    }
}
