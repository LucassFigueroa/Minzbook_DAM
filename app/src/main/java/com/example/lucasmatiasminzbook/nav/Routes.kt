package com.example.lucasmatiasminzbook.nav

sealed class Route(val path: String) {
    object Home : Route("home")
    object Login : Route("login")
    object Register : Route("register")

    object Menu : Route("menu")
    object Catalog : Route("catalog")
    object BookDetail : Route("book")

    object MyBooks : Route("mybooks")
    object Ratings : Route("ratings")
    object Profile : Route("profile")
    object Cart : Route("cart")
    object Support : Route("support")
    object Checkout : Route("checkout")
}
