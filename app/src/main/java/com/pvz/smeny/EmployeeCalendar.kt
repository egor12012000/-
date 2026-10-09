package com.pvz.smeny

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EmployeeCalendar(
    shifts: MutableMap<String, String>,
    onLogout: () -> Unit
) {
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val todayStatus = shifts[today]

    val monthNames = listOf(
        "Январь","Февраль","Март","Апрель","Май","Июнь",
        "Июль","Август","Сентябрь","Октябрь","Ноябрь","Декабрь"
    )

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Мой календарь", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onLogout) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
            }
        }

        // Month
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "${monthNames[currentMonth.get(Calendar.MONTH)]} ${currentMonth.get(Calendar.YEAR)}",
                fontSize = 18.sp, fontWeight = FontWeight.Bold
            )
            Row {
                IconButton(onClick = {
                    val c = currentMonth.clone() as Calendar
                    c.add(Calendar.MONTH, -1)
                    currentMonth = c
                }) { Icon(Icons.Default.ChevronLeft, "Назад") }
                IconButton(onClick = {
                    val c = currentMonth.clone() as Calendar
                    c.add(Calendar.MONTH, 1)
                    currentMonth = c
                }) { Icon(Icons.Default.ChevronRight, "Вперёд") }
            }
        }

        // Weekdays
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        ) {
            listOf("Пн","Вт","Ср","Чт","Пт","Сб","Вс").forEach { d ->
                Text(
                    d,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF8E8E93)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Days grid
        CalendarGrid(currentMonth = currentMonth, today = today, shifts = shifts)

        Spacer(Modifier.weight(1f))

        // Status
        val statusText = when (todayStatus) {
            "work" -> "🟣 Вы отметились: на смене"
            "off" -> "🟡 Вы отметились: выходной"
            "miss" -> "🔴 Вы не отметились — прогул"
            else -> "Сегодня ещё не отмечено (9:00 – 21:00)"
        }
        val statusColor = when (todayStatus) {
            "work", "off" -> Color(0xFF16A34A)
            "miss" -> Color(0xFFDC2626)
            else -> Color(0xFF8E8E93)
        }
        Text(
            statusText,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
            textAlign = TextAlign.Center,
            color = statusColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )

        // Buttons
        Column(modifier = Modifier.padding(24.dp)) {
            Button(
                onClick = {
                    if (shifts[today] == null) shifts[today] = "work"
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
            ) {
                Text("🟣 Я на смене", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = {
                    if (shifts[today] == null) shifts[today] = "off"
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFDE68A),
                    contentColor = Color(0xFF78350F)
                )
            ) {
                Text("🟡 Выходной", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick = {
                    if (shifts[today] == null) shifts[today] = "miss"
                },
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("🌙 Демо: уже 22:00 (я забыл отметиться)", fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun CalendarGrid(currentMonth: Calendar, today: String, shifts: Map<String, String>) {
    val year = currentMonth.get(Calendar.YEAR)
    val month = currentMonth.get(Calendar.MONTH)

    val first = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val startWeekday = (first.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Пн = 0
    val daysInMonth = first.getActualMaximum(Calendar.DAY_OF_MONTH)

    val cells = mutableListOf<Int?>() // null = пустая ячейка
    repeat(startWeekday) { cells.add(null) }
    for (d in 1..daysInMonth) cells.add(d)
    while (cells.size % 7 != 0) cells.add(null)

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        cells.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { day ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (day != null) {
                            val cal = Calendar.getInstance().apply {
                                set(Calendar.YEAR, year)
                                set(Calendar.MONTH, month)
                                set(Calendar.DAY_OF_MONTH, day)
                            }
                            val key = sdf.format(cal.time)
                            val status = shifts[key]
                            val isToday = key == today

                            val bg = when (status) {
                                "work" -> Color(0xFFA855F7)
                                "off" -> Color(0xFFFDE68A)
                                "miss" -> Color(0xFFEF4444)
                                else -> if (isToday) Color(0xFFF0F0F3) else Color.Transparent
                            }
                            val fg = when (status) {
                                "work", "miss" -> Color.White
                                "off" -> Color(0xFF78350F)
                                else -> Color(0xFF1D1D1F)
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(bg, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    day.toString(),
                                    color = fg,
                                    fontWeight = if (isToday || status != null) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
