-- Script para Recrear la Tabla usuarios desde Cero
-- ADVERTENCIA: Este script ELIMINARÁ todos los datos de la tabla usuarios
-- Úsalo solo si estás seguro de que quieres perder los datos existentes
-- O si la tabla está completamente corrupta

USE laboratorio_db;

-- Eliminar la tabla si existe (CUIDADO: Esto borra todos los datos)
DROP TABLE IF EXISTS usuarios;

-- Crear la tabla usuarios con la estructura completa
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

-- Insertar usuario administrador por defecto
INSERT INTO usuarios (cedula, nombres, apellidos, usuario, password, rol, 
    modulo_ventas_laboratorio, modulo_compras, modulo_administrativos,
    puede_control_precios, permisos_administrativos) 
VALUES ('00000000', 'Administrador', 'Sistema', 'admin', 'admin123', 'Administrador',
    TRUE, TRUE, TRUE, TRUE, TRUE);

SELECT 'Tabla usuarios recreada exitosamente. Usuario admin creado.' AS resultado;

