package lk.sparkx.ncms.models;

import lk.sparkx.ncms.enums.Role;
import lk.sparkx.ncms.interfaces.DatabaseModel;

/**
 * Moh User
 */
public class Moh extends User implements DatabaseModel {

    /**
     * Crate MoH user
     *
     * @param name
     * @param email
     * @param password
     * @param role
     */
    public Moh(String name, String email, String password, Role role) {
        super(name, email, role);
    }
}
