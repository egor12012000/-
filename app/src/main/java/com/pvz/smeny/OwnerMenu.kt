package com.pvz.smeny

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OwnerMenu(
    userName: String,
    pvzList: List<Pvz>,
    employees: List<Employee>,
    onAddPvz: (String, String) -> Unit,
    onAddEmployee: (String, String, Int) -> Unit,
    onUpdateSalary: (String, Int) -> Unit,
    onLogout: () -> Unit
) {
    var screen by remember { mutableStateOf("menu") }

    when (screen) {
        "menu" -> MenuContent(userName, onLogout) { screen = it }
        "reports" -> ReportsScreen { screen = "menu" }
        "pvz" -> PvzListScreen(pvzList, employees, onUpdateSalary) { screen = "menu" }
        "add_pvz" -> AddPvzScreen(onAddPvz) { screen = "menu" }
        "add_emp" -> AddEmpScreen(pvzList, onAddEmployee) { screen = "menu" }
    }
}

@Composable
fun MenuContent(userName: String, onLogout: () -> Unit, onNavigate: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Владелец", color = Color(0xFF8E8E93), fontSize = 12.sp)
                Text(userName.ifBlank { "Владелец" }, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = onLogout) {
                Icon(Icons.Default.ArrowBack, "Выйти")
            }
        }

        Column(modifier = Modifier.padding(24.dp)) {
            MenuCard("📊", Color(0xFFEDE9FE), "Отчёты", "PDF раз в неделю по каждому пункту") { onNavigate("reports") }
            Spacer(Modifier.height(14.dp))
            MenuCard("🏪", Color(0xFFDBEAFE), "Пункты", "Список ПВЗ, кто на смене") { onNavigate("pvz") }
            Spacer(Modifier.height(14.dp))
            MenuCard("➕", Color(0xFFDCFCE7), "Добавить пункт выдачи", "Озон или Валдберис") { onNavigate("add_pvz") }
            Spacer(Modifier.height(14.dp))
            MenuCard("👥", Color(0xFFFEF3C7), "Добавить сотрудника", "По email + зарплата за смену") { onNavigate("add_emp") }
        }
    }
}

@Composable
fun MenuCard(emoji: String, bg: Color, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFAFAFA), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(48.dp).background(bg, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 22.sp)
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, fontSize = 13.sp, color = Color(0xFF8E8E93))
        }
    }
}

@Composable
fun ReportsScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        TopBar("Отчёты", onBack)
        Column(modifier = Modifier.padding(24.dp)) {
            ReportItem("📄 Отчёт за неделю", "Формируется каждый понедельник")
            Spacer(Modifier.height(12.dp))
            ReportItem("📄 Отчёт за прошлую неделю", "Скачать PDF")
        }
    }
}

@Composable
fun ReportItem(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFAFAFA), RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        Text(subtitle, fontSize = 13.sp, color = Color(0xFF8E8E93))
    }
}

@Composable
fun PvzListScreen(
    pvzList: List<Pvz>,
    employees: List<Employee>,
    onUpdateSalary: (String, Int) -> Unit,
    onBack: () -> Unit
) {
    var selectedPvz by remember { mutableStateOf<Pvz?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).verticalScroll(rememberScrollState())) {
        TopBar("Мои пункты", onBack)

        if (pvzList.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                Text("Пока нет ни одного пункта.\nДобавьте первый через меню.",
                    color = Color(0xFF8E8E93), fontSize = 14.sp)
            }
        } else {
            Column(modifier = Modifier.padding(24.dp)) {
                pvzList.forEach { p ->
                    val count = employees.count { it.pvzId == p.id }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFAFAFA), RoundedCornerShape(14.dp))
                            .clickable { selectedPvz = p }
                            .padding(16.dp)
                    ) {
                        Text("${p.service} — ${p.address}", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Text("Сотрудников: $count", color = Color(0xFF8E8E93), fontSize = 13.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }

    if (selectedPvz != null) {
        val p = selectedPvz!!
        val emps = employees.filter { it.pvzId == p.id }
        AlertDialog(
            onDismissRequest = { selectedPvz = null },
            title = { Text("${p.service} — ${p.address}") },
            text = {
                Column {
                    if (emps.isEmpty()) {
                        Text("Нет привязанных сотрудников", color = Color(0xFF8E8E93))
                    } else {
                        emps.forEach { e ->
                            Text("${e.email}\nЗарплата: ${e.salary} ₽ / смена",
                                fontSize = 14.sp, modifier = Modifier.padding(vertical = 6.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedPvz = null }) { Text("Закрыть") }
            }
        )
    }
}

@Composable
fun AddPvzScreen(onAddPvz: (String, String) -> Unit, onBack: () -> Unit) {
    var service by remember { mutableStateOf("Озон") }
    var address by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        TopBar("Добавить пункт", onBack)
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Служба", fontSize = 13.sp, color = Color(0xFF6E6E73), fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            Row {
                listOf("Озон", "Валдберис").forEach { s ->
                    FilterChip(
                        selected = service == s,
                        onClick = { service = s },
                        label = { Text(s) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = address, onValueChange = { address = it },
                label = { Text("Адрес пункта") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    if (address.isNotBlank()) {
                        onAddPvz(service, address)
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
            ) { Text("Сохранить") }
        }
    }
}

@Composable
fun AddEmpScreen(
    pvzList: List<Pvz>,
    onAddEmployee: (String, String, Int) -> Unit,
    onBack: () -> Unit
) {
    if (pvzList.isEmpty()) {
        Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
            TopBar("Добавить сотрудника", onBack)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Сначала добавьте пункт", color = Color(0xFF8E8E93))
            }
        }
        return
    }

    var email by remember { mutableStateOf("") }
    var selectedPvz by remember { mutableStateOf(pvzList.first().id) }
    var salary by remember { mutableStateOf("2500") }
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        TopBar("Добавить сотрудника", onBack)
        Column(modifier = Modifier.padding(24.dp)) {
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email сотрудника") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(Modifier.height(16.dp))

            Box {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    val p = pvzList.find { it.id == selectedPvz }
                    Text("${p?.service} — ${p?.address}")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    pvzList.forEach { p ->
                        DropdownMenuItem(
                            text = { Text("${p.service} — ${p.address}") },
                            onClick = { selectedPvz = p.id; expanded = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = salary, onValueChange = { salary = it },
                label = { Text("Зарплата за смену (₽)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    val s = salary.toIntOrNull() ?: 0
                    if (email.isNotBlank() && s > 0) {
                        onAddEmployee(email, selectedPvz, s)
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
            ) { Text("Добавить") }
        }
    }
}

@Composable
fun TopBar(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Назад") }
        Spacer(Modifier.width(8.dp))
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}
