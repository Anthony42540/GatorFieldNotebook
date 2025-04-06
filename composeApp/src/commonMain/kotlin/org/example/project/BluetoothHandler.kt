package org.example.project

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

interface BluetoothHandler {
    fun startPrinter()
    fun printLabel(data: String)
//    @Composable
//    fun PrintView()
    fun getContext(): Any
    fun setConnection(type: String)
    fun getConnection(): String
    fun disconnectPrinter()
}