# Plan de Proyecto — RideUnify

> Última actualización: 2026-05-19

## Resumen
App Android que unifica viajes de Uber, Cabify y DiDi en una sola interfaz para conductores en Argentina.
Usa Android Accessibility Services para leer pantallas y aceptar viajes programáticamente.

---

## FASE 0: Cimientos (Semana 1-2)
**Objetivo:** Proyecto configurado profesionalmente antes de escribir una línea de producto.

- [ ] Inicializar repositorio Git con .gitignore para Android/Kotlin
- [ ] Crear proyecto Android Studio con Kotlin + Jetpack Compose
- [ ] Implementar estructura de carpetas Clean Architecture
- [ ] Configurar Hilt para inyección de dependencias
- [ ] Configurar Supabase: proyecto, auth, esquema de DB inicial
- [ ] App "Hello World" que compila y corre en emulador
- [ ] Configurar firma del APK (keystore)
- [ ] Configurar ProGuard/R8 básico
- [ ] Primer commit con estructura base

**Entregable:** Proyecto que compila con estructura profesional.

---

## FASE 1: Accessibility Service + Uber (Semana 3-8)
**Objetivo:** Demostrar que el concepto funciona leyendo y aceptando viajes de Uber.

### 1A — Accessibility Service básico (Sem 3-4)
- [ ] Implementar AccessibilityService que detecta apps en primer plano
- [ ] Configurar accessibility-service-config.xml con las apps target
- [ ] Detectar cuándo Uber muestra una solicitud de viaje
- [ ] Extraer nodos de texto de la pantalla de solicitud
- [ ] Logear datos extraídos en modo debug

### 1B — Parser de Uber (Sem 5-6)
- [ ] Crear modelo Trip (destino, tarifa estimada, distancia, tiempo pickup, rating pasajero)
- [ ] Implementar UberParser: convierte nodos de accesibilidad en Trip
- [ ] Manejar diferentes estados de pantalla de Uber (viaje normal, viaje largo, tarifa dinámica)
- [ ] Tests unitarios con nodos simulados basados en screenshots reales
- [ ] Sistema de detección de cambios de UI (si el parser falla X veces, alertar)

### 1C — UI propia (Sem 6-8)
- [ ] Pantalla principal: estado del conductor (online/offline por plataforma)
- [ ] Card de viaje entrante con datos clave
- [ ] Botón aceptar: navega a Uber y toca "aceptar" programáticamente
- [ ] Botón rechazar: toca "rechazar" en Uber
- [ ] Countdown timer (5 segundos configurable)
- [ ] Notificación persistente del servicio de accesibilidad
- [ ] Pantalla de configuración básica

### 1D — Seguridad Fase 1
- [ ] Auditoría de permisos (solo los estrictamente necesarios)
- [ ] Verificar que no se loguean datos sensibles
- [ ] EncryptedSharedPreferences para datos locales
- [ ] /security-review completa

**Entregable:** App que lee viajes de Uber y permite aceptarlos desde nuestra interfaz.

---

## FASE 2: Multi-plataforma (Semana 9-14)
**Objetivo:** Agregar Cabify y DiDi con arquitectura modular.

### 2A — Cabify (Sem 9-11)
- [ ] Analizar UI de Cabify Argentina (screenshots de referencia)
- [ ] Implementar CabifyParser
- [ ] Aceptar/rechazar viajes de Cabify desde nuestra UI
- [ ] Tests unitarios del parser
- [ ] Integrar con el dispatcher central de accesibilidad

### 2B — DiDi (Sem 12-13)
- [ ] Analizar UI de DiDi Argentina
- [ ] Implementar DiDiParser
- [ ] Aceptar/rechazar viajes de DiDi desde nuestra UI
- [ ] Tests unitarios del parser

### 2C — Auto-Switch (Sem 14)
- [ ] Al aceptar viaje en plataforma X, poner offline en las demás
- [ ] Detectar cuándo un viaje se completa (volver a pantalla principal de la app target)
- [ ] Al completar viaje, volver online en todas las plataformas configuradas
- [ ] Manejar edge cases: viaje cancelado, viaje no encontrado, timeout

**Entregable:** App funcional con Uber + Cabify + DiDi.

---

## FASE 3: Filtros + Dashboard (Semana 15-20)
**Objetivo:** Convertir la app de "funcional" a "útil".

### Filtros inteligentes
- [ ] Configuración de filtros por plataforma y globales
- [ ] Filtro: distancia mínima/máxima de pickup
- [ ] Filtro: tarifa mínima
- [ ] Filtro: rating mínimo del pasajero
- [ ] Filtro: zonas preferidas (geofencing)
- [ ] Filtro: viajes largos (aceptar/rechazar/solo)
- [ ] Auto-accept cuando todos los filtros pasan
- [ ] Auto-decline con notificación del motivo

### Dashboard de ganancias
- [ ] Vista de ganancias del día (por plataforma y total)
- [ ] Vista semanal y mensual
- [ ] Historial de viajes unificado
- [ ] Estadísticas: viajes/hora, $/hora, mejor plataforma por franja horaria
- [ ] Gráficos básicos (Compose Charts o Vico)

**Entregable:** Producto con valor real, listo para beta con conductores.

---

## FASE 4: Beta + Monetización (Semana 21-26)
**Objetivo:** Validar con usuarios reales y activar el negocio.

### Beta cerrada
- [ ] Onboarding flow con tutorial de permisos de accesibilidad
- [ ] Sistema de cuentas con Supabase Auth (email + Google)
- [ ] Reclutar 20-50 conductores en AMBA para beta
- [ ] Sistema de feedback in-app
- [ ] Analytics con PostHog (retención, features más usadas, crashs)
- [ ] Iterar basado en feedback real

### Monetización
- [ ] Definir pricing para Argentina (considerar poder adquisitivo)
- [ ] Integrar MercadoPago SDK para suscripciones
- [ ] Modelo freemium: gratis con 1 plataforma, pago para multi-plataforma + filtros
- [ ] Landing page para captación (dominio propio)
- [ ] Sistema de referidos (conductor invita conductor, ambos reciben mes gratis)

**Entregable:** App en producción con ingresos activos.

---

## Métricas de éxito por fase
| Fase | Métrica |
|---|---|
| 0 | Proyecto compila y estructura está lista |
| 1 | Lectura exitosa de >90% de viajes de Uber en testing manual |
| 2 | Las 3 plataformas funcionan y el auto-switch es confiable |
| 3 | Un conductor de prueba la usa un día completo sin problemas |
| 4 | 20+ conductores activos diarios, retención semanal >50% |
