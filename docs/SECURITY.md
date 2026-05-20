# Checklist de Seguridad — RideUnify

> Este documento se revisa al final de cada fase.
> Última revisión: Pendiente (Fase 0 aún no completada)

---

## Principios de seguridad del proyecto

1. **Mínimo privilegio:** Solo pedir los permisos Android estrictamente necesarios
2. **Defensa en profundidad:** No confiar en una sola capa de protección
3. **Datos sensibles siempre encriptados:** En reposo y en tránsito
4. **No loguear datos personales:** Ni en debug — usar IDs ofuscados
5. **Ofuscar el APK:** Dificultar ingeniería inversa por las plataformas target

---

## Checklist por fase

### FASE 0 — Cimientos
- [ ] .gitignore incluye: keystore, local.properties, *.jks, google-services.json
- [ ] Ningún secreto hardcodeado en el código
- [ ] Variables sensibles en local.properties (no versionado)
- [ ] Keystore de firma almacenado fuera del repositorio
- [ ] ProGuard/R8 habilitado para builds de release

### FASE 1 — Accessibility Service + Uber
- [ ] AccessibilityService declara solo los eventos y paquetes necesarios
- [ ] No se almacenan nodos de accesibilidad de otras apps (solo datos parseados)
- [ ] Datos del conductor almacenados con EncryptedSharedPreferences
- [ ] Timber desactivado en builds de release (no logs en producción)
- [ ] Trip.rawData no incluye datos personales del pasajero
- [ ] Permisos del AndroidManifest.xml revisados — solo los necesarios:
  - BIND_ACCESSIBILITY_SERVICE (obligatorio)
  - FOREGROUND_SERVICE (para servicio persistente)
  - INTERNET (para Supabase)
  - No pedir: CAMERA, CONTACTS, LOCATION (a menos que sea para zonas en Fase 3)

### FASE 2 — Multi-plataforma
- [ ] Cada Handler de plataforma aislado (un crash en Uber no expone datos de Cabify)
- [ ] No se mezclan datos entre plataformas sin pasar por el modelo genérico Trip
- [ ] Auto-switch no deja al conductor en estado inconsistente si falla a mitad
- [ ] Timeout en todas las operaciones de accesibilidad (no bloquear si la app target se cuelga)

### FASE 3 — Filtros + Dashboard
- [ ] Datos de ganancias almacenados localmente en Room DB encriptada
- [ ] Si se sincronizan ganancias con Supabase, van encriptadas o tokenizadas
- [ ] Filtros de zona no envían ubicación exacta al backend (solo zona genérica)
- [ ] Validación de inputs en filtros (no aceptar valores negativos, rangos absurdos)

### FASE 4 — Beta + Monetización
- [ ] Supabase Row Level Security (RLS) activado — cada conductor solo ve sus datos
- [ ] Auth con email verification obligatorio
- [ ] Rate limiting en endpoints de Supabase
- [ ] MercadoPago: validar webhooks de pago server-side, no confiar en el cliente
- [ ] HTTPS everywhere (Supabase lo fuerza por defecto)
- [ ] Política de privacidad escrita (requerida para Argentina, Ley 25.326)
- [ ] No almacenar más datos del conductor de los necesarios
- [ ] Plan de respuesta si una plataforma bloquea a usuarios de la app

---

## Amenazas específicas de este proyecto

| Amenaza | Impacto | Mitigación |
|---|---|---|
| Plataformas detectan nuestra app por package name | Bloquean conductores | Ofuscar package name, distribuir vía web, no Play Store |
| Plataformas detectan Accessibility Service activo | Bloquean conductores | No declarar más paquetes de los necesarios en config |
| Ingeniería inversa del APK | Copian la lógica de parseo | ProGuard/R8 agresivo, lógica crítica ofuscada |
| Leak de tokens de sesión del conductor | Acceso no autorizado a cuentas | EncryptedSharedPreferences, nunca en logs, borrar al desloguearse |
| Alguien intercepta comunicación con Supabase | Roba datos de conductores | HTTPS obligatorio (Supabase default), certificate pinning en futuro |
| App crashea durante un viaje activo | Conductor pierde viaje o queda en estado inconsistente | Guardar estado en disco, recuperar al reiniciar |

---

## Permisos Android requeridos (por fase)

| Permiso | Fase | Justificación |
|---|---|---|
| `BIND_ACCESSIBILITY_SERVICE` | 1 | Core: leer y actuar en otras apps |
| `FOREGROUND_SERVICE` | 1 | Mantener servicio activo |
| `FOREGROUND_SERVICE_SPECIAL_USE` | 1 | Requerido en Android 14+ para accessibility foreground services |
| `INTERNET` | 1 | Comunicación con Supabase |
| `POST_NOTIFICATIONS` | 1 | Notificaciones de viaje entrante |
| `ACCESS_FINE_LOCATION` | 3 | Filtros por zona geográfica (solo si se implementa) |
| `RECEIVE_BOOT_COMPLETED` | 2 | Reiniciar servicio después de reboot del dispositivo |
