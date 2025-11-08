package com.example.lucasmatiasminzbook.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.lucasmatiasminzbook.data.local.book.Book
import com.example.lucasmatiasminzbook.data.local.book.BookDao
import com.example.lucasmatiasminzbook.data.local.book.Review
import com.example.lucasmatiasminzbook.data.local.book.ReviewDao
import com.example.lucasmatiasminzbook.data.local.cart.CartDao
import com.example.lucasmatiasminzbook.data.local.cart.CartItem
import com.example.lucasmatiasminzbook.data.local.ticket.Ticket
import com.example.lucasmatiasminzbook.data.local.ticket.TicketDao
import com.example.lucasmatiasminzbook.data.local.ticket.TicketMessage
import com.example.lucasmatiasminzbook.data.local.user.UserDao
import com.example.lucasmatiasminzbook.data.local.user.UserEntity

@Database(
    entities = [UserEntity::class, Book::class, Review::class, CartItem::class, Ticket::class, TicketMessage::class],
    version = 25, // Forzar la recreación de la base de datos
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun bookDao(): BookDao
    abstract fun reviewDao(): ReviewDao
    abstract fun cartDao(): CartDao
    abstract fun ticketDao(): TicketDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "minzbook.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
