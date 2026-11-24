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
    public ObservableList<ObservableList<String>> getAppointments(String searchBy, String search, String status, LocalDate date) {

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

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

        String sql = "SELECT a.AppointmentID, CONCAT(p.FirstName, ' ', p.LastName) AS PatientName, CONCAT(d.FirstName, ' ', d.LastName) AS DoctorName, DATE(a.AppointmentDate), TIME(a.AppointmentDate), a.Reason, a.Status, a.PatientID, a.DoctorID FROM Appointment a JOIN Patient p ON a.PatientID = p.PatientID JOIN Doctor d ON a.DoctorID = d.DoctorID WHERE (? = '' OR " + column + " LIKE ?) AND (? = 'All Status' OR a.Status = ?) AND (? IS NULL OR DATE(a.AppointmentDate) = ?) ORDER BY a.AppointmentDate";

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, search);
            ps.setString(2, "%" + search + "%");

            ps.setString(3, status);
            ps.setString(4, status);

            ps.setDate(5, date == null ? null : java.sql.Date.valueOf(date));
            ps.setDate(6, date == null ? null : java.sql.Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    ObservableList<String> row = FXCollections.observableArrayList();

                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
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
    public ObservableList<ObservableList<String>> getMedicalRecords(String searchBy, String search) {

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

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

        String sql = "SELECT m.RecordID, CONCAT(p.FirstName, ' ', p.LastName), CONCAT(d.FirstName, ' ', d.LastName), m.AppointmentID, m.RecordDate, m.Symptoms, m.Diagnosis, m.Treatment, m.Notes, m.PatientID, d.Specialization FROM MedicalRecord m JOIN Patient p ON m.PatientID = p.PatientID JOIN Doctor d ON m.DoctorID = d.DoctorID WHERE (? = '' OR " + column + " LIKE ?) ORDER BY m.RecordDate DESC";

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, search);
            ps.setString(2, "%" + search + "%");

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    ObservableList<String> row = FXCollections.observableArrayList();

                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {

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
    public boolean updateMedicalRecord(int recordId, LocalDate recordDate, String symptoms, String diagnosis, String treatment, String notes) {

        String sql = "UPDATE MedicalRecord SET RecordDate = ?, Symptoms = ?, Diagnosis = ?, Treatment = ?, Notes = ? WHERE RecordID = ?";

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(recordDate));
            ps.setString(2, symptoms);
            ps.setString(3, diagnosis);
            ps.setString(4, treatment);
            ps.setString(5, notes);
            ps.setInt(6, recordId);

            return ps.executeUpdate() > 0;

        } catch (SQLException | ClassNotFoundException e) {

            e.printStackTrace();

            return false;
        }
    }
    public ObservableList<ObservableList<String>> getAvailableAppointments(int patientId) {

        ObservableList<ObservableList<String>> data =
                FXCollections.observableArrayList();

        String sql = "SELECT a.AppointmentID, a.DoctorID, CONCAT(d.FirstName, ' ', d.LastName), d.Specialization, DATE(a.AppointmentDate), TIME(a.AppointmentDate) FROM Appointment a JOIN Doctor d ON a.DoctorID = d.DoctorID WHERE a.PatientID = ? AND a.Status = 'Completed' AND NOT EXISTS (SELECT 1 FROM MedicalRecord m WHERE m.AppointmentID = a.AppointmentID) ORDER BY a.AppointmentDate DESC";

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    ObservableList<String> row =
                            FXCollections.observableArrayList();

                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {

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
    public boolean addMedicalRecord(int appointmentId, LocalDate recordDate, String symptoms, String diagnosis, String treatment, String notes) {

        if (recordDate == null
                || recordDate.isAfter(LocalDate.now())
                || diagnosis == null
                || diagnosis.isBlank()) {

            return false;
        }

        String sql = "INSERT INTO MedicalRecord (PatientID, DoctorID, AppointmentID, RecordDate, Symptoms, Diagnosis, Treatment, Notes) SELECT a.PatientID, a.DoctorID, a.AppointmentID, ?, ?, ?, ?, ? FROM Appointment a WHERE a.AppointmentID = ? AND a.Status = 'Completed' AND DATE(a.AppointmentDate) <= ? AND NOT EXISTS (SELECT 1 FROM MedicalRecord m WHERE m.AppointmentID = a.AppointmentID)";

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(recordDate));
            ps.setString(2, symptoms);
            ps.setString(3, diagnosis);
            ps.setString(4, treatment);
            ps.setString(5, notes);
            ps.setInt(6, appointmentId);
            ps.setDate(7, java.sql.Date.valueOf(recordDate));

            return ps.executeUpdate() > 0;

        } catch (SQLException | ClassNotFoundException e) {

            e.printStackTrace();

            return false;
        }
    }
    // ...
}