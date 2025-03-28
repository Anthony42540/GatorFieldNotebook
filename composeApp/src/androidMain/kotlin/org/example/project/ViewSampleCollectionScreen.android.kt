package org.example.project

import android.os.Environment
import com.dev.database.cache.Database
import com.dev.database.entity.SampleAndData
import java.io.File
import java.lang.StringBuilder

actual fun exportToCSV(
    form: String,
    database: Database?,
    groupedSamples: Map<String, List<SampleAndData>>
): Boolean {
    val csvBuilder = StringBuilder()

    val standardHeaders = listOf("SampleCollectionId", "FormId", "SampleId", "Location", "DateCollectedUTC")

    val dataEntryKeys = groupedSamples[form]
        ?.flatMap { it.dataEntries.keys }
        ?.distinct()
        ?.sorted()
        ?: emptyList()

    val columnNames = dataEntryKeys.map { fieldId ->
        val field = database?.getFieldByID(fieldId)
        field?.fieldName ?: "Unknown"
    }

    val allHeaders = standardHeaders + columnNames
    csvBuilder.append(allHeaders.joinToString(",")).append("\n")

    groupedSamples.forEach { (formName, sampleList) ->
        if (form == formName) {
            sampleList.forEach { sample ->
                csvBuilder.append("${sample.sampleCollectionId ?: "N/A"}, ")
                csvBuilder.append("${sample.formId ?: "N/A"}, ")
                csvBuilder.append("${sample.sampleId ?: "N/A"}, ")

                val rLocation = sample.location.replace(",", "")

                csvBuilder.append("${rLocation ?: "Unknown"}, ")
                csvBuilder.append("${sample.dateCollectedUTC ?: "N/A"}")

                dataEntryKeys.forEach { key ->
                    val dataValue = sample.dataEntries[key] ?: "N/A"
                    csvBuilder.append(", $dataValue")
                }

                csvBuilder.append("\n")
            }
        }
    }

    val fileName = "${form}Samples.csv"
    val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)

    try {
        file.writeText(csvBuilder.toString())
        return true
    } catch (e: Exception) {
        println("Error writing to file: ${e.message}")
        return false
    }
}