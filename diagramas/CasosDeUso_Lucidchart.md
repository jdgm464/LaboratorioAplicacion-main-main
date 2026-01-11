# CASOS DE USO - SISTEMA DE LABORATORIO MABEROCA

## ACTORES
- **Administrador**: Usuario con permisos completos del sistema
- **Bioanalista**: Usuario que gestiona exámenes y resultados
- **Usuario General**: Usuario con permisos limitados
- **Sistema**: Sistema de gestión de laboratorio

---

## MÓDULO: USUARIOS

### CU-001: Iniciar Sesión
- **Actor**: Administrador, Bioanalista, Usuario General
- **Descripción**: El usuario ingresa sus credenciales para acceder al sistema
- **Precondiciones**: El usuario debe estar registrado en el sistema
- **Flujo Principal**:
  1. Usuario ingresa nombre de usuario y contraseña
  2. Usuario selecciona su rol
  3. Sistema valida credenciales
  4. Sistema establece sesión de usuario
  5. Sistema muestra interfaz principal
- **Flujo Alternativo**: Credenciales incorrectas → Sistema muestra mensaje de error
- **Postcondiciones**: Usuario autenticado y sesión activa

### CU-002: Registrar Usuario
- **Actor**: Administrador
- **Descripción**: El administrador crea una nueva cuenta de usuario
- **Precondiciones**: Usuario debe estar autenticado como Administrador
- **Flujo Principal**:
  1. Administrador accede a módulo de usuarios
  2. Administrador selecciona "Nuevo Usuario"
  3. Administrador completa datos del usuario (nombres, apellidos, usuario, contraseña, rol)
  4. Administrador configura permisos y módulos accesibles
  5. Sistema valida datos
  6. Sistema guarda usuario en base de datos
  7. Sistema muestra confirmación
- **Flujo Alternativo**: Datos inválidos → Sistema muestra error de validación
- **Postcondiciones**: Nuevo usuario registrado en el sistema

### CU-003: Modificar Usuario
- **Actor**: Administrador
- **Descripción**: El administrador modifica datos de un usuario existente
- **Precondiciones**: Usuario debe existir en el sistema
- **Flujo Principal**:
  1. Administrador busca usuario
  2. Administrador selecciona usuario a modificar
  3. Administrador modifica datos necesarios
  4. Sistema valida cambios
  5. Sistema actualiza usuario en base de datos
  6. Sistema muestra confirmación
- **Postcondiciones**: Usuario actualizado

### CU-004: Eliminar Usuario
- **Actor**: Administrador
- **Descripción**: El administrador elimina un usuario del sistema
- **Precondiciones**: Usuario debe existir y no tener órdenes asociadas
- **Flujo Principal**:
  1. Administrador busca usuario
  2. Administrador selecciona usuario a eliminar
  3. Sistema solicita confirmación
  4. Administrador confirma eliminación
  5. Sistema elimina usuario de base de datos
  6. Sistema muestra confirmación
- **Flujo Alternativo**: Usuario tiene órdenes asociadas → Sistema muestra error
- **Postcondiciones**: Usuario eliminado del sistema

---

## MÓDULO: PACIENTES

### CU-005: Registrar Paciente
- **Actor**: Administrador, Bioanalista, Usuario General
- **Descripción**: El usuario registra un nuevo paciente en el sistema
- **Precondiciones**: Usuario autenticado
- **Flujo Principal**:
  1. Usuario accede a módulo de pacientes
  2. Usuario selecciona "Nuevo Paciente"
  3. Usuario completa datos (cédula, nombres, apellidos, edad, dirección, teléfono, correo, sexo)
  4. Sistema valida cédula única
  5. Sistema guarda paciente en base de datos
  6. Sistema muestra confirmación
- **Flujo Alternativo**: Cédula duplicada → Sistema muestra error
- **Postcondiciones**: Paciente registrado en el sistema

### CU-006: Buscar Paciente
- **Actor**: Administrador, Bioanalista, Usuario General
- **Descripción**: El usuario busca un paciente por cédula o nombre
- **Precondiciones**: Usuario autenticado
- **Flujo Principal**:
  1. Usuario ingresa criterio de búsqueda (cédula o nombre)
  2. Sistema busca en base de datos
  3. Sistema muestra resultados de búsqueda
  4. Usuario selecciona paciente
- **Flujo Alternativo**: No se encuentra paciente → Sistema muestra mensaje
- **Postcondiciones**: Paciente encontrado y mostrado

### CU-007: Modificar Paciente
- **Actor**: Administrador, Bioanalista
- **Descripción**: El usuario modifica datos de un paciente existente
- **Precondiciones**: Paciente debe existir en el sistema
- **Flujo Principal**:
  1. Usuario busca paciente
  2. Usuario selecciona paciente a modificar
  3. Usuario modifica datos necesarios
  4. Sistema valida cambios
  5. Sistema actualiza paciente en base de datos
  6. Sistema muestra confirmación
- **Postcondiciones**: Paciente actualizado

### CU-008: Eliminar Paciente
- **Actor**: Administrador
- **Descripción**: El administrador elimina un paciente del sistema
- **Precondiciones**: Paciente no debe tener órdenes asociadas
- **Flujo Principal**:
  1. Administrador busca paciente
  2. Administrador selecciona paciente a eliminar
  3. Sistema solicita confirmación
  4. Administrador confirma eliminación
  5. Sistema elimina paciente de base de datos
  6. Sistema muestra confirmación
- **Flujo Alternativo**: Paciente tiene órdenes → Sistema muestra error
- **Postcondiciones**: Paciente eliminado

---

## MÓDULO: ÓRDENES

### CU-009: Crear Orden
- **Actor**: Administrador, Bioanalista, Usuario General
- **Descripción**: El usuario crea una nueva orden de laboratorio
- **Precondiciones**: Usuario autenticado, paciente registrado
- **Flujo Principal**:
  1. Usuario accede a módulo de órdenes
  2. Usuario selecciona "Nueva Orden"
  3. Usuario ingresa o selecciona paciente (por cédula)
  4. Sistema carga datos del paciente automáticamente
  5. Usuario selecciona exámenes a realizar
  6. Sistema calcula total de la orden
  7. Usuario completa datos adicionales (número factura, control, lote)
  8. Usuario guarda orden
  9. Sistema valida datos
  10. Sistema guarda orden en base de datos
  11. Sistema muestra confirmación
- **Flujo Alternativo**: 
  - Paciente no existe → Sistema permite registrar nuevo paciente
  - Datos incompletos → Sistema muestra error de validación
- **Postcondiciones**: Orden creada y guardada

### CU-010: Buscar Orden
- **Actor**: Administrador, Bioanalista, Usuario General
- **Descripción**: El usuario busca una orden por número de orden, cédula o fecha
- **Precondiciones**: Usuario autenticado
- **Flujo Principal**:
  1. Usuario ingresa criterio de búsqueda
  2. Sistema busca en base de datos
  3. Sistema muestra resultados
  4. Usuario selecciona orden
- **Postcondiciones**: Orden encontrada y mostrada

### CU-011: Modificar Orden
- **Actor**: Administrador, Bioanalista (con permiso)
- **Descripción**: El usuario modifica una orden existente
- **Precondiciones**: Orden debe existir y no estar finalizada
- **Flujo Principal**:
  1. Usuario busca orden
  2. Usuario selecciona orden a modificar
  3. Usuario modifica datos necesarios
  4. Sistema valida cambios
  5. Sistema actualiza orden en base de datos
  6. Sistema muestra confirmación
- **Flujo Alternativo**: Orden finalizada → Sistema muestra error
- **Postcondiciones**: Orden actualizada

### CU-012: Anular Orden
- **Actor**: Administrador (con permiso)
- **Descripción**: El administrador anula una orden existente
- **Precondiciones**: Orden debe existir
- **Flujo Principal**:
  1. Administrador busca orden
  2. Administrador selecciona orden a anular
  3. Sistema solicita confirmación
  4. Administrador confirma anulación
  5. Sistema marca orden como anulada
  6. Sistema muestra confirmación
- **Postcondiciones**: Orden anulada

### CU-013: Registrar Paciente desde Orden
- **Actor**: Administrador, Bioanalista, Usuario General
- **Descripción**: El usuario registra un nuevo paciente desde la ventana de creación de orden
- **Precondiciones**: Usuario en proceso de crear orden
- **Flujo Principal**:
  1. Usuario hace clic en "Registrar Paciente" desde VentanaDetallesOrdenes
  2. Sistema abre VentanaRegistroPacientes
  3. Usuario completa datos del paciente
  4. Usuario guarda paciente
  5. Sistema guarda paciente en base de datos
  6. Sistema cierra ventana de registro
  7. Sistema actualiza automáticamente campos en VentanaDetallesOrdenes con datos del paciente
- **Postcondiciones**: Paciente registrado y datos cargados en orden

---

## MÓDULO: EXÁMENES

### CU-014: Gestionar Exámenes
- **Actor**: Administrador, Bioanalista (con permiso)
- **Descripción**: El usuario gestiona el catálogo de exámenes disponibles
- **Precondiciones**: Usuario autenticado con permisos
- **Flujo Principal**:
  1. Usuario accede a módulo de exámenes
  2. Usuario puede crear, modificar o eliminar exámenes
  3. Usuario configura códigos, nombres y precios
  4. Sistema guarda cambios en base de datos
- **Postcondiciones**: Catálogo de exámenes actualizado

### CU-015: Control de Precios
- **Actor**: Administrador (con permiso)
- **Descripción**: El administrador modifica precios de exámenes
- **Precondiciones**: Usuario autenticado como Administrador con permiso
- **Flujo Principal**:
  1. Administrador accede a control de precios
  2. Administrador selecciona examen
  3. Administrador modifica precio
  4. Sistema valida precio
  5. Sistema actualiza precio en base de datos
  6. Sistema muestra confirmación
- **Postcondiciones**: Precio actualizado

---

## MÓDULO: RESULTADOS

### CU-016: Ver Resultados
- **Actor**: Administrador, Bioanalista, Usuario General
- **Descripción**: El usuario visualiza resultados de exámenes de una orden
- **Precondiciones**: Orden debe existir
- **Flujo Principal**:
  1. Usuario accede a módulo de resultados
  2. Sistema muestra lista de órdenes
  3. Usuario selecciona orden
  4. Sistema carga datos de la orden y paciente
  5. Sistema muestra resultados de exámenes
- **Postcondiciones**: Resultados mostrados

### CU-017: Ingresar Resultados
- **Actor**: Bioanalista (con permiso)
- **Descripción**: El bioanalista ingresa los resultados de los exámenes realizados
- **Precondiciones**: Orden debe existir y estar activa
- **Flujo Principal**:
  1. Bioanalista selecciona orden
  2. Sistema muestra formulario de resultados
  3. Bioanalista ingresa valores de cada examen
  4. Sistema valida valores
  5. Bioanalista guarda resultados
  6. Sistema guarda resultados en base de datos
  7. Sistema muestra confirmación
- **Flujo Alternativo**: Valores inválidos → Sistema muestra error
- **Postcondiciones**: Resultados guardados

### CU-018: Finalizar Resultados
- **Actor**: Bioanalista (con permiso)
- **Descripción**: El bioanalista marca los resultados como finalizados
- **Precondiciones**: Resultados deben estar completos
- **Flujo Principal**:
  1. Bioanalista verifica que todos los resultados estén completos
  2. Bioanalista selecciona "Finalizar"
  3. Sistema valida completitud
  4. Sistema marca orden como finalizada
  5. Sistema muestra confirmación
- **Flujo Alternativo**: Resultados incompletos → Sistema muestra error
- **Postcondiciones**: Orden finalizada

### CU-019: Imprimir Resultados
- **Actor**: Administrador, Bioanalista, Usuario General
- **Descripción**: El usuario imprime los resultados de una orden
- **Precondiciones**: Resultados deben estar finalizados
- **Flujo Principal**:
  1. Usuario selecciona orden con resultados finalizados
  2. Usuario selecciona "Imprimir"
  3. Sistema genera documento PDF
  4. Sistema abre visor de PDF
  5. Usuario imprime documento
- **Postcondiciones**: Documento impreso

---

## CASOS DE USO TRANSVERSALES

### CU-020: Exportar Datos
- **Actor**: Administrador
- **Descripción**: El administrador exporta datos a Excel
- **Precondiciones**: Usuario autenticado como Administrador
- **Flujo Principal**:
  1. Administrador selecciona módulo y datos a exportar
  2. Administrador selecciona "Exportar"
  3. Sistema genera archivo Excel
  4. Sistema permite descargar archivo
- **Postcondiciones**: Datos exportados

### CU-021: Generar Reportes
- **Actor**: Administrador
- **Descripción**: El administrador genera reportes del sistema
- **Precondiciones**: Usuario autenticado como Administrador
- **Flujo Principal**:
  1. Administrador accede a módulo de reportes
  2. Administrador selecciona tipo de reporte
  3. Administrador configura parámetros
  4. Sistema genera reporte
  5. Sistema muestra o permite descargar reporte
- **Postcondiciones**: Reporte generado

---

## RELACIONES ENTRE CASOS DE USO

- **CU-009 (Crear Orden)** <<include>> **CU-006 (Buscar Paciente)**
- **CU-009 (Crear Orden)** <<extend>> **CU-005 (Registrar Paciente)**
- **CU-013 (Registrar Paciente desde Orden)** <<extend>> **CU-005 (Registrar Paciente)**
- **CU-017 (Ingresar Resultados)** <<include>> **CU-010 (Buscar Orden)**
- **CU-018 (Finalizar Resultados)** <<include>> **CU-017 (Ingresar Resultados)**

---

## DIAGRAMA DE CASOS DE USO (Resumen)

```
┌─────────────────────────────────────────────────────────────┐
│                    SISTEMA DE LABORATORIO                   │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────────┐                                           │
│  │  USUARIOS    │                                           │
│  │  CU-001      │                                           │
│  │  CU-002      │                                           │
│  │  CU-003      │                                           │
│  │  CU-004      │                                           │
│  └──────────────┘                                           │
│                                                              │
│  ┌──────────────┐                                           │
│  │  PACIENTES   │                                           │
│  │  CU-005      │                                           │
│  │  CU-006      │                                           │
│  │  CU-007      │                                           │
│  │  CU-008      │                                           │
│  └──────────────┘                                           │
│                                                              │
│  ┌──────────────┐                                           │
│  │   ÓRDENES    │                                           │
│  │  CU-009      │                                           │
│  │  CU-010      │                                           │
│  │  CU-011      │                                           │
│  │  CU-012      │                                           │
│  │  CU-013      │                                           │
│  └──────────────┘                                           │
│                                                              │
│  ┌──────────────┐                                           │
│  │  EXÁMENES    │                                           │
│  │  CU-014      │                                           │
│  │  CU-015      │                                           │
│  └──────────────┘                                           │
│                                                              │
│  ┌──────────────┐                                           │
│  │  RESULTADOS  │                                           │
│  │  CU-016      │                                           │
│  │  CU-017      │                                           │
│  │  CU-018      │                                           │
│  │  CU-019      │                                           │
│  └──────────────┘                                           │
│                                                              │
│  ┌──────────────┐                                           │
│  │ TRANSVERSALES│                                           │
│  │  CU-020      │                                           │
│  │  CU-021      │                                           │
│  └──────────────┘                                           │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

