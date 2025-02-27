package com.dev.database.cache
import com.dev.database.entity.Field
import com.dev.database.entity.FieldNoID
import com.dev.database.entity.FieldType
import com.dev.database.entity.SampleForm
import com.dev.database.entity.SampleData
import com.dev.database.entity.DataEntry
import com.dev.database.entity.SampleAndData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = AppDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.appDatabaseQueries


    //list to track new fields when creating a new form
    var newFieldsList = mutableListOf<FieldNoID>()
    private var newFormName = ""

    /************************** GETTER FUNCTIONS **************************/
    internal fun getAllSampleForms(): List<SampleForm> { //get a list of all sample forms (to choose from when adding new sample)
        return dbQuery.getAllSampleForms(::mapSampleForm).executeAsList()
    }
    internal fun getSampleForm(formId: Long): SampleForm { //get a form by form ID
        return dbQuery.getSampleForm(formId, ::mapSampleForm).executeAsOne()
    }
    internal fun getFormFields(formId: Long): List<Field> { //get the list of fields for specific form (to display for form field entry)
        return dbQuery.getFormFields(formId, ::mapField).executeAsList()
    }
    internal fun getFieldByID(fieldId: Long): Field {
        return dbQuery.getFieldByID(fieldId, ::mapField).executeAsOne()
    }
    internal fun getAllSampleData(): List<SampleData> { //get all samples collected
        return dbQuery.getAllSampleData(::mapSampleData).executeAsList()
    }
    internal fun getSampleDataByForm(formId: Long): List<SampleData> { //get all sample data from specific form
        return dbQuery.getSampleDataByForm(formId, ::mapSampleData).executeAsList()
    }
    internal fun getSampleData(sampleId: Long): SampleData { //gets single sample by sample ID
        return dbQuery.getSampleData(sampleId, ::mapSampleData).executeAsOne()
    }
    internal fun getDataEntry(sampleId: Long): List<DataEntry> { //get list of all data entries for a sample type
        return dbQuery.getDataEntry(sampleId, ::mapDataEntry).executeAsList()
    }
    internal fun getSampleAndData(sampleId: Long): SampleAndData { //uses getSampleData and getDataEntry to create data class object SampleAndData (easier to use in frontend)
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
    /************************** GETTER FUNCTIONS **************************/


    /************************** INSERT FUNCTIONS **************************/
    internal fun insertSampleForm(formName: String): Long { //add new sample form and return unique sample ID
        dbQuery.insertSampleForm(formName)
        return dbQuery.getLastRowID().executeAsOne()
    }
    internal fun insertField( //add new field to a sample form
        formId: Long,
        fieldName: String,
        orderNum: Long,
        fieldType: FieldType,
        isRequired: Boolean,
        options: List<String>?
    ): Long {
        dbQuery.insertField(formId, fieldName, orderNum, ftToStr(fieldType), if (isRequired) 1 else 0, listToJsonString(options))
        return dbQuery.getLastRowID().executeAsOne()
    }
    internal fun insertSampleData( //add new sample collected
        formId: Long,
        dateCollectedUtc: String,
        location: String,
    ): Long {
        dbQuery.insertSampleData(formId, dateCollectedUtc, location)
        return dbQuery.getLastRowID().executeAsOne()
    }


    internal fun insertDataEntry(
        sampleId: Long,
        fieldId: Long,
        userInput: String
    ): Long {
        dbQuery.insertDataEntry(sampleId, fieldId, userInput)
        return dbQuery.getLastRowID().executeAsOne()
    }
    /************************** INSERT FUNCTIONS **************************/

    /************************** HELPER FUNCTIONS **************************/
    internal fun insertFieldsFromList(
        formId: Long
    ) {
        newFieldsList.forEachIndexed { index, field ->
            insertField(
                formId = formId,
                fieldName = field.fieldName,
                orderNum = index.toLong(),
                fieldType = field.fieldType,
                isRequired = field.isRequired,
                options = field.options
            )
        }
        newFieldsList.clear()
    }
    internal fun clearFieldsList() {
        newFieldsList.clear()
    }
    /************************** HELPER FUNCTIONS **************************/

    internal fun deleteAllSamples() {
        dbQuery.transaction {
            dbQuery.clearAllDataEntries()
            dbQuery.clearAllSamples()
        }
    }

    /************************** EDIT FUNCTIONS **************************/

    internal fun updateSampleData(
        sampleId: Long,
        newDateCollectedUtc: String,
        newLocation: String
    ) {
        dbQuery.updateSampleData(
            newDateCollectedUtc,
            newLocation,
            sampleId
        )
    }

    internal fun updateDataEntry(
        sampleId: Long,
        fieldId: Long,
        newUserInput: String
    ) {
        dbQuery.updateDataEntry(
            newUserInput,
            sampleId,
            fieldId
        )
    }

    /************************** DELETE FUNCTIONS **************************/
    internal fun deleteSample(sampleId: Long) {
        dbQuery.transaction {
            // First delete all data entries associated with this sample
            dbQuery.deleteDataEntryBySampleId(sampleId)
            // Now delete the sample itself
            dbQuery.deleteSample(sampleId)
        }
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

fun strToFT(fieldType: String): FieldType {
    return try {
        FieldType.valueOf(fieldType.uppercase())
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException("Unknown FieldType: $fieldType")
    }
}

fun ftToStr(fieldType: FieldType): String {
    return fieldType.toString()
}

fun readableToFT(fieldType: String): FieldType {
    val tmp : FieldType
    if (fieldType == "small text box") {
        return FieldType.SHORT_STRING
    }
    else if (fieldType == "large text box") {
        return FieldType.LONG_STRING
    }
    else if (fieldType == "numerical") {
        return FieldType.NUMBER
    }
    else if (fieldType == "dropdown") {
        return FieldType.DROPDOWN
    }
    else {
        return FieldType.MULTI_SELECT
    }
}

fun listToJsonString(list: List<String>?): String? {
    return list?.let { Json.encodeToString(it) }
}

fun jsonStringToList(json: String?): List<String>? {
    return json?.let { Json.decodeFromString(it) }
}



