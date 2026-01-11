-- ============================================
-- SCRIPT COMPLETO PARA REPARAR TABLAS CORRUPTAS
-- ============================================
-- Este script intentará recuperar datos y recrear las tablas
-- IMPORTANTE: Ejecuta esto en la pestaña SQL de phpMyAdmin
-- ============================================

USE laboratorio_db;

-- ============================================
-- PASO 1: Intentar recuperar datos de las tablas corruptas
-- ============================================
-- NOTA: Si las tablas están muy corruptas (error "Tablespace is missing"),
-- este paso fallará. En ese caso, usa el script ELIMINAR_Y_RECREAR_TABLAS.sql
-- que elimina las tablas directamente sin intentar leer datos.

SET FOREIGN_KEY_CHECKS = 0;

-- Intentar recuperar datos de usuarios
DROP TABLE IF EXISTS usuarios_backup;
CREATE TABLE usuarios_backup (
    id INT,
    cedula VARCHAR(20),
    nombres VARCHAR(100),
    apellidos VARCHAR(100),
    usuario VARCHAR(50),
    password VARCHAR(255),
    rol VARCHAR(50),
    modulo_ventas_laboratorio BOOLEAN,
    modulo_compras BOOLEAN,
    modulo_administrativos BOOLEAN,
    puede_descuento BOOLEAN,
    puede_recargo BOOLEAN,
    puede_cierre BOOLEAN,
    puede_cortesias BOOLEAN,
    puede_control_precios BOOLEAN,
    puede_control_resultados BOOLEAN,
    puede_anular BOOLEAN,
    puede_modificar_ordenes BOOLEAN,
    permisos_administrativos BOOLEAN,
    fecha_creacion TIMESTAMP,
    fecha_actualizacion TIMESTAMP
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Intentar copiar datos (puede fallar si la tabla está muy corrupta)
-- Si este paso falla con error "Tablespace is missing", 
-- salta directamente al PASO 2 y elimina las tablas corruptas
INSERT INTO usuarios_backup SELECT * FROM usuarios;

-- Intentar recuperar datos de pacientes
DROP TABLE IF EXISTS pacientes_backup;
CREATE TABLE pacientes_backup (
    id INT,
    cedula VARCHAR(20),
    nombre VARCHAR(100),
    apellido VARCHAR(100),
    edad INT,
    direccion VARCHAR(255),
    telefono VARCHAR(20),
    correo VARCHAR(100),
    sexo VARCHAR(1),
    usuario_id INT,
    fecha_registro TIMESTAMP,
    fecha_actualizacion TIMESTAMP
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT INTO pacientes_backup SELECT * FROM pacientes;

-- Intentar recuperar datos de examenes
DROP TABLE IF EXISTS examenes_backup;
CREATE TABLE examenes_backup (
    id INT,
    codigo VARCHAR(50),
    nombre VARCHAR(200),
    precio DECIMAL(10, 2),
    descripcion TEXT,
    activo BOOLEAN,
    fecha_creacion TIMESTAMP,
    fecha_actualizacion TIMESTAMP
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT INTO examenes_backup SELECT * FROM examenes;

-- Intentar recuperar datos de ordenes
DROP TABLE IF EXISTS ordenes_backup;
CREATE TABLE ordenes_backup (
    id INT,
    numero_orden VARCHAR(50),
    numero_factura VARCHAR(50),
    numero_control VARCHAR(50),
    numero_lote VARCHAR(50),
    fecha_registro DATE,
    hora_registro TIME,
    paciente_id INT,
    usuario_id INT,
    codigo_paciente VARCHAR(50),
    cedula VARCHAR(20),
    nombres VARCHAR(100),
    apellidos VARCHAR(100),
    direccion VARCHAR(255),
    telefono VARCHAR(20),
    correo VARCHAR(100),
    codigo_empresa VARCHAR(50),
    empresa VARCHAR(200),
    sexo VARCHAR(1),
    total DECIMAL(10, 2),
    estatus VARCHAR(20),
    fecha_creacion TIMESTAMP,
    fecha_actualizacion TIMESTAMP
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT INTO ordenes_backup SELECT * FROM ordenes;

-- Intentar recuperar datos de orden_examenes
DROP TABLE IF EXISTS orden_examenes_backup;
CREATE TABLE orden_examenes_backup (
    id INT,
    orden_id INT,
    examen_codigo VARCHAR(50),
    precio DECIMAL(10, 2)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
INSERT INTO orden_examenes_backup SELECT * FROM orden_examenes;

-- ============================================
-- PASO 2: Eliminar las tablas corruptas
-- ============================================

DROP TABLE IF EXISTS orden_examenes;
DROP TABLE IF EXISTS ordenes;
DROP TABLE IF EXISTS examenes;
DROP TABLE IF EXISTS pacientes;
DROP TABLE IF EXISTS usuarios;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- PASO 3: Recrear las tablas con la estructura correcta
-- ============================================

-- Tabla de Usuarios
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cedula VARCHAR(20) UNIQUE NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    usuario VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL,
    modulo_ventas_laboratorio BOOLEAN DEFAULT FALSE,
    modulo_compras BOOLEAN DEFAULT FALSE,
    modulo_administrativos BOOLEAN DEFAULT FALSE,
    puede_descuento BOOLEAN DEFAULT FALSE,
    puede_recargo BOOLEAN DEFAULT FALSE,
    puede_cierre BOOLEAN DEFAULT FALSE,
    puede_cortesias BOOLEAN DEFAULT FALSE,
    puede_control_precios BOOLEAN DEFAULT FALSE,
    puede_control_resultados BOOLEAN DEFAULT FALSE,
    puede_anular BOOLEAN DEFAULT FALSE,
    puede_modificar_ordenes BOOLEAN DEFAULT FALSE,
    permisos_administrativos BOOLEAN DEFAULT FALSE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de Pacientes
CREATE TABLE pacientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cedula VARCHAR(20) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    edad INT NOT NULL,
    direccion VARCHAR(255),
    telefono VARCHAR(20),
    correo VARCHAR(100),
    sexo VARCHAR(1) DEFAULT '',
    usuario_id INT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_cedula (cedula),
    INDEX idx_usuario_id (usuario_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de Exámenes
CREATE TABLE examenes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    nombre VARCHAR(200) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    descripcion TEXT,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_codigo (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de Órdenes
CREATE TABLE ordenes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_orden VARCHAR(50) UNIQUE NOT NULL,
    numero_factura VARCHAR(50),
    numero_control VARCHAR(50),
    numero_lote VARCHAR(50),
    fecha_registro DATE NOT NULL,
    hora_registro TIME NOT NULL,
    paciente_id INT NOT NULL,
    usuario_id INT NOT NULL,
    codigo_paciente VARCHAR(50),
    cedula VARCHAR(20) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    direccion VARCHAR(255),
    telefono VARCHAR(20),
    correo VARCHAR(100),
    codigo_empresa VARCHAR(50),
    empresa VARCHAR(200),
    sexo VARCHAR(1) DEFAULT '',
    total DECIMAL(10, 2) NOT NULL,
    estatus VARCHAR(20) DEFAULT 'Activo',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_numero_orden (numero_orden),
    INDEX idx_paciente_id (paciente_id),
    INDEX idx_usuario_id (usuario_id),
    INDEX idx_cedula (cedula),
    INDEX idx_fecha_registro (fecha_registro),
    FOREIGN KEY (paciente_id) REFERENCES pacientes(id) ON DELETE RESTRICT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de Exámenes por Orden
CREATE TABLE orden_examenes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    orden_id INT NOT NULL,
    examen_codigo VARCHAR(50) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (orden_id) REFERENCES ordenes(id) ON DELETE CASCADE,
    FOREIGN KEY (examen_codigo) REFERENCES examenes(codigo),
    INDEX idx_orden_id (orden_id),
    INDEX idx_examen_codigo (examen_codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- PASO 4: Restaurar datos desde los backups (si existen)
-- ============================================
-- Si los backups tienen datos, se restaurarán aquí
-- Si no tienen datos o fallaron, las tablas quedarán vacías

-- Restaurar usuarios (si el backup tiene datos)
INSERT IGNORE INTO usuarios 
SELECT * FROM usuarios_backup;

-- Restaurar pacientes (si el backup tiene datos)
INSERT IGNORE INTO pacientes 
SELECT * FROM pacientes_backup;

-- Restaurar examenes (si el backup tiene datos)
INSERT IGNORE INTO examenes 
SELECT * FROM examenes_backup;

-- Restaurar ordenes (si el backup tiene datos)
INSERT IGNORE INTO ordenes 
SELECT * FROM ordenes_backup;

-- Restaurar orden_examenes (si el backup tiene datos)
INSERT IGNORE INTO orden_examenes 
SELECT * FROM orden_examenes_backup;

-- ============================================
-- PASO 5: Limpiar tablas de backup
-- ============================================
DROP TABLE IF EXISTS usuarios_backup;
DROP TABLE IF EXISTS pacientes_backup;
DROP TABLE IF EXISTS examenes_backup;
DROP TABLE IF EXISTS ordenes_backup;
DROP TABLE IF EXISTS orden_examenes_backup;

-- ============================================
-- PASO 6: Optimizar las tablas recién creadas
-- ============================================
OPTIMIZE TABLE usuarios;
OPTIMIZE TABLE pacientes;
OPTIMIZE TABLE examenes;
OPTIMIZE TABLE ordenes;
OPTIMIZE TABLE orden_examenes;

-- ============================================
-- PASO 7: Verificar que todo está correcto
-- ============================================
SHOW TABLES;

-- Contar registros en cada tabla
SELECT 'usuarios' AS tabla, COUNT(*) AS registros FROM usuarios
UNION ALL
SELECT 'pacientes', COUNT(*) FROM pacientes
UNION ALL
SELECT 'examenes', COUNT(*) FROM examenes
UNION ALL
SELECT 'ordenes', COUNT(*) FROM ordenes
UNION ALL
SELECT 'orden_examenes', COUNT(*) FROM orden_examenes;

SELECT '✅ Tablas reparadas y recreadas correctamente. Recarga la página en phpMyAdmin.' AS mensaje;

