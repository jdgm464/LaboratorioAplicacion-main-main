# Solución para Error "Tablespace is missing"

## 🔴 Problema
Error: `#1030 - Error 194 "Tablespace is missing for a table" desde el manejador de la tabla InnoDB`

Este error significa que las tablas existen en el catálogo de MySQL pero los archivos físicos (tablespace) están corruptos o faltan.

## 📋 Pasos para Solucionar

### PASO 1: Diagnosticar el Problema
1. Abre phpMyAdmin
2. Selecciona la base de datos `laboratorio_db`
3. Ve a la pestaña "SQL"
4. Ejecuta el script: `diagnosticar_tablas.sql`
5. Revisa qué tablas tienen problemas

### PASO 2: Intentar Reparar
1. En la pestaña "SQL" de phpMyAdmin
2. Ejecuta el script: `reparar_tablas.sql`
3. Revisa los mensajes de resultado

**Si la reparación funciona:**
- ✅ Las tablas deberían funcionar normalmente
- Recarga la página en phpMyAdmin
- Verifica que puedes ver los datos

**Si la reparación NO funciona:**
- Continúa con el PASO 3

### PASO 3: Recrear las Tablas (ÚLTIMO RECURSO)

⚠️ **ADVERTENCIA**: Este paso recreará las tablas. Si los datos están completamente corruptos, se perderán.

**Antes de continuar:**
1. Intenta hacer un backup de los archivos físicos de MySQL si es posible
2. Verifica si tienes algún backup anterior de la base de datos

**Procedimiento:**
1. En la pestaña "SQL" de phpMyAdmin
2. Ejecuta el script: `recrear_tablas_con_datos.sql`
3. Este script:
   - Intentará recuperar datos si es posible
   - Eliminará las tablas corruptas
   - Recreará las tablas con la estructura correcta
   - Intentará restaurar los datos recuperados

### PASO 4: Verificar
1. Recarga la página en phpMyAdmin
2. Verifica que las tablas aparecen correctamente
3. Intenta ver los datos de cada tabla
4. Si los datos se perdieron, tendrás que cargarlos nuevamente

## 🔧 Solución Alternativa: Reiniciar MySQL

Si los scripts anteriores no funcionan, puedes intentar:

1. **Detener MySQL** (desde servicios de Windows o XAMPP/WAMP)
2. **Verificar los archivos de datos** en la carpeta de MySQL
3. **Reiniciar MySQL**
4. **Ejecutar los scripts de reparación nuevamente**

## 📝 Notas Importantes

- **Backup**: Siempre haz backup antes de ejecutar scripts que eliminen tablas
- **Datos**: Si los datos están completamente corruptos, puede que no se puedan recuperar
- **Prevención**: Este error suele ocurrir por:
  - Cierre incorrecto de MySQL
  - Problemas de disco duro
  - Corrupción de archivos

## 🆘 Si Nada Funciona

Si después de todos estos pasos las tablas siguen sin funcionar:

1. Verifica que MySQL esté funcionando correctamente
2. Revisa los logs de error de MySQL
3. Considera restaurar desde un backup anterior
4. Si no tienes backup, tendrás que recrear las tablas y cargar los datos manualmente

