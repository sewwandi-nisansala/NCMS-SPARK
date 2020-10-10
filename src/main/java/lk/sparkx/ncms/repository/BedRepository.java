package lk.sparkx.ncms.repository;

import lk.sparkx.ncms.dao.Bed;
import lk.sparkx.ncms.db.DBConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BedRepository {



    public void makeAvailable(String patientId) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DBConnectionPool.getInstance().getConnection();

            statement = connection.prepareStatement("DELETE FROM hospital_bed WHERE patient_id='"+patientId+"'");
            System.out.println(statement);
            statement.executeUpdate();

            connection.close();

        } catch (Exception ignored) {

        }
    }

}
