package com.example.lucasmatiasminzbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.centerAlignedTopAppBarColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lucasmatiasminzbook.nav.Route
import com.example.lucasmatiasminzbook.ui.LoginScreen
import com.example.lucasmatiasminzbook.ui.RegisterScreen
import com.example.lucasmatiasminzbook.ui.theme.MinzbookTheme

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MinzbookTheme {
                val ui by authViewModel.uiState.collectAsState()
                val nav = rememberNavController()

                // Redirige según estado de autenticación
                LaunchedEffect(ui.isAuthenticated) {
                    if (ui.isAuthenticated) {
                        nav.navigate(Route.Menu.path) {
                            popUpTo(Route.Home.path) { inclusive = true }
                        }
                    } else {
                        nav.navigate(Route.Home.path) {
                            popUpTo(0)
                        }
                    }
                }

                Scaffold { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavHost(
                            navController = nav,
                            startDestination = Route.Home.path
                        ) {
                            // HOME: logo + botón que navega a Login
                            composable(Route.Home.path) {
                                MinzbookHome(
                                    uiState = ui,
                                    onLoginClick = { nav.navigate(Route.Login.path) }
                                )
                            }

                            // LOGIN: valida existencia de cuenta y contraseña
                            composable(Route.Login.path) {
                                LoginScreen(
                                    onBack = { nav.popBackStack() },
                                    onGoRegister = { nav.navigate(Route.Register.path) },
                                    onTryLogin = { email, password ->
                                        val res = AuthLocalStore.validateLogin(
                                            this@MainActivity, email, password
                                        )
                                        if (res.isSuccess) {
                                            val display = res.getOrNull()!!
                                            AuthLocalStore.setSession(this@MainActivity, email, display)
                                            authViewModel.simulateLogin(display)
                                            null // sin error
                                        } else {
                                            res.exceptionOrNull()?.message ?: "Error desconocido"
                                        }
                                    }
                                )
                            }

                            // REGISTER: crea usuario si no existe
                            composable(Route.Register.path) {
                                RegisterScreen(
                                    onBack = { nav.popBackStack() },
                                    onGoLogin = { nav.navigate(Route.Login.path) },
                                    onTryRegister = { email, password ->
                                        val res = AuthLocalStore.register(this@MainActivity, email, password)
                                        if (res.isSuccess) {
                                            val display = res.getOrNull()!!
                                            AuthLocalStore.setSession(this@MainActivity, email, display)
                                            authViewModel.simulateLogin(display)
                                            null
                                        } else {
                                            res.exceptionOrNull()?.message ?: "Error desconocido"
                                        }
                                    }
                                )
                            }

                            // MENÚ post-login
                            composable(Route.Menu.path) {
                                MinzbookMenu(
                                    userName = ui.displayName ?: "Usuario",
                                    onExplore = { nav.navigate(Route.Catalog.path) },
                                    onMyBooks = { nav.navigate(Route.MyBooks.path) },
                                    onRatings = { nav.navigate(Route.Ratings.path) },
                                    onLogout = {
                                        AuthLocalStore.clearSession(this@MainActivity)
                                        authViewModel.simulateLogout()
                                    }
                                )
                            }

                            // Placeholders (siguientes etapas)
                            composable(Route.Catalog.path) { PlaceholderScreen("Catálogo (próximo)") }
                            composable(Route.MyBooks.path) { PlaceholderScreen("Mis libros (próximo)") }
                            composable(Route.Ratings.path) { PlaceholderScreen("Calificaciones (próximo)") }
                        }
                    }
                }
            }
        }
    }
}

/* ================== UI ================== */

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
                    onClick = { /* TODO: pantalla de perfil próximamente */ },
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
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.minzbook_logo),
                    contentDescription = "Logo Minzbook",
                    modifier = Modifier
                        .size(160.dp)
                        .padding(bottom = 24.dp)
                )

                if (!uiState.isAuthenticated) {
                    Button(
                        onClick = onLoginClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Regístrate / Inicia sesión")
                    }
                } else {
                    Text("Bienvenida, ${uiState.displayName}", fontSize = 18.sp)
                }
            }
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
