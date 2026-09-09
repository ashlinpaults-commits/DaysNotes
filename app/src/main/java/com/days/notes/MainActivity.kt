package com.days.notes

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.days.notes.databinding.ActivityMainBinding
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.prevDayButton.setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            refresh()
        }
        binding.nextDayButton.setOnClickListener {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            refresh()
        }
        binding.addNoteButton.setOnClickListener { addNote() }
        binding.noteEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                addNote()
                true
            } else {
                false
            }
        }

        binding.notesListView.setOnItemClickListener { _, _, position, _ ->
            deleteNoteAt(position)
        }

        refresh()
    }

    private fun currentKey(): String = NotesRepository.keyFor(calendar)

    private fun addNote() {
        val text = binding.noteEditText.text.toString().trim()
        if (text.isEmpty()) return
        NotesRepository.addNote(this, currentKey(), text)
        binding.noteEditText.setText("")
        refresh()
    }

    private fun deleteNoteAt(position: Int) {
        NotesRepository.deleteNote(this, currentKey(), position)
        refresh()
    }

    private fun refresh() {
        binding.dayLabel.text = NotesRepository.prettyLabel(currentKey())
        val notes = NotesRepository.getNotes(this, currentKey())

        binding.emptyStateText.visibility =
            if (notes.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        binding.notesListView.visibility =
            if (notes.isEmpty()) android.view.View.GONE else android.view.View.VISIBLE

        val adapter = object : ArrayAdapter<String>(
            this, R.layout.note_row, R.id.noteRowText, notes
        ) {
            override fun getView(
                position: Int,
                convertView: android.view.View?,
                parent: android.view.ViewGroup
            ): android.view.View {
                val view = super.getView(position, convertView, parent)
                val deleteIcon = view.findViewById<android.widget.TextView>(R.id.noteRowDelete)
                deleteIcon.setOnClickListener { deleteNoteAt(position) }
                return view
            }
        }
        binding.notesListView.adapter = adapter
    }
}
