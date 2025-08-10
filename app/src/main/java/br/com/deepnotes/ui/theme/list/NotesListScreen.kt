package br.com.deepnotes.ui.list
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.deepnotes.data.Note
import br.com.deepnotes.viewmodel.NotesUiState
import br.com.deepnotes.viewmodel.Sort
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.DismissDirection
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.MaterialTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
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
                    val dismissState = rememberDismissState(confirmStateChange = { value ->
                        if (value == DismissValue.DismissedToStart || value == DismissValue.DismissedToEnd) {
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
                            true
                        } else {
                            false
                        }
                    })

                    SwipeToDismiss(
                        state = dismissState,
                        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
                        dismissThresholds = { FractionalThreshold(0.35f) },
                        background = {
                            // Fundo vermelho com ícone de lixeira alinhado ao lado do swipe
                            val color = MaterialTheme.colorScheme.errorContainer
                            val contentColor = MaterialTheme.colorScheme.onErrorContainer
                            val alignment = when (dismissState.dismissDirection) {
                                DismissDirection.StartToEnd -> Alignment.CenterStart
                                DismissDirection.EndToStart -> Alignment.CenterEnd
                                else -> Alignment.CenterEnd
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min)
                                    .padding(horizontal = 4.dp)
                                    .background(color),
                                contentAlignment = alignment
                            ) {
                                Icon(Icons.Filled.Delete, contentDescription = null, tint = contentColor)
                            }
                        },
                        dismissContent = {
                            Column {
                                ListItem(
                                    headlineContent = { Text(note.title) },
                                    supportingContent = { Text(note.content, maxLines = 2) },
                                    modifier = Modifier
                                        .clickable { onOpen(note) }
                                        .padding(horizontal = 4.dp)
                                )
                                HorizontalDivider(
                                    thickness = DividerDefaults.Thickness,
                                    color = DividerDefaults.color
                                )
                            }
                        }
                    )
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