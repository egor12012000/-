package com.pvz.smeny

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthScreen(
    onOwnerLogin: (User) -> Unit,
    onEmployeeLogin: (User) -> Unit
) {
    var name by remember { mutableStateOf("Иван") }
    var surname by remember { mutableStateOf("Иванов") }
    var email by remember { mutableStateOf("ivan@mail.ru") }
    var pass by remember { mutableStateOf("123456") }
    var role by remember { mutableStateOf("owner") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .size(72.dp)
                .background(
                    Brush.linearGradient(listOf(Color(0xFFA855F7), Color(0xFF6366F1))),
                    RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("ПВЗ", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(16.dp))
        Text("Смены ПВЗ", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text("Учёт смен для пунктов выдачи", color = Color(0xFF8E8E93), fontSize = 14.sp)

        Spacer(Modifier.height(28.dp))

        FieldLabel("Имя")
        OutlinedTextField(
            value = name, onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), singleLine = true
        )
        Spacer(Modifier.height(12.dp))

        FieldLabel("Фамилия")
        OutlinedTextField(
            value = surname, onValueChange = { surname = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), singleLine = true
        )
        Spacer(Modifier.height(12.dp))

        FieldLabel("Email")
        OutlinedTextField(
            value = email, onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), singleLine = true
        )
        Spacer(Modifier.height(12.dp))

        FieldLabel("Пароль")
        OutlinedTextField(
            value = pass, onValueChange = { pass = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(Modifier.height(16.dp))

        FieldLabel("Роль")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F0F3), RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            RoleButton("Владелец", role == "owner", Modifier.weight(1f)) { role = "owner" }
            RoleButton("Сотрудник", role == "employee", Modifier.weight(1f)) { role = "employee" }
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                if (name.isBlank() || surname.isBlank() || email.isBlank() || pass.isBlank()) return@Button
                val user = User(name, surname, email, role)
                if (role == "owner") onOwnerLogin(user) else onEmployeeLogin(user)
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
        ) {
            Text("Зарегистрироваться", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
fun FieldLabel(text: String) {
    Text(
        text,
        modifier = Modifier.fillMaxWidth().padding(start = 4.dp, bottom = 4.dp),
        color = Color(0xFF6E6E73), fontSize = 13.sp, fontWeight = FontWeight.Medium
    )
}

@Composable
fun RoleButton(text: String, active: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(42.dp),
        shape = RoundedCornerShape(9.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (active) Color.White else Color.Transparent,
            contentColor = if (active) Color(0xFF1D1D1F) else Color(0xFF6E6E73)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (active) 2.dp else 0.dp
        )
    ) {
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}
