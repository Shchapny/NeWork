package ru.netology.diplom.util

import android.content.Context
import android.text.format.DateFormat
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*

//функцию для события не забыть

fun Long.convertLongToString(): String {
    val date = this
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return dateFormat.format(date)
}

fun String.convertStringToLong(): Long? {
    val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).parse(this)
    return date?.time
}

fun String.dateFormatEntity(): String {
    val oldFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(this)
    val newFormat = SimpleDateFormat("dd MMM yyyy г.", Locale.getDefault())
    return newFormat.format(oldFormat as Date)
}

fun selectDateDialog(editText: EditText?, fragment: Fragment) {
    val calendar = Calendar.getInstance()

    val datePickerDialog = MaterialDatePicker.Builder.datePicker()
        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        .build()

    datePickerDialog.addOnPositiveButtonClickListener {
        calendar.timeInMillis = it
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val result = GregorianCalendar(year, month, day).time
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        editText?.setText(dateFormat.format(result))
    }
    datePickerDialog.show(fragment.childFragmentManager, "show")
}

fun selectTimeDialog(editText: EditText?, fragment: Fragment, context: Context) {
    val isSystem24Hour = DateFormat.is24HourFormat(context)
    val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

    val timePickerDialog = MaterialTimePicker.Builder()
        .setTimeFormat(clockFormat)
        .setHour(12)
        .setMinute(0)
        .build()
    timePickerDialog.addOnPositiveButtonClickListener {
        val result = String.format(
            Locale.getDefault(),
            "%02d:%02d",
            timePickerDialog.hour,
            timePickerDialog.minute
        )
        editText?.setText(result)
    }
    timePickerDialog.show(fragment.childFragmentManager, "show")
}


