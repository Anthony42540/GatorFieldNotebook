package com.dev.database.cache
import com.dev.database.entity.Field
import com.dev.database.entity.FieldType
import com.dev.database.entity.SampleForm
import com.dev.database.entity.SampleData
import com.dev.database.entity.DataEntry
import com.dev.database.entity.SampleAndData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = AppDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.appDatabaseQueries

    //get a list of all sample forms (to choose from when adding new sample)
    internal fun getAllSampleForms(): List<SampleForm> {
        return dbQuery.getAllSampleForms(::mapSampleForm).executeAsList()
    }

    //get the list of fields for specific form (to display for form field entry)
    internal fun getFormFields(formId: Long): List<Field> {
        return dbQuery.getFormFields(formId, ::mapField).executeAsList()
    }

    //get all sample data
    internal fun getAllSampleData(): List<SampleData> {
        return dbQuery.getAllSampleData(::mapSampleData).executeAsList()
    }
    //get all sample data from specific form
    internal fun getSampleDataByForm(formId: Long): List<SampleData> {
        return dbQuery.getSampleDataByForm(formId, ::mapSampleData).executeAsList()
    }
    //gets single sample by sample ID
    internal fun getSampleData(sampleId: Long): SampleData {
        return dbQuery.getSampleData(sampleId, ::mapSampleData).executeAsOne()
    }

    //get list of all data entries for a sample type
    internal fun getDataEntry(sampleId: Long): List<DataEntry> {
        return dbQuery.getDataEntry(sampleId, ::mapDataEntry).executeAsList()
    }

    //uses getSampleData and getDataEntry to create data class object SampleAndData (easier to use in frontend)
    internal fun getSampleAndData(sampleId: Long): SampleAndData {
        val sampleData = getSampleData(sampleId)
        val dataEntries = getDataEntry(sampleId)
        //converts list of data entries into map<Long, String> (iterates through list)
        val dataEntriesMap = dataEntries.associate{it.fieldId.toLong() to it.userInput}

        return SampleAndData(
            sampleId = sampleData.sampleId,
            formId = sampleData.formId,
            dateCollectedUTC = sampleData.dateCollectedUTC,
            location = sampleData.location,
            dataEntries = dataEntriesMap
        )
    }
}

private fun mapSampleForm(
    form_id: Long,
    form_name: String
): SampleForm {
    return SampleForm(
        formId = form_id.toInt(),
        formName = form_name
    )
}

// Maps db field data to kotlin class obj
private fun mapField(
    field_id: Long,
    form_id: Long,
    field_name: String,
    order_num: Long,
    field_type: String,
    is_required: Long?,
    options: String?,
): Field {
    return Field(
        fieldId = field_id.toInt(),
        formId = form_id.toInt(),
        fieldName = field_name,
        orderNum = order_num.toInt(),
        fieldType = strToFT(field_type), // converts string to fieldType enum
        isRequired = is_required == 1L,
        options = jsonStringToList(options), // converts string to list<string>
    )
}

// Maps db sample data to kotlin class obj
private fun mapSampleData(
    sample_id: Long,
    form_id: Long,
    date_collected_utc: String,
    location: String
): SampleData {
    return SampleData(
        sampleId = sample_id.toInt(),
        formId = form_id.toInt(),
        dateCollectedUTC = date_collected_utc,
        location = location,
    )
}

private fun mapDataEntry(
    entry_id: Long,
    sample_id: Long,
    field_id: Long,
    user_input: String
): DataEntry {
    return DataEntry(
        entryId = entry_id.toInt(),
        sampleId = sample_id.toInt(),
        fieldId = field_id.toInt(),
        userInput = user_input,
    )
}

private fun strToFT(fieldType: String): FieldType {
    return try {
        FieldType.valueOf(fieldType.uppercase())
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException("Unknown FieldType: $fieldType")
    }
}

private fun listToJsonString(list: List<String>?): String? {
    return list?.let { Json.encodeToString(it) }
}

private fun jsonStringToList(json: String?): List<String>? {
    return json?.let { Json.decodeFromString(it) }
}