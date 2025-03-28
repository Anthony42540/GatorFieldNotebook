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
    @SerialName("form_id") val formId: Long,
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

// Images that a user uploads
@Serializable
data class SampleImage(
    @SerialName("image_id") val imageId: Int,
    @SerialName("sample_id") val sampleId: Int,
    @SerialName("image_data") val imageData: ByteArray,
    @SerialName("image_name") val imageName: String?,
    @SerialName("image_type") val imageType: String?,
    @SerialName("timestamp") val timestamp: String
) {
    // Override equals and hashCode because ByteArray needs special handling
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SampleImage

        if (imageId != other.imageId) return false
        if (sampleId != other.sampleId) return false
        if (!imageData.contentEquals(other.imageData)) return false
        if (imageName != other.imageName) return false
        if (imageType != other.imageType) return false
        if (timestamp != other.timestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = imageId
        result = 31 * result + sampleId
        result = 31 * result + imageData.contentHashCode()
        result = 31 * result + (imageName?.hashCode() ?: 0)
        result = 31 * result + (imageType?.hashCode() ?: 0)
        result = 31 * result + timestamp.hashCode()
        return result
    }
}