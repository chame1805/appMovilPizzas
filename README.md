# Pizzería Distrito — App Móvil Android

Aplicación Android nativa desarrollada en Kotlin con Jetpack Compose para la gestión operativa de una pizzería. La app maneja tres roles de usuario (Mesero, Cocinero, Administrador) con comunicación en tiempo real via WebSocket y notificaciones push via Firebase Cloud Messaging.

---

## Tecnologías principales

| Tecnología | Versión | Uso |
|---|---|---|
| Kotlin | 2.x | Lenguaje principal |
| Jetpack Compose | BOM | UI declarativa |
| Hilt (Dagger) | — | Inyección de dependencias |
| Retrofit + OkHttp | — | Consumo de API REST |
| Room (SQLite) | — | Persistencia local / caché offline |
| OkHttp WebSocket | — | Comunicación en tiempo real |
| Firebase Cloud Messaging | — | Notificaciones push |
| AndroidX Biometric | 1.2.0-alpha05 | Autenticación biométrica |
| Navigation Compose | — | Navegación entre pantallas |
| Coil | — | Carga de imágenes |
| Material 3 | — | Sistema de diseño |

---

## Arquitectura

El proyecto sigue **Clean Architecture** en capas:

```
Presentation (UI / ViewModel)
    ↓
Domain (UseCases / Entities / Repository interfaces)
    ↓
Data (RepositoryImpl / Remote API / Local Room)
```

El patrón de UI usado es **MVVM** con `StateFlow` para el estado reactivo y `collectAsStateWithLifecycle()` en Compose.

---

## Módulos de funcionalidad

### Roles y navegación

La app define tres flujos según el rol devuelto por el login:

| Rol | Destino en la app |
|---|---|
| `MESERO` | Menú de pizzas → Crear orden → Historial → Perfil |
| `COCINERO` | Panel de cocina con órdenes activas y timers |
| `ADMIN` | Dashboard de ventas + Gestión de menú |

El grafo de navegación se define en:
`core/navigation/AppNavigation.kt`

---

## Funcionalidades calificables

### 1. Autenticación con JWT
- Login contra API REST con email y password.
- El token JWT se guarda en memoria via `SessionManager` (Singleton con Hilt).
- Al login exitoso se obtiene el token FCM y se guarda para notificaciones.
- Archivo: `feacture/auth/`

### 2. Registro de usuario
- Pantalla de registro independiente con llamada a la API.
- Archivo: `feature/register/`

### 3. Autenticación Biométrica
- Implementada con `androidx.biometric.BiometricPrompt`.
- El mesero puede activar/desactivar huella dactilar o reconocimiento facial desde su perfil.
- Soporta `BIOMETRIC_STRONG` y `DEVICE_CREDENTIAL`.
- Archivo: `features/pizzeriadistrito/presentation/screens/WaiterProfileScreen.kt`

### 4. Persistencia local con Room
Estrategia **cache-first** para el rol de mesero:

- `getMyOrders()` — intenta la API, si falla devuelve caché de Room.
- `createOrder()` — persiste en API y también en Room.
- `updateOrderStatus()` / `updateOrder()` — actualiza en ambos lados.

El admin usa Room como **último fallback** si todos los endpoints de historial fallan.

**Archivos Room:**
```
core/database/entity/OrderEntity.kt   ← tabla "orders"
core/database/dao/OrderDao.kt         ← consultas SQL
core/database/PizzaDatabase.kt        ← clase principal Room
core/di/DatabaseModule.kt             ← instancia con Room.databaseBuilder()
```

### 5. WebSocket en tiempo real

**Para el cocinero** (`KitchenWebSocketManager`):
- Conecta a `ws://<servidor>/orders/ws/kitchen?token=<jwt>`
- Emite eventos `NEW_ORDER` / `ORDER_CREATED` que disparan notificaciones locales.
- Corre dentro de un `ForegroundService` (`KitchenOrderForegroundService`) para mantenerse activo en background.

**Para el mesero** (`WaiterWebSocketManager`):
- Conecta al inicio de sesión, desconecta al cerrar.
- Recibe eventos `ORDER_COMPLETED` y muestra un Toast en la pantalla de menú.

### 6. Firebase Cloud Messaging (FCM)
- `PizzaFirebaseMessagingService` extiende `FirebaseMessagingService`.
- `onMessageReceived()` muestra una notificación local del sistema.
- `onNewToken()` guarda el nuevo token en `SharedPreferences`.
- Al login se obtiene el token con `FirebaseMessaging.getInstance().token` y se guarda en `SessionManager`.

### 7. Foreground Service
- `KitchenOrderForegroundService` mantiene el WebSocket de cocina activo cuando la app está en background.
- Declarado con `foregroundServiceType="dataSync"` en el Manifest.
- Usa `START_STICKY` para reiniciarse si el sistema lo mata.

### 8. Pantalla de Cocina
- Lista de órdenes activas con estados: `PENDING` → `IN_PROGRESS` → `COMPLETED`.
- Timer visual por orden con alerta roja si supera los 15 minutos.
- Actualización de estado via API con un botón por orden.
- Archivo: `feacturecocina/presentation/screens/CocineroScreen.kt`

### 9. Dashboard de Administrador
- Historial de ventas con búsqueda por pizza/cliente y filtros por estado.
- Gráfico de barras de pedidos por estado.
- Ranking top 5 pizzas más vendidas.
- Resumen diario (hoy vs ayer).
- Exportación del historial en formato CSV vía `Intent.ACTION_SEND`.
- Gestión de menú (CRUD de pizzas): crear, editar y eliminar pizzas.
- Archivo: `feacture/administrador/presentation/screens/AdminDashboardScreen.kt`

### 10. Perfil de Usuario
- Foto de perfil tomada con cámara, almacenada en Base64 en `SessionManager`.
- Toggle de modo oscuro persistido en `SharedPreferences`.
- Toggle de autenticación biométrica.
- Disponible para mesero y cocinero.

---

## Comunicación con el backend

Todos los endpoints se consumen via Retrofit. Las interfaces API están definidas por feature:

| Archivo | Endpoints |
|---|---|
| `feacture/auth/data/datasource/AuthApi.kt` | Login |
| `feature/register/data/datasource/RegisterApi.kt` | Registro |
| `features/pizzeriadistrito/data/datasources/remote/WaiterOrderApi.kt` | CRUD de órdenes del mesero |
| `feacture/administrador/data/datasource/AdminOrdersApi.kt` | Historial de ventas (3 versiones de endpoint con fallback) |
| `feactures/Admin/data/datasource/AdminApi.kt` | Gestión de pizzas (CRUD menú) |
| `feacturecocina/data/datasource/CocineroApi.kt` | Órdenes activas de cocina y cambio de estado |

El módulo de red se configura en `core/di/NetworkModule.kt` con Hilt.

---

## Permisos declarados

```xml
INTERNET                    — Comunicación con el servidor
VIBRATE                     — Notificaciones
POST_NOTIFICATIONS          — Notificaciones push (Android 13+)
FOREGROUND_SERVICE          — Servicio de cocina en background
FOREGROUND_SERVICE_DATA_SYNC — Tipo específico del foreground service
```

---

## Estructura de carpetas

```
app/src/main/java/com/chame/myapplication/
├── core/
│   ├── database/           ← Room: entity, dao, PizzaDatabase
│   ├── di/                 ← Módulos Hilt: Network, Database, Repository
│   ├── navigation/         ← AppNavigation.kt (grafo completo)
│   ├── notifications/      ← PizzaFirebaseMessagingService
│   ├── service/            ← KitchenOrderForegroundService
│   ├── session/            ← SessionManager (token, rol, FCM token)
│   └── websocket/          ← KitchenWebSocketManager, WaiterWebSocketManager
├── feacture/
│   ├── auth/               ← Login (screen, viewModel, useCase, repo)
│   └── administrador/      ← Dashboard admin (ventas, stats, CSV)
├── feactures/
│   └── Admin/              ← Gestión de menú de pizzas (CRUD)
├── feacturecocina/         ← Panel de cocina (órdenes, estados, timer)
├── feature/
│   └── register/           ← Registro de usuarios
├── features/
│   └── pizzeriadistrito/   ← Flujo mesero (menú, orden, historial, perfil)
└── ui/theme/               ← Colores, tipografía, tema Material 3
```

---

## Flujo general de la aplicación

```
MainActivity
    └── AppNavigation
            ├── LoginScreen
            │     └── [MESERO]   → WaiterWebSocket.connect() → PizzaMenuScreen
            │     └── [COCINERO] → CocineroScreen (+ ForegroundService iniciado)
            │     └── [ADMIN]    → AdminDashboardScreen
            │
            ├── [MESERO] PizzaMenuScreen → OrderScreen → HistoryScreen → WaiterProfileScreen
            ├── [COCINERO] CocineroScreen → CocineroProfileScreen
            └── [ADMIN] AdminDashboardScreen → AdminScreen (gestión de menú)
```
