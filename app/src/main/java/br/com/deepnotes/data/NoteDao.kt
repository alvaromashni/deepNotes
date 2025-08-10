package br.com.deepnotes.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY createdAt DESC")
    suspend fun getAll(): List<Note>

    @Insert suspend fun insert(note: Note): Long
    @Update suspend fun update(note: Note)
    @Delete suspend fun delete(note: Note)
}