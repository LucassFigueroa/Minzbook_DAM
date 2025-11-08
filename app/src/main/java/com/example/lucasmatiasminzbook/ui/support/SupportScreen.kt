package com.example.lucasmatiasminzbook.ui.support

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.lucasmatiasminzbook.data.local.ticket.MessageAuthor
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
        if (uiState.selectedTicketId == null) {
            TicketListScreen(viewModel)
        } else {
            ChatScreen(viewModel)
        }
    }
}

@Composable
private fun TicketListScreen(viewModel: SupportViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Nuevo Ticket") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.currentUser?.let {
                        Text(
                            "Enviando como: ${it.name} (${it.email})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Asunto") })
                    OutlinedTextField(value = message, onValueChange = { message = it }, label = { Text("Descripción del problema") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.addTicket(subject, message)
                    subject = ""
                    message = ""
                    showDialog = false
                }) { Text("Crear") }
            }
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        if (uiState.currentUser?.role == Role.USER) {
            Button(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth()) { Text("Crear nuevo ticket") }
            Spacer(Modifier.height(16.dp))
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.tickets) { ticket ->
                Card(onClick = { viewModel.selectTicket(ticket.id) }, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(ticket.subject, style = MaterialTheme.typography.titleMedium)
                        Text(if (ticket.isResolved) "Resuelto" else "Pendiente")
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatScreen(viewModel: SupportViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var message by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            reverseLayout = true
        ) {
            items(uiState.messages.reversed()) { msg ->
                val isCurrentUser = (uiState.currentUser?.role == Role.USER && msg.author == MessageAuthor.USER) ||
                        (uiState.currentUser?.role == Role.SUPPORT && msg.author == MessageAuthor.SUPPORT)

                MessageBubble(message = msg.message, isCurrentUser = isCurrentUser)
            }
        }

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(value = message, onValueChange = { message = it }, modifier = Modifier.weight(1f), placeholder = { Text("Escribe un mensaje...") })
            IconButton(onClick = {
                viewModel.addMessage(message)
                message = ""
            }, enabled = message.isNotBlank()) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar")
            }
        }

        if (uiState.currentUser?.role == Role.SUPPORT) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Marcar como resuelto")
                Checkbox(checked = uiState.tickets.find { it.id == uiState.selectedTicketId }?.isResolved ?: false,
                    onCheckedChange = { isChecked ->
                        uiState.selectedTicketId?.let { viewModel.resolveTicket(it, isChecked) }
                    }
                )
            }
        }
    }
}

@Composable
private fun MessageBubble(message: String, isCurrentUser: Boolean) {
    val alignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (isCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isCurrentUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Surface(
            modifier = Modifier.padding(4.dp).clip(RoundedCornerShape(8.dp)),
            color = bubbleColor
        ) {
            Text(text = message, modifier = Modifier.padding(8.dp), color = textColor)
        }
    }
}
