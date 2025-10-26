package com.example.lucasmatiasminzbook.ui.support

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.lucasmatiasminzbook.data.local.user.Role
import com.example.lucasmatiasminzbook.data.local.user.UserRepository

@Composable
fun SupportScreen(viewModel: SupportViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val user = UserRepository(context).getLoggedInUser()
        if (user != null) {
            viewModel.setCurrentUser(user)
        }
    }

    if (uiState.isLoading) {
        CircularProgressIndicator()
    } else {
        when (uiState.currentUser?.role) {
            Role.USER -> UserSupportScreen(viewModel)
            Role.SUPPORT -> SupportTeamScreen(viewModel)
            else -> {
                // Moderator or other roles
                Text("No tienes acceso a esta sección.")
            }
        }
    }
}

@Composable
private fun UserSupportScreen(viewModel: SupportViewModel) {
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Crea un nuevo ticket de soporte", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Asunto") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = message, onValueChange = { message = it }, label = { Text("Mensaje") }, modifier = Modifier.fillMaxWidth().height(120.dp))
        Spacer(Modifier.height(16.dp))
        Button(onClick = { viewModel.addTicket(subject, message) }) {
            Text("Enviar Ticket")
        }

        Spacer(Modifier.height(24.dp))
        Text("Mis Tickets", style = MaterialTheme.typography.headlineSmall)
        LazyColumn {
            items(uiState.tickets) { ticket ->
                Card(modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(ticket.subject, style = MaterialTheme.typography.titleMedium)
                        Text(ticket.message)
                        Text(if (ticket.isResolved) "Estado: Resuelto" else "Estado: Pendiente")
                        ticket.response?.let {
                            Spacer(Modifier.height(8.dp))
                            Text("Respuesta de soporte: $it")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SupportTeamScreen(viewModel: SupportViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Tickets de Soporte", style = MaterialTheme.typography.headlineSmall)
        LazyColumn {
            items(uiState.tickets) { ticket ->
                var response by remember(ticket.id) { mutableStateOf(ticket.response ?: "") }
                Card(modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(ticket.subject, style = MaterialTheme.typography.titleMedium)
                        Text(ticket.message)
                        OutlinedTextField(value = response, onValueChange = { response = it }, label = { Text("Respuesta") }, modifier = Modifier.fillMaxWidth())
                        Button(onClick = { viewModel.addResponse(ticket, response) }, modifier = Modifier.fillMaxWidth()) {
                            Text("Enviar Respuesta")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Resuelto")
                            Checkbox(checked = ticket.isResolved, onCheckedChange = { viewModel.resolveTicket(ticket.id) })
                        }
                    }
                }
            }
        }
    }
}
