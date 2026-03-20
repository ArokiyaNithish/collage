-- Workshop Portal Schema (Restored)
CREATE TABLE Users (
    id INT PRIMARY KEY IDENTITY(1,1),
    full_name NVARCHAR(100) NOT NULL,
    email NVARCHAR(100) UNIQUE NOT NULL,
    password NVARCHAR(255) NOT NULL,
    role NVARCHAR(20) DEFAULT 'STUDENT',
    department NVARCHAR(50),
    phone NVARCHAR(15),
    roll_number NVARCHAR(50),
    address NVARCHAR(MAX),
    hod_contact NVARCHAR(100),
    total_amount_spent DECIMAL(10,2) DEFAULT 0,
    created_at DATETIME DEFAULT GETDATE()
);

CREATE TABLE Workshops (
    id INT PRIMARY KEY IDENTITY(1,1),
    title NVARCHAR(200) NOT NULL,
    description NVARCHAR(MAX),
    instructor NVARCHAR(100),
    venue NVARCHAR(100),
    category NVARCHAR(50),
    start_date DATETIME,
    end_date DATETIME,
    capacity INT,
    seats_available INT,
    fee DECIMAL(10,2),
    status NVARCHAR(20) DEFAULT 'UPCOMING',
    coordinator_name NVARCHAR(100),
    coordinator_phone NVARCHAR(15),
    coordinator_email NVARCHAR(100),
    team_size INT DEFAULT 1
);

CREATE TABLE Registrations (
    registration_id INT PRIMARY KEY IDENTITY(1,1),
    student_id INT REFERENCES Users(id),
    workshop_id INT REFERENCES Workshops(id),
    registration_status NVARCHAR(20) DEFAULT 'PENDING',
    registered_at DATETIME DEFAULT GETDATE(),
    team_members_count INT DEFAULT 1,
    team_members_list NVARCHAR(MAX) -- JSON String
);

CREATE TABLE Payments (
    payment_id INT PRIMARY KEY IDENTITY(1,1),
    registration_id INT REFERENCES Registrations(registration_id),
    amount DECIMAL(10,2),
    payment_method NVARCHAR(20),
    payment_status NVARCHAR(20) DEFAULT 'PENDING',
    transaction_id NVARCHAR(100) UNIQUE,
    upi_id NVARCHAR(100),
    paid_at DATETIME
);
