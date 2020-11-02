package lk.sparkx.ncms.models;

import com.google.gson.JsonObject;
import lk.sparkx.ncms.enums.DeceaseLevel;
import lk.sparkx.ncms.enums.PatientStatus;
import lk.sparkx.ncms.enums.Role;
import lk.sparkx.ncms.helpers.DBConnectionPool;
import lk.sparkx.ncms.interfaces.DatabaseModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

/**
 * Patient data model
 */
public class Patient extends User implements DatabaseModel {
    private int id;
    private int userId;
    private String serialNo;
    private int geolocationX;
    private int geolocationY;
    private int district;
    private String contactNumber;
    private DeceaseLevel deceaseLevel;
    private PatientStatus patientStatus;
    private Date registerDate;
    private Date admissionDate;
    private Date dischargedDate;

    private String hospitalName;
    private int hospitalId = 0;
    private int bedNo = 0;

    /**
     * Create patient
     *
     * @param id Patient ID
     */
    public Patient(int id) {
        this.id = id;
    }

    public Patient() {}

    /**
     * Get patient serial number
     *
     * @return Serial number
     */
    public String getSerialNo() {
        return serialNo;
    }

    /**
     * Load data from database
     */
    @Override
    public void loadModel() {
        try {
            Connection connection = DBConnectionPool.getInstance().getConnection();

            PreparedStatement statement;
            ResultSet resultSet;

            statement = connection.prepareStatement("SELECT patients.*, users.name, users.email, users.role FROM patients INNER JOIN users ON users.id = patients.user_id WHERE patients.id=? LIMIT 1");
            statement.setInt(1, this.id);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                this.userId = resultSet.getInt("user_id");
                this.name = resultSet.getString("name");
                this.email = resultSet.getString("email");
                this.role = Role.values()[resultSet.getInt("role")];
                this.serialNo = resultSet.getString("serial_no");
                this.geolocationX = resultSet.getInt("geolocation_x");
                this.geolocationY = resultSet.getInt("geolocation_y");
                this.district = resultSet.getInt("district");
                this.contactNumber = resultSet.getString("contact_number");
                this.deceaseLevel = DeceaseLevel.values()[resultSet.getInt("decease_level")];
                this.patientStatus = PatientStatus.values()[resultSet.getInt("status")];
                this.registerDate = resultSet.getDate("register_date");
                this.admissionDate = resultSet.getDate("admission_date");
                this.dischargedDate = resultSet.getDate("discharged_date");
            }

            connection.close();
        } catch (Exception exception) {

        }
    }

    /**
     * Load model based on user id
     * @param userId
     */
    public void loadModel(int userId) {
        try {
            Connection connection = DBConnectionPool.getInstance().getConnection();

            PreparedStatement statement;
            ResultSet resultSet;

            statement = connection.prepareStatement("SELECT patients.*, users.name, users.email, users.role FROM patients INNER JOIN users ON users.id = patients.user_id WHERE user_id=? LIMIT 1");
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                this.id = resultSet.getInt("id");
                this.userId = userId;
                this.name = resultSet.getString("name");
                this.email = resultSet.getString("email");
                this.role = Role.values()[resultSet.getInt("role")];
                this.serialNo = resultSet.getString("serial_no");
                this.geolocationX = resultSet.getInt("geolocation_x");
                this.geolocationY = resultSet.getInt("geolocation_y");
                this.district = resultSet.getInt("district");
                this.contactNumber = resultSet.getString("contact_number");
                this.deceaseLevel = DeceaseLevel.values()[resultSet.getInt("decease_level")];
                this.patientStatus = PatientStatus.values()[resultSet.getInt("status")];
                this.registerDate = resultSet.getDate("register_date");
                this.admissionDate = resultSet.getDate("admission_date");
                this.dischargedDate = resultSet.getDate("discharged_date");
            }

            connection.close();
        } catch (Exception exception) {

        }
    }

    @Override
    public void loadRelationalModels() {
        try {
            Connection connection = DBConnectionPool.getInstance().getConnection();

            PreparedStatement statement;
            ResultSet resultSet;

            statement = connection.prepareStatement("SELECT beds.id, hospitals.name, hospitals.id AS hospital_id FROM beds INNER JOIN hospitals ON hospitals.id = beds.hospital_id WHERE beds.serial_no=? LIMIT 1");
            statement.setString(1, this.serialNo);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                this.hospitalName = resultSet.getString("name");
                this.bedNo = resultSet.getInt("id");
                this.hospitalId = resultSet.getInt("hospital_id");
            }

            connection.close();
        } catch (Exception exception) {

        }
    }

    /**
     * Serialise model to Json Object
     *
     * @return Json object of model attributes
     */
    @Override
    public JsonObject serialize() {
        JsonObject dataObject = new JsonObject();

        dataObject.addProperty("id", this.id);
        dataObject.addProperty("user_id", this.userId);
        dataObject.addProperty("name", this.name);
        dataObject.addProperty("email", this.email);
        dataObject.addProperty("role", this.role.getRole());
        dataObject.addProperty("serial_no", this.serialNo != null ? this.serialNo : null);
        dataObject.addProperty("geolocation_x", this.geolocationX);
        dataObject.addProperty("geolocation_y", this.geolocationY);
        dataObject.addProperty("contact_number", this.contactNumber);
        dataObject.addProperty("district", this.district);
        dataObject.addProperty("decease_level", this.deceaseLevel.getLevel());
        dataObject.addProperty("status", this.patientStatus.getStatus());
        dataObject.addProperty("register_date", this.registerDate.toString());
        dataObject.addProperty("admission_date", this.admissionDate != null ? this.admissionDate.toString() : null);
        dataObject.addProperty("discharged_date", this.dischargedDate != null ? this.dischargedDate.toString() : null);

        dataObject.addProperty("hospital_name", this.hospitalName != null ? this.hospitalName : null);
        dataObject.addProperty("bed_no", this.bedNo != 0 ? this.bedNo : null);
        dataObject.addProperty("hospital_id", this.hospitalId);
        return dataObject;
    }
}
