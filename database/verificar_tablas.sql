-- Script para verificar que todas las tablas se crearon correctamente
-- Ejecuta este script en phpMyAdmin después de ejecutar schema.sql

USE laboratorio_db;

-- Ver todas las tablas en la base de datos
SHOW TABLES;

-- Verificar estructura de cada tabla
DESCRIBE usuarios;
DESCRIBE pacientes;
DESCRIBE examenes;
DESCRIBE ordenes;
DESCRIBE orden_examenes;

-- Verificar foreign keys
SELECT 
    TABLE_NAME,
    CONSTRAINT_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'laboratorio_db'
    AND REFERENCED_TABLE_NAME IS NOT NULL
ORDER BY TABLE_NAME, CONSTRAINT_NAME;

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

