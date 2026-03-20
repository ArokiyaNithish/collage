/* ── COMPREHENSIVE VIEW: User Profiles & Activity ──
   Combines Users, Registrations, and Payments with Plaintext Password Hints
*/

IF OBJECT_ID('vw_UserProfiles_Audit', 'V') IS NOT NULL DROP VIEW vw_UserProfiles_Audit;
GO

CREATE VIEW vw_UserProfiles_Audit AS
SELECT 
    u.id AS UserId,
    u.full_name AS StudentName,
    u.email AS Email,
    -- NOTE: For recovery/admin purposes only. In production, use hashing.
    u.password AS Plaintext_Password_Hint, 
    u.department AS Department,
    u.roll_number AS RollNumber,
    u.phone AS Phone,
    COUNT(r.registration_id) AS TotalRegistrations,
    SUM(CASE WHEN p.payment_status = 'SUCCESS' THEN p.amount ELSE 0 END) AS TotalPaidAmount,
    MAX(r.registered_at) AS LastActivity
FROM Users u
LEFT JOIN Registrations r ON u.id = r.student_id
LEFT JOIN Payments p ON r.registration_id = p.registration_id
WHERE u.role = 'STUDENT'
GROUP BY u.id, u.full_name, u.email, u.password, u.department, u.roll_number, u.phone;
GO

/* ── COMPREHENSIVE VIEW: Workshop Revenue Analysis ── */
IF OBJECT_ID('vw_WorkshopRevenue_Full', 'V') IS NOT NULL DROP VIEW vw_WorkshopRevenue_Full;
GO

CREATE VIEW vw_WorkshopRevenue_Full AS
SELECT 
    w.id AS WorkshopId,
    w.title AS WorkshopTitle,
    w.instructor AS Instructor,
    w.fee AS IndividualFee,
    w.capacity AS TotalCapacity,
    (w.capacity - w.seats_available) AS SeatsBooked,
    COUNT(r.registration_id) AS TotalRegistrationAttempts,
    SUM(CASE WHEN p.payment_status = 'SUCCESS' THEN 1 ELSE 0 END) AS ConfirmedParticipants,
    FORMAT(SUM(CASE WHEN p.payment_status = 'SUCCESS' THEN p.amount ELSE 0 END), 'C', 'en-IN') AS TotalRevenue,
    w.status AS WorkshopStatus
FROM Workshops w
LEFT JOIN Registrations r ON w.id = r.workshop_id
LEFT JOIN Payments p ON r.registration_id = p.registration_id
GROUP BY w.id, w.title, w.instructor, w.fee, w.capacity, w.seats_available, w.status;
GO
