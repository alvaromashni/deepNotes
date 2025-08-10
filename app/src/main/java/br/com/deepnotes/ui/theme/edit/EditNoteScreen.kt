package br.com.deepnotes.ui.edit

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    initialTitle: String = "",
    initialContent: String = "",
    isEditing: Boolean = false,
    onSave: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var content by remember { mutableStateOf(initialContent) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(if (isEditing) "Editar nota" else "Nova nota") }) }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Título") }, singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = content, onValueChange = { content = it },
                label = { Text("Conteúdo") }, minLines = 5, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onBack) { Text("Voltar") }
                Button(onClick = { onSave(title, content) }, enabled = title.isNotBlank()) {
                    Text(if (isEditing) "Atualizar" else "Salvar")
                }
            }
        }
    }
}