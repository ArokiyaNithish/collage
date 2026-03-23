USE WorkshopDB;
GO

-- 1. Create View for Registration Details (Matches Upper Table)
IF OBJECT_ID('vw_RegistrationDetails', 'V') IS NOT NULL
    DROP VIEW vw_RegistrationDetails;
GO

CREATE VIEW vw_RegistrationDetails AS
SELECT 
    r.id AS Id,
    u.full_name AS UserName,
    u.email AS Email,
    u.phone AS Phone,
    u.department AS Dept,
    u.roll_number AS YearOrRoll,
    w.title AS EventName,
    r.team_members_count AS NumTickets,
    ISNULL(p.amount, 0) AS TotalAmount,
    r.registered_at AS BookingDate
FROM 
    registrations r
JOIN 
    users u ON r.student_id = u.id
JOIN 
    workshops w ON r.workshop_id = w.id
LEFT JOIN 
    payments p ON p.registration_id = r.id;
GO

-- 2. Create View for Workshop Details (Matches Lower Table)
IF OBJECT_ID('vw_WorkshopDetails', 'V') IS NOT NULL
    DROP VIEW vw_WorkshopDetails;
GO

CREATE VIEW vw_WorkshopDetails AS
SELECT 
    w.id AS Id,
    w.title AS Name,
    -- Department of coordinator (if user exists, else default)
    ISNULL(u.department, 'Various') AS Department,
    CONVERT(VARCHAR, w.start_date, 107) + ' | ' + ISNULL(CONVERT(VARCHAR, w.start_date, 108), '') AS DateTimeDisplay,
    w.venue AS Venue,
    w.fee AS Price,
    w.seats_available AS AvailableTickets,
    w.description AS Description,
    w.description AS Details,
    w.coordinator_name AS Coordinator
FROM 
    workshops w
LEFT JOIN 
    users u ON w.created_by = u.id;
GO
