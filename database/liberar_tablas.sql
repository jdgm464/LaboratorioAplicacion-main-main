-- Script para liberar tablas que aparecen como "en uso" en phpMyAdmin
-- Ejecuta este script en la pestaña SQL de phpMyAdmin
-- Esto optimizará las tablas y actualizará las estadísticas

USE laboratorio_db;

-- ============================================
-- PASO 1: Ver conexiones actuales
-- ============================================
-- Descomenta la siguiente línea si quieres ver qué conexiones hay activas:
-- SHOW PROCESSLIST;

-- ============================================
-- PASO 2: Optimizar tablas (libera espacio y actualiza estadísticas)
-- Esto hará que las tablas dejen de aparecer como "en uso"
-- ============================================
OPTIMIZE TABLE usuarios;
OPTIMIZE TABLE pacientes;
OPTIMIZE TABLE examenes;
OPTIMIZE TABLE ordenes;
OPTIMIZE TABLE orden_examenes;

-- ============================================
-- PASO 3: Analizar tablas (actualiza estadísticas para el optimizador)
-- ============================================
ANALYZE TABLE usuarios;
ANALYZE TABLE pacientes;
ANALYZE TABLE examenes;
ANALYZE TABLE ordenes;
ANALYZE TABLE orden_examenes;

-- ============================================
-- PASO 4: Verificar que las tablas se liberaron
-- ============================================
-- Después de ejecutar, recarga la página en phpMyAdmin
-- Las tablas deberían mostrar el número de filas en lugar de "en uso"

SELECT 'Tablas optimizadas correctamente. Recarga la página en phpMyAdmin para ver los cambios.' AS mensaje;

