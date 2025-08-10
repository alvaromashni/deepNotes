# DeepNotes (Kotlin + Compose)

App simples de notas, **offline**, feito para demonstrar **Kotlin**, **Android Studio**, **Jetpack Compose**, **Room**, **MVVM (ViewModel + StateFlow)**, **Navigation Compose** e **Material 3** com **cores dinâmicas**.

## Features
- CRUD de notas (criar, listar, editar, apagar)
- **UNDO** ao apagar (Snackbar)
- **Ordenação**: recentes, antigas, título (A–Z)
- **Busca** por título/conteúdo
- **Tema claro/escuro** + **Dynamic Color** (Android 12+)
- UI com **Material 3** (Compose)

## Stack & Arquitetura
- **UI**: Jetpack Compose + Material 3  
- **Estado**: `ViewModel` + `StateFlow` (+ `collectAsStateWithLifecycle`)  
- **Dados**: Room (`@Entity`, `@Dao`, `RoomDatabase`) via **Repository**  
- **Navegação**: Navigation Compose  
- **Build**: KSP (no lugar de kapt)  

Fluxo de dados (MVVM):
```
[Compose UI] -> [ViewModel] -> [Repository] -> [DAO] -> [Room DB]
        ^------------------- StateFlow (ui state) -------------------^
```

## Estrutura
```
app/src/main/java/br/com/deepnotes/
  data/               # Room + Repository
    Note.kt
    NoteDao.kt
    AppDb.kt
    NoteRepository.kt
  viewmodel/
    NotesViewModel.kt
    NotesViewModelFactory.kt
    Sort.kt
  ui/
    edit/EditNoteScreen.kt
    list/NotesListScreen.kt
    theme/Theme.kt
  MainActivity.kt
```
## Decisões rápidas
- **KSP** no Room: builds mais rápidos e alinhados ao Kotlin.  
- **Repository** isola a fonte de dados (fácil plugar API depois).  
- **StateFlow + collectAsStateWithLifecycle**: UI reativa e lifecycle-aware.  
- **Dynamic Color**: integra com paleta do sistema (Monet) em Android 12+; fallback claro/escuro.

## Pontos para entrevista
- **UNDO**: UI dispara `delete` → Snackbar → se **DESFAZER**, VM chama `restore()` no repo (reinsere a mesma `Note`).  
- **MVVM**: UI “burra”; VM orquestra; Repository decide fonte; DAO só SQL.  
- Extensões: busca, ordenação, tema — tudo sem mexer na base.

## Roadmap curto
- [ ] **Swipe-to-delete** com UNDO  
- [ ] **Testes de DAO** (Room in-memory)  
- [ ] **Tela de detalhes** + compartilhamento  
- [ ] **Validação** de campos e UI tests  
- [ ] **Sincronização com API** (Retrofit) mantendo cache Room

---

Esse é um projeto feito especialmente para aprender Kotlin e android studio na prática -- vou continuar atualizando ele e descobrindo novas funções e dinâmicas.
