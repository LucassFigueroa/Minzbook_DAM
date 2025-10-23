package com.example.lucasmatiasminzbook

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lucasmatiasminzbook.data.local.user.UserRepository
import com.example.lucasmatiasminzbook.nav.Route
import com.example.lucasmatiasminzbook.ui.auth.canUseBiometric
import com.example.lucasmatiasminzbook.ui.auth.showBiometricPrompt
import com.example.lucasmatiasminzbook.ui.catalog.CatalogScreen
import com.example.lucasmatiasminzbook.ui.ratings.RatingsScreen
import com.example.lucasmatiasminzbook.ui.theme.MinzbookTheme

class MainActivity : FragmentActivity() { // Requisito para BiometricPrompt
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MinzbookTheme {
                val ui by authViewModel.uiState.collectAsState()
                val nav = rememberNavController()

                Scaffold { innerPadding ->
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
                            // HOME
                            composable(Route.Home.path) {
                                MinzbookHome(
                                    uiState = ui,
                                    onLoginClick = { nav.navigate(Route.Login.path) }
                                )
                            }

                            // LOGIN (valida y luego exige huella para entrar)
                            composable(Route.Login.path) {
                                LoginScreen(
                                    onBack = { nav.popBackStack() },
                                    onGoRegister = { nav.navigate(Route.Register.path) },

                                    // 1) Valida credenciales; NO navega aquí
                                    onTryLogin = { email, password ->
                                        val repo = UserRepository(this@MainActivity)
                                        val res = repo.login(email, password)
                                        if (res.isSuccess) {
                                            val user = res.getOrNull()!!
                                            // Guardamos sesión para tener nombre/email disponibles
                                            AuthLocalStore.setSession(
                                                applicationContext,
                                                user.email,
                                                user.name
                                            )
                                            // Si quieres forzar huella para ese usuario:
                                            AuthLocalStore.setBiometricEnabled(applicationContext, true)
                                            null // OK → LoginScreen mostrará el prompt cuando el usuario toque el botón de huella
                                        } else {
                                            res.exceptionOrNull()?.localizedMessage
                                                ?: "Usuario o contraseña incorrectos"
                                        }
                                    },

                                    // 2) Prompt biométrico: si OK → recién navega al menú
                                    onBiometricClick = {
                                        if (!canUseBiometric(this@MainActivity)) {
                                            // Aquí puedes mostrar un Toast/snackbar si no hay soporte
                                            return@LoginScreen
                                        }
                                        showBiometricPrompt(
                                            activity = this@MainActivity,
                                            onSuccess = {
                                                val name = AuthLocalStore.lastName(applicationContext) ?: "Usuario"
                                                authViewModel.simulateLogin(name)
                                                nav.navigate(Route.Menu.path) {
                                                    popUpTo(Route.Home.path) { inclusive = true }
                                                }
                                            },
                                            onError = {
                                                // Opcional: mostrar error en snackbar/toast
                                            }
                                        )
                                    }
                                )
                            }

                            // REGISTER (tras crear, vuelve a Login)
                            composable(Route.Register.path) {
                                RegisterScreen(
                                    onBack = { nav.popBackStack() },
                                    onGoLogin = { nav.navigate(Route.Login.path) },
                                    onTryRegister = { email, password ->
                                        val repo = UserRepository(this@MainActivity)
                                        val display = email.substringBefore("@")
                                            .replaceFirstChar { it.uppercase() }
                                        val res = repo.register(
                                            name = display,
                                            email = email,
                                            password = password
                                        )
                                        if (res.isSuccess) null
                                        else res.exceptionOrNull()?.localizedMessage
                                            ?: "No se pudo crear la cuenta"
                                    },
                                    onRegistered = {
                                        nav.navigate(Route.Login.path) {
                                            popUpTo(Route.Register.path) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            // MENÚ
                            composable(Route.Menu.path) {
                                MinzbookMenu(
                                    userName = ui.displayName ?: "Usuario",
                                    onExplore = { nav.navigate(Route.Catalog.path) },
                                    onMyBooks = { nav.navigate(Route.MyBooks.path) },
                                    onRatings = { nav.navigate(Route.Ratings.path) },
                                    onLogout = {
                                        AuthLocalStore.clearSession(this@MainActivity)
                                        authViewModel.simulateLogout()
                                        nav.navigate(Route.Home.path) {
                                            popUpTo(Route.Menu.path) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            // CATÁLOGO
                            composable(Route.Catalog.path) {
                                CatalogScreen(
                                    onBack = { nav.popBackStack() },
                                    onOpenBook = { bookId ->
                                        // Cuando agregues BookDetail:
                                        // nav.navigate("${Route.BookDetail.path}/$bookId")
                                    }
                                )
                            }

                            // CALIFICACIONES (muestra las reseñas del usuario)
                            composable(Route.Ratings.path) {
                                RatingsScreen(onBack = { nav.popBackStack() })
                            }

                            // MIS LIBROS (placeholder por ahora)
                            composable(Route.MyBooks.path) {
                                PlaceholderScreen("Mis libros (próximo)")
                            }
                        }
                    }
                }
            }
        }
    }
}

/* ================== UI de soporte ================== */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(title: String, userLabel: String?) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp
            )
        },
        colors = centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        actions = {
            if (userLabel != null) {
                AssistChip(
                    onClick = { /* TODO: perfil próximamente */ },
                    label = { Text(userLabel) }
                )
            }
        }
    )
}

@Composable
fun MinzbookHome(
    uiState: AuthUiState,
    onLoginClick: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        TopBar(title = "Minzbook", userLabel = uiState.displayName)

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
                // Logo centrado
                Image(
                    painter = painterResource(id = R.drawable.minzbook_logo),
                    contentDescription = "Logo Minzbook",
                    modifier = Modifier
                        .size(140.dp)
                        .padding(bottom = 16.dp)
                )

                // Mensaje de bienvenida
                WelcomeMessageCard()

                Spacer(Modifier.height(16.dp))

                if (!uiState.isAuthenticated) {
                    Button(
                        onClick = onLoginClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
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
private fun WelcomeMessageCard() {
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
                text = "Bienvenido a Minzbook, nuestra app de lectura y creadora de nuevos autores! " +
                        "Ingresa abajo para acceder a nuestro menú o ser uno de nuestros autores",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Corazón",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinzbookMenu(
    userName: String,
    onExplore: () -> Unit,
    onMyBooks: () -> Unit,
    onRatings: () -> Unit,
    onLogout: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        TopBar(title = "Minzbook", userLabel = userName)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onExplore,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge
            ) { Text("Explorar libros") }

            OutlinedButton(
                onClick = onMyBooks,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge
            ) { Text("Mis libros") }

            OutlinedButton(
                onClick = onRatings,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge
            ) { Text("Calificaciones") }

            TextButton(onClick = onLogout) { Text("Cerrar sesión") }
        }
    }
}

@Composable
fun PlaceholderScreen(text: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text)
    }
}


HAY QUE ARREGLAR QUE SE CRASHEA AL 