package lk.sparkx.ncms.models;

import com.google.gson.JsonObject;
import lk.sparkx.ncms.helpers.DBConnectionPool;
import lk.sparkx.ncms.helpers.DbFunctions;
import lk.sparkx.ncms.interfaces.DatabaseModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Hospital implements DatabaseModel {
    protected final int BED_COUNT = 4;
    private int id;
    private String name;
    private int userId;
    private int district;
    private int geolocationX;
    private int geolocationY;

    private Doctor director;

    private ArrayList<Bed> beds;

    public Hospital(int id) {
        this.id = id;
    }

    public Hospital(int id, String name, int userId, int district, int geolocationX, int geolocationY) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.district = district;
        this.geolocationX = geolocationX;
        this.geolocationY = geolocationY;
    }

    public int getId() {
        return id;
    }

    /**
     * Get available beds
     *
     * @return bed count
     */
    public int getAvailableBedsCount() {
        try {
            Connection connection = DBConnectionPool.getInstance().getConnection();

            PreparedStatement statement;
            ResultSet resultSet;

            this.beds = new ArrayList<Bed>();

            statement = connection.prepareStatement("SELECT COUNT(*) FROM beds WHERE hospital_id=? and serial_no IS NULL");
            statement.setInt(1, this.id);

            int count = DbFunctions.count(statement.executeQuery());
            connection.close();

            return count;
        } catch (Exception exception) {
            return 0;
        }
    }

    /**
     * Get the distance to a hospital from a given coordinates
     *
     * @param geolocationX
     * @param geolocationY
     * @return
     */
    public double getDistanceToHospital(int geolocationX, int geolocationY) {
        int xDifference = Math.abs(this.geolocationX - geolocationX);
        int yDifference = Math.abs(this.geolocationY - geolocationY);

        double distance = Math.sqrt(Math.pow(xDifference, 2) + Math.pow(yDifference, 2));

        return distance;
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

            statement = connection.prepareStatement("SELECT * FROM hospitals WHERE id=? LIMIT 1");
            statement.setInt(1, this.id);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                this.name = resultSet.getString("name");
                this.userId = resultSet.getInt("user_id");
                this.district = resultSet.getInt("district");
                this.geolocationX = resultSet.getInt("geolocation_x");
                this.geolocationY = resultSet.getInt("geolocation_y");
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
        try {
            Connection connection = DBConnectionPool.getInstance().getConnection();

            PreparedStatement statement;
            ResultSet resultSet;

            this.beds = new ArrayList<Bed>();

            statement = connection.prepareStatement("SELECT * FROM beds WHERE hospital_id=?");
            statement.setInt(1, this.id);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                this.beds.add(new Bed(resultSet.getInt("id"), resultSet.getInt("hospital_id"), resultSet.getString("serial_no")));
            }

            statement = connection.prepareStatement("SELECT * FROM users INNER JOIN doctors ON users.id=doctors.user_id WHERE id=? LIMIT 1");
            statement.setInt(1, this.userId);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                this.director = new Doctor(resultSet.getInt("doctors.id"), resultSet.getString("name"), resultSet.getString("email"), resultSet.getInt("hospital_id"));
            }

            connection.close();
        } catch (Exception exception) {

        }
    }

    /**
     * Serialise model to Json Object
     *
     * @return
     */
    @Override
    public JsonObject serialize() {
        JsonObject dataObject = new JsonObject();

        dataObject.addProperty("id", this.id);
        dataObject.addProperty("name", this.name);
        dataObject.addProperty("user_id", this.userId);
        dataObject.addProperty("district", this.district);
        dataObject.addProperty("geolocation_x", this.geolocationX);
        dataObject.addProperty("geolocation_y", this.geolocationY);

        return dataObject;
    }
}
