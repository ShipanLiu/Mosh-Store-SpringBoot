-- Initialize MySQL database for Mosh Store application
-- This script runs when the MySQL container starts for the first time

-- Create production database if it doesn't exist
CREATE DATABASE IF NOT EXISTS cdb_mosh_p1_ecommerce_app_prod 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- Create UAT database if it doesn't exist
CREATE DATABASE IF NOT EXISTS cdb_mosh_p1_ecommerce_app_uat 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- Create development database if it doesn't exist
CREATE DATABASE IF NOT EXISTS cdb_mosh_p1_ecommerce_app_dev 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- Create test database if it doesn't exist
CREATE DATABASE IF NOT EXISTS cdb_mosh_p1_ecommerce_app_test 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- Show created databases
SHOW DATABASES;

-- Show root user privileges
SHOW GRANTS FOR 'root'@'%';
SHOW GRANTS FOR 'root'@'localhost'; 