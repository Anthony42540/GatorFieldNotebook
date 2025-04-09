package org.example.project

//import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
//import com.dantsu.escposprinter.textparser.PrinterTextParserImg


class BluetoothHandlerImp(private val context: Context) : BluetoothHandler {


//    private var connectionType = "DantSu (ESCPOS)"
    var printer : Any? = null

    override fun getContext(): Context {
        return context
    }

    override fun disconnectPrinter() {
        if (printer is BluetoothConnection)
        {
            (printer as BluetoothConnection).disconnect()
            printer = null
        }
    }

    @SuppressLint("MissingPermission")
    fun getPrinterName() : String {
        if (printer is BluetoothConnection) {
            return (printer as BluetoothConnection).device.name
        }
        return "No Connection"
    }

    override fun startPrinter() {
        // do the printer scan

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
            println("N/A")
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


    override fun printLabel(data: String) {

        val printer = EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 48f, 32)
        printer
            .printFormattedText(
                """
                [L]
                [C]<u><font size='big'>ORDER N°045</font></u>
                [L]
                [C]================================
                [L]
                [L]<b>BEAUTIFUL SHIRT</b>[R]9.99e
                [L]  + Size : S
                [L]
                [L]<b>AWESOME HAT</b>[R]24.99e
                [L]  + Size : 57/58
                [L]
                [C]--------------------------------
                [R]TOTAL PRICE :[R]34.98e
                [R]TAX :[R]4.23e
                [L]
                [C]================================
                [L]
                [L]<font size='tall'>Customer :</font>
                [L]Raymond DUPONT
                [L]5 rue des girafes
                [L]31547 PERPETES
                [L]Tel : +33801201456
                [L]
                [C]<barcode type='ean13' height='10'>831254784551</barcode>
                [C]<qrcode size='20'>https://dantsu.com/</qrcode>
                """.trimIndent()
            )

        /*
            val printer = EscPosPrinter(
//                                BluetoothPrintersConnections.selectFirstPaired(),
                ((bt as BluetoothHandlerImp).printer as BluetoothConnection),
                300,
                48f,
                20
            )
            printer.printFormattedText(
                    """
                    [L]<b>HELLO WORLD </b>[R]
                    """.trimIndent()
                )
}, Color(0xFF12BF7A), Color.White)
         */
//        when (connectionType) {
//            "Printooth" -> {
//
//                if (!Printooth.hasPairedPrinter()) {
//                    Log.e("BluetoothManager", "No printer paired")
//                    return
//                }
//
//                val printables = ArrayList<Printable>()
//
//                val printable = TextPrintable.Builder()
//                    .setText(data)
//                    .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
//                    .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
//                    .build()
//
//                printables.add(printable)
//
//                try {
//                    Printooth.printer().print(printables)
//                } catch (e: Exception) {
//                    Log.e("BluetoothManager", "Printing failed: ${e.message}")
//                }
//            }
//        }
    }
}
