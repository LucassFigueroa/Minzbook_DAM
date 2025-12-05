package com.example.lucasmatiasminzbook.ui.support

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lucasmatiasminzbook.data.remote.support.SupportConversationDto
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    userId: Long?,
    isSupport: Boolean,                        // viene desde MainActivity
    onBack: () -> Unit,
    onOpenTicket: (Long) -> Unit,
    viewModel: SupportViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val canCreateTicket = !isSupport          // soporte no crea tickets nuevos

    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    // Cargar tickets según rol
    LaunchedEffect(userId, isSupport) {
        if (isSupport) {
            viewModel.loadAllTickets()        // SOPORTE: TODOS los tickets
        } else {
            userId?.let { viewModel.loadUserTickets(it) }   // CLIENTE: solo los suyos
        }
    }

    // Mostrar errores
    LaunchedEffect(uiState.error) {
        uiState.error?.let { msg ->
            scope.launch { snackbarHostState.showSnackbar(msg) }
        }
    }

    // Snack cuando se crea ticket
    LaunchedEffect(uiState.ticketCreated) {
        if (uiState.ticketCreated) {
            scope.launch { snackbarHostState.showSnackbar("Tu ticket fue enviado 💌") }
            viewModel.clearTicketCreatedFlag()
            subject = ""
            message = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isSupport) "Tickets de soporte" else "Soporte / Mis tickets"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            // Texto superior según rol
            if (isSupport) {
                Text(
                    text = "Panel de soporte: puedes ver y responder los tickets de todos los usuarios.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    text = "Estamos aquí para ayudarte 😊",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // FORMULARIO NUEVO TICKET (solo clientes)
            if (canCreateTicket) {
                Text(
                    text = "Crear nuevo ticket",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Asunto") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Describe tu problema o consulta") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        viewModel.createTicket(
                            userId = userId,
                            email = "",
                            subject = subject,
                            message = message
                        )
                    },
                    enabled = !uiState.isLoading &&
                            !subject.isBlank() &&
                            !message.isBlank() &&
                            userId != null,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(if (uiState.isLoading) "Enviando..." else "Enviar ticket")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // LOADING inicial
            if (uiState.isLoading && uiState.tickets.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // LISTA DE TICKETS
            if (uiState.tickets.isNotEmpty()) {
                Text(
                    text = if (isSupport) "Todos los tickets" else "Mis tickets",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = true)
                ) {
                    items(uiState.tickets) { ticket ->
                        TicketRow(
                            ticket = ticket,
                            onClick = { onOpenTicket(ticket.id) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            } else if (!uiState.isLoading) {
                Text(
                    text = if (isSupport)
                        "No hay tickets registrados."
                    else
                        "Aún no tienes tickets de soporte.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun TicketRow(
    ticket: SupportConversationDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = ticket.asunto,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Estado: ${ticket.status}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Usuario: ${ticket.userId}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Creado: ${ticket.fechaCreacion ?: "-"}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Actualizado: ${ticket.fechaActualizacion ?: "-"}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
