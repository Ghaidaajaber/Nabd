package org.example.nabd;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class HospitalOperations {

    // Departments
    // getDepartments()

    public ObservableList<ObservableList<String>> getData(String tableName) {

        ObservableList<ObservableList<String>> data =
                FXCollections.observableArrayList();

        if (!List.of("Department", "Doctor", "Patient", "Appointment", "MedicalRecord", "Users").contains(tableName)) {
            return data;
        }

        String sql = "SELECT * FROM " + tableName;

        try (Connection con = TestConnection.makeConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int columnCount = rs.getMetaData().getColumnCount();

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();

                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getString(i));
                }

                data.add(row);
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return data;
    }

    // addDepartment()
    public boolean addDepartment(String name, String location, String phone) {

        String sql = "INSERT INTO Department (DepartmentName, Location, Phone) VALUES ('"
                + name + "', '" + location + "', '" + phone + "')";

        try (Connection con = TestConnection.makeConnection();
             Statement stmt = con.createStatement()) {

            return stmt.executeUpdate(sql) > 0;

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    // updateDepartment()
    public boolean updateDepartment(int id, String name, String location, String phone) {

        String sql = "UPDATE Department SET " + "DepartmentName = '" + name.replace("'", "''") + "', "
                + "Location = '" + location.replace("'", "''") + "', "
                + "Phone = '" + phone.replace("'", "''") + "' "
                + "WHERE DepartmentID = " + id;

        try (Connection con = TestConnection.makeConnection();
             Statement stmt = con.createStatement()) {

            return stmt.executeUpdate(sql) > 0;

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    // deleteDepartment()

    public boolean deleteData(String tableName, String idColumn, int id) {

        if (!List.of("Department", "Doctor", "Patient", "Appointment", "MedicalRecord").contains(tableName)) {
            return false;
        }

        if (!idColumn.equals(tableName + "ID") && !(tableName.equals("MedicalRecord") && idColumn.equals("RecordID"))) {
            return false;
        }

        String sql = "DELETE FROM " + tableName + " WHERE " + idColumn + " = " + id;

        try (Connection con = TestConnection.makeConnection();
             Statement stmt = con.createStatement()) {

            return stmt.executeUpdate(sql) > 0;

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    // searchDepartments()

    public ObservableList<ObservableList<String>> searchData(String tableName, String column, String search) {

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        String allowedColumns;

        switch (tableName) {
            case "Department": allowedColumns = "DepartmentID,DepartmentName,Location,Phone";
                break;
            case "Doctor": allowedColumns = "DoctorID,Name,Specialization,Phone,Email,DepartmentID";
                break;
            case "Patient": allowedColumns = "PatientID,Name,DateOfBirth,Gender,Phone,Email,Address,BloodType";
                break;
            case "Appointment": allowedColumns = "AppointmentID,PatientID,DoctorID,AppointmentDate,Reason,Status";
                break;
            case "MedicalRecord": allowedColumns = "RecordID,PatientID,DoctorID,AppointmentID,RecordDate,Symptoms,Diagnosis,Treatment,Notes";
                break;
            default:
                return data;
        }

        if (!List.of(allowedColumns.split(",")).contains(column)) {
            return data;
        }

        String searchColumn = column;

        if (column.equals("Name") && (tableName.equals("Doctor") || tableName.equals("Patient"))) {
            searchColumn = "CONCAT(FirstName, ' ', LastName)";
        }

        String sql = "SELECT * FROM " + tableName
                + " WHERE " + searchColumn + " LIKE ?";
        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + search + "%");

            try (ResultSet rs = ps.executeQuery()) {

                int columnCount = rs.getMetaData().getColumnCount();

                while (rs.next()) {

                    ObservableList<String> row =
                            FXCollections.observableArrayList();

                    for (int i = 1; i <= columnCount; i++) {
                        row.add(rs.getString(i));
                    }

                    data.add(row);
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return data;
    }


    // Doctors
    // getDoctors()
    // addDoctor()

    public boolean addDoctor(String firstName, String lastName, String specialization, String phone, String email, int departmentId) {

        String sql = "INSERT INTO Doctor "
                + "(FirstName, LastName, Specialization, Phone, Email, DepartmentID) "
                + "VALUES ('" + firstName + "', '" + lastName + "', '" + specialization + "', '"
                + phone + "', '" + email + "', " + departmentId + ")";

        try (Connection con = TestConnection.makeConnection();
             Statement stmt = con.createStatement()) {

            return stmt.executeUpdate(sql) > 0;

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    // updateDoctor()
    public boolean updateDoctor(int doctorId, String firstName, String lastName, String specialization, String phone, String email, int departmentId) {

        String sql = "UPDATE Doctor SET " + "FirstName = '" + firstName + "', " + "LastName = '" + lastName + "', "
                + "Specialization = '" + specialization + "', " + "Phone = '" + phone + "', " + "Email = '" + email + "', "
                + "DepartmentID = " + departmentId + " WHERE DoctorID = " + doctorId;

        try (Connection con = TestConnection.makeConnection();
             Statement stmt = con.createStatement()) {

            return stmt.executeUpdate(sql) > 0;

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }



    // deleteDoctor()
    // searchDoctors()

    // Doctor Patients

    public ObservableList<ObservableList<String>> getDoctorPatients(
            String searchBy, String search) {

        ObservableList<ObservableList<String>> data =
                FXCollections.observableArrayList();

        Integer doctorId = TestConnection.getCurrentDoctorId();

        if (!"Doctor".equals(TestConnection.getCurrentRole())
                || doctorId == null) {

            return data;
        }

        String column;

        switch (searchBy) {

            case "PatientID":
                column = "p.PatientID";
                break;

            case "Name":
                column = "CONCAT(p.FirstName, ' ', p.LastName)";
                break;

            default:
                return data;
        }

        String sql =
                "SELECT p.PatientID, p.FirstName, p.LastName, " +
                        "p.DateOfBirth, p.Gender, p.Phone, " +
                        "p.Email, p.Address, p.BloodType " +
                        "FROM Patient p " +
                        "WHERE EXISTS (" +
                        "SELECT 1 FROM Appointment a " +
                        "WHERE a.PatientID = p.PatientID " +
                        "AND a.DoctorID = ? " +
                        "AND a.Status <> 'Cancelled') " +
                        "AND (? = '' OR " + column + " LIKE ?) " +
                        "ORDER BY p.FirstName, p.LastName";

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            ps.setString(2, search);
            ps.setString(3, "%" + search + "%");

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    ObservableList<String> row =
                            FXCollections.observableArrayList();

                    for (int i = 1; i <= 9; i++) {
                        row.add(rs.getString(i));
                    }

                    data.add(row);
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return data;
    }

    // Patients
    public int addPatient(String firstName, String lastName, LocalDate dateOfBirth, String gender, String phone, String email, String address, String bloodType) {

        String sql = "INSERT INTO Patient (FirstName, LastName, DateOfBirth, Gender, Phone, Email, Address, BloodType) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setDate(3, java.sql.Date.valueOf(dateOfBirth));
            ps.setString(4, gender);
            ps.setString(5, phone);
            ps.setString(6, email == null || email.isBlank() ? null : email);
            ps.setString(7, address);
            ps.setString(8, bloodType);

            if (ps.executeUpdate() > 0) {

                try (ResultSet rs = ps.getGeneratedKeys()) {

                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return -1;
    }
    public boolean updatePatient(int patientId, String firstName, String lastName, LocalDate dateOfBirth, String gender, String phone, String email, String address, String bloodType) {

        String sql = "UPDATE Patient SET FirstName = ?, LastName = ?, DateOfBirth = ?, Gender = ?, Phone = ?, Email = ?, Address = ?, BloodType = ? WHERE PatientID = ?";

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setDate(3, java.sql.Date.valueOf(dateOfBirth));
            ps.setString(4, gender);
            ps.setString(5, phone);
            ps.setString(6, email == null || email.isBlank() ? null : email);
            ps.setString(7, address);
            ps.setString(8, bloodType);
            ps.setInt(9, patientId);

            return ps.executeUpdate() > 0;

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    // ...

    // Appointments
    public ObservableList<ObservableList<String>> getAppointments(
            String searchBy, String search, String status, LocalDate date) {

        ObservableList<ObservableList<String>> data =
                FXCollections.observableArrayList();

        String role = TestConnection.getCurrentRole();

        Integer doctorId = TestConnection.getCurrentDoctorId();

        if ("Doctor".equals(role)) {

            if (doctorId == null) {
                return data;
            }

        } else if (!"Administrator".equals(role)
                && !"Receptionist".equals(role)) {

            return data;
        }

        String column;

        switch (searchBy) {

            case "AppointmentID":
                column = "a.AppointmentID";
                break;

            case "PatientName":
                column = "CONCAT(p.FirstName, ' ', p.LastName)";
                break;

            case "DoctorName":
                column = "CONCAT(d.FirstName, ' ', d.LastName)";
                break;

            case "Reason":
                column = "a.Reason";
                break;

            default:
                return data;
        }

        String sql = "SELECT a.AppointmentID, "
                + "CONCAT(p.FirstName, ' ', p.LastName) AS PatientName, "
                + "CONCAT(d.FirstName, ' ', d.LastName) AS DoctorName, "
                + "DATE(a.AppointmentDate), TIME(a.AppointmentDate), "
                + "a.Reason, a.Status, a.PatientID, a.DoctorID "
                + "FROM Appointment a "
                + "JOIN Patient p ON a.PatientID = p.PatientID "
                + "JOIN Doctor d ON a.DoctorID = d.DoctorID "
                + "WHERE (? = '' OR " + column + " LIKE ?) "
                + "AND (? = 'All Status' OR a.Status = ?) "
                + "AND (? IS NULL OR DATE(a.AppointmentDate) = ?) ";

        if ("Doctor".equals(role)) {
            sql += "AND a.DoctorID = ? ";
        }

        sql += "ORDER BY a.AppointmentDate";

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, search);
            ps.setString(2, "%" + search + "%");

            ps.setString(3, status);
            ps.setString(4, status);

            ps.setDate(5, date == null ? null : java.sql.Date.valueOf(date));
            ps.setDate(6, date == null ? null : java.sql.Date.valueOf(date));

            if ("Doctor".equals(role)) {
                ps.setInt(7, doctorId);
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    ObservableList<String> row =
                            FXCollections.observableArrayList();

                    for (int i = 1;
                         i <= rs.getMetaData().getColumnCount(); i++) {

                        row.add(rs.getString(i));
                    }

                    data.add(row);
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return data;
    }
    public boolean addAppointment(int patientId, int doctorId, LocalDateTime appointmentDate, String reason, String status) {

        String sql = "INSERT INTO Appointment (PatientID, DoctorID, AppointmentDate, Reason, Status) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setTimestamp(3, Timestamp.valueOf(appointmentDate));
            ps.setString(4, reason);
            ps.setString(5, status);

            return ps.executeUpdate() > 0;

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean updateAppointment(int appointmentId, int patientId, int doctorId, LocalDateTime appointmentDate, String reason, String status) {

        String sql = "UPDATE Appointment SET PatientID = ?, DoctorID = ?, AppointmentDate = ?, Reason = ?, Status = ? WHERE AppointmentID = ?";

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setTimestamp(3, Timestamp.valueOf(appointmentDate));
            ps.setString(4, reason);
            ps.setString(5, status);
            ps.setInt(6, appointmentId);

            return ps.executeUpdate() > 0;

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasAppointmentConflict(int appointmentId, int patientId, int doctorId, LocalDateTime appointmentDate) throws SQLException, ClassNotFoundException {

        String sql = "SELECT COUNT(*) FROM Appointment WHERE AppointmentDate = ? AND (DoctorID = ? OR PatientID = ?) AND Status <> 'Cancelled' AND AppointmentID <> ?";

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(appointmentDate));
            ps.setInt(2, doctorId);
            ps.setInt(3, patientId);
            ps.setInt(4, appointmentId);

            try (ResultSet rs = ps.executeQuery()) {

                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
    public boolean hasMedicalRecord(int appointmentId) throws SQLException, ClassNotFoundException {

        String sql = "SELECT COUNT(*) FROM MedicalRecord WHERE AppointmentID = ?";

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, appointmentId);

            try (ResultSet rs = ps.executeQuery()) {

                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
    // ...

    // Medical Records
    public ObservableList<ObservableList<String>> getMedicalRecords(
            String searchBy, String search) {

        ObservableList<ObservableList<String>> data =
                FXCollections.observableArrayList();

        String role = TestConnection.getCurrentRole();

        Integer doctorId = TestConnection.getCurrentDoctorId();

        if ("Doctor".equals(role)) {

            if (doctorId == null) {
                return data;
            }

        } else if (!"Administrator".equals(role)) {

            return data;
        }

        String column;

        switch (searchBy) {

            case "RecordID":
                column = "m.RecordID";
                break;

            case "PatientID":
                column = "m.PatientID";
                break;

            case "PatientName":
                column = "CONCAT(p.FirstName, ' ', p.LastName)";
                break;

            case "DoctorName":
                column = "CONCAT(d.FirstName, ' ', d.LastName)";
                break;

            case "AppointmentID":
                column = "m.AppointmentID";
                break;

            case "Diagnosis":
                column = "m.Diagnosis";
                break;

            case "RecordDate":
                column = "m.RecordDate";
                break;

            default:
                return data;
        }

        String sql =
                "SELECT m.RecordID, " +
                        "CONCAT(p.FirstName, ' ', p.LastName), " +
                        "CONCAT(d.FirstName, ' ', d.LastName), " +
                        "m.AppointmentID, m.RecordDate, " +
                        "m.Symptoms, m.Diagnosis, m.Treatment, " +
                        "m.Notes, m.PatientID, d.Specialization " +
                        "FROM MedicalRecord m " +
                        "JOIN Patient p ON m.PatientID = p.PatientID " +
                        "JOIN Doctor d ON m.DoctorID = d.DoctorID " +
                        "WHERE (? = '' OR " + column + " LIKE ?) ";

        if ("Doctor".equals(role)) {

            sql += "AND m.DoctorID = ? ";
        }

        sql += "ORDER BY m.RecordDate DESC";

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, search);
            ps.setString(2, "%" + search + "%");

            if ("Doctor".equals(role)) {

                ps.setInt(3, doctorId);
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    ObservableList<String> row =
                            FXCollections.observableArrayList();

                    for (int i = 1;
                         i <= rs.getMetaData().getColumnCount(); i++) {

                        row.add(rs.getString(i));
                    }

                    data.add(row);
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return data;
    }
    public boolean updateMedicalRecord(int recordId, LocalDate recordDate,
                                       String symptoms, String diagnosis,
                                       String treatment, String notes) {

        String role = TestConnection.getCurrentRole();

        Integer doctorId = TestConnection.getCurrentDoctorId();

        // Check permissions

        if ("Doctor".equals(role)) {

            if (doctorId == null) {
                return false;
            }

        } else if (!"Administrator".equals(role)) {

            return false;
        }

        // Validate data

        if (recordDate == null
                || recordDate.isAfter(LocalDate.now())
                || diagnosis == null
                || diagnosis.isBlank()) {

            return false;
        }

        String sql = "UPDATE MedicalRecord m "
                + "JOIN Appointment a ON m.AppointmentID = a.AppointmentID "
                + "SET m.RecordDate = ?, "
                + "m.Symptoms = ?, m.Diagnosis = ?, "
                + "m.Treatment = ?, m.Notes = ? "
                + "WHERE m.RecordID = ? "
                + "AND DATE(a.AppointmentDate) <= ? ";

        // Doctor can only update their own records

        if ("Doctor".equals(role)) {

            sql += "AND m.DoctorID = ? ";
        }

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(recordDate));
            ps.setString(2, symptoms);
            ps.setString(3, diagnosis);
            ps.setString(4, treatment);
            ps.setString(5, notes);
            ps.setInt(6, recordId);
            ps.setDate(7, java.sql.Date.valueOf(recordDate));

            if ("Doctor".equals(role)) {

                ps.setInt(8, doctorId);
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException | ClassNotFoundException e) {

            e.printStackTrace();

            return false;
        }
    }
    public ObservableList<ObservableList<String>> getAvailableAppointments(
            int patientId) {

        ObservableList<ObservableList<String>> data =
                FXCollections.observableArrayList();

        String role = TestConnection.getCurrentRole();

        Integer doctorId = TestConnection.getCurrentDoctorId();

        // Check permissions

        if ("Doctor".equals(role)) {

            if (doctorId == null) {
                return data;
            }

        } else if (!"Administrator".equals(role)) {

            return data;
        }

        String sql = "SELECT a.AppointmentID, a.DoctorID, "
                + "CONCAT(d.FirstName, ' ', d.LastName), "
                + "d.Specialization, DATE(a.AppointmentDate), "
                + "TIME(a.AppointmentDate) "
                + "FROM Appointment a "
                + "JOIN Doctor d ON a.DoctorID = d.DoctorID "
                + "WHERE a.PatientID = ? "
                + "AND a.Status = 'Completed' "
                + "AND NOT EXISTS (SELECT 1 FROM MedicalRecord m "
                + "WHERE m.AppointmentID = a.AppointmentID) ";

        // Doctor can only select their own appointments

        if ("Doctor".equals(role)) {

            sql += "AND a.DoctorID = ? ";
        }

        sql += "ORDER BY a.AppointmentDate DESC";

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, patientId);

            if ("Doctor".equals(role)) {

                ps.setInt(2, doctorId);
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    ObservableList<String> row =
                            FXCollections.observableArrayList();

                    for (int i = 1;
                         i <= rs.getMetaData().getColumnCount(); i++) {

                        row.add(rs.getString(i));
                    }

                    data.add(row);
                }
            }

        } catch (SQLException | ClassNotFoundException e) {

            e.printStackTrace();
        }

        return data;
    }
    public boolean addMedicalRecord(int appointmentId, LocalDate recordDate,
                                    String symptoms, String diagnosis,
                                    String treatment, String notes) {

        String role = TestConnection.getCurrentRole();

        Integer doctorId = TestConnection.getCurrentDoctorId();

        // Check permissions

        if ("Doctor".equals(role)) {

            if (doctorId == null) {
                return false;
            }

        } else if (!"Administrator".equals(role)) {

            return false;
        }

        // Validate data

        if (recordDate == null
                || recordDate.isAfter(LocalDate.now())
                || diagnosis == null
                || diagnosis.isBlank()) {

            return false;
        }

        String sql = "INSERT INTO MedicalRecord "
                + "(PatientID, DoctorID, AppointmentID, RecordDate, "
                + "Symptoms, Diagnosis, Treatment, Notes) "
                + "SELECT a.PatientID, a.DoctorID, a.AppointmentID, "
                + "?, ?, ?, ?, ? "
                + "FROM Appointment a "
                + "WHERE a.AppointmentID = ? "
                + "AND a.Status = 'Completed' "
                + "AND DATE(a.AppointmentDate) <= ? "
                + "AND NOT EXISTS (SELECT 1 FROM MedicalRecord m "
                + "WHERE m.AppointmentID = a.AppointmentID) ";

        // Doctor can only add records for their own appointments

        if ("Doctor".equals(role)) {

            sql += "AND a.DoctorID = ? ";
        }

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(recordDate));
            ps.setString(2, symptoms);
            ps.setString(3, diagnosis);
            ps.setString(4, treatment);
            ps.setString(5, notes);
            ps.setInt(6, appointmentId);
            ps.setDate(7, java.sql.Date.valueOf(recordDate));

            if ("Doctor".equals(role)) {

                ps.setInt(8, doctorId);
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException | ClassNotFoundException e) {

            e.printStackTrace();

            return false;
        }
    }
    public ObservableList<ObservableList<String>> getUsers(String search, String role, String status) {

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        String sql = "SELECT UserID, Username, FullName, Role, IsActive, PasswordResetRequested, MustChangePassword FROM Users WHERE (Username LIKE ? OR FullName LIKE ?) AND (? = 'All Roles' OR Role = ?) AND (? = 'All Status' OR IsActive = ?) ORDER BY UserID";

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + search + "%");
            ps.setString(2, "%" + search + "%");

            ps.setString(3, role);
            ps.setString(4, role);

            ps.setString(5, status);
            ps.setBoolean(6, status.equals("Active"));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    ObservableList<String> row =
                            FXCollections.observableArrayList();

                    for (int i = 1; i <= 7; i++) {
                        row.add(rs.getString(i));
                    }

                    data.add(row);
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return data;
    }
    public ObservableList<ObservableList<String>> getDoctorsWithoutAccounts() {
        return getDoctorsWithoutAccounts(0);
    }

    public ObservableList<ObservableList<String>> getDoctorsWithoutAccounts(int userId) {

        ObservableList<ObservableList<String>> data =
                FXCollections.observableArrayList();

        String sql = "SELECT DoctorID, FirstName, LastName, Specialization FROM Doctor WHERE UserID IS NULL OR UserID = ? ORDER BY FirstName, LastName";

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    ObservableList<String> row =
                            FXCollections.observableArrayList();

                    for (int i = 1; i <= 4; i++) {
                        row.add(rs.getString(i));
                    }

                    data.add(row);
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return data;
    }
    public boolean addUser(String username, String fullName, String password, String role, boolean isActive, Integer doctorId) {

        if (username == null || username.isBlank() || fullName == null || fullName.isBlank() || password == null || password.isBlank() || role == null) {
            return false;
        }

        if (!role.equals("Administrator") && !role.equals("Receptionist") && !role.equals("Doctor")) {
            return false;
        }

        if (role.equals("Doctor") && doctorId == null) {
            return false;
        }

        if (!role.equals("Doctor") && doctorId != null) {
            return false;
        }

        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

        String sql = "INSERT INTO Users (Username, PasswordHash, FullName, Role, IsActive, MustChangePassword) VALUES (?, ?, ?, ?, ?, TRUE)";
        try (Connection con = TestConnection.makeConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, username.trim());
                ps.setString(2, passwordHash);
                ps.setString(3, fullName.trim());
                ps.setString(4, role);
                ps.setBoolean(5, isActive);

                if (ps.executeUpdate() != 1) {
                    con.rollback();
                    return false;
                }

                if (role.equals("Doctor")) {
                    int userId;
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }

                        userId = rs.getInt(1);
                    }

                    String linkSql = "UPDATE Doctor SET UserID = ? WHERE DoctorID = ? AND UserID IS NULL";

                    try (PreparedStatement linkPs = con.prepareStatement(linkSql)) {

                        linkPs.setInt(1, userId);
                        linkPs.setInt(2, doctorId);

                        if (linkPs.executeUpdate() != 1) {
                            con.rollback();
                            return false;
                        }
                    }
                }

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                e.printStackTrace();
                return false;
            }

        } catch (SQLException | ClassNotFoundException e) {

            e.printStackTrace();
            return false;
        }
    }
    public boolean isUsernameExists(String username)
            throws SQLException, ClassNotFoundException {

        return isUsernameExists(username, 0);
    }

    public boolean isUsernameExists(String username, int userId)
            throws SQLException, ClassNotFoundException {

        String sql = "SELECT COUNT(*) FROM Users WHERE Username = ? AND UserID <> ?";

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username.trim());
            ps.setInt(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
    public Integer getLinkedDoctor(int userId)
            throws SQLException, ClassNotFoundException {

        String sql = "SELECT DoctorID FROM Doctor WHERE UserID = ?";

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("DoctorID");
                }

                return null;
            }
        }
    }
    public boolean updateUser(int userId, String username,
                              String fullName, String role,
                              boolean isActive, Integer doctorId) {

        if (username == null || username.isBlank() ||
                fullName == null || fullName.isBlank() ||
                role == null) {
            return false;
        }

        if (!role.equals("Administrator") &&
                !role.equals("Receptionist") &&
                !role.equals("Doctor")) {
            return false;
        }

        if (role.equals("Doctor") && doctorId == null) {
            return false;
        }

        if (!role.equals("Doctor") && doctorId != null) {
            return false;
        }

        String sql = "UPDATE Users SET Username = ?, FullName = ?, Role = ?, IsActive = ? WHERE UserID = ?";

        String unlinkSql = "UPDATE Doctor SET UserID = NULL WHERE UserID = ?";

        String linkSql = "UPDATE Doctor SET UserID = ? WHERE DoctorID = ? AND UserID IS NULL";

        try (Connection con = TestConnection.makeConnection()) {

            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sql);
                 PreparedStatement unlinkPs = con.prepareStatement(unlinkSql);
                 PreparedStatement linkPs = con.prepareStatement(linkSql)) {

                // Protect the last active administrator

                boolean lastAdmin = isLastActiveAdministrator(con, userId);

                if (lastAdmin &&
                        (!role.equals("Administrator") || !isActive)) {

                    con.rollback();
                    return false;
                }

                // Update user information

                ps.setString(1, username.trim());
                ps.setString(2, fullName.trim());
                ps.setString(3, role);
                ps.setBoolean(4, isActive);
                ps.setInt(5, userId);

                if (ps.executeUpdate() != 1) {
                    con.rollback();
                    return false;
                }

                // Remove the previous doctor link

                unlinkPs.setInt(1, userId);
                unlinkPs.executeUpdate();

                // Link the account if the role is Doctor

                if (role.equals("Doctor")) {

                    linkPs.setInt(1, userId);
                    linkPs.setInt(2, doctorId);

                    if (linkPs.executeUpdate() != 1) {
                        con.rollback();
                        return false;
                    }
                }

                con.commit();
                return true;

            } catch (SQLException e) {

                con.rollback();
                e.printStackTrace();
                return false;
            }

        } catch (SQLException | ClassNotFoundException e) {

            e.printStackTrace();
            return false;
        }
    }

    public boolean resetPassword(int userId, String password) {

        if (password == null || password.isBlank()) {
            return false;
        }

        if (password.getBytes(
                java.nio.charset.StandardCharsets.UTF_8
        ).length > 72) {
            return false;
        }

        String passwordHash = BCrypt.hashpw(
                password, BCrypt.gensalt()
        );

        String sql = "UPDATE Users SET PasswordHash = ?, MustChangePassword = TRUE, PasswordResetRequested = FALSE WHERE UserID = ? AND IsActive = TRUE";

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, passwordHash);
            ps.setInt(2, userId);

            return ps.executeUpdate() == 1;

        } catch (SQLException | ClassNotFoundException e) {

            e.printStackTrace();
            return false;
        }
    }
    public ObservableList<ObservableList<String>> getPasswordRequests() {

        ObservableList<ObservableList<String>> data =
                FXCollections.observableArrayList();

        String sql = "SELECT UserID, Username, FullName, Role, IsActive FROM Users WHERE PasswordResetRequested = TRUE AND IsActive = TRUE ORDER BY UserID";

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                ObservableList<String> row =
                        FXCollections.observableArrayList();

                for (int i = 1; i <= 5; i++) {
                    row.add(rs.getString(i));
                }

                data.add(row);
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return data;
    }
    public boolean rejectPasswordRequest(int userId) {

        String sql = "UPDATE Users SET PasswordResetRequested = FALSE WHERE UserID = ? AND PasswordResetRequested = TRUE";

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            return ps.executeUpdate() == 1;

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean requestPasswordReset(String username) {

        if (username == null || username.isBlank()) {
            return false;
        }

        String sql = "UPDATE Users SET PasswordResetRequested = TRUE WHERE Username = ? AND IsActive = TRUE";

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username.trim());

            ps.executeUpdate();

            return true;

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean changePassword(int userId, String currentPassword, String newPassword) {

        if (currentPassword == null || currentPassword.isBlank()
                || newPassword == null || newPassword.isBlank()
                || currentPassword.equals(newPassword)
                || newPassword.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) {
            return false;
        }

        String selectSql = "SELECT PasswordHash FROM Users WHERE UserID = ? AND IsActive = TRUE AND MustChangePassword = TRUE";

        String updateSql = "UPDATE Users SET PasswordHash = ?, MustChangePassword = FALSE, PasswordResetRequested = FALSE WHERE UserID = ? AND PasswordHash = ? AND IsActive = TRUE AND MustChangePassword = TRUE";

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement select = con.prepareStatement(selectSql)) {

            select.setInt(1, userId);

            String oldHash;

            try (ResultSet rs = select.executeQuery()) {

                if (!rs.next()) {
                    return false;
                }

                oldHash = rs.getString("PasswordHash");
            }

            if (!BCrypt.checkpw(currentPassword, oldHash)) {
                return false;
            }

            String newHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());

            try (PreparedStatement ps = con.prepareStatement(updateSql)) {

                ps.setString(1, newHash);
                ps.setInt(2, userId);
                ps.setString(3, oldHash);

                return ps.executeUpdate() == 1;
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    public int getCount(String tableName) {

        if (!java.util.Set.of(
                "Patient",
                "Doctor",
                "Department",
                "Appointment",
                "MedicalRecord",
                "Users"
        ).contains(tableName)) {
            throw new IllegalArgumentException("Invalid table name.");
        }

        String sql = "SELECT COUNT(*) FROM " + tableName;

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return -1;
    }
    protected boolean isLastActiveAdministrator(Connection con, int userId)
            throws SQLException {

        String sql = "SELECT UserID FROM Users WHERE Role = 'Administrator' AND IsActive = TRUE ORDER BY UserID FOR UPDATE";

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int count = 0;
            boolean currentUserIsAdmin = false;

            while (rs.next()) {

                count++;

                if (rs.getInt("UserID") == userId) {
                    currentUserIsAdmin = true;
                }
            }

            return currentUserIsAdmin && count == 1;
        }
    }

    public boolean isLastActiveAdministrator(int userId)
            throws SQLException, ClassNotFoundException {

        try (Connection con = TestConnection.makeConnection()) {
            return isLastActiveAdministrator(con, userId);
        }
    }
}
