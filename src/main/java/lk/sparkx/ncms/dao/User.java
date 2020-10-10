package lk.sparkx.ncms.dao;

import lk.sparkx.ncms.db.DBConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class User {
    private String userName;
    private String password;
    private String name;
    private boolean isMoh;
    private boolean isHospital;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMoh() {
        return isMoh;
    }

    public void setMoh(boolean moh) {
        isMoh = moh;
    }

    public boolean isHospital() {
        return isHospital;
    }

    public void setHospital(boolean hospital) {
        isHospital = hospital;
    }

    public void getModel() {
        try {
            Connection connection = DBConnectionPool.getInstance().getConnection();
            PreparedStatement statement;
            ResultSet resultSet;

            statement = connection.prepareStatement("SELECT * FROM user WHERE username=?");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                this.userName = resultSet.getString("username");
                this.password = resultSet.getString("password");
                this.name = resultSet.getString("name");
                this.isMoh = resultSet.getBoolean("isMoh");
                this.isHospital = resultSet.getBoolean("isHospital");
            }

            connection.close();

        } catch (Exception exception) {

        }
    }
}
