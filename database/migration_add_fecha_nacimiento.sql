-- Script para agregar el campo fecha_nacimiento a la tabla pacientes
-- Esto permite calcular la edad exacta automáticamente

USE laboratorio_db;

-- Agregar columna fecha_nacimiento si no existe
ALTER TABLE pacientes 
ADD COLUMN IF NOT EXISTS fecha_nacimiento DATE NULL AFTER edad;

-- Actualizar comentario de la columna edad para indicar que se calcula desde fecha_nacimiento
ALTER TABLE pacientes 
MODIFY COLUMN edad INT NOT NULL COMMENT 'Edad calculada desde fecha_nacimiento (se actualiza automáticamente)';
