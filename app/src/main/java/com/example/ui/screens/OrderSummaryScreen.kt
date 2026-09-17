package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NovaBadgeBg
import com.example.ui.theme.NovaBadgeText
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrderSummaryScreen(
    uiState: MenuUiState,
    viewModel: MenuViewModel,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = NovaDarkBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Header Bar with Back Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.MAIN_MENU) },
                    modifier = Modifier.testTag("summary_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver al Menú Principal",
                        tint = NovaTextPrimary
                    )
                }

                Text(
                    text = "Resumen del Pedido",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = NovaTextPrimary
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NovaBadgeBg
                ) {
                    Text(
                        text = "Planta Nova",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = NovaBadgeText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoadingSummary) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = NovaRedBright)
                }
            } else if (uiState.ultimoPedido == null) {
                // BLANK / EMPTY STATE (SINO HIZO PEDIDO ESTARÁ EN BLANCO)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp)
                        .testTag("empty_order_summary_view"),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(NovaCardBgElevated, CircleShape)
                            .border(1.dp, NovaCardBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = "Sin pedidos",
                            tint = NovaTextMuted,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Sin Pedidos Registrados",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = NovaTextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "No has realizado ningún pedido de menú aún o no hay registros activos en Firestore para tu usuario.",
                        fontSize = 13.sp,
                        color = NovaTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.ORDER_FORM) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NovaRedPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(48.dp)
                            .testTag("btn_hacer_primer_pedido")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "HACER PEDIDO AHORA",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { viewModel.navigateTo(AppScreen.MAIN_MENU) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NovaTextSecondary)
                    ) {
                        Text("Volver al Menú Principal", fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Developed by Vallumi-System credit
                    com.example.ui.components.VallumiFooter()
                }
            } else {
                // ORDER SUMMARY TICKET (ACTIVE ORDER FROM FIRESTORE)
                val pedido = uiState.ultimoPedido

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("order_ticket_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = NovaCardBg),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(listOf(NovaRedBright, NovaRedDark))
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Ticket Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = NovaFlameOrange,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Fogón Gastronómico",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NovaTextPrimary
                                    )
                                    Text(
                                        text = "Ticket de Pedido",
                                        fontSize = 11.sp,
                                        color = NovaTextMuted
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = NovaGreenSync.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = NovaGreenSync,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Confirmado",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NovaGreenSync
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = NovaCardBorder, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Empleado Detail
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = NovaRedBright,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "DATOS DEL EMPLEADO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NovaRedGlow,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = pedido.empleadoNombre,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = NovaTextPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "DNI: ${pedido.empleadoDni}",
                                fontSize = 12.sp,
                                color = NovaTextSecondary
                            )
                            Text(
                                text = pedido.empleadoArea,
                                fontSize = 12.sp,
                                color = NovaTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = NovaCardBorder, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Resumen de Elecciones del Mes
                        if (pedido.elecciones.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = NovaRedBright,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "RESUMEN DE ELECCIONES:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NovaRedGlow
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = NovaGreenSync.copy(alpha = 0.18f)
                                ) {
                                    Text(
                                        text = "${pedido.totalElecciones} ${if (pedido.totalElecciones == 1) "elección" else "elecciones"}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NovaGreenSync,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Lista de elecciones del mes
                            pedido.elecciones.forEach { eleccion ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = NovaCardBgElevated,
                                    border = BorderStroke(1.dp, NovaCardBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Elegido",
                                                    tint = NovaGreenSync,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(5.dp))
                                                Text(
                                                    text = eleccion.fecha,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = NovaRedBright
                                                )
                                            }
                                            Text(
                                                text = eleccion.diaSemana.uppercase(),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = NovaTextMuted
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = eleccion.platoTitulo,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NovaTextPrimary
                                        )

                                        if (eleccion.platoDescripcion.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = eleccion.platoDescripcion,
                                                fontSize = 11.sp,
                                                color = NovaTextSecondary,
                                                lineHeight = 15.sp
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            // Vista tradicional para pedidos individuales
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = NovaRedBright,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Fecha de Menú:",
                                        fontSize = 12.sp,
                                        color = NovaTextSecondary
                                    )
                                }
                                Text(
                                    text = pedido.fechaMenu,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NovaTextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = NovaCardBorder, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Restaurant,
                                    contentDescription = null,
                                    tint = NovaRedBright,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PLATO ELEGIDO",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NovaRedGlow,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = NovaCardBgElevated,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = pedido.opcionTitulo,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NovaRedGlow
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = pedido.opcionDescripcion,
                                        fontSize = 12.sp,
                                        color = NovaTextSecondary,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }

                        // Observaciones
                        if (pedido.observaciones.isNotBlank()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Observaciones / Alergias:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NovaTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = pedido.observaciones,
                                fontSize = 12.sp,
                                color = NovaTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = NovaCardBorder, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Footer Meta
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Código: ${pedido.id}",
                                fontSize = 11.sp,
                                color = NovaTextMuted
                            )
                            val formattedTime = try {
                                SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(pedido.timestamp))
                            } catch (e: Exception) {
                                "Registrado hoy"
                            }
                            Text(
                                text = formattedTime,
                                fontSize = 11.sp,
                                color = NovaTextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // CONFIRMAR PEDIDO BUTTON
                Button(
                    onClick = { viewModel.confirmarPedido() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NovaGreenSync,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_confirmar_pedido")
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CONFIRMAR PEDIDO",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.ORDER_FORM) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NovaRedPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_modificar_pedido")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Modificar",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = { viewModel.cancelarPedido() },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = NovaRedGlow
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_cancelar_pedido")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = null,
                            tint = NovaRedGlow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Cancelar",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { viewModel.navigateTo(AppScreen.MAIN_MENU) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NovaTextSecondary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_volver_menu_principal")
                ) {
                    Text("Volver al Menú Principal", fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Footer sync note
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Sensors,
                        contentDescription = null,
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
            }
        }
    }
}
