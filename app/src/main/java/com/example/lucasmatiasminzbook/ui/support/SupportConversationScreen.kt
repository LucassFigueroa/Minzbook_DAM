package com.example.lucasmatiasminzbook.ui.support

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lucasmatiasminzbook.data.remote.support.SupportMessageDto
import com.example.lucasmatiasminzbook.ui.AppViewModelProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportConversationScreen(
    conversationId: Long,
    userId: Long?,                 // quién escribe (cliente o soporte)
    isSupport: Boolean,            // true si es rol SUPPORT
    onBack: () -> Unit,
    viewModel: SupportChatViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsState()
    var newMessage by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Cargar mensajes de la conversación
    LaunchedEffect(conversationId) {
        viewModel.loadMessages(conversationId)
    }

    // Mostrar errores
    LaunchedEffect(state.error) {
        state.error?.let { msg ->
            scope.launch { snackbarHostState.showSnackbar(msg) }
        }
    }

    // Cuando el ticket se cierra correctamente
    LaunchedEffect(state.ticketClosed) {
        if (state.ticketClosed) {
            scope.launch {
                snackbarHostState.showSnackbar("Ticket cerrado correctamente")
            }
            viewModel.clearTicketClosedFlag()
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ticket #$conversationId") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    if (isSupport) {
                        TextButton(
                            onClick = {
                                viewModel.closeConversation(conversationId)
                            },
                            enabled = !state.closing
                        ) {
                            Text("Marcar resuelto")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            // Lista de mensajes
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                items(state.messages) { msg ->
                    MessageBubble(
                        message = msg,
                        isMine = (userId != null && msg.userId == userId)
                    )
                }
            }

            // Caja para escribir mensaje (solo si tenemos userId)
            if (userId != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    OutlinedTextField(
                        value = newMessage,
                        onValueChange = { newMessage = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Escribir mensaje") }
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newMessage.isNotBlank()) {
                                viewModel.sendMessage(
                                    conversationId = conversationId,
                                    userId = userId,
                                    contenido = newMessage
                                )
                                newMessage = ""
                            }
                        },
                        enabled = !state.sending && newMessage.isNotBlank()
                    ) {
                        Text("Enviar")
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: SupportMessageDto,
    isMine: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = if (isMine) "Tú:" else "User ${message.userId}:",
            style = MaterialTheme.typography.labelSmall
        )
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = if (isMine)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = message.contenido,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
