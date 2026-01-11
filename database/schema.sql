-- Script SQL para crear la base de datos y tablas del Laboratorio
-- Ejecuta este script en phpMyAdmin o desde la línea de comandos de MySQL

-- Crear la base de datos (si no existe)
CREATE DATABASE IF NOT EXISTS laboratorio_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE laboratorio_db;

-- Tabla de Usuarios
CREATE TABLE IF NOT EXISTS usuarios (
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
CREATE TABLE IF NOT EXISTS pacientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cedula VARCHAR(20) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    edad INT NOT NULL,
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
CREATE TABLE IF NOT EXISTS examenes (
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
CREATE TABLE IF NOT EXISTS ordenes (
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
CREATE TABLE IF NOT EXISTS orden_examenes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    orden_id INT NOT NULL,
    examen_codigo VARCHAR(50) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (orden_id) REFERENCES ordenes(id) ON DELETE CASCADE,
    FOREIGN KEY (examen_codigo) REFERENCES examenes(codigo),
    INDEX idx_orden_id (orden_id),
    INDEX idx_examen_codigo (examen_codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertar algunos datos de ejemplo (opcional)
-- Usuario administrador por defecto
INSERT INTO usuarios (cedula, nombres, apellidos, usuario, password, rol, 
    modulo_ventas_laboratorio, modulo_compras, modulo_administrativos,
    puede_control_precios, permisos_administrativos) 
VALUES ('00000000', 'Administrador', 'Sistema', 'admin', 'admin123', 'Administrador',
    TRUE, TRUE, TRUE, TRUE, TRUE)
ON DUPLICATE KEY UPDATE usuario = usuario;

