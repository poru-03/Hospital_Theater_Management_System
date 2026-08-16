# Theater Management System

The **Theater Management System** is a Java-based desktop application built with a MySQL database to manage hospital surgical operations. It provides role-based access where admins manage staff and resources, nurses register patients and book surgeries, doctors update post-operation notes, and patients can check their surgery status and medical records. The system uses JDBC for database connectivity and features a GUI separated into distinct dashboards for each user type.

## 🚀 Features

- **Admin Dashboard**: Manage hospital staff (Doctors, Nurses) and resources (Theaters, Operation Types).
- **Nurse Dashboard**: Register new patients and schedule surgical bookings for specific doctors and theaters.
- **Doctor Dashboard**: View upcoming surgery schedules and update post-operation medical notes and prescriptions.
- **Patient Portal**: Log in to check the status of scheduled surgeries and view post-operation medical records.

## 🛠️ Technology Stack

- **Language**: Java
- **UI Framework**: Java Swing / AWT
- **Database**: MySQL
- **Database Connectivity**: JDBC

## 📋 Prerequisites

To run this project, you will need:
- Java Development Kit (JDK) 8 or higher
- MySQL Server
- MySQL JDBC Connector

## ⚙️ Setup & Installation

1. **Database Setup**:
   - Start your MySQL server.
   - Create a database named `theater_management_db`.
   - Run the SQL scripts provided in the `SQL file` directory to set up the necessary tables and seed data.

2. **Project Setup**:
   - Clone the repository.
   - Open the project in your preferred IDE (e.g., IntelliJ IDEA, Eclipse, VS Code).
   - Add the MySQL JDBC Connector (`mysql-connector-java.jar`) to your project's build path/libraries.

3. **Running the Application**:
   - Run the `Main.java` file (or the appropriate UI launcher) to start the application.

## 👥 User Roles

| Role | Username (Example) | Password | Responsibilities |
| :--- | :--- | :--- | :--- |
| **Admin** | admin | admin123 | Manage Staff, Manage Resources |
| **Doctor** | doctor_username | pass123 | View Schedule, Complete Surgery (Post-Op) |
| **Nurse** | nurse_username | pass123 | Register Patient, Book Surgery |
| **Patient** | patient_id | phone_number | View Booking Status, View Medical Notes |

---
*This project was developed as a university OOP and Database project.*
