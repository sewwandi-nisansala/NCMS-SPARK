package lk.sparkx.ncms.dao;

import lk.sparkx.ncms.db.DBConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Doctor {
    private String id;
    private String name;
    private String hospitalId;
    private boolean isDirector;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public boolean isDirector() {
        return isDirector;
    }

    public void setDirector(boolean director) {
        isDirector = director;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    /**
     * load doctor details to the database
     * @param doctor
     */
    public void loadData(Doctor doctor){
        try{
            Connection con = DBConnectionPool.getInstance().getConnection();

            PreparedStatement statement = con.prepareStatement("SELECT * FROM doctor WHERE id=?");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                this.id = resultSet.getString("id");
                this.name = resultSet.getString("name");
                this.hospitalId = resultSet.getString("hospital_id");
                this.isDirector = resultSet.getBoolean("is_director");
            }
            con.close();
        }catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }
}
