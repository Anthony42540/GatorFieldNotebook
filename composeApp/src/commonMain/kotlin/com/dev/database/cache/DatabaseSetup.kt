package com.dev.database.cache

import com.dev.database.entity.FieldType

object DatabaseSetup {
    fun createDefaultSampleForm(database: Database) {
        val formId = database.insertSampleForm("General Sample Collection")

        // Add fields in the order they appear in UI
        database.insertField(
            formId = formId,
            fieldName = "Sample Name",
            orderNum = 1,
            fieldType = FieldType.SHORT_STRING,
            isRequired = true,
            options = null
        )

        database.insertField(
            formId = formId,
            fieldName = "Sample Info",
            orderNum = 2,
            fieldType = FieldType.LONG_STRING,
            isRequired = false,
            options = null
        )
    }
}