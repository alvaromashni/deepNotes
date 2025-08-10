package br.com.deepnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.deepnotes.data.AppDb
import br.com.deepnotes.data.NoteRepository
import br.com.deepnotes.ui.edit.EditNoteScreen
import br.com.deepnotes.ui.list.NotesListScreen
import br.com.deepnotes.viewmodel.NotesViewModel
import br.com.deepnotes.viewmodel.NotesViewModelFactory
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import br.com.deepnotes.ui.theme.DeepNotesTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDb.get(this)
        val repo = NoteRepository(db.noteDao())

        setContent {
            var useDarkTheme by rememberSaveable { mutableStateOf(false) }
            DeepNotesTheme(darkTheme = useDarkTheme, dynamicColor = true) {
                val nav = rememberNavController()
                val vm: NotesViewModel = viewModel(factory = NotesViewModelFactory(repo))
                val state by vm.ui.collectAsStateWithLifecycle()

                NavHost(navController = nav, startDestination = "list") {
                    composable("list") {
                        NotesListScreen(
                            uiState = state,
                            onAdd = { nav.navigate("edit") },
                            onOpen = { note -> nav.navigate("edit/${note.id}")},
                            onDelete = { vm.delete(it) },
                            onChangeSort = { vm.setSort(it) },
                            isDarkTheme = useDarkTheme,
                            onToggleTheme = { useDarkTheme = !useDarkTheme },
                            onRestore = { vm.restore(it) }
                        )
                    }
                    composable("edit") {
                        EditNoteScreen(
                            onSave = { title, content ->
                                vm.add(title, content)
                                nav.popBackStack()
                            },
                            onBack = { nav.popBackStack() }
                        )
                    }
                    composable(
                            route = "edit/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.LongType }))
                        { backStackEntry ->
                        val id = backStackEntry.arguments?.getLong("id") ?: 0L
                        val note = state.notes.firstOrNull { it.id == id }
                        EditNoteScreen(
                            initialTitle = note?.title.orEmpty(),
                            initialContent = note?.content.orEmpty(),
                            isEditing = true,
                            onSave = { title, content ->
                                if (note != null) vm.update(note.copy(title = title, content = content))
                                nav.popBackStack()
                            },
                            onBack = { nav.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}