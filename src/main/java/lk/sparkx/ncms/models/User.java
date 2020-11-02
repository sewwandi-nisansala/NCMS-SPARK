package lk.sparkx.ncms.models;

import com.google.gson.JsonObject;
import lk.sparkx.ncms.enums.Role;
import lk.sparkx.ncms.helpers.DBConnectionPool;
import lk.sparkx.ncms.interfaces.DatabaseModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * User of NCMS
 */
public class User implements DatabaseModel {
    private int id;
    protected String name;
    protected String email;
    protected Role role;

    /**
     * Default constructor
     */
    public User() {

    }

    /**
     * Create user with ID
     *
     * @param id User ID
     */
    public User(int id) {
        this.id = id;
    }

    /**
     * Create new user
     *
     * @param name  User name
     * @param email User email
     * @param role  User role
     */
    public User(String name, String email, Role role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }

    /**
     * Get user id
     *
     * @return User id
     */
    public int getId() {
        return id;
    }

    /**
     * Get user role
     *
     * @return User role
     */
    public Role getRole() {
        return role;
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

            statement = connection.prepareStatement("SELECT * FROM users WHERE id=? LIMIT 1");
            statement.setInt(1, this.id);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                this.id = resultSet.getInt("id");
                this.name = resultSet.getString("name");
                this.email = resultSet.getString("email");
                this.role = Role.values()[resultSet.getInt("role")];
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
     * @return Json Object
     */
    @Override
    public JsonObject serialize() {
        JsonObject dataObject = new JsonObject();

        dataObject.addProperty("id", this.id);
        dataObject.addProperty("name", this.name);
        dataObject.addProperty("email", this.email);
        dataObject.addProperty("role", this.role.getRole());

        return dataObject;
    }
}
