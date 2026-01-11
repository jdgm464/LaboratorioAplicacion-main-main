-- ============================================
-- Script SQL completo para crear la base de datos y tablas del Laboratorio
-- Incluye estructura actualizada con migraciones (sexo y fecha_nacimiento)
-- Incluye datos de ejemplo para todas las tablas
-- ============================================
-- Ejecuta este script en phpMyAdmin o desde la línea de comandos de MySQL
-- Este script creará todo desde cero con datos de ejemplo
-- ============================================

-- Crear la base de datos (si no existe)
CREATE DATABASE IF NOT EXISTS laboratorio_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE laboratorio_db;

-- ============================================
-- ELIMINAR TABLAS EXISTENTES (si existen)
-- ============================================
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS orden_examenes;
DROP TABLE IF EXISTS ordenes;
DROP TABLE IF EXISTS examenes;
DROP TABLE IF EXISTS pacientes;
DROP TABLE IF EXISTS usuarios;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- CREAR TABLAS CON ESTRUCTURA ACTUALIZADA
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

-- Tabla de Pacientes (con fecha_nacimiento y sexo)
CREATE TABLE pacientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cedula VARCHAR(20) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    edad INT NOT NULL COMMENT 'Edad calculada desde fecha_nacimiento (se actualiza automáticamente)',
    fecha_nacimiento DATE NULL,
    direccion VARCHAR(255),
    telefono VARCHAR(20),
    correo VARCHAR(100),
    sexo VARCHAR(1) DEFAULT '',  -- M o F
    usuario_id INT NULL,  -- Relación opcional: si el paciente tiene cuenta de usuario
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

-- Tabla de Órdenes (con sexo)
CREATE TABLE ordenes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_orden VARCHAR(50) UNIQUE NOT NULL,
    numero_factura VARCHAR(50),
    numero_control VARCHAR(50),
    numero_lote VARCHAR(50),
    fecha_registro DATE NOT NULL,
    hora_registro TIME NOT NULL,
    paciente_id INT NOT NULL,  -- Foreign Key a pacientes
    usuario_id INT NOT NULL,    -- Foreign Key a usuarios (quien creó la orden)
    codigo_paciente VARCHAR(50),  -- Código interno opcional
    cedula VARCHAR(20) NOT NULL,   -- Se mantiene para búsquedas rápidas
    nombres VARCHAR(100) NOT NULL, -- Se mantiene para historial
    apellidos VARCHAR(100) NOT NULL,
    direccion VARCHAR(255),
    telefono VARCHAR(20),
    correo VARCHAR(100),
    codigo_empresa VARCHAR(50),
    empresa VARCHAR(200),
    sexo VARCHAR(1) DEFAULT '',  -- M o F
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

-- Tabla de Exámenes por Orden (relación muchos a muchos)
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
-- INSERTAR DATOS DE EJEMPLO
-- ============================================

-- Insertar Usuarios
INSERT INTO usuarios (cedula, nombres, apellidos, usuario, password, rol, 
    modulo_ventas_laboratorio, modulo_compras, modulo_administrativos,
    puede_control_precios, permisos_administrativos) VALUES
('00000000', 'Administrador', 'Sistema', 'admin', 'admin123', 'Administrador',
    TRUE, TRUE, TRUE, TRUE, TRUE),
('12345678', 'Juan', 'Pérez', 'jperez', 'password123', 'Técnico',
    TRUE, FALSE, FALSE, FALSE, FALSE),
('87654321', 'María', 'González', 'mgonzalez', 'password123', 'Recepcionista',
    TRUE, FALSE, FALSE, FALSE, FALSE),
('11223344', 'Carlos', 'Rodríguez', 'crodriguez', 'password123', 'Supervisor',
    TRUE, TRUE, TRUE, TRUE, FALSE);

-- Insertar Pacientes (con fecha_nacimiento y sexo)
INSERT INTO pacientes (cedula, nombre, apellido, edad, fecha_nacimiento, direccion, telefono, correo, sexo, usuario_id) VALUES
('10000001', 'Ana', 'Martínez', 35, '1989-05-15', 'Av. Principal 123', '0412-1234567', 'ana.martinez@email.com', 'F', NULL),
('10000002', 'Luis', 'Hernández', 42, '1982-03-20', 'Calle 45 #67-89', '0414-2345678', 'luis.hernandez@email.com', 'M', NULL),
('10000003', 'Carmen', 'López', 28, '1996-11-10', 'Urbanización Los Pinos', '0424-3456789', 'carmen.lopez@email.com', 'F', NULL),
('10000004', 'Roberto', 'Sánchez', 55, '1969-07-25', 'Sector El Centro', '0416-4567890', 'roberto.sanchez@email.com', 'M', NULL),
('10000005', 'Laura', 'Torres', 31, '1993-09-30', 'Residencia Las Flores', '0426-5678901', 'laura.torres@email.com', 'F', NULL);

-- Insertar Exámenes
INSERT INTO examenes (codigo, nombre, precio, descripcion, activo) VALUES
('HEMO001', 'Hemograma Completo', 25.50, 'Análisis completo de células sanguíneas', TRUE),
('GLUC001', 'Glucosa en Ayunas', 8.00, 'Medición de glucosa en sangre en ayunas', TRUE),
('COL001', 'Colesterol Total', 12.00, 'Medición de colesterol total en sangre', TRUE),
('TRIG001', 'Triglicéridos', 10.50, 'Medición de triglicéridos en sangre', TRUE),
('UREA001', 'Urea', 7.50, 'Análisis de urea en sangre', TRUE),
('CREA001', 'Creatinina', 8.50, 'Medición de creatinina en sangre', TRUE),
('TSH001', 'TSH (Hormona Tiroidea)', 15.00, 'Análisis de hormona estimulante de tiroides', TRUE),
('VDRL001', 'VDRL', 12.50, 'Prueba serológica para sífilis', TRUE),
('HIV001', 'Prueba de VIH', 20.00, 'Detección de anticuerpos del VIH', TRUE),
('PCR001', 'PCR (Proteína C Reactiva)', 18.00, 'Medición de proteína C reactiva', TRUE);

-- Insertar Órdenes (con sexo)
INSERT INTO ordenes (numero_orden, numero_factura, fecha_registro, hora_registro, paciente_id, usuario_id,
    cedula, nombres, apellidos, direccion, telefono, correo, sexo, total, estatus) VALUES
('ORD-2024-001', 'FAC-2024-001', '2024-01-15', '09:30:00', 1, 2,
    '10000001', 'Ana', 'Martínez', 'Av. Principal 123', '0412-1234567', 'ana.martinez@email.com', 'F', 33.50, 'Activo'),
('ORD-2024-002', 'FAC-2024-002', '2024-01-15', '10:15:00', 2, 2,
    '10000002', 'Luis', 'Hernández', 'Calle 45 #67-89', '0414-2345678', 'luis.hernandez@email.com', 'M', 20.50, 'Activo'),
('ORD-2024-003', 'FAC-2024-003', '2024-01-16', '08:45:00', 3, 3,
    '10000003', 'Carmen', 'López', 'Urbanización Los Pinos', '0424-3456789', 'carmen.lopez@email.com', 'F', 45.00, 'Activo'),
('ORD-2024-004', 'FAC-2024-004', '2024-01-16', '11:20:00', 4, 2,
    '10000004', 'Roberto', 'Sánchez', 'Sector El Centro', '0416-4567890', 'roberto.sanchez@email.com', 'M', 16.00, 'Completado'),
('ORD-2024-005', 'FAC-2024-005', '2024-01-17', '14:00:00', 5, 3,
    '10000005', 'Laura', 'Torres', 'Residencia Las Flores', '0426-5678901', 'laura.torres@email.com', 'F', 38.00, 'Activo');

-- Insertar Orden Exámenes (relación entre órdenes y exámenes)
INSERT INTO orden_examenes (orden_id, examen_codigo, precio) VALUES
-- Orden 1: Ana Martínez
(1, 'HEMO001', 25.50),
(1, 'GLUC001', 8.00),
-- Orden 2: Luis Hernández
(2, 'GLUC001', 8.00),
(2, 'TRIG001', 10.50),
(2, 'COL001', 12.00),
-- Orden 3: Carmen López
(3, 'HEMO001', 25.50),
(3, 'TSH001', 15.00),
(3, 'VDRL001', 12.50),
-- Orden 4: Roberto Sánchez
(4, 'GLUC001', 8.00),
(4, 'COL001', 12.00),
-- Orden 5: Laura Torres
(5, 'HEMO001', 25.50),
(5, 'PCR001', 18.00);

-- ============================================
-- VERIFICACIÓN FINAL
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

SELECT '✅ Base de datos creada exitosamente con todos los datos de ejemplo.' AS mensaje;

