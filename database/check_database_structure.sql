-- Script para Verificar la Estructura de la Base de Datos
-- Este script muestra qué columnas existen en cada tabla
-- Útil para diagnosticar problemas de estructura

USE laboratorio_db;

-- Verificar estructura de la tabla usuarios
SELECT 
    'usuarios' AS tabla,
    COLUMN_NAME AS columna,
    DATA_TYPE AS tipo_dato,
    IS_NULLABLE AS permite_null,
    COLUMN_DEFAULT AS valor_default,
    COLUMN_KEY AS clave
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'laboratorio_db' 
AND TABLE_NAME = 'usuarios'
ORDER BY ORDINAL_POSITION;

-- Verificar índices de la tabla usuarios
SELECT 
    'usuarios' AS tabla,
    INDEX_NAME AS indice,
    COLUMN_NAME AS columna,
    NON_UNIQUE AS no_unico,
    SEQ_IN_INDEX AS secuencia
FROM INFORMATION_SCHEMA.STATISTICS
WHERE TABLE_SCHEMA = 'laboratorio_db' 
AND TABLE_NAME = 'usuarios'
ORDER BY INDEX_NAME, SEQ_IN_INDEX;

-- Verificar si faltan columnas críticas
SELECT 
    CASE 
        WHEN COUNT(*) = 0 THEN 'FALTA: cedula'
        ELSE 'OK: cedula existe'
    END AS estado_cedula
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'laboratorio_db' 
AND TABLE_NAME = 'usuarios' 
AND COLUMN_NAME = 'cedula';

SELECT 
    CASE 
        WHEN COUNT(*) = 0 THEN 'FALTA: nombres'
        ELSE 'OK: nombres existe'
    END AS estado_nombres
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'laboratorio_db' 
AND TABLE_NAME = 'usuarios' 
AND COLUMN_NAME = 'nombres';

SELECT 
    CASE 
        WHEN COUNT(*) = 0 THEN 'FALTA: apellidos'
        ELSE 'OK: apellidos existe'
    END AS estado_apellidos
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'laboratorio_db' 
AND TABLE_NAME = 'usuarios' 
AND COLUMN_NAME = 'apellidos';

-- Resumen de columnas esperadas vs existentes
SELECT 
    'Columnas esperadas en usuarios: 22' AS esperadas,
    COUNT(*) AS existentes,
    CASE 
        WHEN COUNT(*) = 22 THEN '✓ Estructura completa'
        WHEN COUNT(*) < 22 THEN '✗ Faltan columnas - Ejecutar migrate_fix_usuarios_table.sql'
        ELSE '? Más columnas de las esperadas'
    END AS estado
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'laboratorio_db' 
AND TABLE_NAME = 'usuarios';

