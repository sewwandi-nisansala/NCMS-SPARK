package lk.sparkx.ncms.enums;

/**
 * Patient status
 */
public enum PatientStatus {
    IN_QUEUE(0),

    ADMITTED(1),

    DISCHARGED(2);

    private int status;

    /**
     * Patient status
     *
     * @param status
     */
    PatientStatus(int status) {
        this.status = status;
    }

    /**
     * Get patient status
     *
     * @return status
     */
    public int getStatus() {
        return status;
    }
}
