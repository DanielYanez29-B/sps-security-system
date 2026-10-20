-- =========================================================
-- PROYECTO S.P.S. - SISTEMA DE MONITOREO DE SEGURIDAD
-- Script para MySQL / MariaDB (XAMPP)
-- =========================================================

CREATE DATABASE IF NOT EXISTS sps_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sps_db;

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
                         nombre VARCHAR(50) NOT NULL UNIQUE,
                         descripcion TEXT
) ENGINE=InnoDB;

CREATE TABLE ROL_PERMISO (
                             id_rol INT NOT NULL,
                             id_permiso INT NOT NULL,
                             PRIMARY KEY (id_rol, id_permiso),
                             FOREIGN KEY (id_rol) REFERENCES ROL(id_rol) ON DELETE CASCADE,
                             FOREIGN KEY (id_permiso) REFERENCES PERMISO(id_permiso) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- Módulo 2: Clientes y Usuarios
-- ---------------------------------------------------------

CREATE TABLE CLIENTE (
                         id_cliente INT AUTO_INCREMENT PRIMARY KEY,
                         nombre VARCHAR(100) NOT NULL,
                         rfc VARCHAR(15) UNIQUE,
                         telefono VARCHAR(20),
                         email VARCHAR(100) UNIQUE,
                         calle VARCHAR(100),
                         num_ext VARCHAR(20),
                         num_int VARCHAR(20),
                         colonia VARCHAR(100),
                         ciudad VARCHAR(100),
                         estado VARCHAR(100),
                         cp VARCHAR(10)
) ENGINE=InnoDB;

CREATE TABLE USUARIO (
                         id_usuario INT AUTO_INCREMENT PRIMARY KEY,
                         id_rol INT NOT NULL,
                         id_cliente INT NULL,
                         email VARCHAR(100) NOT NULL UNIQUE,
                         contrasena VARCHAR(255) NOT NULL,
                         estado_activo BOOLEAN DEFAULT TRUE,
                         FOREIGN KEY (id_rol) REFERENCES ROL(id_rol),
                         FOREIGN KEY (id_cliente) REFERENCES CLIENTE(id_cliente) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- Módulo 3: Operativo, Personal y Nómina
-- ---------------------------------------------------------

CREATE TABLE GUARDIA (
                         id_guardia INT AUTO_INCREMENT PRIMARY KEY,
                         id_cliente INT NULL,
                         id_usuario INT UNIQUE NULL,
                         nombres VARCHAR(100) NOT NULL,
                         apellido_paterno VARCHAR(100) NOT NULL,
                         apellido_materno VARCHAR(100),
                         fecha_ingreso DATE NOT NULL,
                         estatus_activo BOOLEAN DEFAULT TRUE,
                         FOREIGN KEY (id_cliente) REFERENCES CLIENTE(id_cliente),
                         FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario)
) ENGINE=InnoDB;

CREATE TABLE JORNADA (
                         id_jornada INT AUTO_INCREMENT PRIMARY KEY,
                         nombre_jornada VARCHAR(50) NOT NULL,
                         hora_inicio TIME NOT NULL,
                         hora_fin TIME NOT NULL,
                         estado_actividad BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE SERVICIO (
                          id_servicio INT AUTO_INCREMENT PRIMARY KEY,
                          id_cliente INT NOT NULL,
                          nombre_servicio VARCHAR(100) NOT NULL,
                          fecha_inicio DATE NOT NULL,
                          fecha_fin DATE NULL,
                          FOREIGN KEY (id_cliente) REFERENCES CLIENTE(id_cliente)
) ENGINE=InnoDB;

CREATE TABLE ASIGNACION (
                            id_asignacion INT AUTO_INCREMENT PRIMARY KEY,
                            id_guardia INT NOT NULL,
                            id_servicio INT NOT NULL,
                            id_jornada INT NOT NULL,
                            fecha_asignacion DATE NOT NULL,
                            FOREIGN KEY (id_guardia) REFERENCES GUARDIA(id_guardia),
                            FOREIGN KEY (id_servicio) REFERENCES SERVICIO(id_servicio),
                            FOREIGN KEY (id_jornada) REFERENCES JORNADA(id_jornada)
) ENGINE=InnoDB;

CREATE TABLE PAGO_NOMINA (
                             id_pago INT AUTO_INCREMENT PRIMARY KEY,
                             id_guardia INT NOT NULL,
                             fecha_pago DATE NOT NULL,
                             periodo_pago VARCHAR(50) NOT NULL,
                             monto_base DECIMAL(10,2) NOT NULL,
                             monto_incentivos DECIMAL(10,2) DEFAULT 0.00,
                             deducciones DECIMAL(10,2) DEFAULT 0.00,
                             monto_neto DECIMAL(10,2) GENERATED ALWAYS AS (monto_base + monto_incentivos - deducciones) STORED,
                             FOREIGN KEY (id_guardia) REFERENCES GUARDIA(id_guardia)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- Módulo 4: Elementos Supervisados (Herencia / Generalización)
-- ---------------------------------------------------------

CREATE TABLE ELEMENTO_SUPERVISADO (
                                      id_elemento INT AUTO_INCREMENT PRIMARY KEY,
                                      id_cliente INT NOT NULL,
                                      tipo_elemento VARCHAR(20) NOT NULL, -- 'VEHICULO', 'PERSONA', 'MASCOTA', 'EQUIPO'
                                      estado_activo BOOLEAN DEFAULT TRUE,
                                      FOREIGN KEY (id_cliente) REFERENCES CLIENTE(id_cliente)
) ENGINE=InnoDB;

CREATE TABLE VEHICULO (
                          id_elemento INT PRIMARY KEY,
                          placas VARCHAR(20) NOT NULL UNIQUE,
                          marca VARCHAR(50),
                          modelo VARCHAR(50),
                          color VARCHAR(30),
                          num_serie VARCHAR(50),
                          FOREIGN KEY (id_elemento) REFERENCES ELEMENTO_SUPERVISADO(id_elemento) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE PERSONA (
                         id_elemento INT PRIMARY KEY,
                         nombre VARCHAR(100) NOT NULL,
                         parentesco VARCHAR(50),
                         telefono_contacto VARCHAR(20),
                         FOREIGN KEY (id_elemento) REFERENCES ELEMENTO_SUPERVISADO(id_elemento) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE MASCOTA (
                         id_elemento INT PRIMARY KEY,
                         nombre VARCHAR(50) NOT NULL,
                         especie VARCHAR(50) NOT NULL,
                         raza VARCHAR(50),
                         color VARCHAR(30),
                         FOREIGN KEY (id_elemento) REFERENCES ELEMENTO_SUPERVISADO(id_elemento) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE EQUIPO (
                        id_elemento INT PRIMARY KEY,
                        num_serie VARCHAR(50) NOT NULL UNIQUE,
                        marca VARCHAR(50),
                        tipo_equipo VARCHAR(50),
                        FOREIGN KEY (id_elemento) REFERENCES ELEMENTO_SUPERVISADO(id_elemento) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- Módulo 5: Supervisión, IoT y Telemetría
-- ---------------------------------------------------------

CREATE TABLE SUPERVISION (
                             id_supervision INT AUTO_INCREMENT PRIMARY KEY,
                             id_guardia INT NOT NULL,
                             id_elemento INT NOT NULL,
                             fecha_hora DATETIME NOT NULL,
                             detalles_observacion TEXT,
                             FOREIGN KEY (id_guardia) REFERENCES GUARDIA(id_guardia),
                             FOREIGN KEY (id_elemento) REFERENCES ELEMENTO_SUPERVISADO(id_elemento)
) ENGINE=InnoDB;

CREATE TABLE DISPOSITIVO (
                             id_dispositivo INT AUTO_INCREMENT PRIMARY KEY,
                             id_elemento INT NULL,
                             tipo_instalacion VARCHAR(20) DEFAULT 'INSTITUCIONAL',
                             num_serie VARCHAR(50) NOT NULL UNIQUE,
                             fabricante VARCHAR(50),
                             fecha_instalacion DATE,
                             estado_actividad BOOLEAN DEFAULT TRUE,
                             FOREIGN KEY (id_elemento) REFERENCES ELEMENTO_SUPERVISADO(id_elemento)
) ENGINE=InnoDB;

CREATE TABLE SENSOR (
                        id_sensor INT AUTO_INCREMENT PRIMARY KEY,
                        id_dispositivo INT NOT NULL,
                        tipo_sensor VARCHAR(50) NOT NULL,
                        ubicacion VARCHAR(100),
                        estado_actividad BOOLEAN DEFAULT TRUE,
                        FOREIGN KEY (id_dispositivo) REFERENCES DISPOSITIVO(id_dispositivo)
) ENGINE=InnoDB;

CREATE TABLE ALARMA (
                        id_alarma INT AUTO_INCREMENT PRIMARY KEY,
                        id_sensor INT NOT NULL,
                        fecha_hora DATETIME NOT NULL,
                        estado_actual VARCHAR(50) NOT NULL,
                        ubicacion VARCHAR(100),
                        tipo_alarma VARCHAR(50) NOT NULL,
                        FOREIGN KEY (id_sensor) REFERENCES SENSOR(id_sensor)
) ENGINE=InnoDB;

CREATE TABLE EMERGENCIA (
                            id_emergencia INT AUTO_INCREMENT PRIMARY KEY,
                            id_alarma INT UNIQUE NULL,
                            fecha_hora DATETIME NOT NULL,
                            detalles_emergencia TEXT,
                            estado_atencion VARCHAR(50) NOT NULL,
                            conducta_tomada TEXT,
                            FOREIGN KEY (id_alarma) REFERENCES ALARMA(id_alarma)
) ENGINE=InnoDB;

CREATE TABLE NOTIFICACION (
                              id_notificacion INT AUTO_INCREMENT PRIMARY KEY,
                              id_emergencia INT NULL,
                              id_alarma INT NULL,
                              fecha_hora DATETIME NOT NULL,
                              mensaje TEXT NOT NULL,
                              estado_envio VARCHAR(50) NOT NULL,
                              FOREIGN KEY (id_emergencia) REFERENCES EMERGENCIA(id_emergencia),
                              FOREIGN KEY (id_alarma) REFERENCES ALARMA(id_alarma)
) ENGINE=InnoDB;

CREATE TABLE AUTORIDAD (
                           id_autoridad INT AUTO_INCREMENT PRIMARY KEY,
                           nombre VARCHAR(100) NOT NULL,
                           dependencia VARCHAR(100) NOT NULL,
                           telefono_contacto VARCHAR(20) NOT NULL,
                           canalizacion_emergencia TEXT
) ENGINE=InnoDB;