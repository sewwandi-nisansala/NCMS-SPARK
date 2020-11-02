package lk.sparkx.ncms.models;

import com.google.gson.JsonObject;
import lk.sparkx.ncms.enums.Role;
import lk.sparkx.ncms.helpers.DBConnectionPool;
import lk.sparkx.ncms.interfaces.DatabaseModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Doctors
 */
public class Doctor extends User implements DatabaseModel {
    private int id;
    private int hospitalId;
    private int userId;

    /**
     * Create doctor
     *
     * @param id
     * @param name
     * @param email
     * @param hospitalId
     */
    public Doctor(int id, String name, String email, int hospitalId) {
        super(name, email, Role.DOCTOR);
        this.hospitalId = hospitalId;
    }

    public Doctor(int id) {
        this.id = id;
    }

    public Doctor(){

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

            statement = connection.prepareStatement("SELECT * FROM doctors WHERE id=? LIMIT 1");
            statement.setInt(1, this.id);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                this.userId = resultSet.getInt("user_id");
                this.hospitalId = resultSet.getInt("hospital_id");
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

            statement = connection.prepareStatement("SELECT doctors.*, users.name, users.email, users.role FROM doctors INNER JOIN users ON users.id = doctors.user_id WHERE user_id=? LIMIT 1");
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                this.id = resultSet.getInt("id");
                this.userId = userId;
                this.name = resultSet.getString("name");
                this.email = resultSet.getString("email");
                this.role = Role.values()[resultSet.getInt("role")];
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
        dataObject.addProperty("hospital_id", this.hospitalId);

        return dataObject;
    }
}

