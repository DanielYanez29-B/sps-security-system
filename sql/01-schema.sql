-- =========================================================
-- PROYECTO S.P.S. - SISTEMA DE MONITOREO DE SEGURIDAD
-- Script Oficial para MySQL / MariaDB (XAMPP)
-- Modelo Optimizado: 12 Entidades
-- =========================================================

CREATE DATABASE IF NOT EXISTS sps_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sps_db;

-- Deshabilitar restricciones temporalmente para borrado limpio
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS NOTIFICACION;
DROP TABLE IF EXISTS ALARMA;
DROP TABLE IF EXISTS DISPOSITIVO;
DROP TABLE IF EXISTS ELEMENTO_SUPERVISADO;
DROP TABLE IF EXISTS ASIGNACION;
DROP TABLE IF EXISTS SERVICIO;
DROP TABLE IF EXISTS JORNADA;
DROP TABLE IF EXISTS GUARDIA;
DROP TABLE IF EXISTS USUARIO;
DROP TABLE IF EXISTS CLIENTE;
DROP TABLE IF EXISTS ROL_PERMISO;
DROP TABLE IF EXISTS PERMISO;
DROP TABLE IF EXISTS ROL;

SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------
-- Módulo 1: Seguridad y Control de Acceso (RBAC)
-- ---------------------------------------------------------

CREATE TABLE ROL (
                     id_rol INT AUTO_INCREMENT PRIMARY KEY,
                     nombre VARCHAR(50) NOT NULL UNIQUE,
                     descripcion TEXT
) ENGINE=InnoDB;

CREATE TABLE PERMISO (
                         id_permiso INT AUTO_INCREMENT PRIMARY KEY,
                         clave_permiso VARCHAR(50) NOT NULL UNIQUE,
                         descripcion TEXT
) ENGINE=InnoDB;

CREATE TABLE ROL_PERMISO (
                             id_rol INT NOT NULL,
                             id_permiso INT NOT NULL,
                             PRIMARY KEY (id_rol, id_permiso),
                             FOREIGN KEY (id_rol) REFERENCES ROL(id_rol) ON DELETE CASCADE,
                             FOREIGN KEY (id_permiso) REFERENCES PERMISO(id_permiso) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE USUARIO (
                         id_usuario INT AUTO_INCREMENT PRIMARY KEY,
                         id_rol INT NOT NULL,
                         username VARCHAR(50) NOT NULL UNIQUE,
                         email VARCHAR(100) NOT NULL UNIQUE,
                         contrasena VARCHAR(255) NOT NULL,
                         estado_activo BOOLEAN DEFAULT TRUE,
                         FOREIGN KEY (id_rol) REFERENCES ROL(id_rol)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- Módulo 2: Clientes y Servicios
-- ---------------------------------------------------------

CREATE TABLE CLIENTE (
                         id_cliente INT AUTO_INCREMENT PRIMARY KEY,
                         razon_social VARCHAR(100) NOT NULL,
                         rfc VARCHAR(15) UNIQUE,
                         telefono VARCHAR(20),
                         email VARCHAR(100) UNIQUE,
                         calle VARCHAR(100),
                         num_ext VARCHAR(20),
                         colonia VARCHAR(100),
                         ciudad VARCHAR(100),
                         estado VARCHAR(100),
                         cp VARCHAR(10)
) ENGINE=InnoDB;

CREATE TABLE SERVICIO (
                          id_servicio INT AUTO_INCREMENT PRIMARY KEY,
                          id_cliente INT NOT NULL,
                          nombre_servicio VARCHAR(100) NOT NULL,
                          direccion TEXT NOT NULL,
                          fecha_inicio DATE NOT NULL,
                          estado VARCHAR(20) DEFAULT 'ACTIVO',
                          FOREIGN KEY (id_cliente) REFERENCES CLIENTE(id_cliente) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- Módulo 3: Personal y Operativa
-- ---------------------------------------------------------

CREATE TABLE GUARDIA (
                         id_guardia INT AUTO_INCREMENT PRIMARY KEY,
                         id_usuario INT UNIQUE NULL,
                         nombres VARCHAR(100) NOT NULL,
                         apellido_paterno VARCHAR(100) NOT NULL,
                         apellido_materno VARCHAR(100),
                         rfc VARCHAR(15) UNIQUE,
                         fecha_ingreso DATE NOT NULL,
                         estatus_activo BOOLEAN DEFAULT TRUE,
                         FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE JORNADA (
                         id_jornada INT AUTO_INCREMENT PRIMARY KEY,
                         nombre_jornada VARCHAR(50) NOT NULL,
                         hora_inicio TIME NOT NULL,
                         hora_fin TIME NOT NULL,
                         estado_actividad BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE ASIGNACION (
                            id_asignacion INT AUTO_INCREMENT PRIMARY KEY,
                            id_guardia INT NOT NULL,
                            id_servicio INT NOT NULL,
                            id_jornada INT NOT NULL,
                            fecha_asignacion DATE NOT NULL,
                            FOREIGN KEY (id_guardia) REFERENCES GUARDIA(id_guardia) ON DELETE CASCADE,
                            FOREIGN KEY (id_servicio) REFERENCES SERVICIO(id_servicio) ON DELETE CASCADE,
                            FOREIGN KEY (id_jornada) REFERENCES JORNADA(id_jornada)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- Módulo 4: Elementos Supervisados (Sin Herencia)
-- ---------------------------------------------------------

CREATE TABLE ELEMENTO_SUPERVISADO (
                                      id_elemento INT AUTO_INCREMENT PRIMARY KEY,
                                      id_servicio INT NOT NULL,
                                      tipo_elemento VARCHAR(30) NOT NULL, -- 'VEHICULO', 'PERSONA', 'MASCOTA', 'EQUIPO'
                                      identificador VARCHAR(100) NOT NULL, -- Placas, Serie, Nombre o Matrícula
                                      descripcion TEXT,
                                      estado_activo BOOLEAN DEFAULT TRUE,
                                      FOREIGN KEY (id_servicio) REFERENCES SERVICIO(id_servicio) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- Módulo 5: IoT y Telemetría
-- ---------------------------------------------------------

CREATE TABLE DISPOSITIVO (
                             id_dispositivo INT AUTO_INCREMENT PRIMARY KEY,
                             id_servicio INT NOT NULL,
                             num_serie VARCHAR(50) NOT NULL UNIQUE,
                             mac_address VARCHAR(17) UNIQUE,
                             ip_asignada VARCHAR(15),
                             fabricante VARCHAR(50) DEFAULT 'ESP32',
                             fecha_instalacion DATE,
                             estado_actividad BOOLEAN DEFAULT TRUE,
                             FOREIGN KEY (id_servicio) REFERENCES SERVICIO(id_servicio) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE ALARMA (
                        id_alarma INT AUTO_INCREMENT PRIMARY KEY,
                        id_dispositivo INT NOT NULL,
                        fecha_hora DATETIME NOT NULL,
                        nivel_prioridad VARCHAR(20) DEFAULT 'MEDIA', -- 'BAJA', 'MEDIA', 'ALTA', 'CRITICA'
                        tipo_alarma VARCHAR(50) NOT NULL,           -- 'PIR_MOVIMIENTO', 'MAGNETICO_PUERTA', 'TEMPERATURA'
                        estado VARCHAR(20) DEFAULT 'PENDIENTE',     -- 'PENDIENTE', 'ATENDIDA', 'FALSA_ALARMA'
                        descripcion TEXT,
                        FOREIGN KEY (id_dispositivo) REFERENCES DISPOSITIVO(id_dispositivo) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE NOTIFICACION (
                              id_notificacion INT AUTO_INCREMENT PRIMARY KEY,
                              id_alarma INT NOT NULL,
                              id_usuario INT NOT NULL,
                              fecha_hora DATETIME NOT NULL,
                              mensaje TEXT NOT NULL,
                              leido BOOLEAN DEFAULT FALSE,
                              FOREIGN KEY (id_alarma) REFERENCES ALARMA(id_alarma) ON DELETE CASCADE,
                              FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario) ON DELETE CASCADE
) ENGINE=InnoDB;


-- ---------------------------------------------------------
-- DATOS INICIALES DE PRUEBA (SEEDING)
-- ---------------------------------------------------------

INSERT INTO ROL (id_rol, nombre, descripcion) VALUES
                                                  (1, 'ROLE_ADMIN', 'Administrador general del sistema'),
                                                  (2, 'ROLE_OPERADOR', 'Monitoreo de alarmas y dispositivos en tiempo real'),
                                                  (3, 'ROLE_GUARDIA', 'Personal operativo de campo');

INSERT INTO PERMISO (id_permiso, clave_permiso, descripcion) VALUES
                                                                 (1, 'USUARIO_GESTION', 'Crear y modificar usuarios'),
                                                                 (2, 'ALARMA_VER', 'Visualizar tablero de alertas'),
                                                                 (3, 'DISPOSITIVO_CONFIG', 'Configurar parámetros de red e IP de ESP32');

INSERT INTO ROL_PERMISO (id_rol, id_permiso) VALUES
                                                 (1, 1), (1, 2), (1, 3),
                                                 (2, 2), (2, 3);

INSERT INTO USUARIO (id_usuario, id_rol, username, email, contrasena, estado_activo) VALUES
    (1, 1, 'admin', 'admin@sps-security.com',
     '$2a$12$4OMPPDOkCIUq03qbF.bZTOwRu5B9Q0vve6KES/oUaAN7dpK9p3WBi',
     TRUE);

INSERT INTO JORNADA (nombre_jornada, hora_inicio, hora_fin, estado_actividad) VALUES
                                                                                  ('Matutino 8h', '06:00:00', '14:00:00', TRUE),
                                                                                  ('Vespertino 8h', '14:00:00', '22:00:00', TRUE),
                                                                                  ('Nocturno 10h', '22:00:00', '06:00:00', TRUE);