package com.example.lucasmatiasminzbook.ui.support

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lucasmatiasminzbook.ui.AppViewModelProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    userId: Long?,                    // id del usuario (puede ser null si es soporte)
    canCreateTicket: Boolean,         // true = usuario normal puede crear tickets, false = soporte solo ve
    onBack: () -> Unit,
    onOpenTicket: (Long) -> Unit,     // navegar al detalle/chat del ticket
    viewModel: SupportViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsState()

    // Cargar tickets según rol:
    // - Usuario normal: solo sus tickets
    // - SUPPORT: todos los tickets
    LaunchedEffect(userId, canCreateTicket) {
        if (canCreateTicket && userId != null) {
            viewModel.loadUserTickets(userId)
        } else if (!canCreateTicket) {
            viewModel.loadAllTickets()
        }
    }

    var email by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Mostrar feedback al crear ticket
    LaunchedEffect(state.ticketCreated) {
        if (state.ticketCreated) {
            scope.launch {
                snackbarHostState.showSnackbar("Ticket enviado correctamente")
            }
            viewModel.clearTicketCreatedFlag()
            subject = ""
            message = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Soporte") },
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
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            // ===============================
            // FORMULARIO SOLO SI PUEDEN CREAR
            // ===============================
            if (canCreateTicket) {

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo de contacto") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Asunto") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Mensaje") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        viewModel.createTicket(
                            userId = userId,
                            email = email,
                            subject = subject,
                            message = message
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading &&
                            email.isNotBlank() &&
                            subject.isNotBlank() &&
                            message.isNotBlank()
                ) {
                    Text("Enviar ticket")
                }

                Spacer(Modifier.height(24.dp))

            } else {
                // ===============================
                // VISTA PARA ROL SUPPORT
                // ===============================
                Text(
                    "Estás ingresando como usuario de soporte.\n" +
                            "Puedes ver los tickets disponibles, pero no crear nuevos.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))
            }

            // ===============================
            // LISTA DE TICKETS
            // ===============================
            Text(
                text = "Tickets",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            if (state.isLoading && state.tickets.isEmpty()) {
                CircularProgressIndicator()
            } else {
                LazyColumn {
                    items(state.tickets) { ticket ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            onClick = { onOpenTicket(ticket.id) }
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(
                                    ticket.asunto,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Estado: ${ticket.status}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Creado: ${ticket.fechaCreacion ?: "-"}",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }

            // ===============================
            // ERRORES
            // ===============================
            state.error?.let { errorMsg ->
                Spacer(Modifier.height(8.dp))
                Text(
                    text = errorMsg,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
