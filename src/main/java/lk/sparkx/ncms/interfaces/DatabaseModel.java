package lk.sparkx.ncms.interfaces;

import com.google.gson.JsonObject;

public interface DatabaseModel {
    /**
     * Load data to the model from table. Id of the model should be passed via constructor
     */
    void loadModel();

    /**
     * Load the relational models from the database. Model data should be loaded before.
     */
    void loadRelationalModels();

    /**
     * Serialise public parameters of the model
     *
     * @return
     */
    JsonObject serialize();
}
