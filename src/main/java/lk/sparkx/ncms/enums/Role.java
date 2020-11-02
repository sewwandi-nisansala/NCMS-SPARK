package lk.sparkx.ncms.enums;

/**
 * User roles
 */
public enum Role {
    USER(0),

    MOH(1),

    DOCTOR(2);


    private int role;

    /**
     * User role
     *
     * @param role
     */
    Role(int role) {
        this.role = role;
    }

    /**
     * Get user role
     *
     * @return role
     */
    public int getRole() {
        return role;
    }
}
