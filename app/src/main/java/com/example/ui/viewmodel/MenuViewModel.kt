package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.EleccionMenuDia
import com.example.data.model.Empleado
import com.example.data.model.Pedido
import com.example.data.model.PlatoOpcion
import com.example.data.repository.FirebaseOrderRepository
import com.example.util.CalendarioLaboralHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class AppScreen {
    AUTH,
    MAIN_MENU,
    ORDER_FORM,
    ORDER_SUMMARY
}

enum class ModalidadSeleccion(val etiqueta: String, val descripcion: String) {
    UN_DIA("1 Solo Día", "Menú para la fecha elegida"),
    CINCO_DIAS("5 Días Laborables", "Lunes a Viernes laborables"),
    TODO_EL_MES("Todo el Mes", "Todos los días hábiles del mes")
}

data class MenuUiState(
    val currentScreen: AppScreen = AppScreen.AUTH,
    val userEmail: String? = null,
    val isAuthLoading: Boolean = false,
    val authError: String? = null,
    val isRegisterMode: Boolean = false,
    val authEmailInput: String = "soporte_tecnico@nova.pe",
    val authPasswordInput: String = "123456",
    // Order form state - Auto-resolved from Firestore database
    val currentEmpleado: Empleado? = null,
    val selectedEmpleado: Empleado? = null,
    val fechaMenu: String = "08 / 09 / 2026",
    val nombreDiaSemana: String = "Martes",
    val menuOpciones: List<PlatoOpcion> = emptyList(),
    val selectedOpcion: PlatoOpcion? = null,
    val observaciones: String = "",
    val isSubmittingOrder: Boolean = false,
    val orderMessage: String? = null,
    val isLoadingMenuDia: Boolean = false,
    // Elecciones del mes: mapa de fecha -> PlatoOpcion (permite 1 o ningún menú por día)
    val eleccionesPorDia: Map<String, PlatoOpcion> = emptyMap(),
    // Summary state
    val ultimoPedido: Pedido? = null,
    val isLoadingSummary: Boolean = false
) {
    val totalEleccionesMes: Int
        get() = eleccionesPorDia.size
}

class MenuViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FirebaseOrderRepository(application)
    private val dateFormat = SimpleDateFormat("dd / MM / yyyy", Locale.getDefault())

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    init {
        val currentUser = repository.getCurrentUserEmail()
        val cachedEmpleado = repository.getCachedEmpleado()

        val hoyCal = Calendar.getInstance()
        val calHabil = CalendarioLaboralHelper.ajustarADiaHabil(hoyCal)
        val defaultFecha = CalendarioLaboralHelper.formatFecha(calHabil)
        val defaultNombreDia = CalendarioLaboralHelper.obtenerNombreDiaSemana(calHabil)

        _uiState.update { state ->
            state.copy(
                userEmail = currentUser,
                currentScreen = if (currentUser != null) AppScreen.MAIN_MENU else AppScreen.AUTH,
                currentEmpleado = cachedEmpleado,
                selectedEmpleado = cachedEmpleado,
                fechaMenu = defaultFecha,
                nombreDiaSemana = defaultNombreDia
            )
        }

        // Load daily menu for current date from Firestore / catalog
        cargarMenuParaFecha(defaultFecha)

        if (currentUser != null) {
            // Re-validate or fetch employee in Firestore if missing from cache
            viewModelScope.launch {
                val empResult = repository.buscarYValidarEmpleadoEnFirestore(currentUser)
                empResult.onSuccess { emp ->
                    _uiState.update { it.copy(currentEmpleado = emp, selectedEmpleado = emp) }
                }
            }
            loadUltimoPedido()
        }
    }

    fun cargarMenuParaFecha(fechaStr: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMenuDia = true) }
            val opciones = repository.obtenerMenuPorFecha(fechaStr)
            _uiState.update { current ->
                val opcionPrevia = current.eleccionesPorDia[fechaStr]
                current.copy(
                    isLoadingMenuDia = false,
                    menuOpciones = opciones,
                    selectedOpcion = opcionPrevia
                )
            }
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
                onSuccess = { empleado ->
                    _uiState.update {
                        it.copy(
                            isAuthLoading = false,
                            userEmail = empleado.email,
                            currentEmpleado = empleado,
                            selectedEmpleado = empleado,
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
                currentEmpleado = null,
                selectedEmpleado = null,
                currentScreen = AppScreen.AUTH,
                ultimoPedido = null
            )
        }
    }

    // Order form updates (employee is auto-assigned from authenticated user)
    fun selectEmpleado(empleado: Empleado) {
        _uiState.update { it.copy(currentEmpleado = empleado, selectedEmpleado = empleado) }
    }

    fun setFechaMenu(fecha: String) {
        val cal = CalendarioLaboralHelper.parseFecha(fecha)
        val calHabil = CalendarioLaboralHelper.ajustarADiaHabil(cal)
        val fechaHabil = CalendarioLaboralHelper.formatFecha(calHabil)
        val nombreDia = CalendarioLaboralHelper.obtenerNombreDiaSemana(calHabil)
        val opcionElegida = _uiState.value.eleccionesPorDia[fechaHabil]

        _uiState.update {
            it.copy(
                fechaMenu = fechaHabil,
                nombreDiaSemana = nombreDia,
                selectedOpcion = opcionElegida
            )
        }
        cargarMenuParaFecha(fechaHabil)
    }

    fun retrocederDia() {
        val currentStr = _uiState.value.fechaMenu
        val cal = CalendarioLaboralHelper.parseFecha(currentStr)
        val anteriorHabil = CalendarioLaboralHelper.obtenerAnteriorDiaHabil(cal)
        val nuevaFecha = CalendarioLaboralHelper.formatFecha(anteriorHabil)
        setFechaMenu(nuevaFecha)
    }

    fun avanzarDia() {
        val currentStr = _uiState.value.fechaMenu
        val cal = CalendarioLaboralHelper.parseFecha(currentStr)
        val proximoHabil = CalendarioLaboralHelper.obtenerProximoDiaHabil(cal)
        val nuevaFecha = CalendarioLaboralHelper.formatFecha(proximoHabil)
        setFechaMenu(nuevaFecha)
    }

    // Checkbox toggle: permite elegir 1 menú o ningún menú al día
    fun toggleOpcionDia(opcion: PlatoOpcion) {
        val fechaActual = _uiState.value.fechaMenu
        _uiState.update { current ->
            val map = current.eleccionesPorDia.toMutableMap()
            val yaSeleccionada = map[fechaActual]?.id == opcion.id
            if (yaSeleccionada) {
                // Desmarca la opción: este día queda con 0 menú (ningún menú al día)
                map.remove(fechaActual)
                current.copy(
                    eleccionesPorDia = map,
                    selectedOpcion = null,
                    orderMessage = null
                )
            } else {
                // Marca esta opción para el día actual (1 menú por día)
                map[fechaActual] = opcion
                current.copy(
                    eleccionesPorDia = map,
                    selectedOpcion = opcion,
                    orderMessage = null
                )
            }
        }
    }

    fun selectOpcion(opcion: PlatoOpcion) {
        toggleOpcionDia(opcion)
    }

    fun quitarMenuDia(fecha: String) {
        _uiState.update { current ->
            val map = current.eleccionesPorDia.toMutableMap()
            map.remove(fecha)
            val updatedSelectedOpcion = if (current.fechaMenu == fecha) null else current.selectedOpcion
            current.copy(eleccionesPorDia = map, selectedOpcion = updatedSelectedOpcion)
        }
    }

    fun setObservaciones(obs: String) {
        _uiState.update { it.copy(observaciones = obs) }
    }

    // Terminar Pedido (Persistir todas las elecciones del mes en Firestore)
    fun terminarPedido() {
        val state = _uiState.value
        val empleado = state.currentEmpleado ?: state.selectedEmpleado

        if (empleado == null) {
            _uiState.update {
                it.copy(orderMessage = "No se ha podido validar el empleado asociado a este correo en Firestore.")
            }
            return
        }

        if (state.eleccionesPorDia.isEmpty()) {
            _uiState.update {
                it.copy(orderMessage = "No has seleccionado ningún menú en el mes. Marca al menos un plato con el check en los días que desees.")
            }
            return
        }

        // Convertir el mapa de elecciones a una lista ordenada cronológicamente
        val listaElecciones = state.eleccionesPorDia.entries.map { (fecha, plato) ->
            val cal = CalendarioLaboralHelper.parseFecha(fecha)
            val diaSemana = CalendarioLaboralHelper.obtenerNombreDiaSemana(cal)
            com.example.data.model.EleccionMenuDia(
                fecha = fecha,
                diaSemana = diaSemana,
                platoId = plato.id,
                platoTitulo = plato.titulo,
                platoDescripcion = plato.descripcion,
                platoTipo = plato.tipo
            )
        }.sortedWith { a, b ->
            val calA = CalendarioLaboralHelper.parseFecha(a.fecha)
            val calB = CalendarioLaboralHelper.parseFecha(b.fecha)
            calA.compareTo(calB)
        }

        val totalEleccionesCount = listaElecciones.size
        val diasList = listaElecciones.map { it.fecha }
        val primerEleccion = listaElecciones.first()

        val pedido = Pedido(
            id = "PED-${System.currentTimeMillis() % 1000000}",
            userEmail = state.userEmail ?: empleado.email,
            empleadoNombre = empleado.nombreCompleto,
            empleadoDni = empleado.dni,
            empleadoArea = empleado.area,
            fechaMenu = primerEleccion.fecha,
            opcionTitulo = "$totalEleccionesCount elecciones de menú durante el mes",
            opcionDescripcion = listaElecciones.joinToString("; ") { "${it.diaSemana} ${it.fecha}: ${it.platoTitulo}" },
            observaciones = state.observaciones.trim(),
            timestamp = System.currentTimeMillis(),
            estado = "Registrado en Planta Nova",
            modalidadPlanificacion = "MENSUAL_ELECCIONES",
            diasSeleccionados = diasList,
            totalDias = totalEleccionesCount,
            elecciones = listaElecciones,
            totalElecciones = totalEleccionesCount
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
                            orderMessage = "¡Pedido de ${savedOrder.totalElecciones} elecciones del mes registrado exitosamente en Firestore!"
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

    fun confirmarPedido() {
        android.widget.Toast.makeText(getApplication(), "Pedido Confirmado", android.widget.Toast.LENGTH_SHORT).show()
        _uiState.update {
            it.copy(
                orderMessage = "Pedido Confirmado",
                currentScreen = AppScreen.MAIN_MENU
            )
        }
    }

    fun clearOrderMessage() {
        _uiState.update { it.copy(orderMessage = null) }
    }
}
