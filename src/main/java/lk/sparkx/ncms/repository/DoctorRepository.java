package lk.sparkx.ncms.repository;

import lk.sparkx.ncms.dao.Doctor;
import lk.sparkx.ncms.db.DBConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DoctorRepository {

    public String regDoctor(Doctor doctor) {
        String INSERT_USERS_SQL;
        INSERT_USERS_SQL = "INSERT INTO doctor (id, name, hospital_id, is_director) VALUES (?, ?, ?, ?)";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        int result = 0;

        try {
            connection = DBConnectionPool.getInstance().getConnection();

            // Step 2:Create a statement using connection object
            preparedStatement = connection.prepareStatement(INSERT_USERS_SQL);
            preparedStatement.setString(1, doctor.getId());
            preparedStatement.setString(2, doctor.getName());
            preparedStatement.setString(3, doctor.getHospitalId());
            preparedStatement.setBoolean(4, doctor.isDirector());

            System.out.println(preparedStatement);
            // Step 3: Execute the query or update query
            result = preparedStatement.executeUpdate();

            if (result != 0)  //Just to ensure data has been inserted into the database
                return "SUCCESS";

        } catch (SQLException e) {
            // process sql exception
            printSQLException(e);
        }
        return "Oops.. Something went wrong there..!"; // On failure, send a message from here.
    }

    public void dischargePatients(String patientId, String hospitalId) {
        Connection connection = null;
        PreparedStatement statement = null;
        PreparedStatement statement2 = null;
        int result = 0;


        try {
            connection = DBConnectionPool.getInstance().getConnection();
            //PreparedStatement statement;
            ResultSet resultSet;

            statement2 = connection.prepareStatement("SELECT * FROM doctor WHERE hospital_id='" +hospitalId + "' AND is_director=1");
            resultSet = statement2.executeQuery();
            System.out.println(statement2);
            while (resultSet.next()) {
                String director = resultSet.getString("id");
                System.out.println(director);
                statement = connection.prepareStatement("UPDATE patient SET discharge_date=? , discharged_by= '" + director + "' WHERE id = '" + patientId + "'");


                statement.setDate(1, new java.sql.Date(new java.util.Date().getTime()));
                //statement.setString(2, directorId);

                System.out.println(statement);
                result = statement.executeUpdate();
                if(result!=0){
                    System.out.println("success");
                }else
                    System.out.println("Failed");
            }

            connection.close();


        } catch (Exception exception) {

        }
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
