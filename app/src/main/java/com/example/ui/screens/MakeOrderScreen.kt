package com.example.ui.screens

import android.app.DatePickerDialog
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import java.util.Calendar

@Composable
fun MakeOrderScreen(
    uiState: MenuUiState,
    viewModel: MenuViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var employeeDropdownExpanded by remember { mutableStateOf(false) }

    // DatePicker setup
    val calendar = remember { Calendar.getInstance() }
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val formattedDate = String.format("%02d / %02d / %04d", dayOfMonth, month + 1, year)
                viewModel.setFechaMenu(formattedDate)
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

            Spacer(modifier = Modifier.height(10.dp))

            // HEADER BANNER: MENÚ DEL DÍA - Fogón Gastronómico - Planta Nova
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(NovaRedDark, Color(0xFF9E0B0F))
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Fuego",
                                tint = NovaFlameOrange,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "MENÚ DEL DÍA",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.85f),
                                    letterSpacing = 0.8.sp
                                )
                                Text(
                                    text = "Fogón Gastronómico",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF3E0A0C)
                        ) {
                            Text(
                                text = "Planta Nova",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFFFCDD2),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // FIELD 1: Selecciona tu Empleado
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.PersonOutline,
                    contentDescription = "Empleado",
                    tint = NovaRedBright,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Selecciona tu Empleado:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = NovaTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Employee Dropdown Trigger Card
            Box(modifier = Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { employeeDropdownExpanded = true }
                        .testTag("employee_dropdown_trigger"),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = NovaCardBg),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(listOf(NovaCardBorder, NovaCardBorder))
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = uiState.selectedEmpleado?.displayLabel
                                ?: "Carlos Alberto Mendoza Quispe - Producción Metalmecánica",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NovaTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Abrir lista de empleados",
                            tint = NovaTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Dropdown Menu
                DropdownMenu(
                    expanded = employeeDropdownExpanded,
                    onDismissRequest = { employeeDropdownExpanded = false },
                    modifier = Modifier
                        .background(NovaCardBgElevated)
                        .border(1.dp, NovaCardBorder, RoundedCornerShape(8.dp))
                ) {
                    uiState.empleados.forEach { emp ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = emp.nombreCompleto,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NovaTextPrimary
                                    )
                                    Text(
                                        text = "DNI: ${emp.dni} • ${emp.area}",
                                        fontSize = 11.sp,
                                        color = NovaTextMuted
                                    )
                                }
                            },
                            onClick = {
                                viewModel.selectEmpleado(emp)
                                employeeDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Employee sub-info row (DNI & Area)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "DNI: ${uiState.selectedEmpleado?.dni ?: "45892147"}",
                    fontSize = 12.sp,
                    color = NovaTextMuted
                )
                Text(
                    text = uiState.selectedEmpleado?.area ?: "Producción Metalmecánica",
                    fontSize = 12.sp,
                    color = NovaTextMuted
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // FIELD 2: Fecha de Menú
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

            Spacer(modifier = Modifier.height(8.dp))

            // Date picker trigger card
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
                        .padding(horizontal = 14.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = uiState.fechaMenu,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NovaTextPrimary
                    )
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Seleccionar fecha",
                        tint = NovaTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // FIELD 3: Elige tu plato de Fogón Gastronómico
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
                Text(
                    text = "${uiState.menuOpciones.size} opciones",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NovaRedGlow
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Options List
            uiState.menuOpciones.forEach { opcion ->
                val isSelected = uiState.selectedOpcion?.id == opcion.id

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable { viewModel.selectOpcion(opcion) }
                        .testTag("option_card_${opcion.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFF1E1416) else NovaCardBg
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
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

                        RadioButton(
                            selected = isSelected,
                            onClick = { viewModel.selectOpcion(opcion) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = NovaRedBright,
                                unselectedColor = NovaTextMuted
                            ),
                            modifier = Modifier.testTag("radio_button_${opcion.id}")
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

            // ACTION BUTTON: "TERMINAR PEDIDO" / "Confirmar Pedido (Persistir en Firebase)"
            Button(
                onClick = { viewModel.terminarPedido() },
                enabled = !uiState.isSubmittingOrder,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NovaRedPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_terminar_pedido")
            ) {
                if (uiState.isSubmittingOrder) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Guardando en Firestore...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Enviar Pedido",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "TERMINAR PEDIDO",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Confirmar Pedido (Persistir en Firebase)",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.85f)
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

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
