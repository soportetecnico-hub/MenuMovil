package com.example.data.model

data class Empleado(
    val id: String,
    val nombreCompleto: String,
    val dni: String,
    val area: String
) {
    val displayLabel: String
        get() = "$nombreCompleto - $area"
}

data class PlatoOpcion(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val tipo: String
)

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
    val estado: String = "Registrado en Planta Nova"
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
            "estado" to estado
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): Pedido {
            return Pedido(
                id = map["id"] as? String ?: "",
                userEmail = map["userEmail"] as? String ?: "",
                empleadoNombre = map["empleadoNombre"] as? String ?: "",
                empleadoDni = map["empleadoDni"] as? String ?: "",
                empleadoArea = map["empleadoArea"] as? String ?: "",
                fechaMenu = map["fechaMenu"] as? String ?: "",
                opcionTitulo = map["opcionTitulo"] as? String ?: "",
                opcionDescripcion = map["opcionDescripcion"] as? String ?: "",
                observaciones = map["observaciones"] as? String ?: "",
                timestamp = (map["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                estado = map["estado"] as? String ?: "Registrado en Planta Nova"
            )
        }
    }
}
