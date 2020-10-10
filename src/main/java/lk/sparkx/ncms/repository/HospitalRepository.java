package lk.sparkx.ncms.repository;

import lk.sparkx.ncms.dao.Hospital;
import lk.sparkx.ncms.db.DBConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class HospitalRepository
{


public String registerHospital(Hospital hospital) {
    String INSERT_USERS_SQL = "INSERT INTO hospital (id, name, district, location_x, location_y, build_date) VALUES (?, ?, ?, ?, ?, ?)";

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    int result = 0;

    try {
        connection = DBConnectionPool.getInstance().getConnection();

        // Step 2:Create a statement using connection object
        preparedStatement = connection.prepareStatement(INSERT_USERS_SQL);
        preparedStatement.setString(1, hospital.getId());
        preparedStatement.setString(2, hospital.getName());
        preparedStatement.setString(3, hospital.getDistrict());
        preparedStatement.setInt(4, hospital.getLocationX());
        preparedStatement.setInt(5, hospital.getLocationY());
        preparedStatement.setDate(6, (java.sql.Date) hospital.getBuildDate());

        System.out.println(preparedStatement);
        // Step 3: Execute the query or update query
        result = preparedStatement.executeUpdate();

        if (result != 0)
            return "SUCCESS";

    } catch (SQLException e) {
        // process sql exception
        printSQLException(e);
    }
    return "Something went wrong there..!"; // On failure, send a message from here.
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

    public String assignHospital(int patientLocationX, int patientLocationY) {
        Connection connection = null;
        PreparedStatement statement = null;
        Map<String, Double> distance = new HashMap<String, Double>();

        double dist;
        String nearestHospital = "";

        try {
            connection = DBConnectionPool.getInstance().getConnection();
            ResultSet resultSet;

            statement = connection.prepareStatement("SELECT * FROM hospital");
            System.out.println(statement);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                int locationX = resultSet.getInt("location_x");
                int locationY = resultSet.getInt("location_y");
                int distanceX = Math.abs(locationX - patientLocationX);
                int distanceY = Math.abs(locationY - patientLocationY);

                dist = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
                distance.put(id,dist);

            }
            System.out.println(distance);
            System.out.println(Collections.min(distance.values()));
            nearestHospital = Collections.min(distance.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();
            System.out.println(nearestHospital);
            connection.close();

        } catch (Exception exception) {

        }
        return nearestHospital;
    }

}
