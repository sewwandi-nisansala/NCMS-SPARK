package lk.sparkx.ncms.enums;

/**
 * Http status of the JSON response
 */
public enum HttpStatus {
    SUCCESS("success"),

    ERROR("error");

    private String status;

    HttpStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
