package com.pvz.smeny

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WaitingScreen(email: String, onBind: () -> Unit, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("⏳", fontSize = 64.sp)
        Spacer(Modifier.height(16.dp))
        Text("Ожидайте привязки", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text(
            "Владелец ещё не привязал вас к пункту.\nОтправьте ему свой email:",
            color = Color(0xFF6E6E73), fontSize = 14.sp
        )
        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .background(Color(0xFFF0F0F3), RoundedCornerShape(12.dp))
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Text(email, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(32.dp))

        OutlinedButton(
            onClick = onBind,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("[Демо] Владелец привязал меня")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onBack) {
            Text("← Выйти", color = Color(0xFF8E8E93))
        }
    }
}
