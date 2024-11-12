package com.dev.database.Entity

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


// type of fields that the user can add to their form
@Serializable
enum class FieldType {
    SHORT_STRING,
    LONG_STRING,
    DROPDOWN,
    MULTI_SELECT
    //IMAGE_UPLOAD     // For uploading images of sample, to implement later.
}

// Each field is stored in the field table.
// These are the fields that users can add when creating a new form.
@Serializable
data class Field(
    @SerialName("field_name") val fieldName: String,
    @SerialName("field_type") val fieldType: FieldType,
    @SerialName("order_num") val orderNumber : Int,
    @SerialName("is_required") val isRequired: Boolean = false,
    @SerialName("dropdown_options") val dropdownOptions: List<String>? = null,
    @SerialName("multi_select_options") val multiSelectOptions: List<String>? = null
)

// Stores the forms that the user has created (NOT form entries, just forms themselves).
@Serializable
data class SampleForm(
    @SerialName("form_name") val formName: String,
    @SerialName("date_created_utc") val dateCreatedUTC: String,
    @SerialName("location") val location: String,
    @SerialName("fields") val fields: List<Field>
)

// Stores actual form entries
@Serializable
data class SampleData(
    @SerialName("form_name") val formName: String,
    @SerialName("date_collected_utc") val dateCollectedUTC: String,
    @SerialName("location") val location: String,
    @SerialName("data_entries") val dataEntries: Map<Long, String> //First value contains field id, second value contains user input
)




@Serializable
data class RocketLaunch(
    @SerialName("flight_number")
    val flightNumber: Int,
    @SerialName("name")
    val missionName: String,
    @SerialName("date_utc")
    val launchDateUTC: String,
    @SerialName("details")
    val details: String?,
    @SerialName("success")
    val launchSuccess: Boolean?,
    @SerialName("links")
    val links: Links
) {
    var launchYear = Instant.parse(launchDateUTC).toLocalDateTime(TimeZone.UTC).year
}

@Serializable
data class Links(
    @SerialName("patch")
    val patch: Patch?,
    @SerialName("article")
    val article: String?
)

@Serializable
data class Patch(
    @SerialName("small")
    val small: String?,
    @SerialName("large")
    val large: String?
)