package org.example.project

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnections
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter


class BluetoothHandlerImp(private val context: Context, private val scanLauncher: ActivityResultLauncher<Intent>) : BluetoothHandler {

    // I wanted to test several libraries and rather than plugging one in and replacing it, I would
    // use a general handler so it wouldn't take that long; the Dantsu library ended up working so
    // I can take out the Printooth library and clean this up

    // for every possible library, the handler will have an object for each and use them

    private var connectionType = ""
    var printoothBT = PrintoothBT(context, scanLauncher)
    var printer : Any? = null

    override fun getContext(): Context {
        return context
    }

    override fun getConnection(): String {
        return connectionType
    }

    override fun disconnectPrinter() {
        Printooth.removeCurrentPrinter()
        if (printer is BluetoothConnection)
        {
            (printer as BluetoothConnection).disconnect()
        }
    }

    fun getLauncher(): ActivityResultLauncher<Intent> {
        return scanLauncher
    }

    @SuppressLint("MissingPermission")
    fun getPrinterName() : String {
        if (printer is BluetoothConnection) {
            return (printer as BluetoothConnection).device.name
        }
        else if (printer is Printooth) {
            return Printooth.getPairedPrinter()?.name.toString()
        }
        return "No Connection"
    }

    override fun startPrinter() {
        // do the printer scan
        when (connectionType) {
            "Printooth" -> {
                // do nothing, it handles itself
                printer = printoothBT
            }
            "DantSu (ESCPOS)" -> {
                // set Printer has to be called beforehand
                if (printer != null) {
                    try {
                        (printer as BluetoothConnection).connect()
                    } catch(e: Exception) {
                        println("Cannot connect to the printer")
                        Log.e("PrinterManager", "Failed to connect to the printer", e)
                    }

                }
                else {
                    println("Please select a printer first")
                }
            }
        }

    }

    // Dantsu functions
    fun getPrinters() : Array<out BluetoothConnection>? {
//        val printer = EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 48f, 32)
        val bluetoothDevicesList: Array<out BluetoothConnection>? = BluetoothPrintersConnections().list
        return bluetoothDevicesList
    }

    fun setPrinter(printerSelected: BluetoothConnection) {
        printer = printerSelected
//        printerSelected.connect()
        this.startPrinter()
    }

    var libOptions: Array<String> =
        arrayOf(
            "Printooth",
            "DantSu (ESCPOS)",
            "N/A"
        )

    override fun setConnection(type: String) {
        when (type) {
            "Printooth" -> connectionType = "Printooth"
            "DantSu (ESCPOS)" -> connectionType = "DantSu (ESCPOS)"
            "BlueLine" -> connectionType = "BlueLine"
            "peripage" -> connectionType = "peripage"
            "Kable" -> connectionType = "kable"
        }
    }

    override fun printLabel(data: String) {
        when (connectionType) {
            "Printooth" -> {

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
    }
}
