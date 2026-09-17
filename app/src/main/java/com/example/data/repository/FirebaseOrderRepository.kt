package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.model.Empleado
import com.example.data.model.MenuDia
import com.example.data.model.Pedido
import com.example.data.model.PlatoOpcion
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class FirebaseOrderRepository(private val context: Context) {

    private val tag = "FirebaseOrderRepo"
    private val prefs: SharedPreferences =
        context.getSharedPreferences("nova_menu_prefs", Context.MODE_PRIVATE)

    private val isFirebaseAvailable: Boolean by lazy {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context) != null
            } else {
                true
            }
        } catch (e: Exception) {
            Log.w(tag, "Firebase is not initialized via google-services.json: ${e.message}")
            false
        }
    }

    private val auth: FirebaseAuth?
        get() = if (isFirebaseAvailable) {
            try { FirebaseAuth.getInstance() } catch (e: Exception) { null }
        } else null

    private val firestore: FirebaseFirestore?
        get() = if (isFirebaseAvailable) {
            try { FirebaseFirestore.getInstance() } catch (e: Exception) { null }
        } else null

    // Corporate roster of employees in Planta Nova
    fun getEmpleadosPredefinidos(): List<Empleado> {
        return listOf(
            Empleado(
                id = "EMP-001",
                email = "soporte_tecnico@nova.pe",
                nombreCompleto = "Carlos Alberto Mendoza Quispe",
                dni = "45892147",
                area = "Producción Metalmecánica"
            ),
            Empleado(
                id = "EMP-002",
                email = "carlos.mendoza@nova.pe",
                nombreCompleto = "Carlos Alberto Mendoza Quispe",
                dni = "45892147",
                area = "Producción Metalmecánica"
            ),
            Empleado(
                id = "EMP-003",
                email = "maria.torres@nova.pe",
                nombreCompleto = "María Elena Torres Vega",
                dni = "41982341",
                area = "Control de Calidad"
            ),
            Empleado(
                id = "EMP-004",
                email = "jorge.ramos@nova.pe",
                nombreCompleto = "Jorge Luis Ramos Castillo",
                dni = "47219084",
                area = "Logística y Almacén"
            ),
            Empleado(
                id = "EMP-005",
                email = "ana.palacios@nova.pe",
                nombreCompleto = "Ana Sofía Palacios Díaz",
                dni = "46830192",
                area = "Administración y RRHH"
            ),
            Empleado(
                id = "EMP-006",
                email = "operaciones@nova.pe",
                nombreCompleto = "Ricardo Antonio Vílchez Gómez",
                dni = "42871239",
                area = "Operaciones y Mantenimiento"
            )
        )
    }

    // Default daily menu options matching the image exactly
    fun getMenuOpciones(): List<PlatoOpcion> {
        return listOf(
            PlatoOpcion(
                id = "opcion_1",
                titulo = "Opción 1 - Menú Criollo",
                descripcion = "Lomo Saltado con papas nativas fritas y arroz graneado + Ensalada mixta + Refresco de chicha morada",
                tipo = "Criollo"
            ),
            PlatoOpcion(
                id = "opcion_2",
                titulo = "Opción 2 - Menú Saludable / Dietético",
                descripcion = "Pechuga a la plancha con finas hierbas, puré de papas amarillas y verduras al vapor + Infusión de manzanilla",
                tipo = "Saludable"
            ),
            PlatoOpcion(
                id = "opcion_3",
                titulo = "Opción 3 - Menú Tradicional Norteño",
                descripcion = "Seco de Res a la norteña con frejoles batidos y salsa criolla + Refresco de maracuyá",
                tipo = "Tradicional Norteño"
            )
        )
    }

    // Standard monthly menu variations for Fogón Gastronómico (Planta Nova)
    fun getMenuCatalogoPorDia(diaDelMes: Int): List<PlatoOpcion> {
        val cycle = ((diaDelMes - 1).coerceAtLeast(0)) % 10
        return when (cycle) {
            0 -> listOf(
                PlatoOpcion("opcion_1", "Opción 1 - Menú Criollo", "Lomo Saltado con papas nativas fritas y arroz graneado + Ensalada mixta + Chicha morada", "Criollo"),
                PlatoOpcion("opcion_2", "Opción 2 - Menú Saludable / Dietético", "Pechuga a la plancha con finas hierbas, puré de papas amarillas y verduras al vapor + Manzanilla", "Saludable"),
                PlatoOpcion("opcion_3", "Opción 3 - Menú Tradicional Norteño", "Seco de Res a la norteña con frejoles batidos y salsa criolla + Refresco de maracuyá", "Tradicional")
            )
            1 -> listOf(
                PlatoOpcion("opcion_1", "Opción 1 - Ají de Gallina Clásico", "Ají de Gallina con arroz blanco, huevo duro, aceituna de botija y papa sancochada + Refresco de emoliente", "Criollo"),
                PlatoOpcion("opcion_2", "Opción 2 - Filete de Tilapia al Horno", "Filete de pescado a la plancha con puré de espinaca y ensalada fresca + Jugo de cebada", "Saludable"),
                PlatoOpcion("opcion_3", "Opción 3 - Arroz Chaufa Especial", "Arroz chaufa con trozos de pollo al wok, wantán crocante y cebollita china + Chicha morada", "Fusión")
            )
            2 -> listOf(
                PlatoOpcion("opcion_1", "Opción 1 - Tallarines Rojos con Pollo", "Tallarines en salsa pomodoro con presa de pollo y queso parmesano rallado + Refresco de maracuyá", "Criollo"),
                PlatoOpcion("opcion_2", "Opción 2 - Ensalada Caprese con Pollo", "Mix de lechugas, pechuga grillada, tomate, albahaca fresca y queso andino + Infusión de menta", "Saludable"),
                PlatoOpcion("opcion_3", "Opción 3 - Estofado de Res con Champiñones", "Estofado tierno de res con zanahorias, papas amarillas y arroz blanco + Emoliente tibio", "Casero")
            )
            3 -> listOf(
                PlatoOpcion("opcion_1", "Opción 1 - Arroz con Pollo a la Limeña", "Arroz con pollo al culantro, salsa huancaína artesanal y ensalada criolla + Chicha morada", "Criollo"),
                PlatoOpcion("opcion_2", "Opción 2 - Hamburguesa de Quinua y Lentejas", "Hamburguesas de quinua con verduras salteadas al wok y arroz integral + Agua de piña", "Vegetariano"),
                PlatoOpcion("opcion_3", "Opción 3 - Asado de Tira con Puré", "Corte de carne suave en salsa de vino tinto con puré rústico y arroz + Jugo de carambola", "Especial")
            )
            4 -> listOf(
                PlatoOpcion("opcion_1", "Opción 1 - Causa Rellena con Pollo", "Causa limeña con pechuga deshilachada y mayonesa casera + Sopa de verduras + Chicha", "Entrada/Fondo"),
                PlatoOpcion("opcion_2", "Opción 2 - Pollo al Vapor con Legumbres", "Pechuga al limón con brócoli, vainitas, zanahoria y camote glaseado + Infusión de hierbaluisa", "Saludable"),
                PlatoOpcion("opcion_3", "Opción 3 - Chuleta Dorada con Frejoles", "Chuleta de cerdo con arroz graneado, frejoles canario y ensalada criolla + Maracuyá", "Tradicional")
            )
            5 -> listOf(
                PlatoOpcion("opcion_1", "Opción 1 - Tallarines Verdes con Bistec", "Tallarines al pesto criollo con bistec de lomo apanado y papa a la huancaína + Refresco", "Criollo"),
                PlatoOpcion("opcion_2", "Opción 2 - Pechuga al Orégano y Choclo", "Pechuga magra al orégano con pastel de choclo tierno y ensalada verde + Manzanilla", "Saludable"),
                PlatoOpcion("opcion_3", "Opción 3 - Pollo al Horno con Papas Doradas", "Cuarto de pollo horneado a la leña con papas rústicas y arroz + Chicha morada", "Horno")
            )
            6 -> listOf(
                PlatoOpcion("opcion_1", "Opción 1 - Mondonguito a la Italiana", "Mondonguito suave salteado con cebolla, tomate, ají amarillo y papas fritas + Emoliente", "Criollo"),
                PlatoOpcion("opcion_2", "Opción 2 - Salmón o Trucha Andina a la Plancha", "Trucha fresca al vapor con ensalada tibia de habas, choclo y queso + Agua frutal", "Saludable"),
                PlatoOpcion("opcion_3", "Opción 3 - Carapulcra con Sopa Seca", "Plato tradicional chinchano con cerdo crujiente y fideos sazonados + Chicha morada", "Típico")
            )
            7 -> listOf(
                PlatoOpcion("opcion_1", "Opción 1 - Milanesa de Pollo Especial", "Milanesa dorada crujiente con arroz con choclo, papas doradas y tártara + Maracuyá", "Criollo"),
                PlatoOpcion("opcion_2", "Opción 2 - Solterito de Arequipa con Pollo", "Queso andino, choclo, habas tiernas, aceitunas y tiras de pollo asado + Hierbaluisa", "Saludable"),
                PlatoOpcion("opcion_3", "Opción 3 - Cau Cau Criollo de Pollo", "Guiso aromático de papa, hierbabuena y trozos de pollo con arroz blanco + Emoliente", "Casero")
            )
            8 -> listOf(
                PlatoOpcion("opcion_1", "Opción 1 - Escabeche de Pollo con Camote", "Escabeche con cebollas moradas caramelizadas, huevo duro y camote morado + Chicha", "Criollo"),
                PlatoOpcion("opcion_2", "Opción 2 - Bowl Saludable de Pavo y Quinua", "Pavo deshilachado con quinua tricolor, palta y vinagreta de limón + Té verde", "Saludable"),
                PlatoOpcion("opcion_3", "Opción 3 - Guiso de Pavita a la Jardinera", "Pavita tierna con arvejas, zanahorias y papas sancochadas + Refresco de chicha morada", "Tradicional")
            )
            else -> listOf(
                PlatoOpcion("opcion_1", "Opción 1 - Pollo a la Brasa Casero", "Pollo marinado al estilo fogón con papas crocantes y ensalada fresca + Gaseosa o chicha", "Favorito"),
                PlatoOpcion("opcion_2", "Opción 2 - Pechuga en Salsa de Champiñones", "Pechuga magra bañada en crema ligera de setas con arroz integral + Manzanilla", "Saludable"),
                PlatoOpcion("opcion_3", "Opción 3 - Seco de Pollo con Yucas", "Seco norteño con yucas fritas, arroz con choclo y salsa de cebolla + Maracuyá", "Norteño")
            )
        }
    }

    private fun normalizeFechaKey(fechaStr: String): String {
        return try {
            val clean = fechaStr.replace(" ", "")
            val parts = clean.split("/")
            if (parts.size == 3) {
                val day = parts[0].padStart(2, '0')
                val month = parts[1].padStart(2, '0')
                val year = parts[2]
                "$year-$month-$day"
            } else {
                fechaStr.replace("/", "-").replace(" ", "")
            }
        } catch (e: Exception) {
            fechaStr
        }
    }

    private fun getDiaDelMes(fechaStr: String): Int {
        return try {
            val clean = fechaStr.replace(" ", "")
            val parts = clean.split("/")
            if (parts.isNotEmpty()) {
                parts[0].toIntOrNull() ?: 1
            } else 1
        } catch (e: Exception) {
            1
        }
    }

    // --- Firestore Monthly Menu Management & Caching ---

    suspend fun obtenerMenuPorFecha(fechaStr: String): List<PlatoOpcion> {
        val fechaKey = normalizeFechaKey(fechaStr)
        val diaMes = getDiaDelMes(fechaStr)
        val defaultOpciones = getMenuCatalogoPorDia(diaMes)

        // 1. Check local preference cache
        val cachedJson = prefs.getString("menu_dia_$fechaKey", null)
        if (!cachedJson.isNullOrBlank()) {
            try {
                val obj = JSONObject(cachedJson)
                val rawList = obj.optJSONArray("opciones")
                if (rawList != null && rawList.length() > 0) {
                    val list = mutableListOf<PlatoOpcion>()
                    for (i in 0 until rawList.length()) {
                        val item = rawList.getJSONObject(i)
                        list.add(
                            PlatoOpcion(
                                id = item.optString("id"),
                                titulo = item.optString("titulo"),
                                descripcion = item.optString("descripcion"),
                                tipo = item.optString("tipo")
                            )
                        )
                    }
                    return list
                }
            } catch (e: Exception) {
                Log.w(tag, "Error parsing cached menu for $fechaKey: ${e.message}")
            }
        }

        // 2. Query Firestore collection "menu_mensual"
        val fs = firestore
        if (fs != null) {
            try {
                val doc = fs.collection("menu_mensual").document(fechaKey).get().await()
                if (doc.exists()) {
                    val data = doc.data
                    if (data != null) {
                        val menuDia = MenuDia.fromMap(data)
                        if (menuDia.opciones.isNotEmpty()) {
                            guardarMenuLocalmente(menuDia)
                            return menuDia.opciones
                        }
                    }
                }

                // If not in Firestore yet, seed this day's menu to Firestore collection "menu_mensual"
                val menuDiaToSeed = MenuDia(
                    fecha = fechaStr,
                    fechaKey = fechaKey,
                    platoDia = "Menú Fogón Gastronómico - Día $diaMes",
                    opciones = defaultOpciones
                )
                try {
                    fs.collection("menu_mensual")
                        .document(fechaKey)
                        .set(menuDiaToSeed.toMap())
                        .await()
                    Log.d(tag, "Menú del día $fechaKey guardado en Firestore menu_mensual")
                } catch (e: Exception) {
                    Log.w(tag, "No se pudo guardar menú en Firestore: ${e.message}")
                }
                guardarMenuLocalmente(menuDiaToSeed)
                return defaultOpciones
            } catch (e: Exception) {
                Log.w(tag, "Firestore menu query failed, using local generator: ${e.message}")
            }
        }

        // 3. Fallback to catalog and save to local cache
        val localMenu = MenuDia(fecha = fechaStr, fechaKey = fechaKey, opciones = defaultOpciones)
        guardarMenuLocalmente(localMenu)
        return defaultOpciones
    }

    private fun guardarMenuLocalmente(menuDia: MenuDia) {
        try {
            val json = JSONObject().apply {
                put("fecha", menuDia.fecha)
                put("fechaKey", menuDia.fechaKey)
                val array = JSONArray()
                menuDia.opciones.forEach { op ->
                    val opObj = JSONObject().apply {
                        put("id", op.id)
                        put("titulo", op.titulo)
                        put("descripcion", op.descripcion)
                        put("tipo", op.tipo)
                    }
                    array.put(opObj)
                }
                put("opciones", array)
            }
            prefs.edit().putString("menu_dia_${menuDia.fechaKey}", json.toString()).apply()
        } catch (e: Exception) {
            Log.e(tag, "Error saving local menu cache: ${e.message}")
        }
    }

    // --- Employee Lookup & Validation in Firestore Database ---

    suspend fun buscarYValidarEmpleadoEnFirestore(email: String): Result<Empleado> {
        val cleanEmail = email.trim().lowercase(Locale.ROOT)
        if (cleanEmail.isEmpty()) {
            return Result.failure(IllegalArgumentException("Ingresa un correo electrónico"))
        }

        val fs = firestore
        if (fs != null) {
            try {
                // 1. Check if the employee document already exists in Firestore collection "empleados"
                val snapshot = fs.collection("empleados")
                    .whereEqualTo("email", cleanEmail)
                    .limit(1)
                    .get()
                    .await()

                if (!snapshot.isEmpty) {
                    val doc = snapshot.documents.first()
                    val data = doc.data
                    if (data != null) {
                        val emp = Empleado.fromMap(data)
                        saveCachedEmpleado(emp)
                        return Result.success(emp)
                    }
                }

                // 2. If not found in Firestore collection, check the official company employee roster
                val predefinido = getEmpleadosPredefinidos().find {
                    it.email.equals(cleanEmail, ignoreCase = true)
                }

                if (predefinido != null) {
                    // Seed employee into Firestore database so it exists remotely
                    try {
                        fs.collection("empleados")
                            .document(predefinido.id)
                            .set(predefinido.toMap())
                            .await()
                    } catch (e: Exception) {
                        Log.w(tag, "No se pudo sincronizar empleado en Firestore: ${e.message}")
                    }
                    saveCachedEmpleado(predefinido)
                    return Result.success(predefinido)
                } else {
                    // Email does not belong to any employee in the Planta Nova database
                    return Result.failure(
                        Exception("El correo '$cleanEmail' no está registrado en el padrón de empleados de Planta Nova.")
                    )
                }
            } catch (e: Exception) {
                Log.w(tag, "Error consultando Firestore empleados: ${e.message}")
                // Fallback local check
                val predefinido = getEmpleadosPredefinidos().find {
                    it.email.equals(cleanEmail, ignoreCase = true)
                }
                if (predefinido != null) {
                    saveCachedEmpleado(predefinido)
                    return Result.success(predefinido)
                } else {
                    return Result.failure(
                        Exception("El correo '$cleanEmail' no está registrado en el padrón de empleados de Planta Nova.")
                    )
                }
            }
        } else {
            // Local fallback validation
            val predefinido = getEmpleadosPredefinidos().find {
                it.email.equals(cleanEmail, ignoreCase = true)
            }
            if (predefinido != null) {
                saveCachedEmpleado(predefinido)
                return Result.success(predefinido)
            } else {
                return Result.failure(
                    Exception("El correo '$cleanEmail' no está registrado en el padrón de empleados de Planta Nova.")
                )
            }
        }
    }

    // --- Authentication ---

    suspend fun loginWithEmail(email: String, pass: String): Result<Empleado> {
        val trimmedEmail = email.trim().lowercase(Locale.ROOT)
        if (trimmedEmail.isEmpty() || pass.isEmpty()) {
            return Result.failure(IllegalArgumentException("Ingresa el correo y la contraseña"))
        }

        // Validate employee in Firestore database FIRST
        val employeeValidation = buscarYValidarEmpleadoEnFirestore(trimmedEmail)
        if (employeeValidation.isFailure) {
            return Result.failure(
                employeeValidation.exceptionOrNull()
                    ?: Exception("El correo no corresponde a un empleado registrado en Planta Nova")
            )
        }

        val empleado = employeeValidation.getOrThrow()

        val authInstance = auth
        if (authInstance != null) {
            try {
                authInstance.signInWithEmailAndPassword(trimmedEmail, pass).await()
            } catch (e: Exception) {
                Log.e(tag, "FirebaseAuth signIn error: ${e.message}", e)
                val msg = e.localizedMessage ?: ""
                if (msg.contains("password", ignoreCase = true) || msg.contains("wrong", ignoreCase = true)) {
                    return Result.failure(Exception("Contraseña incorrecta para el empleado ${empleado.nombreCompleto}"))
                }
                // If user not in Firebase Auth yet, attempt auto-create for registered employee
                if (msg.contains("no user", ignoreCase = true) || msg.contains("user-not-found", ignoreCase = true)) {
                    try {
                        authInstance.createUserWithEmailAndPassword(trimmedEmail, pass).await()
                    } catch (ex: Exception) {
                        Log.w(tag, "Could not auto-create auth account: ${ex.message}")
                    }
                }
            }
        }

        saveCachedUserEmail(trimmedEmail)
        saveCachedEmpleado(empleado)
        return Result.success(empleado)
    }

    suspend fun registerWithEmail(email: String, pass: String): Result<Empleado> {
        val trimmedEmail = email.trim().lowercase(Locale.ROOT)
        if (trimmedEmail.isEmpty() || pass.length < 6) {
            return Result.failure(IllegalArgumentException("La contraseña debe tener al menos 6 caracteres"))
        }

        // Validate that this email belongs to a pre-authorized employee in the database
        val employeeValidation = buscarYValidarEmpleadoEnFirestore(trimmedEmail)
        if (employeeValidation.isFailure) {
            return Result.failure(
                employeeValidation.exceptionOrNull()
                    ?: Exception("Solo empleados registrados en Planta Nova pueden crear cuenta")
            )
        }

        val empleado = employeeValidation.getOrThrow()

        val authInstance = auth
        if (authInstance != null) {
            try {
                authInstance.createUserWithEmailAndPassword(trimmedEmail, pass).await()
            } catch (e: Exception) {
                Log.e(tag, "FirebaseAuth createUser error: ${e.message}", e)
            }
        }

        saveCachedUserEmail(trimmedEmail)
        saveCachedEmpleado(empleado)
        return Result.success(empleado)
    }

    fun getCurrentUserEmail(): String? {
        val firebaseEmail = auth?.currentUser?.email
        if (!firebaseEmail.isNullOrBlank()) {
            return firebaseEmail
        }
        return prefs.getString("cached_user_email", null)
    }

    fun getCachedEmpleado(): Empleado? {
        val raw = prefs.getString("cached_empleado_data", null) ?: return null
        return try {
            val obj = JSONObject(raw)
            Empleado(
                id = obj.optString("id"),
                email = obj.optString("email"),
                nombreCompleto = obj.optString("nombreCompleto"),
                dni = obj.optString("dni"),
                area = obj.optString("area")
            )
        } catch (e: Exception) {
            null
        }
    }

    fun saveCachedEmpleado(empleado: Empleado) {
        try {
            val obj = JSONObject().apply {
                put("id", empleado.id)
                put("email", empleado.email)
                put("nombreCompleto", empleado.nombreCompleto)
                put("dni", empleado.dni)
                put("area", empleado.area)
            }
            prefs.edit().putString("cached_empleado_data", obj.toString()).apply()
        } catch (e: Exception) {
            Log.e(tag, "Error caching empleado: ${e.message}")
        }
    }

    fun logout() {
        try {
            auth?.signOut()
        } catch (e: Exception) {
            Log.w(tag, "Error on signOut: ${e.message}")
        }
        prefs.edit()
            .remove("cached_user_email")
            .remove("cached_empleado_data")
            .apply()
    }

    private fun saveCachedUserEmail(email: String) {
        prefs.edit().putString("cached_user_email", email).apply()
    }

    // --- Orders (Firestore + Local Persistence) ---

    fun hasInternetConnection(): Boolean {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? android.net.ConnectivityManager
            if (connectivityManager != null) {
                val network = connectivityManager.activeNetwork ?: return false
                val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
                capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET) ||
                    capabilities.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun guardarPedido(pedido: Pedido): Result<Pedido> {
        val orderId = if (pedido.id.isNotBlank()) pedido.id else "PED-${System.currentTimeMillis() % 1000000}"
        val pedidoToSave = pedido.copy(id = orderId)

        // 1. Always save to local cache for instant UI feedback and offline support
        saveOrderToLocalCache(pedidoToSave)

        // 2. Persist to Firebase Firestore collection "pedidos"
        val fs = firestore
        if (fs != null) {
            try {
                fs.collection("pedidos")
                    .document(orderId)
                    .set(pedidoToSave.toMap())
                    .await()
                Log.d(tag, "Pedido persistido exitosamente en Firestore document ID: $orderId")
            } catch (e: Exception) {
                Log.w(tag, "Firestore save failed, order remains in local persistence: ${e.message}")
            }
        }

        return Result.success(pedidoToSave)
    }

    suspend fun obtenerUltimoPedido(userEmail: String): Pedido? {
        // First try Firestore
        val fs = firestore
        if (fs != null && userEmail.isNotBlank()) {
            try {
                val snapshot = fs.collection("pedidos")
                    .whereEqualTo("userEmail", userEmail)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .await()

                if (!snapshot.isEmpty) {
                    val doc = snapshot.documents.first()
                    val data = doc.data
                    if (data != null) {
                        val pedido = Pedido.fromMap(data)
                        saveOrderToLocalCache(pedido)
                        return pedido
                    }
                }
            } catch (e: Exception) {
                Log.w(tag, "Firestore query failed, reading from local cache: ${e.message}")
            }
        }

        // Fallback to local cache
        return getLatestOrderFromLocalCache(userEmail)
    }

    suspend fun eliminarPedido(orderId: String, userEmail: String) {
        val fs = firestore
        if (fs != null) {
            try {
                fs.collection("pedidos").document(orderId).delete().await()
            } catch (e: Exception) {
                Log.w(tag, "Firestore delete error: ${e.message}")
            }
        }
        removeOrderFromLocalCache(orderId)
    }

    // --- Local Storage helpers ---

    private fun saveOrderToLocalCache(pedido: Pedido) {
        val rawJson = prefs.getString("cached_orders", "[]") ?: "[]"
        val list = try {
            JSONArray(rawJson)
        } catch (e: Exception) {
            JSONArray()
        }

        // Build new JSON array placing the latest at the start
        val updated = JSONArray()
        val obj = JSONObject().apply {
            put("id", pedido.id)
            put("userEmail", pedido.userEmail)
            put("empleadoNombre", pedido.empleadoNombre)
            put("empleadoDni", pedido.empleadoDni)
            put("empleadoArea", pedido.empleadoArea)
            put("fechaMenu", pedido.fechaMenu)
            put("opcionTitulo", pedido.opcionTitulo)
            put("opcionDescripcion", pedido.opcionDescripcion)
            put("observaciones", pedido.observaciones)
            put("timestamp", pedido.timestamp)
            put("estado", pedido.estado)
            put("modalidadPlanificacion", pedido.modalidadPlanificacion)
            put("totalDias", pedido.totalDias)
            val diasArray = JSONArray()
            pedido.diasSeleccionados.forEach { diasArray.put(it) }
            put("diasSeleccionados", diasArray)
            val eleccionesArray = JSONArray()
            pedido.elecciones.forEach { eleccion ->
                val elObj = JSONObject().apply {
                    put("fecha", eleccion.fecha)
                    put("diaSemana", eleccion.diaSemana)
                    put("platoId", eleccion.platoId)
                    put("platoTitulo", eleccion.platoTitulo)
                    put("platoDescripcion", eleccion.platoDescripcion)
                    put("platoTipo", eleccion.platoTipo)
                }
                eleccionesArray.put(elObj)
            }
            put("elecciones", eleccionesArray)
            put("totalElecciones", pedido.totalElecciones)
        }
        updated.put(obj)

        for (i in 0 until list.length()) {
            val item = list.getJSONObject(i)
            if (item.optString("id") != pedido.id) {
                updated.put(item)
            }
        }

        prefs.edit().putString("cached_orders", updated.toString()).apply()
    }

    private fun getLatestOrderFromLocalCache(userEmail: String): Pedido? {
        val rawJson = prefs.getString("cached_orders", "[]") ?: "[]"
        try {
            val list = JSONArray(rawJson)
            for (i in 0 until list.length()) {
                val item = list.getJSONObject(i)
                val orderEmail = item.optString("userEmail")
                if (orderEmail.equals(userEmail, ignoreCase = true) || userEmail.isBlank()) {
                    val rawDiasArray = item.optJSONArray("diasSeleccionados")
                    val diasList = mutableListOf<String>()
                    if (rawDiasArray != null) {
                        for (j in 0 until rawDiasArray.length()) {
                            diasList.add(rawDiasArray.getString(j))
                        }
                    }

                    val rawEleccionesArray = item.optJSONArray("elecciones")
                    val eleccionesList = mutableListOf<com.example.data.model.EleccionMenuDia>()
                    if (rawEleccionesArray != null) {
                        for (j in 0 until rawEleccionesArray.length()) {
                            val elObj = rawEleccionesArray.getJSONObject(j)
                            eleccionesList.add(
                                com.example.data.model.EleccionMenuDia(
                                    fecha = elObj.optString("fecha"),
                                    diaSemana = elObj.optString("diaSemana"),
                                    platoId = elObj.optString("platoId"),
                                    platoTitulo = elObj.optString("platoTitulo"),
                                    platoDescripcion = elObj.optString("platoDescripcion"),
                                    platoTipo = elObj.optString("platoTipo")
                                )
                            )
                        }
                    }

                    val fechaVal = item.optString("fechaMenu")
                    val finalDias = when {
                        eleccionesList.isNotEmpty() -> eleccionesList.map { it.fecha }
                        diasList.isNotEmpty() -> diasList
                        fechaVal.isNotBlank() -> listOf(fechaVal)
                        else -> emptyList()
                    }

                    return Pedido(
                        id = item.optString("id"),
                        userEmail = orderEmail,
                        empleadoNombre = item.optString("empleadoNombre"),
                        empleadoDni = item.optString("empleadoDni"),
                        empleadoArea = item.optString("empleadoArea"),
                        fechaMenu = fechaVal,
                        opcionTitulo = item.optString("opcionTitulo"),
                        opcionDescripcion = item.optString("opcionDescripcion"),
                        observaciones = item.optString("observaciones"),
                        timestamp = item.optLong("timestamp"),
                        estado = item.optString("estado", "Registrado en Planta Nova"),
                        modalidadPlanificacion = item.optString("modalidadPlanificacion", "MENSUAL_ELECCIONES"),
                        diasSeleccionados = finalDias,
                        totalDias = item.optInt("totalDias", finalDias.size.coerceAtLeast(1)),
                        elecciones = eleccionesList,
                        totalElecciones = item.optInt("totalElecciones", if (eleccionesList.isNotEmpty()) eleccionesList.size else finalDias.size.coerceAtLeast(1))
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error parsing local orders: ${e.message}")
        }
        return null
    }

    private fun removeOrderFromLocalCache(orderId: String) {
        val rawJson = prefs.getString("cached_orders", "[]") ?: "[]"
        try {
            val list = JSONArray(rawJson)
            val updated = JSONArray()
            for (i in 0 until list.length()) {
                val item = list.getJSONObject(i)
                if (item.optString("id") != orderId) {
                    updated.put(item)
                }
            }
            prefs.edit().putString("cached_orders", updated.toString()).apply()
        } catch (e: Exception) {
            Log.e(tag, "Error removing order from local cache: ${e.message}")
        }
    }
}
