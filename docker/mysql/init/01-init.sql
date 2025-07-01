-- Initialize MySQL database for Mosh Store application
-- This script runs when the MySQL container starts for the first time

-- Create application user if it doesn't exist
CREATE USER IF NOT EXISTS 'moshuser'@'%' IDENTIFIED BY 'your_password_here';

-- Grant necessary privileges
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, INDEX, ALTER, CREATE TEMPORARY TABLES, LOCK TABLES ON *.* TO 'moshuser'@'%';

-- Create production database if it doesn't exist
CREATE DATABASE IF NOT EXISTS cdb_mosh_p1_ecommerce_app_prod 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- Grant privileges on production database
GRANT ALL PRIVILEGES ON cdb_mosh_p1_ecommerce_app_prod.* TO 'moshuser'@'%';

-- Create UAT database if it doesn't exist
CREATE DATABASE IF NOT EXISTS cdb_mosh_p1_ecommerce_app_uat 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- Grant privileges on UAT database
GRANT ALL PRIVILEGES ON cdb_mosh_p1_ecommerce_app_uat.* TO 'moshuser'@'%';

-- Flush privileges to ensure they take effect
FLUSH PRIVILEGES;

-- Show created databases
SHOW DATABASES;

-- Show users
SELECT User, Host FROM mysql.user WHERE User IN ('root', 'moshuser'); 