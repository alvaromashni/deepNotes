package br.com.deepnotes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.deepnotes.data.NoteRepository

class NotesViewModelFactory(private val repo: NoteRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            return NotesViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}