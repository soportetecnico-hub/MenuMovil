package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object CalendarioLaboralHelper {

    private val dateFormat = SimpleDateFormat("dd / MM / yyyy", Locale.getDefault())

    fun esFinDeSemana(cal: Calendar): Boolean {
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
    }

    /**
     * Algoritmo anónimo de Meeus/Jones/Butcher para calcular Domingo de Resurrección
     */
    fun getPascua(year: Int): Calendar {
        val a = year % 19
        val b = year / 100
        val c = year % 100
        val d = b / 4
        val e = b % 4
        val f = (b + 8) / 25
        val g = (b - f + 1) / 3
        val h = (19 * a + b - d - g + 15) % 30
        val i = c / 4
        val k = c % 4
        val l = (32 + 2 * e + 2 * i - h - k) % 7
        val m = (a + 11 * h + 22 * l) / 451
        val month = (h + l - 7 * m + 114) / 31 // 3 = Marzo, 4 = Abril
        val day = ((h + l - 7 * m + 114) % 31) + 1

        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month - 1)
        cal.set(Calendar.DAY_OF_MONTH, day)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal
    }

    /**
     * Retorna el nombre del feriado oficial peruano si la fecha coincide, o null si no lo es.
     */
    fun obtenerNombreFeriado(cal: Calendar): String? {
        val dia = cal.get(Calendar.DAY_OF_MONTH)
        val mes = cal.get(Calendar.MONTH) // 0 = Enero, ..., 11 = Diciembre
        val anio = cal.get(Calendar.YEAR)

        // Feriados con fecha fija (Ley peruana)
        when (mes) {
            Calendar.JANUARY -> if (dia == 1) return "Año Nuevo"
            Calendar.MAY -> if (dia == 1) return "Día del Trabajo"
            Calendar.JUNE -> {
                if (dia == 7) return "Batalla de Arica y Día de la Bandera"
                if (dia == 29) return "San Pedro y San Pablo"
            }
            Calendar.JULY -> {
                if (dia == 23) return "Día de la Fuerza Aérea del Perú"
                if (dia == 28) return "Fiestas Patrias"
                if (dia == 29) return "Fiestas Patrias"
            }
            Calendar.AUGUST -> {
                if (dia == 6) return "Batalla de Junín"
                if (dia == 30) return "Santa Rosa de Lima"
            }
            Calendar.OCTOBER -> if (dia == 8) return "Combate de Angamos"
            Calendar.NOVEMBER -> if (dia == 1) return "Día de Todos los Santos"
            Calendar.DECEMBER -> {
                if (dia == 8) return "Inmaculada Concepción"
                if (dia == 9) return "Batalla de Ayacucho"
                if (dia == 25) return "Navidad"
            }
        }

        // Feriados móviles: Semana Santa (Jueves y Viernes Santo)
        val pascua = getPascua(anio)
        val juevesSanto = (pascua.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, -3) }
        val viernesSanto = (pascua.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, -2) }

        if (cal.get(Calendar.MONTH) == juevesSanto.get(Calendar.MONTH) &&
            cal.get(Calendar.DAY_OF_MONTH) == juevesSanto.get(Calendar.DAY_OF_MONTH)
        ) {
            return "Jueves Santo"
        }

        if (cal.get(Calendar.MONTH) == viernesSanto.get(Calendar.MONTH) &&
            cal.get(Calendar.DAY_OF_MONTH) == viernesSanto.get(Calendar.DAY_OF_MONTH)
        ) {
            return "Viernes Santo"
        }

        return null
    }

    fun esFeriado(cal: Calendar): Boolean {
        return obtenerNombreFeriado(cal) != null
    }

    /**
     * Determina si una fecha es día laborable / hábil (no sábado, no domingo, no feriado).
     */
    fun esDiaHabil(cal: Calendar): Boolean {
        return !esFinDeSemana(cal) && !esFeriado(cal)
    }

    fun obtenerDescripcionNoHabil(cal: Calendar): String {
        val feriado = obtenerNombreFeriado(cal)
        if (feriado != null) return "Feriado ($feriado)"
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        return when (dayOfWeek) {
            Calendar.SATURDAY -> "Sábado (Fin de semana)"
            Calendar.SUNDAY -> "Domingo (Fin de semana)"
            else -> "Día no laborable"
        }
    }

    /**
     * Obtiene el siguiente día laborable (salta sábados, domingos y feriados).
     */
    fun obtenerProximoDiaHabil(calendar: Calendar): Calendar {
        val cal = calendar.clone() as Calendar
        do {
            cal.add(Calendar.DAY_OF_MONTH, 1)
        } while (!esDiaHabil(cal))
        return cal
    }

    /**
     * Obtiene el día laborable anterior (salta sábados, domingos y feriados hacia atrás).
     */
    fun obtenerAnteriorDiaHabil(calendar: Calendar): Calendar {
        val cal = calendar.clone() as Calendar
        do {
            cal.add(Calendar.DAY_OF_MONTH, -1)
        } while (!esDiaHabil(cal))
        return cal
    }

    /**
     * Si la fecha es hábil, la mantiene; si no lo es, avanza al primer día hábil disponible.
     */
    fun ajustarADiaHabil(calendar: Calendar): Calendar {
        val cal = calendar.clone() as Calendar
        while (!esDiaHabil(cal)) {
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        return cal
    }

    fun parseFecha(fechaStr: String): Calendar {
        val cal = Calendar.getInstance()
        try {
            val date = dateFormat.parse(fechaStr)
            if (date != null) cal.time = date
        } catch (e: Exception) {
            // fallback
        }
        return cal
    }

    fun formatFecha(cal: Calendar): String {
        return dateFormat.format(cal.time)
    }

    fun obtenerNombreDiaSemana(cal: Calendar): String {
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Lunes"
            Calendar.TUESDAY -> "Martes"
            Calendar.WEDNESDAY -> "Miércoles"
            Calendar.THURSDAY -> "Jueves"
            Calendar.FRIDAY -> "Viernes"
            Calendar.SATURDAY -> "Sábado"
            Calendar.SUNDAY -> "Domingo"
            else -> ""
        }
    }

    fun obtenerNombreMes(cal: Calendar): String {
        return when (cal.get(Calendar.MONTH)) {
            Calendar.JANUARY -> "Enero"
            Calendar.FEBRUARY -> "Febrero"
            Calendar.MARCH -> "Marzo"
            Calendar.APRIL -> "Abril"
            Calendar.MAY -> "Mayo"
            Calendar.JUNE -> "Junio"
            Calendar.JULY -> "Julio"
            Calendar.AUGUST -> "Agosto"
            Calendar.SEPTEMBER -> "Septiembre"
            Calendar.OCTOBER -> "Octubre"
            Calendar.NOVEMBER -> "Noviembre"
            Calendar.DECEMBER -> "Diciembre"
            else -> ""
        }
    }

    /**
     * Retorna todos los días laborables (hábiles) de un mes y año específicos.
     */
    fun obtenerDiasHabilesDelMes(year: Int, month: Int): List<String> {
        val dias = mutableListOf<String>()
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (day in 1..maxDay) {
            cal.set(Calendar.DAY_OF_MONTH, day)
            if (esDiaHabil(cal)) {
                dias.add(formatFecha(cal))
            }
        }
        return dias
    }

    /**
     * Retorna los próximos N días hábiles a partir de la fecha indicada (incluyéndola si es hábil).
     */
    fun obtenerProximosDiasHabiles(calendar: Calendar, cantidad: Int): List<String> {
        val dias = mutableListOf<String>()
        var cursor = ajustarADiaHabil(calendar)
        while (dias.size < cantidad) {
            if (esDiaHabil(cursor)) {
                dias.add(formatFecha(cursor))
            }
            cursor = (cursor.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, 1) }
        }
        return dias
    }
}
