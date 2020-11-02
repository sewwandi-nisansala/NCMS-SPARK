package lk.sparkx.ncms.controllers;

import lk.sparkx.ncms.enums.PatientStatus;
import lk.sparkx.ncms.helpers.DBConnectionPool;
import lk.sparkx.ncms.models.Hospital;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AllocateBedsController {
    /**
     * Check for patients is in the queue and add to hospital
     */
    public void allocateBeds() {
        try {
            Connection connection = DBConnectionPool.getInstance().getConnection();

            PreparedStatement statement;
            ResultSet resultSet;

            ArrayList<Hospital> hospitals = new ArrayList<Hospital>();
            statement = connection.prepareStatement("SELECT * FROM hospitals");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                hospitals.add(new Hospital(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getInt("user_id"), resultSet.getInt("district"), resultSet.getInt("geolocation_x"), resultSet.getInt("geolocation_y")));
            }

            statement = connection.prepareStatement("SELECT * FROM patients WHERE status=0 ORDER BY id DESC");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                HashMap<Integer, Double> hospitalDistances = new HashMap<Integer, Double>();

                for (int i = 0; i < hospitals.size(); i++) {
                    Hospital hospital = hospitals.get(i);

                    if (hospital.getAvailableBedsCount() > 0) {
                        hospitalDistances.put(hospital.getId(), hospital.getDistanceToHospital(resultSet.getInt("geolocation_x"), resultSet.getInt("geolocation_y")));
                    }
                }

                Map.Entry<Integer, Double> min = null;
                for (Map.Entry<Integer, Double> entry : hospitalDistances.entrySet()) {
                    if (min == null || min.getValue() > entry.getValue()) {
                        min = entry;
                    }
                }

                if (min != null) {
                    UUID uuid = UUID.randomUUID();

                    statement = connection.prepareStatement("UPDATE patients SET serial_no=?, admission_date=?, status=? WHERE id=?");
                    statement.setString(1, uuid.toString());
                    statement.setDate(2, new java.sql.Date(new java.util.Date().getTime()));
                    statement.setInt(3, PatientStatus.ADMITTED.getStatus());
                    statement.setInt(4, resultSet.getInt("id"));
                    statement.executeUpdate();

                    statement = connection.prepareStatement("UPDATE beds SET serial_no=? WHERE hospital_id=? and serial_no IS NULL LIMIT 1");
                    statement.setString(1, uuid.toString());
                    statement.setInt(2, min.getKey());
                    statement.executeUpdate();
                }

                connection.close();
            }
        } catch (Exception exception) {
        }
    }
}
