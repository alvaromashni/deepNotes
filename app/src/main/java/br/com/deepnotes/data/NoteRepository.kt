package br.com.deepnotes.data

class NoteRepository(private val dao: NoteDao) {
    suspend fun list() = dao.getAll()
    suspend fun add(title: String, content: String) =
        dao.insert(Note(title = title, content = content))
    suspend fun update(note: Note) = dao.update(note)
    suspend fun delete(note: Note) = dao.delete(note)

    suspend fun restore(note: Note) = dao.insert(note)
}