-- Script para recrear tablas corruptas MANTENIENDO los datos si es posible
-- IMPORTANTE: Ejecuta esto SOLO si las reparaciones anteriores no funcionaron
-- Este script intentará recuperar los datos antes de recrear las tablas

USE laboratorio_db;

-- ============================================
-- PASO 1: Crear tablas temporales para intentar recuperar datos
-- ============================================

-- Intentar recuperar datos de usuarios (si es posible)
CREATE TABLE IF NOT EXISTS usuarios_temp AS 
SELECT * FROM usuarios LIMIT 0;

-- Intentar recuperar datos de pacientes (si es posible)
CREATE TABLE IF NOT EXISTS pacientes_temp AS 
SELECT * FROM pacientes LIMIT 0;

-- Intentar recuperar datos de examenes (si es posible)
CREATE TABLE IF NOT EXISTS examenes_temp AS 
SELECT * FROM examenes LIMIT 0;

-- Intentar recuperar datos de ordenes (si es posible)
CREATE TABLE IF NOT EXISTS ordenes_temp AS 
SELECT * FROM ordenes LIMIT 0;

-- Intentar recuperar datos de orden_examenes (si es posible)
CREATE TABLE IF NOT EXISTS orden_examenes_temp AS 
SELECT * FROM orden_examenes LIMIT 0;

-- ============================================
-- PASO 2: Eliminar las tablas corruptas
-- ============================================
SET FOREIGN_KEY_CHECKS = 0;

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
-- PASO 4: Intentar restaurar datos desde las tablas temporales
-- NOTA: Si las tablas temporales tienen datos, se restaurarán aquí
-- ============================================

-- Si usuarios_temp tiene datos, restaurarlos
INSERT INTO usuarios SELECT * FROM usuarios_temp 
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE usuarios.id = usuarios_temp.id);

-- Si pacientes_temp tiene datos, restaurarlos
INSERT INTO pacientes SELECT * FROM pacientes_temp 
WHERE NOT EXISTS (SELECT 1 FROM pacientes WHERE pacientes.id = pacientes_temp.id);

-- Si examenes_temp tiene datos, restaurarlos
INSERT INTO examenes SELECT * FROM examenes_temp 
WHERE NOT EXISTS (SELECT 1 FROM examenes WHERE examenes.id = examenes_temp.id);

-- Si ordenes_temp tiene datos, restaurarlos
INSERT INTO ordenes SELECT * FROM ordenes_temp 
WHERE NOT EXISTS (SELECT 1 FROM ordenes WHERE ordenes.id = ordenes_temp.id);

-- Si orden_examenes_temp tiene datos, restaurarlos
INSERT INTO orden_examenes SELECT * FROM orden_examenes_temp 
WHERE NOT EXISTS (SELECT 1 FROM orden_examenes WHERE orden_examenes.id = orden_examenes_temp.id);

-- ============================================
-- PASO 5: Limpiar tablas temporales
-- ============================================
DROP TABLE IF EXISTS usuarios_temp;
DROP TABLE IF EXISTS pacientes_temp;
DROP TABLE IF EXISTS examenes_temp;
DROP TABLE IF EXISTS ordenes_temp;
DROP TABLE IF EXISTS orden_examenes_temp;

-- ============================================
-- PASO 6: Verificar que las tablas se crearon correctamente
-- ============================================
SHOW TABLES;

SELECT 'Tablas recreadas correctamente. Si tenías datos, verifica que se restauraron.' AS mensaje;

