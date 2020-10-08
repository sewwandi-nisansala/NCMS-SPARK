package lk.sparkx.ncms.models;

import com.google.gson.JsonObject;
import lk.sparkx.ncms.enums.DeceaseLevel;
import lk.sparkx.ncms.enums.PatientStatus;
import lk.sparkx.ncms.helpers.Database;
import lk.sparkx.ncms.interfaces.DatabaseModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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

    private User user;

    /**
     * Create patient
     *
     * @param id Patient ID
     */
    public Patient(int id) {
        this.id = id;
    }

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
            Connection connection = Database.open();
            PreparedStatement statement;
            ResultSet resultSet;

            statement = connection.prepareStatement("SELECT * FROM patients WHERE id=? LIMIT 1");
            statement.setInt(1, this.id);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                this.userId = resultSet.getInt("user_id");
                this.serialNo = resultSet.getString("serial_no");
                this.geolocationX = resultSet.getInt("geolocation_x");
                this.geolocationY = resultSet.getInt("geolocation_y");
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
     * Load relationships
     */
    @Override
    public void loadRelationalModels() {

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

        return dataObject;
    }
}
