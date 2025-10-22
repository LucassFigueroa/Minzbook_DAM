package com.example.lucasmatiasminzbook.data

object BookRepository {

    fun initialBooks(): List<Book> = listOf(
        Book(
            id = "1",
            title = "Libro 1",
            author = "Lukkk",
            synopsis = "Una aventura que introduce el mundo de Minzbook y sus misterios."
        ),
        Book(
            id = "2",
            title = "Libro 2",
            author = "Skadi",
            synopsis = "Relatos sobre amistad y coraje en escenarios inesperados."
        ),
        Book(
            id = "3",
            title = "Libro 3",
            author = "Heavy",
            synopsis = "Acción trepidante con giros que mantienen la intriga."
        ),
        Book(
            id = "4",
            title = "Libro 4",
            author = "Crown",
            synopsis = "Estrategia y reflexión en un viaje por decisiones difíciles."
        ),
        Book(
            id = "5",
            title = "Libro 5",
            author = "Pepe",
            synopsis = "Una historia emotiva que conecta con el corazón del lector."
        )
    )
}
