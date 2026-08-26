-- Clase 5 - Primer contacto con un motor de base de datos (MariaDB)

CREATE DATABASE IF NOT EXISTS prog2_db;

USE prog2_db;

CREATE TABLE IF NOT EXISTS estudiantes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    carnet VARCHAR(20) NOT NULL UNIQUE,
    activo TINYINT(1) NOT NULL DEFAULT 1,
    tipo ENUM('Pregrado', 'Posgrado') NOT NULL DEFAULT 'Pregrado'
);

INSERT IGNORE INTO estudiantes (nombre, carnet, activo, tipo)
VALUES
('anticio Gonzalez', '2024001', 1, 'Pregrado'),
('rolant ronzalez', '2024002', 0, 'Pregrado'),
('antonio Gonzalez', '2024003', 1, 'Pregrado'),
('Maria Gonzalez', '2024004', 0, 'Pregrado');
-- Ver todos los estudiantes
SELECT * FROM estudiantes;

-- Ver solamente los estudiantes activos
SELECT  * FROM estudiantes
WHERE activo = 1;

-- Ver solamente los estudiantes activos
SELECT  * FROM estudiantes
WHERE activo = 0;
