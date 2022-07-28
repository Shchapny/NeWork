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

fun String.dateFormat(): String {
    val oldFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(this)
    val newFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return newFormat.format(oldFormat as Date)
}

fun String.sendDate(): String {
    val oldFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).parse(this)
    val newFormat = SimpleDateFormat("YYYY-MM-dd", Locale.getDefault())
    return newFormat.format(oldFormat as Date)
}

fun String.sendTime(): String {
    val oldFormat = SimpleDateFormat("hh:mm", Locale.getDefault()).parse(this)
    val newFormat = SimpleDateFormat("hh:mm:ss", Locale.getDefault())
    return newFormat.format(oldFormat as Date)
}

fun String.timeFormat() = substring(0, 5)

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


