-- Script de Migración para Corregir la Tabla usuarios
-- Este script verifica y agrega las columnas faltantes en la tabla usuarios
-- Útil cuando la base de datos se recreó y falta la estructura completa

USE laboratorio_db;

-- Verificar y agregar columna 'cedula' si no existe
SET @col_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'laboratorio_db' 
    AND TABLE_NAME = 'usuarios' 
    AND COLUMN_NAME = 'cedula'
);

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE usuarios ADD COLUMN cedula VARCHAR(20) UNIQUE NOT NULL DEFAULT "" AFTER id',
    'SELECT "Columna cedula ya existe" AS mensaje'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Verificar y agregar columna 'nombres' si no existe
SET @col_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'laboratorio_db' 
    AND TABLE_NAME = 'usuarios' 
    AND COLUMN_NAME = 'nombres'
);

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE usuarios ADD COLUMN nombres VARCHAR(100) NOT NULL DEFAULT "" AFTER cedula',
    'SELECT "Columna nombres ya existe" AS mensaje'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Verificar y agregar columna 'apellidos' si no existe
SET @col_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'laboratorio_db' 
    AND TABLE_NAME = 'usuarios' 
    AND COLUMN_NAME = 'apellidos'
);

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE usuarios ADD COLUMN apellidos VARCHAR(100) NOT NULL DEFAULT "" AFTER nombres',
    'SELECT "Columna apellidos ya existe" AS mensaje'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Verificar y agregar columnas de módulos si no existen
SET @col_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'laboratorio_db' 
    AND TABLE_NAME = 'usuarios' 
    AND COLUMN_NAME = 'modulo_ventas_laboratorio'
);

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE usuarios ADD COLUMN modulo_ventas_laboratorio BOOLEAN DEFAULT FALSE AFTER rol',
    'SELECT "Columna modulo_ventas_laboratorio ya existe" AS mensaje'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'laboratorio_db' 
    AND TABLE_NAME = 'usuarios' 
    AND COLUMN_NAME = 'modulo_compras'
);

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE usuarios ADD COLUMN modulo_compras BOOLEAN DEFAULT FALSE AFTER modulo_ventas_laboratorio',
    'SELECT "Columna modulo_compras ya existe" AS mensaje'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'laboratorio_db' 
    AND TABLE_NAME = 'usuarios' 
    AND COLUMN_NAME = 'modulo_administrativos'
);

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE usuarios ADD COLUMN modulo_administrativos BOOLEAN DEFAULT FALSE AFTER modulo_compras',
    'SELECT "Columna modulo_administrativos ya existe" AS mensaje'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Verificar y agregar columnas de autorizaciones si no existen
SET @col_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'laboratorio_db' 
    AND TABLE_NAME = 'usuarios' 
    AND COLUMN_NAME = 'puede_descuento'
);

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE usuarios ADD COLUMN puede_descuento BOOLEAN DEFAULT FALSE AFTER modulo_administrativos',
    'SELECT "Columna puede_descuento ya existe" AS mensaje'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'laboratorio_db' 
    AND TABLE_NAME = 'usuarios' 
    AND COLUMN_NAME = 'puede_recargo'
);

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE usuarios ADD COLUMN puede_recargo BOOLEAN DEFAULT FALSE AFTER puede_descuento',
    'SELECT "Columna puede_recargo ya existe" AS mensaje'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'laboratorio_db' 
    AND TABLE_NAME = 'usuarios' 
    AND COLUMN_NAME = 'puede_cierre'
);

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE usuarios ADD COLUMN puede_cierre BOOLEAN DEFAULT FALSE AFTER puede_recargo',
    'SELECT "Columna puede_cierre ya existe" AS mensaje'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'laboratorio_db' 
    AND TABLE_NAME = 'usuarios' 
    AND COLUMN_NAME = 'puede_cortesias'
);

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE usuarios ADD COLUMN puede_cortesias BOOLEAN DEFAULT FALSE AFTER puede_cierre',
    'SELECT "Columna puede_cortesias ya existe" AS mensaje'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'laboratorio_db' 
    AND TABLE_NAME = 'usuarios' 
    AND COLUMN_NAME = 'puede_control_precios'
);

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE usuarios ADD COLUMN puede_control_precios BOOLEAN DEFAULT FALSE AFTER puede_cortesias',
    'SELECT "Columna puede_control_precios ya existe" AS mensaje'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'laboratorio_db' 
    AND TABLE_NAME = 'usuarios' 
    AND COLUMN_NAME = 'puede_control_resultados'
);

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE usuarios ADD COLUMN puede_control_resultados BOOLEAN DEFAULT FALSE AFTER puede_control_precios',
    'SELECT "Columna puede_control_resultados ya existe" AS mensaje'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'laboratorio_db' 
    AND TABLE_NAME = 'usuarios' 
    AND COLUMN_NAME = 'puede_anular'
);

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE usuarios ADD COLUMN puede_anular BOOLEAN DEFAULT FALSE AFTER puede_control_resultados',
    'SELECT "Columna puede_anular ya existe" AS mensaje'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'laboratorio_db' 
    AND TABLE_NAME = 'usuarios' 
    AND COLUMN_NAME = 'puede_modificar_ordenes'
);

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE usuarios ADD COLUMN puede_modificar_ordenes BOOLEAN DEFAULT FALSE AFTER puede_anular',
    'SELECT "Columna puede_modificar_ordenes ya existe" AS mensaje'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'laboratorio_db' 
    AND TABLE_NAME = 'usuarios' 
    AND COLUMN_NAME = 'permisos_administrativos'
);

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE usuarios ADD COLUMN permisos_administrativos BOOLEAN DEFAULT FALSE AFTER puede_modificar_ordenes',
    'SELECT "Columna permisos_administrativos ya existe" AS mensaje'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Verificar y agregar columnas de timestamps si no existen
SET @col_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'laboratorio_db' 
    AND TABLE_NAME = 'usuarios' 
    AND COLUMN_NAME = 'fecha_creacion'
);

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE usuarios ADD COLUMN fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP AFTER permisos_administrativos',
    'SELECT "Columna fecha_creacion ya existe" AS mensaje'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'laboratorio_db' 
    AND TABLE_NAME = 'usuarios' 
    AND COLUMN_NAME = 'fecha_actualizacion'
);

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE usuarios ADD COLUMN fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER fecha_creacion',
    'SELECT "Columna fecha_actualizacion ya existe" AS mensaje'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Agregar índices únicos si no existen
-- Verificar y agregar índice único para cedula
SET @index_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.STATISTICS 
    WHERE TABLE_SCHEMA = 'laboratorio_db' 
    AND TABLE_NAME = 'usuarios' 
    AND INDEX_NAME = 'cedula'
);

SET @sql = IF(@index_exists = 0,
    'ALTER TABLE usuarios ADD UNIQUE INDEX cedula (cedula)',
    'SELECT "Índice único cedula ya existe" AS mensaje'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Verificar y agregar índice único para usuario
SET @index_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.STATISTICS 
    WHERE TABLE_SCHEMA = 'laboratorio_db' 
    AND TABLE_NAME = 'usuarios' 
    AND INDEX_NAME = 'usuario'
);

SET @sql = IF(@index_exists = 0,
    'ALTER TABLE usuarios ADD UNIQUE INDEX usuario (usuario)',
    'SELECT "Índice único usuario ya existe" AS mensaje'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Mensaje final
SELECT 'Migración completada. Verifique que todas las columnas se hayan agregado correctamente.' AS resultado;

