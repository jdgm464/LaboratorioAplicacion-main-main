# 🧪 Instrucciones para Probar la Integración con Base de Datos

## 📋 Pasos para Verificar que Todo Funcione

### 1. Verificar que MySQL esté corriendo

Abre una terminal y verifica que MySQL esté activo:
```bash
# Windows (si MySQL está en el PATH)
mysql --version

# O verifica el servicio
services.msc  # Busca "MySQL" en la lista de servicios
```

### 2. Configurar la Conexión

Edita el archivo `src/main/java/com/mycompany/laboratorioapp/ConexionMySQL.java` y ajusta:
- `USUARIO`: Tu usuario de MySQL (por defecto: "root")
- `PASSWORD`: Tu contraseña de MySQL (por defecto: "")

### 3. Inicializar la Base de Datos

**Opción A: Usando Java (Recomendado)**

Ejecuta la clase `InicializarBaseDatos`:
```bash
# Desde tu IDE, ejecuta:
com.mycompany.laboratorioapp.InicializarBaseDatos

# O desde Maven:
mvn exec:java -Dexec.mainClass="com.mycompany.laboratorioapp.InicializarBaseDatos"
```

Esto creará la base de datos y ejecutará el script SQL automáticamente.

**Opción B: Manualmente**

1. Crea la base de datos:
```sql
CREATE DATABASE laboratorio_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Ejecuta el script SQL:
```bash
mysql -u root -p laboratorio_db < database/schema.sql
```

O desde phpMyAdmin:
- Selecciona `laboratorio_db`
- Ve a "Importar"
- Selecciona `database/schema.sql`
- Haz clic en "Continuar"

### 4. Probar la Integración

Ejecuta la clase de prueba `PruebaBaseDatos`:
```bash
# Desde tu IDE, ejecuta:
com.mycompany.laboratorioapp.PruebaBaseDatos

# O desde Maven:
mvn exec:java -Dexec.mainClass="com.mycompany.laboratorioapp.PruebaBaseDatos"
```

Esta clase probará:
- ✓ Conexión a MySQL
- ✓ Operaciones con Pacientes (insertar, buscar, actualizar)
- ✓ Operaciones con Exámenes (insertar, buscar, actualizar)
- ✓ Operaciones con Usuarios
- ✓ SesionUsuario

### 5. Ejecutar la Aplicación

Una vez que todas las pruebas pasen, ejecuta la aplicación principal:

```bash
# Desde tu IDE, ejecuta:
com.mycompany.laboratorioapp.LaboratorioAplicacion

# O desde Maven:
mvn exec:java -Dexec.mainClass="com.mycompany.laboratorioapp.LaboratorioAplicacion"
```

## 🔍 Verificar que Funciona

### En la Aplicación:

1. **Login**: 
   - Usuario: `admin`
   - Contraseña: `admin123`
   - Rol: `Administrador`

2. **Probar Pacientes**:
   - Ve a "Pacientes" en el menú
   - Crea un nuevo paciente
   - Busca un paciente por cédula
   - Verifica que se guarde en la base de datos

3. **Probar Órdenes**:
   - Ve a "Órdenes" en el menú
   - Crea una nueva orden
   - Verifica que se guarde en la base de datos

4. **Probar Exámenes**:
   - Ve a "Archivos" y carga un archivo de exámenes
   - Verifica que los exámenes se guarden en la base de datos

### Verificar en MySQL:

Abre MySQL y verifica los datos:

```sql
USE laboratorio_db;

-- Ver pacientes
SELECT * FROM pacientes;

-- Ver órdenes
SELECT * FROM ordenes;

-- Ver exámenes
SELECT * FROM examenes;

-- Ver usuarios
SELECT * FROM usuarios;
```

## ⚠️ Solución de Problemas

### Error: "Driver MySQL no encontrado"
```bash
mvn clean install
```

### Error: "Access denied for user"
- Verifica usuario y contraseña en `ConexionMySQL.java`
- Asegúrate de que el usuario tenga permisos

### Error: "Unknown database 'laboratorio_db'"
- Ejecuta `InicializarBaseDatos` para crear la base de datos
- O créala manualmente

### Error: "Table doesn't exist"
- Ejecuta el script `database/schema.sql`
- O ejecuta `InicializarBaseDatos`

### La aplicación no guarda datos
- Verifica que la conexión a MySQL funcione
- Ejecuta `PruebaBaseDatos` para diagnosticar
- Revisa los mensajes de error en la consola

## ✅ Checklist de Verificación

- [ ] MySQL está corriendo
- [ ] Base de datos `laboratorio_db` existe
- [ ] Tablas creadas (usuarios, pacientes, examenes, ordenes, orden_examenes)
- [ ] Usuario `admin` existe en la tabla usuarios
- [ ] `PruebaBaseDatos` pasa todas las pruebas
- [ ] La aplicación inicia correctamente
- [ ] Puedo crear y buscar pacientes
- [ ] Puedo crear órdenes
- [ ] Los datos se guardan en MySQL

## 📝 Notas

- Todos los datos ahora se guardan en MySQL, no en Excel
- La aplicación mantiene compatibilidad con Excel para cargar datos iniciales
- Los cambios se reflejan inmediatamente en la base de datos
- Puedes verificar los datos directamente en MySQL o phpMyAdmin

