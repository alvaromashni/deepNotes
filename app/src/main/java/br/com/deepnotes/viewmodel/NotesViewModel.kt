package br.com.deepnotes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import br.com.deepnotes.data.Note
import br.com.deepnotes.data.NoteRepository

data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val allNotes: List<Note> = emptyList(),
    val loading: Boolean = false,
    val sort: Sort = Sort.NEWEST,
    val query: String = ""
)

class NotesViewModel(private val repo: NoteRepository) : ViewModel() {
    private val _ui = MutableStateFlow(NotesUiState(loading = true))
    val ui = _ui.asStateFlow()

    init {
        refresh()
    }

    fun refresh() = viewModelScope.launch {
        _ui.value = _ui.value.copy(loading = true)
        val sourceList = repo.list()
        val filtered = applyFilter(sourceList, _ui.value.query)
        val sorted = applySort(filtered, _ui.value.sort)
        _ui.value = NotesUiState(allNotes = sourceList, notes = sorted, loading = false)
    }

    fun add(title: String, content: String) = viewModelScope.launch {
        repo.add(title, content); refresh()
    }

    fun delete(note: Note) = viewModelScope.launch {
        repo.delete(note); refresh()
    }

    fun update(note: Note) = viewModelScope.launch {
        repo.update(note)
        refresh()
    }

    fun setSort(sort: Sort) = viewModelScope.launch {
        val sourceList = _ui.value.allNotes
        val filtered = applyFilter(sourceList, _ui.value.query)
        val sorted = applySort(filtered, sort)
        _ui.value = _ui.value.copy(sort = sort, notes = sorted)
    }

    private fun applySort(notes: List<Note>, sort: Sort): List<Note> =
        when (sort) {
            Sort.NEWEST -> notes.sortedByDescending { it.createdAt }
            Sort.OLDEST -> notes.sortedBy { it.createdAt }
            Sort.TITLE -> notes.sortedBy { it.title.lowercase() }
        }

    private fun applyFilter(notes: List<Note>, searchQuery: String): List<Note> {
        if (searchQuery.isBlank()) return notes
        val normalizedQuery = searchQuery.trim().lowercase()
        return notes.filter { note ->
            note.title.contains(
                searchQuery,
                ignoreCase = true
            ) || note.content.contains(searchQuery, ignoreCase = true)
        }
    }

    fun setQuery(searchQuery: String) = viewModelScope.launch {
        val sourceList = _ui.value.allNotes
        val filtered = applyFilter(sourceList, searchQuery)
        val sorted = applySort(filtered, _ui.value.sort)
        _ui.value = _ui.value.copy(query = searchQuery, notes = sorted)
    }

    fun restore(note: Note) = viewModelScope.launch {
        repo.restore(note)
        refresh()
    }
}