package com.days.notes

import android.content.Context
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Stores notes per calendar day in SharedPreferences as a JSON array of strings,
 * keyed by "yyyy-MM-dd". Shared by the app UI and the home screen widget.
 */
object NotesRepository {

    private const val PREFS_NAME = "days_notes_prefs"
    private val keyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun todayKey(): String = keyFormat.format(Date())

    fun keyFor(calendar: Calendar): String = keyFormat.format(calendar.time)

    fun prettyLabel(dateKey: String): String {
        return try {
            val date = keyFormat.parse(dateKey) ?: return dateKey
            val today = keyFormat.format(Date())
            if (dateKey == today) {
                "Today"
            } else {
                SimpleDateFormat("EEE, MMM d", Locale.US).format(date)
            }
        } catch (e: Exception) {
            dateKey
        }
    }

    fun getNotes(context: Context, dateKey: String): List<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(dateKey, null) ?: return emptyList()
        val notes = mutableListOf<String>()
        try {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                notes.add(array.getString(i))
            }
        } catch (e: Exception) {
            // ignore malformed data
        }
        return notes
    }

    fun saveNotes(context: Context, dateKey: String, notes: List<String>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (notes.isEmpty()) {
            prefs.edit().remove(dateKey).apply()
        } else {
            val array = JSONArray()
            notes.forEach { array.put(it) }
            prefs.edit().putString(dateKey, array.toString()).apply()
        }
        NotesWidgetProvider.notifyDataChanged(context)
    }

    fun addNote(context: Context, dateKey: String, text: String) {
        val notes = getNotes(context, dateKey).toMutableList()
        notes.add(text)
        saveNotes(context, dateKey, notes)
    }

    fun deleteNote(context: Context, dateKey: String, index: Int) {
        val notes = getNotes(context, dateKey).toMutableList()
        if (index in notes.indices) {
            notes.removeAt(index)
            saveNotes(context, dateKey, notes)
        }
    }
}
