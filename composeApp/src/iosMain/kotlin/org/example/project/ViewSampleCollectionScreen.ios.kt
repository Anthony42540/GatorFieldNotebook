package org.example.project

import com.dev.database.cache.Database
import com.dev.database.entity.SampleAndData

actual fun exportToCSV(
    form: String,
    database: Database?,
    groupedSamples: Map<String, List<SampleAndData>>
) {
}