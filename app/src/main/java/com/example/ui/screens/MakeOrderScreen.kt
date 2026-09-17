package com.example.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NovaBadgeBg
import com.example.ui.theme.NovaCardBg
import com.example.ui.theme.NovaCardBgElevated
import com.example.ui.theme.NovaCardBorder
import com.example.ui.theme.NovaDarkBg
import com.example.ui.theme.NovaFlameOrange
import com.example.ui.theme.NovaGreenSync
import com.example.ui.theme.NovaRedBright
import com.example.ui.theme.NovaRedDark
import com.example.ui.theme.NovaRedGlow
import com.example.ui.theme.NovaRedPrimary
import com.example.ui.theme.NovaTextMuted
import com.example.ui.theme.NovaTextPrimary
import com.example.ui.theme.NovaTextSecondary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MenuUiState
import com.example.ui.viewmodel.MenuViewModel
import com.example.util.CalendarioLaboralHelper
import java.util.Calendar
import java.util.Locale

@Composable
fun MakeOrderScreen(
    uiState: MenuUiState,
    viewModel: MenuViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // DatePicker setup
    val calendar = remember { Calendar.getInstance() }
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                if (!CalendarioLaboralHelper.esDiaHabil(selectedCal)) {
                    val motivo = CalendarioLaboralHelper.obtenerDescripcionNoHabil(selectedCal)
                    val proxHabil = CalendarioLaboralHelper.obtenerProximoDiaHabil(selectedCal)
                    val fechaHabilStr = CalendarioLaboralHelper.formatFecha(proxHabil)
                    Toast.makeText(
                        context,
                        "Sábados, domingos y feriados no laborables ($motivo). Ajustado al día hábil más cercano: $fechaHabilStr",
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.setFechaMenu(fechaHabilStr)
                } else {
                    val formattedDate = String.format("%02d / %02d / %04d", dayOfMonth, month + 1, year)
                    viewModel.setFechaMenu(formattedDate)
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = NovaDarkBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // TOP BAR: Red dot, Nova App Móvil, 10:14, X
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(NovaRedBright, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Nova App Móvil",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NovaTextPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "10:14",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64B5F6)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.MAIN_MENU) },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("close_order_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = NovaTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // FIELD 1: Empleado Validado Automáticamente por Correo
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PersonOutline,
                        contentDescription = "Empleado",
                        tint = NovaRedBright,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Empleado (Validado en BD):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = NovaTextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF1B382B)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(NovaGreenSync, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Firestore BD",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NovaGreenSync
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Verified Employee Card (Auto-filled by Authenticated Email)
            val currentEmp = uiState.currentEmpleado ?: uiState.selectedEmpleado
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("employee_verified_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = NovaCardBg),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(listOf(NovaCardBorder, NovaCardBorder))
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(NovaCardBgElevated, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonOutline,
                                contentDescription = "Empleado",
                                tint = NovaRedBright,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentEmp?.nombreCompleto ?: "Carlos Alberto Mendoza Quispe",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = NovaTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "DNI: ${currentEmp?.dni ?: "45892147"} • ${currentEmp?.area ?: "Producción Metalmecánica"}",
                                fontSize = 12.sp,
                                color = NovaTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NovaCardBgElevated, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Correo verificado:",
                            fontSize = 11.sp,
                            color = NovaTextMuted
                        )
                        Text(
                            text = currentEmp?.email ?: uiState.userEmail ?: "soporte_tecnico@nova.pe",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = NovaTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // FIELD 2: Fecha de Menú Seleccionada
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Fecha",
                        tint = NovaRedBright,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Fecha de Menú:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = NovaTextPrimary
                    )
                }

                Text(
                    text = "Menú Mensual Firestore",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NovaGreenSync
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date picker trigger card with calendar picker
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() }
                    .testTag("date_picker_trigger"),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = NovaCardBg),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.linearGradient(listOf(NovaCardBorder, NovaCardBorder))
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = uiState.nombreDiaSemana.ifEmpty { "Día Hábil" }.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NovaRedBright,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = NovaGreenSync.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Día Hábil (L - V)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NovaGreenSync,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiState.fechaMenu,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = NovaTextPrimary
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Cambiar fecha",
                            fontSize = 11.sp,
                            color = NovaTextMuted
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Seleccionar fecha en calendario",
                            tint = NovaTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // CONTROLES DE NAVEGACIÓN DE FECHA: RETROCEDER Y AVANZAR FECHA (RESALTADOS EN ROJO)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Retroceder Fecha (Resaltado en Rojo)
                Button(
                    onClick = { viewModel.retrocederDia() },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("btn_retroceder_fecha"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NovaRedPrimary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp, pressedElevation = 1.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Retroceder fecha",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Día Anterior",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Botón Avanzar Fecha (Resaltado en Rojo)
                Button(
                    onClick = { viewModel.avanzarDia() },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("btn_avanzar_fecha"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NovaRedPrimary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp, pressedElevation = 1.dp)
                ) {
                    Text(
                        text = "Día Siguiente",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Avanzar fecha",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // INDICADOR DEL ESTADO DEL DÍA: 1 MENÚ O NINGÚN MENÚ
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (uiState.selectedOpcion != null) NovaGreenSync.copy(alpha = 0.12f) else NovaCardBgElevated,
                border = BorderStroke(
                    1.dp,
                    if (uiState.selectedOpcion != null) NovaGreenSync.copy(alpha = 0.4f) else NovaCardBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("day_selection_status")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (uiState.selectedOpcion != null) Icons.Default.CheckCircle else Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = if (uiState.selectedOpcion != null) NovaGreenSync else NovaTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (uiState.selectedOpcion != null) {
                                "Menú elegido hoy: ${uiState.selectedOpcion.titulo}"
                            } else {
                                "Sin menú para este día (0 platos)"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.selectedOpcion != null) NovaGreenSync else NovaTextPrimary
                        )
                        Text(
                            text = if (uiState.selectedOpcion != null) {
                                "Toca el check para desmarcar y dejar este día sin menú."
                            } else {
                                "Marca con el check una opción si deseas menú hoy, o déjalo sin marcar."
                            },
                            fontSize = 11.sp,
                            color = NovaTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // FIELD 3: Elige tu plato de Fogón Gastronómico (SELECCIÓN POR CHECK)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Elige tu plato de Fogón Gastronómico:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = NovaTextPrimary
                )
                if (uiState.isLoadingMenuDia) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            color = NovaRedBright,
                            modifier = Modifier.size(12.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Cargando menú...",
                            fontSize = 11.sp,
                            color = NovaTextMuted
                        )
                    }
                } else {
                    Text(
                        text = "${uiState.menuOpciones.size} opciones",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NovaRedGlow
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Options List (SELECCIÓN POR CHECK: permite marcar 1 o desmarcar para ningún menú al día)
            uiState.menuOpciones.forEach { opcion ->
                val isSelected = uiState.selectedOpcion?.id == opcion.id

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable { viewModel.toggleOpcionDia(opcion) }
                        .testTag("option_card_${opcion.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFF1E1416) else NovaCardBg
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        brush = Brush.linearGradient(
                            if (isSelected) listOf(NovaRedBright, NovaRedDark) else listOf(NovaCardBorder, NovaCardBorder)
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = opcion.titulo,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NovaRedGlow
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = opcion.descripcion,
                                fontSize = 12.sp,
                                color = NovaTextSecondary,
                                lineHeight = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Checkbox para selección granular (marca o desmarca)
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { viewModel.toggleOpcionDia(opcion) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = NovaRedBright,
                                uncheckedColor = NovaTextMuted,
                                checkmarkColor = Color.White
                            ),
                            modifier = Modifier.testTag("checkbox_option_${opcion.id}")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // FIELD 4: Observaciones / Alergias (Opcional)
            Text(
                text = "Observaciones / Alergias (Opcional):",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = NovaTextSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = uiState.observaciones,
                onValueChange = { viewModel.setObservaciones(it) },
                placeholder = {
                    Text(
                        text = "Ej: Sin cebolla, refresco sin azúcar...",
                        fontSize = 12.sp,
                        color = NovaTextMuted
                    )
                },
                singleLine = false,
                maxLines = 2,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NovaRedBright,
                    unfocusedBorderColor = NovaCardBorder,
                    focusedTextColor = NovaTextPrimary,
                    unfocusedTextColor = NovaTextPrimary,
                    focusedContainerColor = NovaCardBg,
                    unfocusedContainerColor = NovaCardBg
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("observaciones_input")
            )

            // Submit error or info message if any
            if (uiState.orderMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.orderMessage,
                    fontSize = 12.sp,
                    color = NovaRedGlow,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ACTION BUTTON: "TERMINAR PEDIDO" CON CONTEO DE ELECCIONES DEL MES (Persistir en Firestore)
            val total = uiState.totalEleccionesMes
            Button(
                onClick = { viewModel.terminarPedido() },
                enabled = !uiState.isSubmittingOrder && total > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NovaRedPrimary,
                    disabledContainerColor = NovaCardBgElevated,
                    contentColor = Color.White,
                    disabledContentColor = NovaTextMuted
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_enviar_pedido")
            ) {
                if (uiState.isSubmittingOrder) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Registrando en Firestore...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Enviar Pedido",
                        tint = if (total > 0) Color.White else NovaTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (total > 0) {
                                "ENVIAR PEDIDO ($total ${if (total == 1) "ELECCIÓN" else "ELECCIONES"})"
                            } else {
                                "SELECCIONA AL MENOS 1 MENÚ"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = if (total > 0) {
                                "Registrar pedido en base de datos Firestore y persistir para futuros reportes"
                            } else {
                                "Marca con el check los días que desees pedir"
                            },
                            fontSize = 10.sp,
                            color = if (total > 0) Color.White.copy(alpha = 0.85f) else NovaTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // FOOTER: Sincronizado con Firestore de Nova Maquinarias
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Sensors,
                    contentDescription = "Conexión",
                    tint = NovaGreenSync,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Sincronizado con Firestore de Nova Maquinarias",
                    fontSize = 11.sp,
                    color = NovaTextMuted
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Developed by Vallumi-System credit
            com.example.ui.components.VallumiFooter()

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
