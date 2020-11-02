package lk.sparkx.ncms.models;

import com.google.gson.JsonObject;
import lk.sparkx.ncms.interfaces.DatabaseModel;

/**
 * Represent a bed in hospital
 */
public class Bed implements DatabaseModel {
    private int id;
    private int hospitalId;
    private String serialNo;

    private Hospital hospital;

    private Patient patient;

    /**
     * Create a bed
     *
     * @param id
     * @param hospitalId
     * @param serialNo
     */
    public Bed(int id, int hospitalId, String serialNo) {
        this.id = id;
        this.hospitalId = hospitalId;
        this.serialNo = serialNo;
    }

    /**
     * Load data from database
     */
    @Override
    public void loadModel() {

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
     * @return
     */
    @Override
    public JsonObject serialize() {
        JsonObject dataObject = new JsonObject();

        dataObject.addProperty("id", this.id);
        dataObject.addProperty("hospital_id", this.hospitalId);
        dataObject.addProperty("serial_no", this.serialNo);

        return dataObject;
    }
}
