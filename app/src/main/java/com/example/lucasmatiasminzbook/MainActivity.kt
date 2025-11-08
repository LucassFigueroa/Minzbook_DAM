package com.example.lucasmatiasminzbook

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.rememberAsyncImagePainter
import com.example.lucasmatiasminzbook.data.local.book.BookRepository
import com.example.lucasmatiasminzbook.data.local.user.UserRepository
import com.example.lucasmatiasminzbook.nav.Route
import com.example.lucasmatiasminzbook.ui.AppViewModelProvider
import com.example.lucasmatiasminzbook.ui.cart.CartScreen
import com.example.lucasmatiasminzbook.ui.checkout.CheckoutScreen
import com.example.lucasmatiasminzbook.ui.catalog.BookDetailScreen
import com.example.lucasmatiasminzbook.ui.catalog.CatalogScreen
import com.example.lucasmatiasminzbook.ui.mybooks.MyBooksScreen
import com.example.lucasmatiasminzbook.ui.profile.ProfileScreen
import com.example.lucasmatiasminzbook.ui.ratings.RatingsScreen
import com.example.lucasmatiasminzbook.ui.support.SupportScreen
import com.example.lucasmatiasminzbook.ui.support.SupportViewModel
import com.example.lucasmatiasminzbook.ui.theme.MinzbookTheme

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
class MainActivity : FragmentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MinzbookTheme {
                val ui by authViewModel.uiState.collectAsState()
                val nav = rememberNavController()

                Scaffold(
                    topBar = {
                        if (ui.isAuthenticated) {
                            TopAppBar(
                                title = { Text("Minzbook", color = Color.White) },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = Color(0xFF00695C)
                                ),
                                navigationIcon = {
                                    IconButton(onClick = { nav.navigate(Route.Menu.path) }) {
                                        Icon(Icons.Filled.Home, contentDescription = "Inicio", tint = Color.White)
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { nav.navigate(Route.Cart.path) }) {
                                        Icon(Icons.Filled.ShoppingCart, contentDescription = "Carrito", tint = Color.White)
                                    }
                                    IconButton(onClick = { nav.navigate(Route.Profile.path) }) {
                                        Icon(Icons.Filled.AccountCircle, contentDescription = "Perfil", tint = Color.White)
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavHost(
                            navController = nav,
                            startDestination = if (ui.isAuthenticated) Route.Menu.path else Route.Home.path
                        ) {
                            composable(Route.Home.path) {
                                MinzbookHome(
                                    uiState = ui,
                                    onLoginClick = { nav.navigate(Route.Login.path) }
                                )
                            }
                            composable(Route.Login.path) {
                                LoginScreen(
                                    onBack = { nav.popBackStack() },
                                    onGoRegister = { nav.navigate(Route.Register.path) },
                                    onTryLogin = { email, password ->
                                        val repo = UserRepository(this@MainActivity)
                                        val res = repo.login(email, password)
                                        if (res.isSuccess) {
                                            val user = res.getOrNull()!!
                                            AuthLocalStore.setSession(
                                                applicationContext,
                                                user.email,
                                                user.name
                                            )
                                            null
                                        } else {
                                            res.exceptionOrNull()?.localizedMessage
                                                ?: "Usuario o contraseña incorrectos"
                                        }
                                    },
                                    onCredentialsOk = {
                                        val name = AuthLocalStore.lastName(applicationContext) ?: "Usuario"
                                        authViewModel.simulateLogin(name)
                                        nav.navigate(Route.Menu.path) {
                                            popUpTo(Route.Home.path) { inclusive = true }
                                        }
                                    },
                                    rememberInitial = AuthLocalStore.isRememberMe(applicationContext),
                                    onToggleRemember = {
                                        AuthLocalStore.setRememberMe(applicationContext, it)
                                    }
                                )
                            }
                            composable(Route.Register.path) {
                                RegisterScreen(
                                    onBack = { nav.popBackStack() },
                                    onGoLogin = { nav.navigate(Route.Login.path) },
                                    onTryRegister = { name, email, password, photoUri ->
                                        val repo = UserRepository(this@MainActivity)
                                        val res = repo.register(name, email, password)
                                        if (res.isSuccess) {
                                            if (photoUri != null) {
                                                AuthLocalStore.setProfilePhotoUri(applicationContext, photoUri)
                                            }
                                            null
                                        } else {
                                            res.exceptionOrNull()?.localizedMessage
                                                ?: "No se pudo crear la cuenta"
                                        }
                                    },
                                    onRegistered = {
                                        nav.navigate(Route.Login.path) {
                                            popUpTo(Route.Register.path) { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable(Route.Menu.path) {
                                MinzbookMenu(
                                    userName = ui.displayName ?: "Usuario",
                                    onExplore = { nav.navigate(Route.Catalog.path) },
                                    onMyBooks = { nav.navigate(Route.MyBooks.path) },
                                    onRatings = { nav.navigate(Route.Ratings.path) },
                                    onSupport = { nav.navigate(Route.Support.path) },
                                    onLogout = {
                                        AuthLocalStore.clearSession(this@MainActivity)
                                        authViewModel.simulateLogout()
                                        nav.navigate(Route.Home.path) {
                                            popUpTo(Route.Menu.path) { inclusive = true }
                                        }
                                    },
                                    onOpenBook = { id -> nav.navigate("${Route.BookDetail.path}/$id") }
                                )
                            }
                            composable(Route.Catalog.path) {
                                CatalogScreen(
                                    onBack = { nav.popBackStack() },
                                    onOpenBook = { id -> nav.navigate("${Route.BookDetail.path}/$id") }
                                )
                            }
                            composable(
                                route = "${Route.BookDetail.path}/{id}",
                                arguments = listOf(navArgument("id") { type = NavType.LongType })
                            ) { backStackEntry ->
                                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                                BookDetailScreen(bookId = id, onBack = { nav.popBackStack() })
                            }
                            composable(Route.Profile.path) {
                                ProfileScreen(onBack = { nav.popBackStack() })
                            }
                            composable(Route.MyBooks.path) {
                                MyBooksScreen(
                                    onBack = { nav.popBackStack() },
                                    onOpenBook = { id -> nav.navigate("${Route.BookDetail.path}/$id") }
                                )
                            }
                            composable(Route.Ratings.path) {
                                RatingsScreen(onBack = { nav.popBackStack() })
                            }
                            composable(Route.Cart.path) {
                                CartScreen(onBack = { nav.popBackStack() }, onCheckout = { nav.navigate(Route.Checkout.path) })
                            }
                            composable(Route.Support.path) {
                                val supportViewModel: SupportViewModel = viewModel(factory = AppViewModelProvider.Factory)
                                SupportScreen(viewModel = supportViewModel)
                            }
                            composable(Route.Checkout.path) {
                                CheckoutScreen(onBack = { nav.popBackStack() }, onPaymentSuccess = {
                                    nav.navigate(Route.Menu.path) {
                                        popUpTo(Route.Cart.path) { inclusive = true }
                                    }
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MinzbookHome(
    uiState: AuthUiState,
    onLoginClick: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.minzbook_logo),
                    contentDescription = "Logo Minzbook",
                    modifier = Modifier
                        .size(140.dp)
                        .padding(bottom = 16.dp)
                )
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 2.dp,
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Bienvenido a Minzbook, una aplicacion donde podras comprar nuevos libros, arrendar o incluso puedes hacer libros tu mismo!, esperamos que disfrutes tu experiencia 🥑📗✅!",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                if (!uiState.isAuthenticated) {
                    Button(
                        onClick = onLoginClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = MaterialTheme.shapes.extraLarge
                    ) { Text("Vamos!!!") }
                } else {
                    Text(
                        "Bienvenida, ${uiState.displayName}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MinzbookMenu(
    userName: String,
    onExplore: () -> Unit,
    onMyBooks: () -> Unit,
    onRatings: () -> Unit,
    onSupport: () -> Unit,
    onLogout: () -> Unit,
    onOpenBook: (Long) -> Unit
) {
    val repo = BookRepository(LocalContext.current)
    val books by repo.books().collectAsState(initial = emptyList())
    val featuredBook = remember(books) { books.randomOrNull() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Bienvenido, $userName", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        // Libro destacado
        if (featuredBook != null) {
            Text("Libro del día", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Card(
                onClick = { onOpenBook(featuredBook.id) },
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(Modifier.padding(16.dp)) {
                    val painter = if (featuredBook.coverResourceId != null) {
                        painterResource(id = featuredBook.coverResourceId)
                    } else {
                        rememberAsyncImagePainter(model = featuredBook.coverUri)
                    }
                    Image(
                        painter = painter,
                        contentDescription = featuredBook.title,
                        modifier = Modifier.size(100.dp, 150.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(featuredBook.title, style = MaterialTheme.typography.titleMedium)
                        Text(featuredBook.author, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Añadidos recientemente
        Text("Añadidos recientemente", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(books.take(5)) { book ->
                Card(
                    onClick = { onOpenBook(book.id) },
                    modifier = Modifier.width(150.dp)
                ) {
                    Column(Modifier.padding(8.dp)) {
                        Text(book.title, style = MaterialTheme.typography.titleSmall, maxLines = 2)
                        Text(book.author, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Botones de navegación
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onExplore, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Book, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Explorar")
            }
            Button(onClick = onMyBooks, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Crear/Ver")
            }
            Button(onClick = onRatings, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Notas")
            }
            Button(onClick = onSupport, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Soporte")
            }
        }
        TextButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text("Cerrar sesión") }
    }
}
