package lk.sparkx.ncms.repository;

import lk.sparkx.ncms.dao.Patient;
import lk.sparkx.ncms.db.DBConnectionPool;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PatientRepository {
    public String registerPatient(Patient patient) {
        String INSERT_USERS_SQL = "INSERT INTO patient (id, first_name, last_name, district, location_x, location_y, severity_level, gender, contact, email, age, admit_date, admitted_by, discharge_date, discharged_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        int result = 0;

        try {
            connection = DBConnectionPool.getInstance().getConnection();

            // Step 2:Create a statement using connection object
            preparedStatement = connection.prepareStatement(INSERT_USERS_SQL);
            preparedStatement.setInt(1, patient.getId());
            preparedStatement.setString(2, patient.getFirstName());
            preparedStatement.setString(3, patient.getLastName());
            preparedStatement.setString(4, patient.getDistrict());
            preparedStatement.setInt(5, patient.getLocationX());
            preparedStatement.setInt(6, patient.getLocationY());
            preparedStatement.setString(7, patient.getSeverityLevel());
            preparedStatement.setString(8, patient.getGender());
            preparedStatement.setString(9, patient.getContact());
            preparedStatement.setString(10, patient.getEmail());
            preparedStatement.setInt(11, patient.getAge());
            preparedStatement.setDate(12, (Date) patient.getAdmitDate());
            preparedStatement.setString(13, patient.getAdmittedBy());
            preparedStatement.setDate(14, (Date) patient.getDischargeDate());
            preparedStatement.setString(15, patient.getDischargedBy());

            System.out.println(preparedStatement);
            // Step 3: Execute the query or update query
            result = preparedStatement.executeUpdate();

            if (result!=0)  //Just to ensure data has been inserted into the database
                return "SUCCESS";

        } catch (SQLException e) {
            // process sql exception
            printSQLException(e);
        }
        return "Oops.. Something went wrong there..!"; // On failure, send a message from here.
    }

    private void printSQLException(SQLException ex) {
        for (Throwable e: ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
}
