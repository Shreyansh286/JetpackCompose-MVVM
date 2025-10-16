package com.example.myjetpack1.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterAmountScreen(
    navController: NavController,
    selectedUnit: String?,
    selectedFiat: String?,
    onNavigateBack: () -> Unit
) {
    var exchangeRate by remember { mutableDoubleStateOf(0.0) }

    var enterAmount by remember { mutableStateOf("") }
    var displayAmount by remember { mutableStateOf("") }
    var convertedAmountState by remember { mutableDoubleStateOf(0.0) }

    var isUnitToFiat by remember { mutableStateOf(true) }

    val enterCurrencyCode = if (isUnitToFiat) selectedUnit else selectedFiat
    val displayCurrencyCode = if (isUnitToFiat) selectedFiat else selectedUnit

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        exchangeRate = Random.nextDouble(10000.00, 20000.00)
        focusRequester.requestFocus()
    }


    fun formatAmount(amount: Double, currencyCode: String?): String {
        val numberFormat = NumberFormat.getInstance(Locale.getDefault())
        return when (currencyCode) {
            "SATS" -> {
                numberFormat.maximumFractionDigits = 0
                "₿${numberFormat.format(amount.toLong())}"
            }

            "BTC" -> {
                numberFormat.maximumFractionDigits = 8
                numberFormat.minimumFractionDigits = 0
                "฿${numberFormat.format(amount)}"
            }

            else -> {
                try {
                    val currency = Currency.getInstance(currencyCode)
                    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
                    currencyFormat.currency = currency
                    currencyFormat.maximumFractionDigits = 2
                    currencyFormat.minimumFractionDigits = 0
                    currencyFormat.format(amount)
                } catch (e: Exception) {
                    numberFormat.maximumFractionDigits = 2
                    numberFormat.minimumFractionDigits = 0
                    numberFormat.format(amount)
                }
            }
        }
    }

    fun getMaxFractionDigits(currencyCode: String?): Int {
        return when (currencyCode) {
            "SATS" -> 0
            "BTC" -> 8
            else -> 2
        }
    }

    fun calculateAndFormat(amountStr: String) {
        val amount = amountStr.toDoubleOrNull() ?: 0.0
        val convertedAmount = if (isUnitToFiat) {
            amount * exchangeRate
        } else {
            if (exchangeRate != 0.0) amount / exchangeRate else 0.0
        }
        convertedAmountState = convertedAmount
        displayAmount = formatAmount(convertedAmount, displayCurrencyCode)
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text("Enter Amount") },
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
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = enterCurrencyCode ?: "",
                    color = Color.White
                )

                BasicTextField(
                    value = enterAmount,
                    onValueChange = {
                        val filteredInput = it.filter { char -> char.isDigit() || char == '.' }
                        val dotCount = filteredInput.count { char -> char == '.' }
                        val maxFractionDigits = getMaxFractionDigits(enterCurrencyCode)

                        if (dotCount <= 1 && (dotCount == 0 || filteredInput.substringAfter('.').length <= maxFractionDigits)) {
                            enterAmount = filteredInput
                            calculateAndFormat(filteredInput)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(
                        fontSize = 48.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        // 2. Attach the focusRequester to the Modifier
                        .focusRequester(focusRequester)
                )

                IconButton(onClick = {
                    isUnitToFiat = !isUnitToFiat
                    val newAmount = convertedAmountState
                    val newAmountString = formatAmount(newAmount, enterCurrencyCode)
                        .filter { it.isDigit() || it == '.' }
                    enterAmount = newAmountString
                    calculateAndFormat(newAmountString)
                }) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = "Swap",
                        tint = Color.Blue
                    )
                }

                Text(
                    text = displayAmount.ifBlank { formatAmount(0.0, displayCurrencyCode) },
                    style = TextStyle(color = Color.White, fontSize = 16.sp),
                    color = Color.White
                )
                Text(
                    text = displayCurrencyCode ?: "",

                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    Text(
                        text = "1 ${selectedUnit ?: ""} = ",
                        style = TextStyle(color = Color.White, fontSize = 16.sp),
                        color = Color.White
                    )
                    Text(
                        text = formatAmount(exchangeRate, selectedFiat),
                        style = TextStyle(color = Color.White, fontSize = 16.sp),
                        color = Color.White
                    )
                }
            }

            Button(
                onClick = {
                    val result = "Amount: $displayAmount"
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("display_amount", result)
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("SUBMIT")
            }
        }
    }
}
