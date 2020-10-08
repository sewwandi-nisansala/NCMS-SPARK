package lk.sparkx.ncms.db;

import java.sql.*;
import java.util.UUID;

/**
 * Created by: thisum
 * Date      : 2020-08-28
 * Time      : 00:33
 **/

public class DBTest
{
    /**
     * When dealing with DB connections, always use try catch finally
     * finally block is very important. Remember to always close the connections after using
     * Otherwise, the connections will remain open, and will throw errors - exhausting resources
     *
     * rs, con, stmt variable names are commonly used short names, so you can keep them as it is.
     *
     */
    private void simpleInsertQuery()
    {
        ResultSet rs = null;
        Connection con = null;
        PreparedStatement stmt = null;
        try
        {
            UUID uuid = UUID.randomUUID();

            con = DBConnectionPool.getInstance().getConnection();
            stmt = con.prepareStatement("INSERT INTO hospital (id, name, district, location_x, location_y, build_date) VALUES (?,?,?,?,?,?)");
            stmt.setString(1, uuid.toString());
            stmt.setString(2, "Central Hospital, Kandy");
            stmt.setString(3, "Kandy");
            stmt.setInt(4, 300);
            stmt.setInt(5, 257);
            stmt.setDate(6, new Date(new java.util.Date().getTime()));
            int changedRows = stmt.executeUpdate();
            System.out.println( changedRows == 1 ? "Successfully inserted" : "Insertion failed");
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            DBConnectionPool.getInstance().close(rs);
            DBConnectionPool.getInstance().close(stmt);
            DBConnectionPool.getInstance().close(con);
        }
    }

    private void simpleSelectQuery()
    {
        ResultSet rs = null;
        Connection con = null;
        PreparedStatement stmt = null;
        try
        {
            con = DBConnectionPool.getInstance().getConnection();
            stmt = con.prepareStatement("SELECT * FROM hospital WHERE district = ?");
            stmt.setString(1, "Colombo");
            rs = stmt.executeQuery();
            while(rs.next())
            {
                System.out.println(rs.getString("id"));
                System.out.println(rs.getString("name"));
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            DBConnectionPool.getInstance().close(rs);
            DBConnectionPool.getInstance().close(stmt);
            DBConnectionPool.getInstance().close(con);
        }
    }

    private void complexSelectQuery()
    {
        ResultSet rs = null;
        Connection con = null;
        PreparedStatement stmt = null;
        try
        {
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT hospital.name, hospital.district, beds.availability ");
            sb.append("FROM ( ");
            sb.append("       SELECT hospital_id, COUNT(*) AS availability FROM hospital_bed ") ;
            sb.append("       WHERE patient_id IS NULL GROUP BY hospital_id ");
            sb.append(") beds, hospital ");
            sb.append("WHERE beds.hospital_id = hospital.id ");

            con = DBConnectionPool.getInstance().getConnection();
            stmt = con.prepareStatement(sb.toString());
            rs = stmt.executeQuery();
            while(rs.next())
            {
                System.out.println(rs.getString("name"));
                System.out.println(rs.getString("district"));
                System.out.println(rs.getString("availability"));
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            DBConnectionPool.getInstance().close(rs);
            DBConnectionPool.getInstance().close(stmt);
            DBConnectionPool.getInstance().close(con);
        }
    }


    public static void main(String[] args)
    {
        DBTest dbTest = new DBTest();
        dbTest.simpleSelectQuery();
        dbTest.simpleInsertQuery();
        dbTest.complexSelectQuery();
    }
}
