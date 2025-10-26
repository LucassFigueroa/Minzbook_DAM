package com.example.lucasmatiasminzbook.data

import android.content.Context
import com.example.lucasmatiasminzbook.data.local.ticket.TicketRepository
import com.example.lucasmatiasminzbook.data.local.database.AppDatabase

interface AppContainer {
    val ticketRepository: TicketRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val ticketRepository: TicketRepository by lazy {
        TicketRepository(AppDatabase.getInstance(context).ticketDao())
    }
}