package br.com.deepnotes.ui.list
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.deepnotes.data.Note
import br.com.deepnotes.viewmodel.NotesUiState
import br.com.deepnotes.viewmodel.Sort
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(
    uiState: NotesUiState,
    onAdd: () -> Unit,
    onOpen: (Note) -> Unit,
    onDelete: (Note) -> Unit,
    onChangeSort: (Sort) -> Unit = {},
    onRestore: (Note) -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isMenuOpen by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Notas") },
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        if (isDarkTheme) {
                            Icon(Icons.Filled.LightMode, contentDescription = "Tema claro")
                        } else {
                            Icon(Icons.Filled.DarkMode, contentDescription = "Tema escuro")
                        }
                    }
                    Box {
                        TextButton(onClick = { isMenuOpen = true }) {
                            val sortLabel = when (uiState.sort) {
                                Sort.NEWEST -> "Mais recentes"
                                Sort.OLDEST -> "Mais antigas"
                                Sort.TITLE  -> "Título (A–Z)"
                            }
                            Text("Ordenar: $sortLabel")
                        }
                        DropdownMenu(expanded = isMenuOpen, onDismissRequest = { isMenuOpen = false }) {
                            DropdownMenuItem(
                                text = { Text("Mais recentes") },
                                onClick = { onChangeSort(Sort.NEWEST); isMenuOpen = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Mais antigas") },
                                onClick = { onChangeSort(Sort.OLDEST); isMenuOpen = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Título (A–Z)") },
                                onClick = { onChangeSort(Sort.TITLE); isMenuOpen = false }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) { Icon(Icons.Filled.Add, contentDescription = null) }
        }
    ) { padding ->
        if (uiState.loading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if ( uiState.notes.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Sem notas ainda. Toque no + para criar.")
            }
        } else {
            LazyColumn(Modifier.padding(padding)) {
                items(uiState.notes, key = { it.id }) { note ->
                    ListItem(
                        headlineContent = { Text(note.title) },
                        supportingContent = { Text(note.content, maxLines = 2) },
                        modifier = Modifier
                            .clickable { onOpen(note) }
                            .padding(horizontal = 4.dp)
                    )
                    Row(Modifier.padding(start = 16.dp, bottom = 8.dp)) {
                        TextButton(onClick = {
                            val deleted = note
                            onDelete(deleted)
                            scope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = "Nota apagada",
                                    actionLabel = "DESFAZER",
                                    withDismissAction = true,
                                    duration = SnackbarDuration.Short
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    onRestore(deleted)
                                }
                            }
                        }) { Text("Apagar") }
                    }
                    HorizontalDivider(
                        modifier = Modifier,
                        thickness = DividerDefaults.Thickness,
                        color = DividerDefaults.color
                    )
                }
            }
        }
    }
}