-- Script SQL para Laboratorio - PostgreSQL 12 a 18 (pgAdmin)
-- En pgAdmin: crea la base de datos "laboratorio_db" y ejecuta este script en ella.
-- Compatible con PostgreSQL 12, 13, 14, 15, 16, 17 y 18.

-- Suprimir NOTICEs (ej. "table does not exist, skipping") en primera ejecución
SET client_min_messages TO WARNING;

DROP TABLE IF EXISTS orden_examenes CASCADE;
DROP TABLE IF EXISTS ordenes CASCADE;
DROP TABLE IF EXISTS examenes CASCADE;
DROP TABLE IF EXISTS pacientes CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_actualizacion = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Tabla de Usuarios
CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
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
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER update_usuarios_updated_at
    BEFORE UPDATE ON usuarios
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Tabla de Pacientes
CREATE TABLE pacientes (
    id SERIAL PRIMARY KEY,
    cedula VARCHAR(20) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    edad INTEGER NOT NULL,
    direccion VARCHAR(255),
    telefono VARCHAR(20),
    correo VARCHAR(100),
    sexo VARCHAR(1) DEFAULT '',
    usuario_id INTEGER NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pacientes_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL
);

CREATE TRIGGER update_pacientes_updated_at
    BEFORE UPDATE ON pacientes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE INDEX idx_pacientes_cedula ON pacientes(cedula);
CREATE INDEX idx_pacientes_usuario_id ON pacientes(usuario_id);

-- Tabla de Exámenes
CREATE TABLE examenes (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    nombre VARCHAR(200) NOT NULL,
    precio NUMERIC(10, 2) NOT NULL,
    descripcion TEXT,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER update_examenes_updated_at
    BEFORE UPDATE ON examenes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE INDEX idx_examenes_codigo ON examenes(codigo);

-- Tabla de Órdenes
CREATE TABLE ordenes (
    id SERIAL PRIMARY KEY,
    numero_orden VARCHAR(50) UNIQUE NOT NULL,
    numero_factura VARCHAR(50),
    numero_control VARCHAR(50),
    numero_lote VARCHAR(50),
    fecha_registro DATE NOT NULL,
    hora_registro TIME NOT NULL,
    paciente_id INTEGER NOT NULL,
    usuario_id INTEGER NOT NULL,
    codigo_paciente VARCHAR(50),
    cedula VARCHAR(20) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    direccion VARCHAR(255),
    telefono VARCHAR(20),
    correo VARCHAR(100),
    codigo_empresa VARCHAR(50),
    empresa VARCHAR(200),
    sexo VARCHAR(1) DEFAULT '',
    total NUMERIC(10, 2) NOT NULL,
    estatus VARCHAR(20) DEFAULT 'Activo',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ordenes_paciente FOREIGN KEY (paciente_id) REFERENCES pacientes(id) ON DELETE RESTRICT,
    CONSTRAINT fk_ordenes_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE RESTRICT
);

CREATE TRIGGER update_ordenes_updated_at
    BEFORE UPDATE ON ordenes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE INDEX idx_ordenes_numero_orden ON ordenes(numero_orden);
CREATE INDEX idx_ordenes_paciente_id ON ordenes(paciente_id);
CREATE INDEX idx_ordenes_usuario_id ON ordenes(usuario_id);
CREATE INDEX idx_ordenes_cedula ON ordenes(cedula);
CREATE INDEX idx_ordenes_fecha_registro ON ordenes(fecha_registro);

-- Tabla de Exámenes por Orden
CREATE TABLE orden_examenes (
    id SERIAL PRIMARY KEY,
    orden_id INTEGER NOT NULL,
    examen_codigo VARCHAR(50) NOT NULL,
    precio NUMERIC(10, 2) NOT NULL,
    CONSTRAINT fk_orden_examenes_orden FOREIGN KEY (orden_id) REFERENCES ordenes(id) ON DELETE CASCADE,
    CONSTRAINT fk_orden_examenes_examen FOREIGN KEY (examen_codigo) REFERENCES examenes(codigo) ON DELETE RESTRICT
);

CREATE INDEX idx_orden_examenes_orden_id ON orden_examenes(orden_id);
CREATE INDEX idx_orden_examenes_examen_codigo ON orden_examenes(examen_codigo);

-- Usuario administrador por defecto
INSERT INTO usuarios (cedula, nombres, apellidos, usuario, password, rol,
    modulo_ventas_laboratorio, modulo_compras, modulo_administrativos,
    puede_control_precios, permisos_administrativos)
VALUES ('00000000', 'Administrador', 'Sistema', 'admin', 'admin123', 'Administrador',
    TRUE, TRUE, TRUE, TRUE, TRUE)
ON CONFLICT (usuario) DO UPDATE SET usuario = EXCLUDED.usuario;
