# RideUnify MVP Implementation

## Estado implementado
- Arquitectura Gradle modular con `app`, `core`, `platform` y `feature`.
- Dominio puro Kotlin para ofertas, decisiones, acciones, parsers y navegación externa.
- Repositorios local-first con estado en memoria y Room preparado para historial.
- Accessibility Service central con snapshots sanitizados y cola de acciones asistidas.
- Parsers iniciales para Uber y Cabify basados en texto visible, sin IDs internos.
- Click asistido por plataforma tras toque explícito del conductor.
- UI Compose con Inicio, Historial y Ajustes.
- Botón post-aceptación para abrir Waze al punto de búsqueda.
- Tests iniciales para filtros, parsers y Waze deep links.

## Alcance que queda fuera del MVP
- Auto-accept/autodecline autónomo.
- Auto-switch entre plataformas.
- Overlay flotante.
- Backend, login, pagos y Supabase.
- Geofencing o permisos de ubicación.

## Setup local
1. Usar JDK incluido en Android Studio o JDK 17+.
2. Abrir el proyecto en Android Studio.
3. Instalar Android SDK Platform 34 y Build-Tools 34.
4. Aceptar licencias del SDK desde Android Studio SDK Manager o con `sdkmanager --licenses`.
5. Compilar `:app:assembleDebug`.

## Verificación realizada
- `:core:domain:test` pasa correctamente.
- `:app:compileDebugKotlin` queda bloqueado en este entorno porque las licencias del Android SDK 34 no están aceptadas y el sandbox no tiene permiso de escritura sobre `C:\Users\arbit\AppData\Local\Android\Sdk\licenses`.
