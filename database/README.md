# Configuración de Base de Datos MySQL

## 📋 Requisitos Previos

1. **MySQL instalado y corriendo** en tu sistema
2. **phpMyAdmin** (opcional, pero recomendado para administración)
3. **Maven** configurado en el proyecto

## 🚀 Pasos para Configurar la Base de Datos

### 1. Instalar Dependencias de Maven

Primero, descarga las dependencias del proyecto (incluyendo el driver MySQL):

```bash
mvn clean install
```

### 2. Crear la Base de Datos

Tienes dos opciones:

#### Opción A: Usando phpMyAdmin (Recomendado)

1. Abre phpMyAdmin en tu navegador (normalmente: `http://localhost/phpmyadmin`)
2. Haz clic en "Nueva" o "New" para crear una base de datos
3. Nombre: `laboratorio_db`
4. Intercalación: `utf8mb4_unicode_ci`
5. Haz clic en "Crear"

#### Opción B: Usando Línea de Comandos MySQL

```bash
mysql -u root -p
CREATE DATABASE laboratorio_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;
```

### 3. Ejecutar el Script SQL

#### Opción A: Desde phpMyAdmin

1. Selecciona la base de datos `laboratorio_db`
2. Ve a la pestaña "Importar" o "Import"
3. Selecciona el archivo `database/schema.sql`
4. Haz clic en "Continuar" o "Go"

#### Opción B: Desde Línea de Comandos

```bash
mysql -u root -p laboratorio_db < database/schema.sql
```

#### Opción C: Desde Java (después de configurar la conexión)

```java
ConexionMySQL.ejecutarScriptSQL("database/schema.sql");
```

### 4. Configurar la Conexión en Java

Edita el archivo `src/main/java/com/mycompany/laboratorioapp/ConexionMySQL.java` y ajusta:

```java
private static final String HOST = "localhost";        // Tu servidor MySQL
private static final String PUERTO = "3306";            // Puerto MySQL
private static final String BASE_DATOS = "laboratorio_db"; // Nombre de BD
private static final String USUARIO = "root";            // Tu usuario MySQL
private static final String PASSWORD = "tu_contraseña";  // Tu contraseña
```

### 5. Probar la Conexión

Ejecuta la clase `ConexionMySQL`:

```bash
mvn exec:java -Dexec.mainClass="com.mycompany.laboratorioapp.ConexionMySQL"
```

O desde tu IDE, ejecuta el método `main` de `ConexionMySQL.java`

## 🔗 Relaciones entre Tablas

El esquema de la base de datos incluye las siguientes relaciones:

### Relaciones Implementadas:

1. **Pacientes ↔ Usuarios** (Opcional)
   - Un paciente puede tener una cuenta de usuario
   - `pacientes.usuario_id` → `usuarios.id`
   - Relación: `ON DELETE SET NULL` (si se elimina el usuario, el paciente se mantiene)

2. **Órdenes ↔ Pacientes** (Obligatoria)
   - Cada orden debe estar asociada a un paciente
   - `ordenes.paciente_id` → `pacientes.id`
   - Relación: `ON DELETE RESTRICT` (no se puede eliminar un paciente con órdenes)

3. **Órdenes ↔ Usuarios** (Obligatoria)
   - Cada orden debe tener un usuario que la creó (auditoría)
   - `ordenes.usuario_id` → `usuarios.id`
   - Relación: `ON DELETE RESTRICT` (no se puede eliminar un usuario con órdenes creadas)

4. **Órdenes ↔ Exámenes** (Muchos a Muchos)
   - Una orden puede tener múltiples exámenes
   - Tabla intermedia: `orden_examenes`
   - `orden_examenes.orden_id` → `ordenes.id`
   - `orden_examenes.examen_codigo` → `examenes.codigo`

### Ventajas de las Relaciones:

- ✅ **Integridad de datos**: No se pueden crear órdenes sin paciente o usuario válido
- ✅ **Auditoría**: Se sabe quién creó cada orden
- ✅ **Consultas eficientes**: Se pueden hacer JOINs para obtener información relacionada
- ✅ **Eliminación segura**: Las foreign keys protegen contra eliminaciones accidentales

## 📚 Uso de los DAOs

Ahora puedes usar las clases DAO para trabajar con la base de datos:

### Ejemplo: Pacientes

```java
import com.mycompany.laboratorioapp.dao.PacienteDAO;
import com.mycompany.laboratorioapp.pacientes.Paciente;

// Insertar un paciente
Paciente paciente = new Paciente("12345678", "Juan", "Pérez", 30, 
    "Calle 123", "04121234567", "juan@email.com");
PacienteDAO.insertar(paciente);

// Buscar un paciente
Paciente encontrado = PacienteDAO.buscarPorCedula("12345678");

// Obtener todos los pacientes
List<Paciente> todos = PacienteDAO.obtenerTodos();

// Actualizar
paciente.setTelefono("04241234567");
PacienteDAO.actualizar(paciente);

// Eliminar
PacienteDAO.eliminar("12345678");
```

### Ejemplo: Exámenes

```java
import com.mycompany.laboratorioapp.dao.ExamenDAO;
import com.mycompany.laboratorioapp.examenes.Examen;

// Insertar un examen
Examen examen = new Examen("HEMO001", "Hemograma Completo", 25.50);
ExamenDAO.insertar(examen);

// Buscar por código
Examen encontrado = ExamenDAO.buscarPorCodigo("HEMO001");

// Obtener todos
List<Examen> todos = ExamenDAO.obtenerTodos();
```

### Ejemplo: Usuarios

```java
import com.mycompany.laboratorioapp.dao.UsuarioDAO;
import com.mycompany.laboratorioapp.Usuario;

// Login
Usuario usuario = UsuarioDAO.buscarPorCredenciales("admin", "admin123");
if (usuario != null) {
    System.out.println("Login exitoso: " + usuario.getRol());
}
```

### Ejemplo: Órdenes

```java
import com.mycompany.laboratorioapp.dao.OrdenDAO;
import com.mycompany.laboratorioapp.ordenes.Orden;
import java.util.Arrays;

// Crear una orden (requiere usuarioId del usuario que la crea)
Orden orden = new Orden("ORD001", "FAC001", "CTRL001", "LOTE001",
    "2024-01-15", "10:30", "PAC001", "12345678",
    "Juan", "Pérez", "Calle 123", "04121234567", "juan@email.com",
    "EMP001", "Empresa ABC", 
    Arrays.asList("HEMO001", "BIO001"), 50.00);

// El método insertar ahora requiere el ID del usuario que crea la orden
// Si el paciente no existe, se crea automáticamente
int usuarioId = 1; // ID del usuario logueado
OrdenDAO.insertar(orden, usuarioId);

// Obtener órdenes con información relacionada (usando JOINs)
List<Orden> ordenes = OrdenDAO.obtenerTodasConRelaciones();

// Obtener órdenes de un paciente específico
int pacienteId = 1;
List<Orden> ordenesPaciente = OrdenDAO.obtenerPorPacienteId(pacienteId);

// Obtener órdenes creadas por un usuario
List<Orden> ordenesUsuario = OrdenDAO.obtenerPorUsuarioId(usuarioId);
```

## 🔧 Solución de Problemas

### Error: "Driver MySQL no encontrado"
- Ejecuta: `mvn clean install`
- Verifica que `pom.xml` tenga la dependencia `mysql-connector-j`

### Error: "Access denied for user"
- Verifica usuario y contraseña en `ConexionMySQL.java`
- Asegúrate de que el usuario tenga permisos en la base de datos

### Error: "Unknown database 'laboratorio_db'"
- Crea la base de datos primero (paso 2)
- O ejecuta el script SQL completo

### Error: "Table doesn't exist"
- Ejecuta el script `database/schema.sql` para crear las tablas

## 📝 Notas Importantes

- **phpMyAdmin** es solo una herramienta web para administrar MySQL. No es necesaria para que tu aplicación Java funcione.
- La conexión desde Java se hace directamente con MySQL usando el driver JDBC.
- Todas las clases DAO manejan automáticamente las conexiones y las cierran correctamente.
- Los métodos DAO usan `PreparedStatement` para prevenir inyección SQL.

## 🎯 Próximos Pasos

1. Integra los DAOs en tus ventanas existentes (reemplazando Excel/Properties)
2. Actualiza `BaseDeDatosExcel.java` para usar los DAOs
3. Actualiza `login.java` para usar `UsuarioDAO`
4. Actualiza `OrdenManager.java` para usar `OrdenDAO`

