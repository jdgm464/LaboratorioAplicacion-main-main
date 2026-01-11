-- Script para reparar tablas con error "Tablespace is missing"
-- ADVERTENCIA: Este script intentará reparar las tablas. Si los datos están corruptos, puede que se pierdan.
-- Ejecuta primero diagnosticar_tablas.sql para ver el estado

USE laboratorio_db;

-- ============================================
-- PASO 1: Intentar reparar las tablas
-- ============================================
-- Si las tablas tienen problemas de tablespace, intentamos repararlas

REPAIR TABLE usuarios;
REPAIR TABLE pacientes;
REPAIR TABLE examenes;
REPAIR TABLE ordenes;
REPAIR TABLE orden_examenes;

-- ============================================
-- PASO 2: Si la reparación no funciona, recargar las tablas
-- ============================================
-- Esto fuerza a MySQL a recargar la información de las tablas desde el disco

FLUSH TABLES usuarios, pacientes, examenes, ordenes, orden_examenes;

-- ============================================
-- PASO 3: Verificar el estado después de la reparación
-- ============================================
CHECK TABLE usuarios;
CHECK TABLE pacientes;
CHECK TABLE examenes;
CHECK TABLE ordenes;
CHECK TABLE orden_examenes;

