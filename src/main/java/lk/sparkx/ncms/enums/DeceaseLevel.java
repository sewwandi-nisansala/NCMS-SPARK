package lk.sparkx.ncms.enums;

/**
 * Patient decease level
 */
public enum DeceaseLevel {
    NOT_AVAILABLE(0),

    LOW(1),

    MODERATE(2),

    CRITICAL(3);

    private int level;

    /**
     * Patient decease level
     *
     * @param level
     */
    DeceaseLevel(int level) {
        this.level = level;
    }

    /**
     * Get patient decease level
     *
     * @return
     */
    public int getLevel() {
        return level;
    }
}
