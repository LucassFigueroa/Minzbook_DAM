package com.example.lucasmatiasminzbook.data.local.book

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lucasmatiasminzbook.ui.common.StarDisplay

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookId: Long,
    val userEmail: String,
    val userName: String,
    val rating: Int,          // 1..5
    val comment: String,
    val createdAt: Long       // System.currentTimeMillis()
)
@Composable
private fun ReviewItem(r: Review) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(r.userName, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.width(8.dp))
            StarDisplay(rating = r.rating)
        }
        Spacer(Modifier.height(4.dp))
        Text(r.comment)
    }
}
