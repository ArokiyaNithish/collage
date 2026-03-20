-- ============================================================
-- FILE: migration.sql
-- Run this in SSMS connected to WorkshopDB
-- ============================================================

USE WorkshopDB;
GO

-- ─────────────────────────────────────────────────────────────
-- UPDATE: users table
-- ─────────────────────────────────────────────────────────────
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('users') AND name = 'address')
    ALTER TABLE users ADD address NVARCHAR(500);

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('users') AND name = 'hod_contact')
    ALTER TABLE users ADD hod_contact NVARCHAR(100);

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('users') AND name = 'external_user')
    ALTER TABLE users ADD external_user BIT DEFAULT 0;
GO

-- ─────────────────────────────────────────────────────────────
-- UPDATE: workshops table
-- ─────────────────────────────────────────────────────────────
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('workshops') AND name = 'coordinator_name')
    ALTER TABLE workshops ADD coordinator_name NVARCHAR(100);

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('workshops') AND name = 'coordinator_phone')
    ALTER TABLE workshops ADD coordinator_phone NVARCHAR(20);

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('workshops') AND name = 'coordinator_email')
    ALTER TABLE workshops ADD coordinator_email NVARCHAR(100);
GO

-- ─────────────────────────────────────────────────────────────
-- UPDATE: registrations table
-- ─────────────────────────────────────────────────────────────
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('registrations') AND name = 'attended')
    ALTER TABLE registrations ADD attended BIT DEFAULT 0;

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('registrations') AND name = 'check_in_time')
    ALTER TABLE registrations ADD check_in_time DATETIME2;

IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('registrations') AND name = 'check_out_time')
    ALTER TABLE registrations ADD check_out_time DATETIME2;
GO

PRINT 'Successfully updated WorkshopDB schema with missing columns.';
GO
