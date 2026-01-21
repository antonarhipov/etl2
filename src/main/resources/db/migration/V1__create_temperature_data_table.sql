CREATE TABLE temperature_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    datetime DATETIME NOT NULL,
    temp DECIMAL(5,1) NOT NULL,
    UNIQUE KEY uk_name_datetime (name, datetime)
);
