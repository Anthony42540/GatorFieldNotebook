package com.dev.database.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// type of fields that the user can add to their form
@Serializable
enum class FieldType {
    SHORT_STRING,
    LONG_STRING,
    NUMBER,
    DROPDOWN,
    MULTI_SELECT
}

// sampleForm
@Serializable
data class SampleForm(
    @SerialName("form_id") val formId: Int,
    @SerialName("form_name") val formName: String,
    @SerialName("form_active") val formActive: Int
)

@Serializable
data class Field(
    @SerialName("field_id") val fieldId: Int,
    @SerialName("form_id") val formId: Int,
    @SerialName("field_name") val fieldName: String,
    @SerialName("order_num") val orderNum: Int,
    @SerialName("field_type") val fieldType: FieldType,
    @SerialName("is_required") val isRequired: Boolean,
    @SerialName("options") val options: List<String>? = null, //list of options, if drop down or multi-select field type
)

@Serializable
data class FieldNoID(
    @SerialName("field_name") val fieldName: String,
    @SerialName("field_type") val fieldType: FieldType,
    @SerialName("is_required") val isRequired: Boolean,
    @SerialName("options") val options: List<String>? = null, //list of options, if drop down or multi-select field type
)

// Stores actual form entries (Combines sample data and data entries for that sample)
@Serializable
data class SampleData(
    @SerialName("sample_id") val sampleId: Int,
    @SerialName("form_id") val formId: Int,
    @SerialName("sample_collection_id") val sampleCollectionId: Int, // <-- NEW
    @SerialName("date_collected_utc") val dateCollectedUTC: String,
    @SerialName("location") val location: String,
)

@Serializable
data class DataEntry(
    @SerialName("entry_id") val entryId: Int,
    @SerialName("sample_id") val sampleId: Int, //Links to sample
    @SerialName("field_id") val fieldId: Int, //Links to field that was filled out
    @SerialName("user_input") val userInput: String, //Actual data, converted to string
)

// Stores actual form entries (Combines sample data and data entries for that sample)
@Serializable
data class SampleAndData(
    @SerialName("sample_id") val sampleId: Int,
    @SerialName("form_id") val formId: Int,
    @SerialName("sample_collection_id") val sampleCollectionId: Int,
    @SerialName("date_collected_utc") val dateCollectedUTC: String,
    @SerialName("location") val location: String,
    @SerialName("data_entries") val dataEntries: Map<Long, String>
)