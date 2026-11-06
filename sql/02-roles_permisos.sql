-- =========================================================
-- PROYECTO S.P.S. - SISTEMA DE MONITOREO DE SEGURIDAD
-- 02-roles-permisos.sql
-- Carga de roles, catálogo de permisos y matriz ROL_PERMISO
-- Es idempotente: se puede ejecutar varias veces sin duplicar datos.
-- Debe correr DESPUÉS de 01-schema.sql
-- =========================================================

USE sps_db;

-- ---------------------------------------------------------
-- 1. ROLES (upsert por nombre, que es UNIQUE)
-- ---------------------------------------------------------
INSERT INTO ROL (nombre, descripcion) VALUES
                                          ('ROLE_ADMIN',    'Administrador general del sistema'),
                                          ('ROLE_OPERADOR', 'Monitoreo de alarmas y dispositivos en tiempo real'),
                                          ('ROLE_GUARDIA',  'Personal operativo de campo')
    ON DUPLICATE KEY UPDATE descripcion = VALUES(descripcion);

-- ---------------------------------------------------------
-- 2. CATÁLOGO DE PERMISOS (upsert por clave_permiso, que es UNIQUE)
-- ---------------------------------------------------------
INSERT INTO PERMISO (clave_permiso, descripcion) VALUES
                                                     -- Acceso
                                                     ('USUARIO_VER',           'Ver listado de usuarios'),
                                                     ('USUARIO_GESTION',       'Crear, editar y activar/desactivar usuarios'),
                                                     ('ROL_GESTION',           'Ver y editar roles y asignar permisos'),
                                                     -- Clientes
                                                     ('CLIENTE_VER',           'Consultar clientes'),
                                                     ('CLIENTE_GESTION',       'Alta, edición y baja lógica de clientes'),
                                                     -- Servicios
                                                     ('SERVICIO_VER',          'Consultar puntos de servicio'),
                                                     ('SERVICIO_GESTION',      'Alta y edición de servicios'),
                                                     -- Personal
                                                     ('GUARDIA_VER',           'Consultar guardias'),
                                                     ('GUARDIA_GESTION',       'Alta, edición y activar/desactivar guardias'),
                                                     ('JORNADA_VER',           'Consultar turnos'),
                                                     ('JORNADA_GESTION',       'Crear y editar jornadas'),
                                                     ('ASIGNACION_VER',        'Ver matriz de asignaciones'),
                                                     ('ASIGNACION_GESTION',    'Asignar guardias a servicio y jornada'),
                                                     ('ASIGNACION_PROPIA_VER', 'Ver únicamente las asignaciones propias'),
                                                     -- Elementos supervisados
                                                     ('ELEMENTO_VER',          'Consultar elementos supervisados'),
                                                     ('ELEMENTO_GESTION',      'Alta y edición de elementos supervisados'),
                                                     -- IoT
                                                     ('DISPOSITIVO_VER',       'Consultar dispositivos'),
                                                     ('DISPOSITIVO_GESTION',   'Registrar y activar/desactivar dispositivos'),
                                                     ('DISPOSITIVO_CONFIG',    'Configurar parámetros de red (IP/MAC) de ESP32'),
                                                     -- Alarmas
                                                     ('ALARMA_VER',            'Ver tablero e historial de alarmas'),
                                                     ('ALARMA_GESTION',        'Cambiar estado de alarmas (ATENDIDA / FALSA_ALARMA)'),
                                                     ('ALARMA_SIMULAR',        'Disparar alarma de prueba (CU-12)'),
                                                     ('EMERGENCIA_ESCALAR',    'Confirmar y notificar a autoridades'),
                                                     -- Notificaciones
                                                     ('NOTIFICACION_VER',      'Ver bandeja propia y marcar como leídas'),
                                                     -- Dashboard
                                                     ('DASHBOARD_VER',         'Ver KPIs del dashboard')
    ON DUPLICATE KEY UPDATE descripcion = VALUES(descripcion);

-- ---------------------------------------------------------
-- 3. MATRIZ ROL_PERMISO
-- Se limpia y se vuelve a cargar para que el resultado siempre
-- coincida con esta matriz (reemplaza el seed corto de 01-schema.sql).
-- Se resuelve por nombre/clave, así no depende de los IDs.
-- ---------------------------------------------------------
DELETE FROM ROL_PERMISO;

-- ROLE_ADMIN: todos los permisos
INSERT INTO ROL_PERMISO (id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso
FROM ROL r
         CROSS JOIN PERMISO p
WHERE r.nombre = 'ROLE_ADMIN';

-- ROLE_OPERADOR: operación de monitoreo, sin administración de usuarios ni catálogos maestros
INSERT INTO ROL_PERMISO (id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso
FROM ROL r
         JOIN PERMISO p ON p.clave_permiso IN (
                                               'CLIENTE_VER',
                                               'SERVICIO_VER',
                                               'GUARDIA_VER',
                                               'JORNADA_VER',
                                               'ASIGNACION_VER',
                                               'ASIGNACION_GESTION',
                                               'ASIGNACION_PROPIA_VER',
                                               'ELEMENTO_VER',
                                               'ELEMENTO_GESTION',
                                               'DISPOSITIVO_VER',
                                               'DISPOSITIVO_CONFIG',
                                               'ALARMA_VER',
                                               'ALARMA_GESTION',
                                               'ALARMA_SIMULAR',
                                               'EMERGENCIA_ESCALAR',
                                               'NOTIFICACION_VER',
                                               'DASHBOARD_VER'
    )
WHERE r.nombre = 'ROLE_OPERADOR';

-- ROLE_GUARDIA: solo lectura de lo que le concierne
INSERT INTO ROL_PERMISO (id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso
FROM ROL r
         JOIN PERMISO p ON p.clave_permiso IN (
                                               'SERVICIO_VER',
                                               'JORNADA_VER',
                                               'ASIGNACION_PROPIA_VER',
                                               'ELEMENTO_VER',
                                               'ALARMA_VER',
                                               'NOTIFICACION_VER',
                                               'DASHBOARD_VER'
    )
WHERE r.nombre = 'ROLE_GUARDIA';

-- ---------------------------------------------------------
-- 4. VERIFICACIÓN (opcional): permisos por rol
-- ---------------------------------------------------------
-- SELECT r.nombre AS rol, COUNT(*) AS total_permisos
-- FROM ROL r JOIN ROL_PERMISO rp ON rp.id_rol = r.id_rol
-- GROUP BY r.nombre;
-- Esperado: ROLE_ADMIN = 25, ROLE_OPERADOR = 17, ROLE_GUARDIA = 7