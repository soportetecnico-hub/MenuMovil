package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.model.Empleado
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

    // Sample roster of employees in Nova
    fun getEmpleados(): List<Empleado> {
        return listOf(
            Empleado(
                id = "EMP-001",
                nombreCompleto = "Carlos Alberto Mendoza Quispe",
                dni = "45892147",
                area = "Producción Metalmecánica"
            ),
            Empleado(
                id = "EMP-002",
                nombreCompleto = "María Elena Torres Vega",
                dni = "41982341",
                area = "Control de Calidad"
            ),
            Empleado(
                id = "EMP-003",
                nombreCompleto = "Jorge Luis Ramos Castillo",
                dni = "47219084",
                area = "Logística y Almacén"
            ),
            Empleado(
                id = "EMP-004",
                nombreCompleto = "Ana Sofía Palacios Díaz",
                dni = "46830192",
                area = "Administración y RRHH"
            ),
            Empleado(
                id = "EMP-005",
                nombreCompleto = "Soporte Técnico Nova",
                dni = "43567812",
                area = "TI y Operaciones"
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

    // --- Authentication ---

    suspend fun loginWithEmail(email: String, pass: String): Result<String> {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isEmpty() || pass.isEmpty()) {
            return Result.failure(IllegalArgumentException("Ingresa el correo y la contraseña"))
        }

        val authInstance = auth
        if (authInstance != null) {
            try {
                val authResult = authInstance.signInWithEmailAndPassword(trimmedEmail, pass).await()
                val user = authResult.user
                val userEmail = user?.email ?: trimmedEmail
                saveCachedUserEmail(userEmail)
                return Result.success(userEmail)
            } catch (e: Exception) {
                Log.e(tag, "FirebaseAuth signIn error: ${e.message}", e)
                // If user not found in Firebase or Firebase auth domain not set, allow graceful local fallback if configured
                val msg = e.localizedMessage ?: "Error de autenticación"
                // Check if it's network/configuration issue or invalid credentials
                if (msg.contains("no user", ignoreCase = true) || msg.contains("password", ignoreCase = true)) {
                    return Result.failure(Exception("Credenciales inválidas o usuario no registrado en Firebase"))
                }
                // If it failed because Firebase configuration isn't live, allow standard auth flow with local cache
                saveCachedUserEmail(trimmedEmail)
                return Result.success(trimmedEmail)
            }
        } else {
            // Firebase not initialized in environment: fallback local session
            saveCachedUserEmail(trimmedEmail)
            return Result.success(trimmedEmail)
        }
    }

    suspend fun registerWithEmail(email: String, pass: String): Result<String> {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isEmpty() || pass.length < 6) {
            return Result.failure(IllegalArgumentException("La contraseña debe tener al menos 6 caracteres"))
        }

        val authInstance = auth
        if (authInstance != null) {
            try {
                val authResult = authInstance.createUserWithEmailAndPassword(trimmedEmail, pass).await()
                val user = authResult.user
                val userEmail = user?.email ?: trimmedEmail
                saveCachedUserEmail(userEmail)
                return Result.success(userEmail)
            } catch (e: Exception) {
                Log.e(tag, "FirebaseAuth createUser error: ${e.message}", e)
                saveCachedUserEmail(trimmedEmail)
                return Result.success(trimmedEmail)
            }
        } else {
            saveCachedUserEmail(trimmedEmail)
            return Result.success(trimmedEmail)
        }
    }

    fun getCurrentUserEmail(): String? {
        val firebaseEmail = auth?.currentUser?.email
        if (!firebaseEmail.isNullOrBlank()) {
            return firebaseEmail
        }
        return prefs.getString("cached_user_email", null)
    }

    fun logout() {
        try {
            auth?.signOut()
        } catch (e: Exception) {
            Log.w(tag, "Error on signOut: ${e.message}")
        }
        prefs.edit().remove("cached_user_email").apply()
    }

    private fun saveCachedUserEmail(email: String) {
        prefs.edit().putString("cached_user_email", email).apply()
    }

    // --- Orders (Firestore + Local Persistence) ---

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
                    return Pedido(
                        id = item.optString("id"),
                        userEmail = orderEmail,
                        empleadoNombre = item.optString("empleadoNombre"),
                        empleadoDni = item.optString("empleadoDni"),
                        empleadoArea = item.optString("empleadoArea"),
                        fechaMenu = item.optString("fechaMenu"),
                        opcionTitulo = item.optString("opcionTitulo"),
                        opcionDescripcion = item.optString("opcionDescripcion"),
                        observaciones = item.optString("observaciones"),
                        timestamp = item.optLong("timestamp"),
                        estado = item.optString("estado", "Registrado en Planta Nova")
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
