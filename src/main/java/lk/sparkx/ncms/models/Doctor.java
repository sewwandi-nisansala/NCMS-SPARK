package lk.sparkx.ncms.models;

import lk.sparkx.ncms.enums.Role;
import lk.sparkx.ncms.interfaces.DatabaseModel;

/**
 * Doctors
 */
public class Doctor extends User implements DatabaseModel {
    private int hospitalId;
    private int id;

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
}

