package com.example.lucasmatiasminzbook.ui.support

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lucasmatiasminzbook.data.remote.support.SupportMessageDto
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportConversationScreen(
    conversationId: Long,
    userId: Long?,                 // quién escribe (cliente o soporte)
    isSupport: Boolean,            // true si es rol SUPPORT
    onBack: () -> Unit,
    viewModel: SupportChatViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var newMessage by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Cargar mensajes de la conversación
    LaunchedEffect(conversationId) {
        viewModel.loadMessages(conversationId)
    }

    // Mostrar errores como snackbar
    LaunchedEffect(state.error) {
        state.error?.let { msg ->
            scope.launch { snackbarHostState.showSnackbar(msg) }
        }
    }

    // Cuando el ticket se cierra correctamente
    LaunchedEffect(state.ticketClosed) {
        if (state.ticketClosed) {
            scope.launch {
                snackbarHostState.showSnackbar("Ticket cerrado correctamente ✅")
            }
            viewModel.clearTicketClosedFlag()
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Ticket #$conversationId")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    // Solo el rol SUPPORT puede cerrar el ticket
                    if (isSupport) {
                        TextButton(
                            onClick = { viewModel.closeConversation(conversationId) },
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

            // Lista de mensajes (chat)
            if (state.isLoading && state.messages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.messages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aún no hay mensajes en este ticket.\nEscribe el primero ✉️",
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    reverseLayout = false
                ) {
                    items(state.messages) { msg ->
                        val isMine = userId != null && msg.userId == userId

                        MessageBubble(
                            message = msg,
                            isMine = isMine,
                            isSupport = isSupport
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }

            // Caja para escribir mensaje (solo si tenemos userId)
            if (userId != null) {
                Divider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newMessage,
                        onValueChange = { newMessage = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Escribir mensaje") },
                        maxLines = 3
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newMessage.isNotBlank()) {
                                viewModel.sendMessage(
                                    conversationId = conversationId,
                                    userId = userId,
                                    contenido = newMessage.trim()
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
    isMine: Boolean,
    isSupport: Boolean
) {
    // Alineamos tipo WhatsApp: mis mensajes a la derecha, los otros a la izquierda
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isMine) Alignment.End else Alignment.Start,
            modifier = Modifier
                .widthIn(max = 280.dp)
        ) {
            val label = when {
                isMine && isSupport -> "Soporte:"
                isMine -> "Tú:"
                else -> "User ${message.userId}:"
            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )

            val bubbleColor = when {
                isMine && isSupport ->
                    MaterialTheme.colorScheme.primary   // Soporte = burbuja verde/primaria
                isMine && !isSupport ->
                    MaterialTheme.colorScheme.secondary  // Cliente = otro color para sus mensajes
                else ->
                    MaterialTheme.colorScheme.surfaceVariant // el otro lado = grisito
            }

            Surface(
                shape = MaterialTheme.shapes.medium,
                color = bubbleColor,
                tonalElevation = 2.dp
            ) {
                Text(
                    text = message.contenido,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
