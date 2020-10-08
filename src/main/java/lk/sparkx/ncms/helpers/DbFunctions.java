package lk.sparkx.ncms.helpers;

import java.sql.ResultSet;

public class DbFunctions {
    /**
     * Count rows of Mysql query
     *
     * @param resultSet
     * @return
     */
    public static int count(ResultSet resultSet) {
        try {
            while (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
