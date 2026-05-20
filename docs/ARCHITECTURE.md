# Arquitectura Técnica — RideUnify

> Última actualización: 2026-05-19

## Diagrama de alto nivel

```
┌─────────────────────────────────────────────────┐
│                   UI LAYER                       │
│           Jetpack Compose + Material 3           │
│  ┌──────┐ ┌───────┐ ┌─────────┐ ┌──────────┐   │
│  │ Home │ │ Trips │ │ Filters │ │Dashboard │   │
│  └──┬───┘ └───┬───┘ └────┬────┘ └────┬─────┘   │
│     └─────────┴──────────┴───────────┘          │
│                    ViewModels                    │
├─────────────────────────────────────────────────┤
│                 DOMAIN LAYER                     │
│              (Kotlin puro, sin Android)          │
│  ┌────────────┐ ┌─────────────┐ ┌────────────┐ │
│  │ Trip Model │ │  Use Cases  │ │  Interfaces │ │
│  │ Filter     │ │ AcceptTrip  │ │ TripRepo    │ │
│  │ Platform   │ │ DeclineTrip │ │ PlatformRepo│ │
│  │ Driver     │ │ ApplyFilter │ │ FilterRepo  │ │
│  └────────────┘ └─────────────┘ └────────────┘ │
├─────────────────────────────────────────────────┤
│                  DATA LAYER                      │
│  ┌─────────────────┐  ┌───────────────────────┐ │
│  │  Local Storage   │  │    Remote (Supabase)  │ │
│  │  EncryptedPrefs  │  │    Auth + PostgreSQL  │ │
│  │  Room DB (trips) │  │    Realtime updates   │ │
│  └─────────────────┘  └───────────────────────┘ │
├─────────────────────────────────────────────────┤
│            ACCESSIBILITY LAYER                   │
│  ┌──────────────────────────────────────┐       │
│  │    RideUnifyAccessibilityService     │       │
│  │    (Android Service, siempre activo) │       │
│  └──────────┬───────────────────────────┘       │
│             │ dispatch por packageName           │
│  ┌──────────┴──────────────────────────┐        │
│  │  ┌───────┐  ┌────────┐  ┌───────┐  │        │
│  │  │ Uber  │  │ Cabify │  │  DiDi │  │        │
│  │  │Handler│  │Handler │  │Handler│  │        │
│  │  └───┬───┘  └───┬────┘  └───┬───┘  │        │
│  │      │          │           │       │        │
│  │  ┌───┴───┐  ┌───┴────┐  ┌──┴────┐  │        │
│  │  │ Uber  │  │ Cabify │  │ DiDi  │  │        │
│  │  │Parser │  │Parser  │  │Parser │  │        │
│  │  └───────┘  └────────┘  └───────┘  │        │
│  └─────────────────────────────────────┘        │
└─────────────────────────────────────────────────┘
```

## Cómo funciona el Accessibility Service

### Flujo de un viaje entrante
```
1. Uber muestra solicitud de viaje
2. Android dispara AccessibilityEvent (TYPE_WINDOW_CONTENT_CHANGED)
3. Nuestro AccessibilityService recibe el evento
4. Identifica que el paquete es com.ubercab.driver
5. Despacha al UberHandler
6. UberHandler navega el árbol de nodos de accesibilidad
7. UberParser extrae: destino, tarifa, distancia, rating, tiempo
8. Crea un objeto Trip y lo emite via Flow
9. TripViewModel recibe el Trip
10. UI muestra la card de viaje con countdown
11. Si conductor acepta → UberHandler.acceptTrip()
    → Encuentra el nodo del botón "Aceptar" en Uber
    → Ejecuta AccessibilityNodeInfo.performAction(ACTION_CLICK)
12. AutoSwitchUseCase → pone offline en Cabify y DiDi
```

### Package names de las apps target
| App | Package name | Notas |
|---|---|---|
| Uber Driver | `com.ubercab.driver` | Verificar en Argentina |
| Cabify Driver | `com.cabify.driver` | Verificar en Argentina |
| DiDi Driver | `com.xiaojukeji.didi.driver` | Verificar nombre exacto AR |

> IMPORTANTE: Estos package names deben verificarse en un dispositivo real con las apps
> instaladas en Argentina. Pueden diferir de otros mercados.

## Modelos de dominio clave

### Trip
```kotlin
data class Trip(
    val id: String,                    // UUID generado por nosotros
    val platform: Platform,            // UBER, CABIFY, DIDI
    val pickupAddress: String?,
    val destinationAddress: String?,
    val estimatedFare: Money?,
    val estimatedDistance: Distance?,
    val estimatedPickupTime: Duration?,
    val passengerRating: Float?,
    val surgeMultiplier: Float?,
    val isLongTrip: Boolean,
    val rawData: Map<String, String>,  // Datos crudos para debug
    val receivedAt: Instant,
    val expiresAt: Instant?,
    val status: TripStatus
)

enum class TripStatus { PENDING, ACCEPTED, DECLINED, EXPIRED, COMPLETED, CANCELLED }
enum class Platform { UBER, CABIFY, DIDI }
```

### Filter
```kotlin
data class TripFilter(
    val minFare: Money?,
    val maxPickupDistance: Distance?,
    val maxPickupTime: Duration?,
    val minPassengerRating: Float?,
    val minSurgeMultiplier: Float?,
    val longTripPolicy: LongTripPolicy,
    val preferredZones: List<GeoZone>,
    val platform: Platform?            // null = aplica a todas
)

enum class LongTripPolicy { ACCEPT, DECLINE, ONLY_LONG }
```

## Dependencias principales
| Librería | Propósito |
|---|---|
| Jetpack Compose + Material 3 | UI |
| Hilt | Inyección de dependencias |
| Kotlin Coroutines + Flow | Async y streams reactivos |
| Room | Base de datos local (historial de viajes) |
| Supabase Kotlin SDK | Backend: auth, database, realtime |
| EncryptedSharedPreferences | Almacenamiento seguro de tokens/config |
| Timber | Logging (se desactiva en release) |
| JUnit 5 + MockK | Testing |

## Esquema de base de datos (Supabase)

### Tabla: drivers
| Columna | Tipo | Notas |
|---|---|---|
| id | uuid | PK, generado por Supabase Auth |
| email | text | Del registro |
| display_name | text | |
| created_at | timestamptz | |
| subscription_status | text | free, active, expired |
| subscription_expires_at | timestamptz | |
| referral_code | text | Único, para sistema de referidos |

### Tabla: trips (historial)
| Columna | Tipo | Notas |
|---|---|---|
| id | uuid | PK |
| driver_id | uuid | FK → drivers |
| platform | text | uber, cabify, didi |
| pickup_address | text | |
| destination_address | text | |
| fare | numeric | Monto real cobrado |
| distance_km | numeric | |
| duration_minutes | integer | |
| status | text | completed, cancelled |
| started_at | timestamptz | |
| completed_at | timestamptz | |

### Tabla: filter_configs
| Columna | Tipo | Notas |
|---|---|---|
| id | uuid | PK |
| driver_id | uuid | FK → drivers |
| platform | text | null = global |
| config_json | jsonb | Filtros serializados |
| updated_at | timestamptz | |
