package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Empleado
import com.example.data.model.Pedido
import com.example.data.model.PlatoOpcion
import com.example.data.repository.FirebaseOrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppScreen {
    AUTH,
    MAIN_MENU,
    ORDER_FORM,
    ORDER_SUMMARY
}

data class MenuUiState(
    val currentScreen: AppScreen = AppScreen.AUTH,
    val userEmail: String? = null,
    val isAuthLoading: Boolean = false,
    val authError: String? = null,
    val isRegisterMode: Boolean = false,
    val authEmailInput: String = "soporte_tecnico@nova.pe",
    val authPasswordInput: String = "123456",
    // Order form state
    val empleados: List<Empleado> = emptyList(),
    val selectedEmpleado: Empleado? = null,
    val fechaMenu: String = "08 / 09 / 2026",
    val menuOpciones: List<PlatoOpcion> = emptyList(),
    val selectedOpcion: PlatoOpcion? = null,
    val observaciones: String = "",
    val isSubmittingOrder: Boolean = false,
    val orderMessage: String? = null,
    // Summary state
    val ultimoPedido: Pedido? = null,
    val isLoadingSummary: Boolean = false
)

class MenuViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FirebaseOrderRepository(application)

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    init {
        val empleados = repository.getEmpleados()
        val opciones = repository.getMenuOpciones()
        val currentUser = repository.getCurrentUserEmail()

        val defaultFecha = try {
            val sdf = SimpleDateFormat("dd / MM / yyyy", Locale.getDefault())
            sdf.format(Date())
        } catch (e: Exception) {
            "08 / 09 / 2026"
        }

        _uiState.update { state ->
            state.copy(
                userEmail = currentUser,
                currentScreen = if (currentUser != null) AppScreen.MAIN_MENU else AppScreen.AUTH,
                empleados = empleados,
                selectedEmpleado = empleados.firstOrNull(),
                menuOpciones = opciones,
                selectedOpcion = opciones.firstOrNull(),
                fechaMenu = defaultFecha
            )
        }

        if (currentUser != null) {
            loadUltimoPedido()
        }
    }

    // Screen navigation
    fun navigateTo(screen: AppScreen) {
        _uiState.update { it.copy(currentScreen = screen, orderMessage = null) }
        if (screen == AppScreen.ORDER_SUMMARY) {
            loadUltimoPedido()
        }
    }

    // Auth actions
    fun setAuthEmail(email: String) {
        _uiState.update { it.copy(authEmailInput = email, authError = null) }
    }

    fun setAuthPassword(pass: String) {
        _uiState.update { it.copy(authPasswordInput = pass, authError = null) }
    }

    fun toggleAuthMode() {
        _uiState.update { it.copy(isRegisterMode = !it.isRegisterMode, authError = null) }
    }

    fun login() {
        val email = _uiState.value.authEmailInput.trim()
        val pass = _uiState.value.authPasswordInput

        if (email.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(authError = "Por favor ingresa correo y contraseña") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isAuthLoading = true, authError = null) }
            val result = if (_uiState.value.isRegisterMode) {
                repository.registerWithEmail(email, pass)
            } else {
                repository.loginWithEmail(email, pass)
            }

            result.fold(
                onSuccess = { userEmail ->
                    _uiState.update {
                        it.copy(
                            isAuthLoading = false,
                            userEmail = userEmail,
                            currentScreen = AppScreen.MAIN_MENU,
                            authError = null
                        )
                    }
                    loadUltimoPedido()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isAuthLoading = false,
                            authError = error.message ?: "Error en la autenticación"
                        )
                    }
                }
            )
        }
    }

    fun logout() {
        repository.logout()
        _uiState.update {
            it.copy(
                userEmail = null,
                currentScreen = AppScreen.AUTH,
                ultimoPedido = null
            )
        }
    }

    // Order form updates
    fun selectEmpleado(empleado: Empleado) {
        _uiState.update { it.copy(selectedEmpleado = empleado) }
    }

    fun setFechaMenu(fecha: String) {
        _uiState.update { it.copy(fechaMenu = fecha) }
    }

    fun selectOpcion(opcion: PlatoOpcion) {
        _uiState.update { it.copy(selectedOpcion = opcion) }
    }

    fun setObservaciones(obs: String) {
        _uiState.update { it.copy(observaciones = obs) }
    }

    // Terminar Pedido (Save to Firestore & navigate to summary)
    fun terminarPedido() {
        val state = _uiState.value
        val empleado = state.selectedEmpleado
        val opcion = state.selectedOpcion

        if (empleado == null || opcion == null) {
            _uiState.update { it.copy(orderMessage = "Selecciona un empleado y un plato de menú") }
            return
        }

        val pedido = Pedido(
            id = "PED-${System.currentTimeMillis() % 1000000}",
            userEmail = state.userEmail ?: "usuario@nova.pe",
            empleadoNombre = empleado.nombreCompleto,
            empleadoDni = empleado.dni,
            empleadoArea = empleado.area,
            fechaMenu = state.fechaMenu,
            opcionTitulo = opcion.titulo,
            opcionDescripcion = opcion.descripcion,
            observaciones = state.observaciones.trim(),
            timestamp = System.currentTimeMillis(),
            estado = "Registrado en Planta Nova"
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingOrder = true, orderMessage = null) }
            val result = repository.guardarPedido(pedido)
            result.fold(
                onSuccess = { savedOrder ->
                    _uiState.update {
                        it.copy(
                            isSubmittingOrder = false,
                            ultimoPedido = savedOrder,
                            currentScreen = AppScreen.ORDER_SUMMARY,
                            orderMessage = "¡Pedido registrado exitosamente!"
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isSubmittingOrder = false,
                            orderMessage = "Error al guardar el pedido: ${error.message}"
                        )
                    }
                }
            )
        }
    }

    fun loadUltimoPedido() {
        val email = _uiState.value.userEmail ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingSummary = true) }
            val pedido = repository.obtenerUltimoPedido(email)
            _uiState.update {
                it.copy(
                    ultimoPedido = pedido,
                    isLoadingSummary = false
                )
            }
        }
    }

    fun cancelarPedido() {
        val pedido = _uiState.value.ultimoPedido ?: return
        val email = _uiState.value.userEmail ?: ""
        viewModelScope.launch {
            repository.eliminarPedido(pedido.id, email)
            _uiState.update { it.copy(ultimoPedido = null) }
        }
    }
}
