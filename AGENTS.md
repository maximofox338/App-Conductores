# RideUnify — Contexto del Proyecto

## Qué es este proyecto
App Android nativa para conductores de plataformas de transporte en Argentina (Uber, Cabify, DiDi).
Unifica solicitudes de viaje de todas las plataformas en una sola interfaz usando Android Accessibility Services.
Modelo inspirado en Mystro (EE.UU.) adaptado al mercado argentino.

## Estado actual
- **Fase:** 0 — Cimientos (proyecto sin código aún)
- **Última sesión:** 2026-05-19 — Definición de plan, stack y arquitectura

## Stack
- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Arquitectura:** Clean Architecture (presentation / domain / data) con módulos por plataforma
- **Backend:** Supabase (Auth + PostgreSQL + Realtime)
- **Min SDK:** 26 (Android 8.0) — necesario para Accessibility Services modernas
- **Target SDK:** 34 (Android 14)
- **Build:** Gradle con Kotlin DSL

## Estructura del proyecto (target)
```
app/
├── src/main/java/com/rideunify/
│   ├── core/              # DI, utils, constantes
│   ├── domain/            # Modelos y casos de uso (sin dependencias Android)
│   │   ├── model/         # Trip, Driver, Platform, Filter
│   │   └── usecase/       # AcceptTrip, DeclineTrip, TogglePlatform
│   ├── data/              # Repositorios, Supabase client, almacenamiento local
│   ├── platform/          # UN MÓDULO POR PLATAFORMA
│   │   ├── uber/          # UberParser, UberAccessibilityHandler
│   │   ├── cabify/        # CabifyParser, CabifyAccessibilityHandler
│   │   └── didi/          # DiDiParser, DiDiAccessibilityHandler
│   ├── accessibility/     # AccessibilityService central, dispatcher
│   └── ui/                # Screens, ViewModels, Navigation (Compose)
│       ├── home/
│       ├── trips/
│       ├── filters/
│       ├── dashboard/
│       └── settings/
```

## Convenciones de código
- Kotlin idiomático: data classes, sealed classes para estados, coroutines para async
- ViewModels con StateFlow (no LiveData)
- Inyección de dependencias con Hilt
- Nombrar en inglés (código), UI en español (strings.xml)
- Un archivo por clase/interfaz
- Tests junto al código que testean (directorio test/ paralelo)

## Decisiones tomadas y por qué
| Decisión | Razón |
|---|---|
| Android nativo (no React Native/Flutter) | Accessibility Services requiere APIs nativas de Android, frameworks cross-platform no tienen acceso completo |
| Supabase como backend | Free tier generoso, auth listo, no necesitamos mantener servidor propio en fase temprana |
| APK directo (no Play Store inicialmente) | Mystro fue presionado vía Play Store. Distribución propia da control total |
| Un módulo por plataforma | Si Uber cambia su UI y rompe el parser, Cabify y DiDi siguen funcionando |
| Clean Architecture | Separación clara permite testear lógica sin emulador y reemplazar componentes fácilmente |

## Decisiones pendientes
- Nombre final de la app (RideUnify es provisional)
- Estrategia de ofuscación de código (ProGuard vs R8 custom rules)
- Estrategia de distribución de APK (web propia, Firebase App Distribution, etc.)
- Pricing del modelo de suscripción para Argentina

## Errores a evitar
- NO guardar credenciales o tokens en SharedPreferences sin encriptar — usar EncryptedSharedPreferences
- NO logear datos sensibles del conductor en ningún build (ni debug)
- NO hacer el parser dependiente de IDs internos de las apps target — usar texto visible y estructura de layout
- NO acoplar la lógica de UI a una plataforma específica — siempre pasar por el modelo Trip genérico

## Equipo
- **Desarrollador:** Codex (código, arquitectura, seguridad, testing)
- **Producto/Testing/Negocio:** Usuario (decisiones de producto, testing en dispositivo real, beta con conductores, captación)
- Sin financiamiento externo actualmente
