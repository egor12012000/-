package com.pvz.smeny

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PVZApp()
        }
    }
}

@Composable
fun PVZApp() {
    val nav = rememberNavController()
    // Глобальное состояние (для демо — в памяти)
    val pvzList = remember { mutableStateListOf<Pvz>() }
    val employees = remember { mutableStateListOf<Employee>() }
    val shifts = remember { mutableStateMapOf<String, String>() } // date -> "work"|"off"|"miss"
    var currentUser by remember { mutableStateOf<User?>(null) }

    NavHost(navController = nav, startDestination = "auth") {
        composable("auth") {
            AuthScreen(
                onOwnerLogin = { user ->
                    currentUser = user
                    nav.navigate("owner_menu")
                },
                onEmployeeLogin = { user ->
                    currentUser = user
                    nav.navigate("waiting")
                }
            )
        }
        composable("waiting") {
            WaitingScreen(
                email = currentUser?.email ?: "",
                onBind = { nav.navigate("calendar") },
                onBack = {
                    currentUser = null
                    nav.navigate("auth") { popUpTo("auth") { inclusive = true } }
                }
            )
        }
        composable("calendar") {
            EmployeeCalendar(
                shifts = shifts,
                onLogout = {
                    currentUser = null
                    shifts.clear()
                    nav.navigate("auth") { popUpTo("auth") { inclusive = true } }
                }
            )
        }
        composable("owner_menu") {
            OwnerMenu(
                userName = "${currentUser?.name ?: ""} ${currentUser?.surname ?: ""}".trim(),
                pvzList = pvzList,
                employees = employees,
                onAddPvz = { service, address ->
                    pvzList.add(Pvz("pvz_${System.currentTimeMillis()}", service, address))
                },
                onAddEmployee = { email, pvzId, salary ->
                    employees.add(Employee(email, pvzId, salary))
                },
                onUpdateSalary = { email, salary ->
                    val idx = employees.indexOfFirst { it.email == email }
                    if (idx >= 0) employees[idx] = employees[idx].copy(salary = salary)
                },
                onLogout = {
                    currentUser = null
                    pvzList.clear()
                    employees.clear()
                    nav.navigate("auth") { popUpTo("auth") { inclusive = true } }
                }
            )
        }
    }
}

data class User(val name: String, val surname: String, val email: String, val role: String)
data class Pvz(val id: String, val service: String, val address: String)
data class Employee(val email: String, val pvzId: String, val salary: Int, val name: String = "")
