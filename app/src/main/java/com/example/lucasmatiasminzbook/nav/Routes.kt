package com.example.lucasmatiasminzbook.nav

sealed class Route(val path: String) {
    data object Home : Route("home")
    data object Login : Route("login")
    data object Register : Route("register")
    data object Menu : Route("menu")
    data object Catalog : Route("catalog")
    data object BookDetail : Route("book")
    data object MyBooks : Route("my_books")
    data object Ratings : Route("ratings")
}
