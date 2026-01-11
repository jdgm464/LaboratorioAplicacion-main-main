# Diagrama de Relaciones de la Base de Datos

## 📊 Esquema de Relaciones

```
┌─────────────────┐
│    usuarios     │
├─────────────────┤
│ id (PK)         │◄─────┐
│ cedula (UK)     │      │
│ nombres         │      │
│ apellidos       │      │
│ usuario (UK)    │      │
│ password        │      │
│ rol             │      │
│ ...permisos...  │      │
└─────────────────┘      │
         │               │
         │               │
         │ 1:0..1       │ 1:N
         │               │
         ▼               │
┌─────────────────┐      │
│   pacientes     │      │
├─────────────────┤      │
│ id (PK)         │      │
│ cedula (UK)     │      │
│ nombre          │      │
│ apellido        │      │
│ edad            │      │
│ direccion       │      │
│ telefono        │      │
│ correo          │      │
│ usuario_id (FK) │──────┘
└─────────────────┘
         │
         │ 1:N
         │
         ▼
┌─────────────────┐
│    ordenes      │
├─────────────────┤
│ id (PK)         │
│ numero_orden(UK)│
│ paciente_id (FK)│──────┐
│ usuario_id (FK) │──────┼──┐
│ fecha_registro  │      │  │
│ hora_registro   │      │  │
│ total           │      │  │
│ estatus         │      │  │
│ ...datos...     │      │  │
└─────────────────┘      │  │
         │                │  │
         │ N:M            │  │
         │                │  │
         ▼                │  │
┌─────────────────┐       │  │
│ orden_examenes  │       │  │
├─────────────────┤       │  │
│ id (PK)         │       │  │
│ orden_id (FK)   │───────┘  │
│ examen_codigo(FK)         │
│ precio          │         │
└─────────────────┘         │
         │                   │
         │ N:1               │
         │                   │
         ▼                   │
┌─────────────────┐          │
│    examenes    │          │
├─────────────────┤          │
│ id (PK)         │          │
│ codigo (UK)     │          │
│ nombre          │          │
│ precio          │          │
│ activo          │          │
└─────────────────┘          │
                             │
                             │
                    ┌────────┘
                    │
                    │ Referencia: ordenes.usuario_id → usuarios.id
                    │ Referencia: ordenes.paciente_id → pacientes.id
                    │ Referencia: pacientes.usuario_id → usuarios.id
                    │ Referencia: orden_examenes.orden_id → ordenes.id
                    │ Referencia: orden_examenes.examen_codigo → examenes.codigo
```

## 🔑 Claves y Relaciones

### Claves Primarias (PK)
- `usuarios.id`
- `pacientes.id`
- `ordenes.id`
- `examenes.id`
- `orden_examenes.id`

### Claves Foráneas (FK)
- `pacientes.usuario_id` → `usuarios.id` (Opcional, ON DELETE SET NULL)
- `ordenes.paciente_id` → `pacientes.id` (Obligatoria, ON DELETE RESTRICT)
- `ordenes.usuario_id` → `usuarios.id` (Obligatoria, ON DELETE RESTRICT)
- `orden_examenes.orden_id` → `ordenes.id` (Obligatoria, ON DELETE CASCADE)
- `orden_examenes.examen_codigo` → `examenes.codigo` (Obligatoria)

### Restricciones de Integridad

1. **ON DELETE RESTRICT**: 
   - No se puede eliminar un paciente si tiene órdenes
   - No se puede eliminar un usuario si creó órdenes
   - Protege la integridad referencial

2. **ON DELETE SET NULL**:
   - Si se elimina un usuario, el `paciente.usuario_id` se pone en NULL
   - Permite mantener los datos del paciente

3. **ON DELETE CASCADE**:
   - Si se elimina una orden, se eliminan automáticamente sus exámenes asociados
   - Mantiene la consistencia de datos

## 📝 Notas Importantes

- Las relaciones garantizan que:
  - Toda orden tiene un paciente válido
  - Toda orden tiene un usuario creador (auditoría)
  - Los datos están normalizados y sin duplicación
  - Se puede rastrear quién creó cada orden

- Los campos de texto (cedula, nombres, apellidos) en la tabla `ordenes` se mantienen para:
  - Búsquedas rápidas sin JOINs
  - Historial inmutable (si el paciente cambia, la orden mantiene los datos originales)
  - Compatibilidad con código existente

