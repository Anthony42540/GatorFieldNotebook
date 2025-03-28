package org.example.project


import KhandFontFamily
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.dev.database.cache.Database
import com.dev.database.entity.FilterDateOption
import com.dev.database.entity.SampleAndData
import com.dev.database.entity.SampleForm
import com.dev.database.entity.ViewOption
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

object GlobalState {
    var sampleId: Long? = null
}

expect fun exportToCSV(form: String, database: Database? = null, groupedSamples: Map<String, List<SampleAndData>>): Boolean

@Composable
fun ViewSampleCollectionScreen(navController: NavController, database: Database? = null) {
    var check by remember { mutableStateOf<Boolean?>(null) }
    var formNameVar by remember { mutableStateOf<String?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    var samples by remember { mutableStateOf<List<SampleAndData>>(emptyList()) } //List of all samples

    val listOfForms = database?.getAllSampleForms() //List of all forms
    var selectedForms by remember { mutableStateOf(listOfForms) } //List of selected forms (default is all)
    var viewOption by remember { mutableStateOf(ViewOption.BY_COLLECTION) }
    var selectedDate by remember { mutableStateOf(getCurrentTimeMillis()) }
    var selectedDateFilter by remember { mutableStateOf(FilterDateOption.BEFORE_DATE) }

    val filteredSamples = samples.filter { sample ->
        val isFormMatch = selectedForms!!.any { it.formId.toInt() == sample.formId }

        //convert UTC string to millis
        val dateMillis = utcStringToMillis(sample.dateCollectedUTC)
        //check if date matches selected date (need to remove time from long)
        val dateValue = isDateMatch(dateMillis, selectedDate, selectedDateFilter)

        isFormMatch && dateValue
    }

    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    var expandedGroups by remember { mutableStateOf(setOf<String>()) }
    var expandedFilters by remember { mutableStateOf(false) }
    var expandedSort by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        loadAllSamples(database) { newSamples, errorMessage ->
            samples = newSamples
            error = errorMessage
            isLoading = false
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        Header()

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.CenterHorizontally)
            )
            return@Column
        }

        if (error != null) {
            Text(
                text = error!!,
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
            return@Column
        }

        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text("Delete Samples") },
                text = { Text("Are you sure you want to delete all samples? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                clearAllSamples(database)
                                showDeleteConfirmation = false
                                loadAllSamples(database) { newSamples, errorMessage ->
                                    samples = newSamples
                                    error = errorMessage
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)

                    ) {
                        Text(
                            style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Medium),
                            text = "Delete All", color = Color.White,
                            fontSize = 20.sp
                        )
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDeleteConfirmation = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0021A5)
                        )
                    ) {
                        Text(
                            style = TextStyle(fontFamily = KhandFontFamily(), fontWeight = FontWeight.Medium),
                            text = "Cancel",
                            fontSize = 20.sp
                        )
                    }
                }
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {

            SectionTitle("Samples")

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .clickable {
                        expandedFilters = !expandedFilters
                    },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .animateContentSize()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Filters",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    if (expandedFilters) {

                        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

                        selectedForms?.let {
                            if (listOfForms != null) {
                                MultiSelectForms(
                                    listOfForms,
                                    selectedForms!!,
                                    onSelectionChange = { newSelection ->
                                        selectedForms = newSelection
                                    }
                                )
                            }
                        }
                        DateFilter(
                            selectedDate = selectedDate,
                            selectedDateFilter = selectedDateFilter,
                            onDateSelected = { dateMillis ->
                                if (dateMillis != null) {
                                    selectedDate = dateMillis
                                }
                            },
                            onFilterDateChanged = { dateFilter ->
                                selectedDateFilter = dateFilter
                            }
                        )
                    }
                }
            }

            Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp))

            Box (
                modifier = Modifier.fillMaxSize()
            ) {
                val groupedSamples = filteredSamples.groupBy { database?.getSampleForm(it.formId.toLong())?.formName ?: "Unknown" }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (filteredSamples.isEmpty() && samples.isNotEmpty()) {
                        Text(
                            text = "Select at least one collection or modify your date range.",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(10.dp),
                        )
                    } else if (samples.isEmpty()) {
                        Text(
                            text = "No samples yet!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 70.dp)
                ) {

                    groupedSamples.forEach { (formName, sampleList) ->
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .clickable {
                                        expandedGroups = if (expandedGroups.contains(formName)) {
                                            expandedGroups - formName
                                        } else {
                                            expandedGroups + formName
                                        }
                                    },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .animateContentSize()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Collection: $formName",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Icon(
                                            imageVector = if (expandedGroups.contains(formName)) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Expand/Collapse"
                                        )

                                        Row (
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Button(
                                                onClick = {
                                                    formNameVar = formName
                                                    check = exportToCSV(formName, database, groupedSamples) },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0021A5))
                                            ) {
                                                Text("Export (CSV)")
                                            }
                                        }
                                    }

                                    if (expandedGroups.contains(formName)) {
                                        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                                        sampleList.forEach { sample ->
                                            SampleRow(
                                                sample = sample,
                                                form = database!!.getSampleForm(sample.formId.toLong()),
                                                onSampleClick = { sampleId ->
                                                    GlobalState.sampleId = sampleId
                                                    navController.navigate("sampleDetail/$sampleId")
                                                }
                                            )
                                            Divider(color = Color.LightGray, thickness = 0.5.dp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                check?.let { success ->
                    LaunchedEffect(check) {
                        delay(2000)
                        check = null
                    }

                    Dialog(onDismissRequest = { check = null }) {
                        Card(
                            modifier = Modifier
                                .height(100.dp)
                                .padding(20.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Text(
                                text = if (success) "$formNameVar was successfully exported to your downloads folder." else "$formNameVar could not be exported to your downloads folder.",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .wrapContentSize(Alignment.Center),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ActionButton("Back", onClick = { navController.navigate("home") }, Color(0xFF0021A5), Color.White)
                    ActionButton("Delete All", onClick = { showDeleteConfirmation = true }, Color.Red, Color.White)
                }
            }
        }
    }
}

private fun clearAllSamples(database: Database?) {
    if (database == null) return

    try {
        database.deleteAllSamples()
    } catch (e: Exception) {
        println("Error clearing samples: ${e.message}")
    }
}

private fun loadAllSamples(
    database: Database?,
    onComplete: (List<SampleAndData>, String?) -> Unit
) {
    try {
        if (database == null) {
            onComplete(emptyList(), "Database not initialized")
            return
        }

        val allSamples = database.getAllSampleData()
        val sampleDetails = allSamples.map { sample ->
            database.getSampleAndData(sample.sampleId.toLong())
        }
        onComplete(sampleDetails, null)
    } catch (e: Exception) {
        onComplete(emptyList(), "Error loading samples: ${e.message}")
    }
}

@Composable
private fun SampleRow(sample: SampleAndData, form: SampleForm, onSampleClick: (Long) -> Unit) {
    val pair = formatDate(sample.dateCollectedUTC).split("T")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable { onSampleClick(sample.sampleId.toLong()) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#${sample.sampleCollectionId}",
                fontSize = 16.sp,
                color = Color.Black
            )

            Text(
                text = "${pair[0]} at ${pair[1]}",
                fontSize = 16.sp,
                color = Color.Black
            )

        }
    }
}


private fun formatDate(dateString: String): String {
    return try {
        val instant = Instant.parse(dateString)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        "${localDateTime.monthNumber.toString().padStart(2, '0')} - " +
                "${localDateTime.dayOfMonth.toString().padStart(2, '0')} - " +
                "${localDateTime.year}"
    } catch (e: Exception) {
        dateString
    }
}


@Composable
fun MultiSelectForms(
    options: List<SampleForm>,
    selectedOptions: List<SampleForm>,
    onSelectionChange: (List<SampleForm>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val allSelected = options.isNotEmpty() && selectedOptions.size == options.size

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    expanded = !expanded
                },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
            shape = RoundedCornerShape(12.dp)
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(5.dp)
                ) {
                    Text(
                        text = "Filter by Collection",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                if (expanded) {

                    Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                    ) {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 300.dp)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val newSelection = if (allSelected) emptyList() else options
                                            onSelectionChange(newSelection)
                                        }
                                        .padding(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = allSelected,
                                        onCheckedChange = {
                                            val newSelection = if (allSelected) emptyList() else options
                                            onSelectionChange(newSelection)
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = if (allSelected) "Deselect All" else "Select All")
                                }
                            }

                            items(options) { option ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val newSelection =
                                                if (selectedOptions.contains(option)) {
                                                    selectedOptions - option
                                                } else {
                                                    selectedOptions + option
                                                }
                                            onSelectionChange(newSelection)
                                        }
                                        .padding(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = selectedOptions.contains(option),
                                        onCheckedChange = {
                                            val newSelection =
                                                if (selectedOptions.contains(option)) {
                                                    selectedOptions - option
                                                } else {
                                                    selectedOptions + option
                                                }
                                            onSelectionChange(newSelection)
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = option.formName)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateFilter(
    selectedDate: Long?,
    selectedDateFilter: FilterDateOption,
    onDateSelected: (Long?) -> Unit,
    onFilterDateChanged: (FilterDateOption) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    expanded = !expanded
                },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(5.dp)
                ) {
                    Text(
                        text = "Filter by Date",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                if (expanded) {

                    Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                    ) {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 300.dp)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            showDatePicker = !showDatePicker
                                        }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = selectedDate?.let { convertMillisToDate(it) }
                                            ?: "Select a date",
                                        fontSize = 16.sp,
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit date"
                                    )
                                }
                            }
                            item {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onFilterDateChanged(FilterDateOption.BEFORE_DATE)
                                            }
                                            .padding(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = selectedDateFilter == FilterDateOption.BEFORE_DATE,
                                            onClick = {
                                                onFilterDateChanged(FilterDateOption.BEFORE_DATE)
                                            }
                                        )
                                        Text(
                                            text = "On or Before Date",
                                            modifier = Modifier.padding(start = 8.dp)
                                        )
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onFilterDateChanged(FilterDateOption.ON_DATE)
                                            }
                                            .padding(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = selectedDateFilter == FilterDateOption.ON_DATE,
                                            onClick = {
                                                onFilterDateChanged(FilterDateOption.ON_DATE)
                                            }
                                        )
                                        Text(
                                            text = "On Date",
                                            modifier = Modifier.padding(start = 8.dp)
                                        )
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onFilterDateChanged(FilterDateOption.AFTER_DATE)
                                            }
                                            .padding(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = selectedDateFilter == FilterDateOption.AFTER_DATE,
                                            onClick = {
                                                onFilterDateChanged(FilterDateOption.AFTER_DATE)
                                            }
                                        )
                                        Text(
                                            text = "On or After Date",
                                            modifier = Modifier.padding(start = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    onDateSelected(datePickerState.selectedDateMillis?.let { millis ->
                                        val localDate = Instant.fromEpochMilliseconds(millis)
                                            .toLocalDateTime(TimeZone.UTC)
                                            .date
                                        localDate.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                                    })
                                    showDatePicker = false
                                }) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePicker = false }) {
                                    Text("Cancel")
                                }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }
                }
            }
        }
    }
}

fun convertMillisToDate(millis: Long): String {
    val localDateTime = Instant.fromEpochMilliseconds(millis)
        .toLocalDateTime(TimeZone.currentSystemDefault())

    return "${localDateTime.monthNumber}/${localDateTime.dayOfMonth}/${localDateTime.year}"
}

fun getCurrentTimeMillis(): Long {
    return Clock.System.now().toEpochMilliseconds()
}

fun utcStringToMillis(utcString: String): Long {
    val instant = Instant.parse(utcString + "Z")
    return instant.toEpochMilliseconds()
}

fun isDateMatch(dateMillis: Long, selectedDate: Long, selectedDateFilter: FilterDateOption): Boolean {
    val date = Instant.fromEpochMilliseconds(dateMillis).toLocalDateTime(TimeZone.currentSystemDefault()).date
    val selected = Instant.fromEpochMilliseconds(selectedDate).toLocalDateTime(TimeZone.currentSystemDefault()).date

    return when (selectedDateFilter) {
        FilterDateOption.BEFORE_DATE -> date <= selected
        FilterDateOption.ON_DATE -> date == selected
        FilterDateOption.AFTER_DATE -> date >= selected
    }
}