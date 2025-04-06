package org.example.project

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
expect fun LabelPrint(navController: NavController, bt: BluetoothHandler)