-- Script para diagnosticar el problema de las tablas
-- Ejecuta este script primero para ver qué tablas tienen problemas

USE laboratorio_db;

-- Ver todas las tablas y su estado
SHOW TABLES;

-- Verificar el estado de cada tabla
SELECT 
    TABLE_NAME,
    ENGINE,
    TABLE_ROWS,
    DATA_LENGTH,
    INDEX_LENGTH
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA = 'laboratorio_db'
ORDER BY TABLE_NAME;

-- Intentar verificar cada tabla
CHECK TABLE usuarios;
CHECK TABLE pacientes;
CHECK TABLE examenes;
CHECK TABLE ordenes;
CHECK TABLE orden_examenes;

