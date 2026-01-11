-- Script de migración para agregar el campo sexo a las tablas
-- Ejecutar este script si la base de datos ya existe y necesita actualizarse

-- Agregar columna sexo a la tabla pacientes si no existe
ALTER TABLE pacientes 
ADD COLUMN IF NOT EXISTS sexo VARCHAR(1) DEFAULT '' AFTER correo;

-- Agregar columna sexo a la tabla ordenes si no existe
ALTER TABLE ordenes 
ADD COLUMN IF NOT EXISTS sexo VARCHAR(1) DEFAULT '' AFTER empresa;

-- Actualizar índices si es necesario (opcional)
-- CREATE INDEX IF NOT EXISTS idx_sexo_pacientes ON pacientes(sexo);
-- CREATE INDEX IF NOT EXISTS idx_sexo_ordenes ON ordenes(sexo);

