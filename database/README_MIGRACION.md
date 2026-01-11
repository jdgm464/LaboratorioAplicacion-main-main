# Scripts de Migración y Reparación de Base de Datos

Este directorio contiene scripts SQL para corregir problemas de estructura en la base de datos, especialmente cuando la carpeta `data` de MySQL se ha dañado y se necesita recrear o reparar las tablas.

## Problema Común

Cuando la carpeta `data` de MySQL se daña y se recrea la base de datos, es posible que:
- Las tablas se creen con una estructura incompleta
- Faltan columnas necesarias (como `cedula` en la tabla `usuarios`)
- Los índices únicos no se crean correctamente

Esto causa errores como:
- `Error al registrar el usuario en la base de datos`
- `La columna 'cedula' en field list es desconocida` en phpMyAdmin

## Soluciones

### Opción 1: Migración Segura (Recomendada)

**Archivo:** `migrate_fix_usuarios_table.sql`

Este script verifica qué columnas faltan y las agrega sin perder datos existentes.

**Cómo usar:**
1. Abre phpMyAdmin
2. Selecciona la base de datos `laboratorio_db`
3. Ve a la pestaña **SQL**
4. Copia y pega el contenido de `migrate_fix_usuarios_table.sql`
5. Ejecuta el script

**Ventajas:**
- ✅ No elimina datos existentes
- ✅ Solo agrega columnas faltantes
- ✅ Seguro de ejecutar múltiples veces

**Desventajas:**
- Si la tabla está muy corrupta, puede no funcionar

### Opción 2: Recrear Tabla (Si no hay datos importantes)

**Archivo:** `recreate_usuarios_table.sql`

Este script elimina y recrea la tabla `usuarios` desde cero.

**⚠️ ADVERTENCIA:** Este script **ELIMINARÁ TODOS LOS USUARIOS** existentes.

**Cómo usar:**
1. Abre phpMyAdmin
2. Selecciona la base de datos `laboratorio_db`
3. Ve a la pestaña **SQL**
4. Copia y pega el contenido de `recreate_usuarios_table.sql`
5. Ejecuta el script

**Ventajas:**
- ✅ Garantiza una estructura perfecta
- ✅ Crea el usuario administrador por defecto

**Desventajas:**
- ❌ Elimina todos los usuarios existentes
- ❌ Solo usar si no hay datos importantes

### Opción 3: Verificar Estructura

**Archivo:** `check_database_structure.sql`

Este script solo verifica la estructura actual sin hacer cambios.

**Cómo usar:**
1. Abre phpMyAdmin
2. Selecciona la base de datos `laboratorio_db`
3. Ve a la pestaña **SQL**
4. Copia y pega el contenido de `check_database_structure.sql`
5. Ejecuta el script

**Resultado:**
- Muestra todas las columnas existentes
- Indica qué columnas faltan
- Muestra el estado de los índices

## Pasos Recomendados

### Si tienes usuarios importantes en la base de datos:

1. **Primero:** Ejecuta `check_database_structure.sql` para ver qué falta
2. **Segundo:** Ejecuta `migrate_fix_usuarios_table.sql` para agregar columnas faltantes
3. **Tercero:** Verifica que el registro de usuarios funcione

### Si NO tienes usuarios importantes o la tabla está vacía:

1. **Ejecuta:** `recreate_usuarios_table.sql` para recrear la tabla completa
2. **Verifica:** Que puedas iniciar sesión con `admin` / `admin123`

## Estructura Esperada de la Tabla `usuarios`

La tabla `usuarios` debe tener las siguientes columnas:

1. `id` (INT, PRIMARY KEY, AUTO_INCREMENT)
2. `cedula` (VARCHAR(20), UNIQUE, NOT NULL)
3. `nombres` (VARCHAR(100), NOT NULL)
4. `apellidos` (VARCHAR(100), NOT NULL)
5. `usuario` (VARCHAR(50), UNIQUE, NOT NULL)
6. `password` (VARCHAR(255), NOT NULL)
7. `rol` (VARCHAR(50), NOT NULL)
8. `modulo_ventas_laboratorio` (BOOLEAN, DEFAULT FALSE)
9. `modulo_compras` (BOOLEAN, DEFAULT FALSE)
10. `modulo_administrativos` (BOOLEAN, DEFAULT FALSE)
11. `puede_descuento` (BOOLEAN, DEFAULT FALSE)
12. `puede_recargo` (BOOLEAN, DEFAULT FALSE)
13. `puede_cierre` (BOOLEAN, DEFAULT FALSE)
14. `puede_cortesias` (BOOLEAN, DEFAULT FALSE)
15. `puede_control_precios` (BOOLEAN, DEFAULT FALSE)
16. `puede_control_resultados` (BOOLEAN, DEFAULT FALSE)
17. `puede_anular` (BOOLEAN, DEFAULT FALSE)
18. `puede_modificar_ordenes` (BOOLEAN, DEFAULT FALSE)
19. `permisos_administrativos` (BOOLEAN, DEFAULT FALSE)
20. `fecha_creacion` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP)
21. `fecha_actualizacion` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)

**Total: 21 columnas**

## Solución Rápida

Si solo necesitas que funcione rápido y no te importa perder datos:

```sql
USE laboratorio_db;
DROP TABLE IF EXISTS usuarios;
SOURCE database/schema.sql;
```

Luego ejecuta solo la parte de creación de la tabla `usuarios` del archivo `schema.sql`.

## Verificación Post-Migración

Después de ejecutar cualquier script de migración:

1. Abre la aplicación Java
2. Intenta registrar un nuevo usuario
3. Si funciona, la migración fue exitosa
4. Si sigue fallando, ejecuta `check_database_structure.sql` para ver qué falta

## Contacto y Soporte

Si después de ejecutar estos scripts el problema persiste:
1. Ejecuta `check_database_structure.sql` y guarda los resultados
2. Revisa los logs de error en la consola de Java
3. Verifica que la conexión a MySQL esté funcionando correctamente
