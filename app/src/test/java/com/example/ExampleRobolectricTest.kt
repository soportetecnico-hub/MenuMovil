package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.FirebaseOrderRepository
import com.example.util.CalendarioLaboralHelper
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Nova Menú", appName)

    val developedBy = context.getString(R.string.developed_by_vallumi)
    assertEquals("Desarrollado por Vallumi-System", developedBy)
  }

  @Test
  fun `validar empleado por correo en base de datos asigna nombre y datos automaticamente`() = runBlocking {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val repo = FirebaseOrderRepository(app)

    // Test registered corporate email
    val result = repo.buscarYValidarEmpleadoEnFirestore("carlos.mendoza@nova.pe")
    assertTrue(result.isSuccess)

    val empleado = result.getOrNull()
    assertNotNull(empleado)
    assertEquals("Carlos Alberto Mendoza Quispe", empleado?.nombreCompleto)
    assertEquals("45892147", empleado?.dni)
    assertEquals("Producción Metalmecánica", empleado?.area)

    // Test cached persistence
    val cached = repo.getCachedEmpleado()
    assertNotNull(cached)
    assertEquals("Carlos Alberto Mendoza Quispe", cached?.nombreCompleto)

    // Test non-registered email is rejected
    val invalidResult = repo.buscarYValidarEmpleadoEnFirestore("externo_desconocido@gmail.com")
    assertTrue(invalidResult.isFailure)
  }

  @Test
  fun `menu de diferentes dias del mes varia y persiste correctamente`() = runBlocking {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val repo = FirebaseOrderRepository(app)

    // Verify day 1 menu
    val menuDia1 = repo.obtenerMenuPorFecha("01 / 09 / 2026")
    assertTrue(menuDia1.isNotEmpty())
    assertEquals(3, menuDia1.size)

    // Verify day 2 menu has distinct options
    val menuDia2 = repo.obtenerMenuPorFecha("02 / 09 / 2026")
    assertTrue(menuDia2.isNotEmpty())
    assertEquals(3, menuDia2.size)

    // Verify menus across different days are different
    val plato1Dia1 = menuDia1[0].titulo
    val plato1Dia2 = menuDia2[0].titulo
    assertTrue(plato1Dia1 != plato1Dia2)

    // Verify catalog variation for full month coverage
    val menuDia30 = repo.obtenerMenuPorFecha("30 / 09 / 2026")
    assertTrue(menuDia30.isNotEmpty())
    assertEquals(3, menuDia30.size)
  }

  @Test
  fun `calendario excluye fines de semana y feriados peruanos`() {
    // 1. Sábado 05 / 09 / 2026 y Domingo 06 / 09 / 2026 son fines de semana
    val sabadoCal = CalendarioLaboralHelper.parseFecha("05 / 09 / 2026")
    val domingoCal = CalendarioLaboralHelper.parseFecha("06 / 09 / 2026")
    assertTrue(CalendarioLaboralHelper.esFinDeSemana(sabadoCal))
    assertTrue(CalendarioLaboralHelper.esFinDeSemana(domingoCal))
    assertFalse(CalendarioLaboralHelper.esDiaHabil(sabadoCal))
    assertFalse(CalendarioLaboralHelper.esDiaHabil(domingoCal))

    // 2. Lunes 07 / 09 / 2026 es día hábil
    val lunesCal = CalendarioLaboralHelper.parseFecha("07 / 09 / 2026")
    assertFalse(CalendarioLaboralHelper.esFinDeSemana(lunesCal))
    assertTrue(CalendarioLaboralHelper.esDiaHabil(lunesCal))

    // 3. Feriados peruanos oficiales (28 y 29 de Julio, 1 de Mayo, 1 de Enero, etc.)
    val fiestasPatrias = CalendarioLaboralHelper.parseFecha("28 / 07 / 2026")
    assertTrue(CalendarioLaboralHelper.esFeriado(fiestasPatrias))
    assertFalse(CalendarioLaboralHelper.esDiaHabil(fiestasPatrias))
    assertEquals("Fiestas Patrias", CalendarioLaboralHelper.obtenerNombreFeriado(fiestasPatrias))

    val diaTrabajo = CalendarioLaboralHelper.parseFecha("01 / 05 / 2026")
    assertTrue(CalendarioLaboralHelper.esFeriado(diaTrabajo))
    assertFalse(CalendarioLaboralHelper.esDiaHabil(diaTrabajo))

    // 4. Salto de día hacia adelante: desde Viernes 04/09/2026 el siguiente día hábil debe ser Lunes 07/09/2026
    val viernesCal = CalendarioLaboralHelper.parseFecha("04 / 09 / 2026")
    val proximoHabil = CalendarioLaboralHelper.obtenerProximoDiaHabil(viernesCal)
    val proxFechaStr = CalendarioLaboralHelper.formatFecha(proximoHabil)
    assertEquals("07 / 09 / 2026", proxFechaStr)

    // 5. Salto de día hacia atrás: desde Lunes 07/09/2026 el día hábil anterior debe ser Viernes 04/09/2026
    val anteriorHabil = CalendarioLaboralHelper.obtenerAnteriorDiaHabil(lunesCal)
    val antFechaStr = CalendarioLaboralHelper.formatFecha(anteriorHabil)
    assertEquals("04 / 09 / 2026", antFechaStr)
  }
}
