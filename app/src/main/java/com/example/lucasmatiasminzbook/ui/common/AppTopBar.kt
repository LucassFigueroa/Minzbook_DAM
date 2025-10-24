package com.example.lucasmatiasminzbook.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    userLabel: String?,
    userPhotoUri: String?,
    onAvatarClick: () -> Unit
) {
    val avatarModifier = Modifier
        .size(32.dp)
        .clip(CircleShape)

    CenterAlignedTopAppBar(
        title = { Text(title) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFF285A4B), // verde pedido
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        actions = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!userLabel.isNullOrBlank()) {
                    Text(
                        userLabel,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                IconButton(onClick = onAvatarClick) {
                    if (userPhotoUri.isNullOrBlank()) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Perfil"
                        )
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(userPhotoUri),
                            contentDescription = "Foto de perfil",
                            modifier = avatarModifier,
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    )
}
