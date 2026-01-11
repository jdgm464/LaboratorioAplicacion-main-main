# DIAGRAMAS UML PARA LUCIDCHART - SISTEMA DE LABORATORIO MABEROCA

Este directorio contiene los diagramas de secuencia UML y casos de uso del sistema de laboratorio, preparados para importar en Lucidchart.

## Archivos Incluidos

### 1. `CasosDeUso_Lucidchart.md`
   - **Descripción**: Documento completo con 21 casos de uso del sistema
   - **Contenido**:
     - Módulo Usuarios (CU-001 a CU-004)
     - Módulo Pacientes (CU-005 a CU-008)
     - Módulo Órdenes (CU-009 a CU-013)
     - Módulo Exámenes (CU-014 a CU-015)
     - Módulo Resultados (CU-016 a CU-019)
     - Casos de uso transversales (CU-020 a CU-021)
   - **Uso**: Leer y crear diagrama de casos de uso en Lucidchart manualmente

### 2. `DiagramaSecuencia_Lucidchart.puml`
   - **Descripción**: Archivo PlantUML con 6 diagramas de secuencia
   - **Contenido**:
     - Diagrama de Secuencia - Inicio de Sesión
     - Diagrama de Secuencia - Crear Orden Completa
     - Diagrama de Secuencia - Registrar Paciente
     - Diagrama de Secuencia - Ingresar Resultados de Exámenes
     - Diagrama de Secuencia - Gestionar Usuarios (Administrador)
     - Diagrama de Secuencia - Flujo Completo: Orden → Resultados
   - **Uso**: Importar directamente en Lucidchart (soporta PlantUML)

### 3. `DiagramaSecuencia_Texto_Lucidchart.txt`
   - **Descripción**: Versión en texto plano de los diagramas de secuencia
   - **Uso**: Guía para crear los diagramas manualmente en Lucidchart si no puedes importar PlantUML

### 4. `DiagramaActividades_CasosDeUso_Lucidchart.puml`
   - **Descripción**: Archivo PlantUML con 11 diagramas de actividades (flujo) para casos de uso
   - **Contenido**:
     - CU-001: Iniciar Sesión
     - CU-002: Registrar Usuario
     - CU-005: Registrar Paciente
     - CU-006: Buscar Paciente
     - CU-009: Crear Orden
     - CU-011: Modificar Orden
     - CU-012: Anular Orden
     - CU-013: Registrar Paciente desde Orden
     - CU-017: Ingresar Resultados
     - CU-018: Finalizar Resultados
     - Flujo Completo: Crear Orden y Procesar Resultados
   - **Uso**: Importar directamente en Lucidchart (soporta PlantUML)

### 5. `DiagramaActividades_Texto_Lucidchart.txt`
   - **Descripción**: Versión en texto plano de los diagramas de actividades
   - **Uso**: Guía para crear los diagramas manualmente en Lucidchart

## Cómo Importar en Lucidchart

### Opción 1: Importar PlantUML (Recomendado)

**Para Diagramas de Secuencia:**
1. Abre Lucidchart
2. Ve a **File > Import > PlantUML**
3. Selecciona el archivo `DiagramaSecuencia_Lucidchart.puml`
4. Lucidchart generará automáticamente los diagramas

**Para Diagramas de Actividades (Casos de Uso):**
1. Abre Lucidchart
2. Ve a **File > Import > PlantUML**
3. Selecciona el archivo `DiagramaActividades_CasosDeUso_Lucidchart.puml`
4. Lucidchart generará automáticamente los diagramas de flujo

### Opción 2: Crear Manualmente

**Para Diagramas de Secuencia:**
1. Abre Lucidchart y crea un nuevo diagrama
2. Usa `DiagramaSecuencia_Texto_Lucidchart.txt` como guía
3. Sigue estos pasos:
   - Agrega los participantes (actores y objetos) en la parte superior
   - Dibuja las líneas de vida verticales
   - Agrega los mensajes (flechas) entre participantes
   - Agrega activaciones (rectángulos delgados) cuando un objeto está procesando
   - Agrega cuadros de alternativas [Alt: condición] donde corresponda

**Para Diagramas de Actividades:**
1. Abre Lucidchart y crea un nuevo diagrama de flujo
2. Usa `DiagramaActividades_Texto_Lucidchart.txt` como guía
3. Sigue estos pasos:
   - Agrega una elipse para "Inicio"
   - Agrega rectángulos con esquinas redondeadas para actividades
   - Agrega rombos para decisiones (condiciones)
   - Conecta con flechas etiquetadas "Sí" o "No"
   - Agrega una elipse para "Fin"
   - Usa notas para información adicional

## Estructura de los Diagramas

### Diagrama 1: Inicio de Sesión
- **Participantes**: Usuario, login, UsuarioDAO, SesionUsuario, InterfazPrincipal, MySQL
- **Flujo**: Autenticación y establecimiento de sesión

### Diagrama 2: Crear Orden Completa
- **Participantes**: Usuario, InterfazPrincipal, VentanaOrdenes, VentanaDetallesOrdenes, GestorPacientes, PacienteDAO, OrdenManager, OrdenDAO, ExamenDAO, MySQL
- **Flujo**: Creación de orden con búsqueda/registro de paciente y selección de exámenes

### Diagrama 3: Registrar Paciente
- **Participantes**: Usuario, VentanaRegistroPacientes, GestorPacientes, PacienteDAO, MySQL
- **Flujo**: Registro de nuevo paciente y actualización automática de ventana de órdenes

### Diagrama 4: Ingresar Resultados
- **Participantes**: Bioanalista, InterfazPrincipal, VentanaResultados, OrdenDAO, ResultadoDAO, MySQL
- **Flujo**: Ingreso de resultados de exámenes y finalización de orden

### Diagrama 5: Gestionar Usuarios
- **Participantes**: Administrador, InterfazPrincipal, VentanaRegistroUsuarios, UsuarioDAO, MySQL
- **Flujo**: CRUD completo de usuarios

### Diagrama 6: Flujo Completo
- **Participantes**: Usuario, VentanaOrdenes, VentanaDetallesOrdenes, OrdenDAO, VentanaResultados, ResultadoDAO, MySQL
- **Flujo**: Proceso completo desde creación de orden hasta finalización de resultados

## Convenciones de Notación

### Símbolos UML
- **Actor**: Figura de persona (Usuario, Administrador, Bioanalista)
- **Participante**: Rectángulo con nombre de clase/objeto
- **Mensaje Sincrónico**: Flecha sólida con punta llena →
- **Mensaje Asíncrono**: Flecha punteada con punta llena ⇢
- **Retorno**: Flecha punteada con punta vacía ⇠
- **Activación**: Rectángulo delgado en la línea de vida
- **Alternativa**: Cuadro con etiqueta [Alt: condición]
- **Nota**: Cuadro con esquina doblada para comentarios

### Colores Sugeridos
- **Actores**: Azul claro (#E3F2FD)
- **Ventanas UI**: Verde claro (#E8F5E9)
- **DAOs**: Amarillo claro (#FFF9C4)
- **Base de Datos**: Gris (#F5F5F5)
- **Managers/Gestores**: Naranja claro (#FFF3E0)

## Casos de Uso

El archivo `CasosDeUso_Lucidchart.md` contiene la especificación completa de 21 casos de uso organizados por módulos:

### Módulo Usuarios
- CU-001: Iniciar Sesión
- CU-002: Registrar Usuario
- CU-003: Modificar Usuario
- CU-004: Eliminar Usuario

### Módulo Pacientes
- CU-005: Registrar Paciente
- CU-006: Buscar Paciente
- CU-007: Modificar Paciente
- CU-008: Eliminar Paciente

### Módulo Órdenes
- CU-009: Crear Orden
- CU-010: Buscar Orden
- CU-011: Modificar Orden
- CU-012: Anular Orden
- CU-013: Registrar Paciente desde Orden

### Módulo Exámenes
- CU-014: Gestionar Exámenes
- CU-015: Control de Precios

### Módulo Resultados
- CU-016: Ver Resultados
- CU-017: Ingresar Resultados
- CU-018: Finalizar Resultados
- CU-019: Imprimir Resultados

### Transversales
- CU-020: Exportar Datos
- CU-021: Generar Reportes

## Relaciones entre Casos de Uso

- **CU-009** <<include>> **CU-006** (Crear Orden incluye Buscar Paciente)
- **CU-009** <<extend>> **CU-005** (Crear Orden puede extender a Registrar Paciente)
- **CU-013** <<extend>> **CU-005** (Registrar Paciente desde Orden extiende Registrar Paciente)
- **CU-017** <<include>> **CU-010** (Ingresar Resultados incluye Buscar Orden)
- **CU-018** <<include>> **CU-017** (Finalizar Resultados incluye Ingresar Resultados)

## Notas Importantes

1. **PlantUML**: Si Lucidchart no soporta directamente PlantUML, puedes usar herramientas como:
   - [PlantUML Online](http://www.plantuml.com/plantuml/uml/) para generar imágenes
   - [PlantText](https://www.planttext.com/) para editar y exportar

2. **Actualización**: Estos diagramas reflejan la estructura modular actual del sistema:
   - `usuarios/` - Gestión de usuarios y autenticación
   - `pacientes/` - Gestión de pacientes
   - `ordenes/` - Gestión de órdenes de laboratorio
   - `examenes/` - Catálogo de exámenes
   - `resultados/` - Gestión de resultados

3. **Base de Datos**: Todos los diagramas incluyen interacciones con MySQL a través de los DAOs correspondientes.

## Soporte

Si tienes problemas al importar o crear los diagramas, consulta:
- [Documentación de Lucidchart](https://support.lucidchart.com/)
- [PlantUML Documentation](https://plantuml.com/sequence-diagram)

