package lk.sparkx.ncms.dao;

import lk.sparkx.ncms.db.DBConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Bed {
    private int id;
    private String hospitalId;
    private String patientId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public int allocateBed(String hospitalId, String patientId) {
        setHospitalId(hospitalId);
        setPatientId(patientId);
        int bedId = 0;
        Connection connection;
        PreparedStatement statement;
        PreparedStatement statement2 = null;
        PreparedStatement statement3 = null;
        int result;
        result = 0;
        int [] bed = new int[10];

        try {
            connection = DBConnectionPool.getInstance().getConnection();
            ResultSet resultSet;
            statement = connection.prepareStatement("SELECT * FROM hospital_bed where hospital_id= '" + getHospitalId() + "'");
            System.out.println(statement);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                bed[id-1]=id;
            }
            for(int i=0; i<10; i++){
                if(bed[i]==0){
                    bedId = i+1;
                    break;
                }
            }
            if(bedId!=0) {
                statement2 = connection.prepareStatement("INSERT INTO hospital_bed (id, hospital_id, patient_id) VALUES (" + bedId + ",'" + hospitalId + "','" + patientId + "')");
                System.out.println(statement2);
            }

            statement2.executeUpdate();

            connection.close();

        } catch (Exception e) {
        }

        return  bedId;
    }

    //when discharge a patient have to remove patient and delete bed+id that is assigned for the patient
    public void removePatient(String patient_id) {

        try {
            Connection con = DBConnectionPool.getInstance().getConnection();

            try (PreparedStatement stmt = con.prepareStatement("DELETE FROM hospital_bed WHERE patient_id='" + patient_id + "'")) {
                System.out.println(stmt);
                int result;
                result = stmt.executeUpdate();
            }

            con.close();

        } catch (Exception ignored) {

        }
    }

}
