package com.days.notes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.days.notes.databinding.ActivityQuickAddBinding

class QuickAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuickAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuickAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.quickAddCancel.setOnClickListener { finish() }
        binding.quickAddSave.setOnClickListener {
            val text = binding.quickAddEditText.text.toString().trim()
            if (text.isNotEmpty()) {
                NotesRepository.addNote(this, NotesRepository.todayKey(), text)
            }
            finish()
        }
    }
}
