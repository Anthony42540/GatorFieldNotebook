package org.example.project

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.BluetoothCallback
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.PrintingCallback
import com.mazenrashed.printooth.data.printable.ImagePrintable
import com.mazenrashed.printooth.utilities.Printing
import kotlinx.coroutines.*
import kotlin.coroutines.jvm.internal.CompletedContinuation.context

class PrintoothBT(context: Context, private val scanLauncher: ActivityResultLauncher<Intent>) {

    // We can remove this since we found a library other than the Printooth library that
    // can work

//
//    private var callback: BluetoothCallback? = null
    private var printing : Printing? = null

    init {
        // Initialize Printooth
        Printooth.init(context)
        // if we want to select from a list again
        if (Printooth.hasPairedPrinter()) {
            Printooth.removeCurrentPrinter()
        }
    }

    fun init(context: Context) {
        Printooth.init(context)
    }

    private var printerInUse = Printooth.getPairedPrinter()

    fun startPrinter(context: Context, scanLauncher: ActivityResultLauncher<Intent>) {
        CoroutineScope(Dispatchers.Main).launch {
            // Run the scanning process in the background
            if (!Printooth.hasPairedPrinter()) {
                println("Launched")
                scanLauncher.launch(
                    Intent(
                        context,
                        ScanningActivity::class.java
                    ),
                )
            }
            else {
                printerInUse = Printooth.getPairedPrinter()
            }
        }
    }

    fun handleScanResult(resultCode: Int) {
        if (resultCode == Activity.RESULT_OK) {
            Log.d("BluetoothHandler - Printooth", "Printer paired successfully")
        } else {
            Log.e("BluetoothHandler - Printooth", "Failed to pair printer")
        }

        fun printLabels() {

        }
    }

    /*
    Printooth.printer().printingCallback = object : PrintingCallback {
    override fun connectingWithPrinter() { }

    override fun printingOrderSentSuccessfully() { }  //printer was received your printing order successfully.

    override fun connectionFailed(error: String) { }

    override fun onError(error: String) { }

    override fun onMessage(message: String) { }
    }
     */

//    /* callback from printooth to get printer process */
//    printing?.printingCallback = object : PrintingCallback {
//        override fun connectingWithPrinter() {
//            Toast.makeText(this@MainActivity,"Connecting with printer", Toast.LENGTH_SHORT).show()
//        }
//
//        override fun printingOrderSentSuccessfully() {
//            Toast.makeText(this@MainActivity, "Order sent to printer", Toast.LENGTH_SHORT).show()
//        }
//
//        override fun connectionFailed(error: String) {
//            Toast.makeText(this@MainActivity, "Failed to connect printer", Toast.LENGTH_SHORT).show()
//        }


        fun printLabel(data: String) {

        if (!Printooth.hasPairedPrinter()) {
            Log.e("BluetoothManager", "No printer paired")
            return
        }

        val printables = ArrayList<Printable>()

        val printable = TextPrintable.Builder()
            .setText(data)
            .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
            .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
            .build()

        printables.add(printable)

        try {
            Printooth.printer().print(printables)
        } catch (e: Exception) {
            Log.e("BluetoothManager", "Printing failed: ${e.message}")
        }
    }
}