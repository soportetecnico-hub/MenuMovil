package com.example.data.model

data class Empleado(
    val id: String = "",
    val email: String = "",
    val nombreCompleto: String = "",
    val dni: String = "",
    val area: String = ""
) {
    val displayLabel: String
        get() = "$nombreCompleto - $area"

    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "email" to email.lowercase().trim(),
            "nombreCompleto" to nombreCompleto,
            "dni" to dni,
            "area" to area
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): Empleado {
            return Empleado(
                id = map["id"] as? String ?: "",
                email = map["email"] as? String ?: "",
                nombreCompleto = map["nombreCompleto"] as? String ?: "",
                dni = map["dni"] as? String ?: "",
                area = map["area"] as? String ?: ""
            )
        }
    }
}

data class PlatoOpcion(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val tipo: String = ""
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "titulo" to titulo,
            "descripcion" to descripcion,
            "tipo" to tipo
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): PlatoOpcion {
            return PlatoOpcion(
                id = map["id"] as? String ?: "",
                titulo = map["titulo"] as? String ?: "",
                descripcion = map["descripcion"] as? String ?: "",
                tipo = map["tipo"] as? String ?: ""
            )
        }
    }
}

data class MenuDia(
    val fecha: String = "", // Format: "dd / MM / yyyy" or "yyyy-MM-dd"
    val fechaKey: String = "", // Normalized key e.g. "2026-09-08"
    val platoDia: String = "", // e.g. "Especialidad del Chef"
    val opciones: List<PlatoOpcion> = emptyList()
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "fecha" to fecha,
            "fechaKey" to fechaKey,
            "platoDia" to platoDia,
            "opciones" to opciones.map { it.toMap() }
        )
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any?>): MenuDia {
            val rawList = map["opciones"] as? List<Map<String, Any?>> ?: emptyList()
            val parsedOpciones = rawList.map { PlatoOpcion.fromMap(it) }
            return MenuDia(
                fecha = map["fecha"] as? String ?: "",
                fechaKey = map["fechaKey"] as? String ?: "",
                platoDia = map["platoDia"] as? String ?: "",
                opciones = parsedOpciones
            )
        }
    }
}

data class EleccionMenuDia(
    val fecha: String = "",
    val diaSemana: String = "",
    val platoId: String = "",
    val platoTitulo: String = "",
    val platoDescripcion: String = "",
    val platoTipo: String = ""
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "fecha" to fecha,
            "diaSemana" to diaSemana,
            "platoId" to platoId,
            "platoTitulo" to platoTitulo,
            "platoDescripcion" to platoDescripcion,
            "platoTipo" to platoTipo
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): EleccionMenuDia {
            return EleccionMenuDia(
                fecha = map["fecha"] as? String ?: "",
                diaSemana = map["diaSemana"] as? String ?: "",
                platoId = map["platoId"] as? String ?: "",
                platoTitulo = map["platoTitulo"] as? String ?: "",
                platoDescripcion = map["platoDescripcion"] as? String ?: "",
                platoTipo = map["platoTipo"] as? String ?: ""
            )
        }
    }
}

data class Pedido(
    val id: String = "",
    val userEmail: String = "",
    val empleadoNombre: String = "",
    val empleadoDni: String = "",
    val empleadoArea: String = "",
    val fechaMenu: String = "",
    val opcionTitulo: String = "",
    val opcionDescripcion: String = "",
    val observaciones: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val estado: String = "Registrado en Planta Nova",
    val modalidadPlanificacion: String = "MENSUAL_ELECCIONES",
    val diasSeleccionados: List<String> = emptyList(), // Lista de fechas con elecciones
    val totalDias: Int = 1,
    val elecciones: List<EleccionMenuDia> = emptyList(), // Lista detallada de elecciones por día
    val totalElecciones: Int = elecciones.size
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "userEmail" to userEmail,
            "empleadoNombre" to empleadoNombre,
            "empleadoDni" to empleadoDni,
            "empleadoArea" to empleadoArea,
            "fechaMenu" to fechaMenu,
            "opcionTitulo" to opcionTitulo,
            "opcionDescripcion" to opcionDescripcion,
            "observaciones" to observaciones,
            "timestamp" to timestamp,
            "estado" to estado,
            "modalidadPlanificacion" to modalidadPlanificacion,
            "diasSeleccionados" to diasSeleccionados,
            "totalDias" to totalDias,
            "elecciones" to elecciones.map { it.toMap() },
            "totalElecciones" to totalElecciones
        )
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any?>): Pedido {
            val rawElecciones = map["elecciones"] as? List<Map<String, Any?>> ?: emptyList()
            val parsedElecciones = rawElecciones.map { EleccionMenuDia.fromMap(it) }

            val rawDias = map["diasSeleccionados"] as? List<String> ?: emptyList()
            val fechaMenuVal = map["fechaMenu"] as? String ?: ""
            val diasList = when {
                parsedElecciones.isNotEmpty() -> parsedElecciones.map { it.fecha }
                rawDias.isNotEmpty() -> rawDias
                fechaMenuVal.isNotBlank() -> listOf(fechaMenuVal)
                else -> emptyList()
            }
            val diasCount = (map["totalDias"] as? Number)?.toInt() ?: diasList.size.coerceAtLeast(1)
            val eleccionesCount = (map["totalElecciones"] as? Number)?.toInt()
                ?: (if (parsedElecciones.isNotEmpty()) parsedElecciones.size else diasCount)

            val firstEleccion = parsedElecciones.firstOrNull()
            val finalTitulo = map["opcionTitulo"] as? String ?: firstEleccion?.platoTitulo ?: ""
            val finalDesc = map["opcionDescripcion"] as? String ?: firstEleccion?.platoDescripcion ?: ""

            return Pedido(
                id = map["id"] as? String ?: "",
                userEmail = map["userEmail"] as? String ?: "",
                empleadoNombre = map["empleadoNombre"] as? String ?: "",
                empleadoDni = map["empleadoDni"] as? String ?: "",
                empleadoArea = map["empleadoArea"] as? String ?: "",
                fechaMenu = if (fechaMenuVal.isNotBlank()) fechaMenuVal else firstEleccion?.fecha ?: "",
                opcionTitulo = finalTitulo,
                opcionDescripcion = finalDesc,
                observaciones = map["observaciones"] as? String ?: "",
                timestamp = (map["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                estado = map["estado"] as? String ?: "Registrado en Planta Nova",
                modalidadPlanificacion = map["modalidadPlanificacion"] as? String ?: "MENSUAL_ELECCIONES",
                diasSeleccionados = diasList,
                totalDias = diasCount,
                elecciones = parsedElecciones,
                totalElecciones = eleccionesCount
            )
        }
    }
}
