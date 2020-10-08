package lk.sparkx.ncms.repository;

import lk.sparkx.ncms.db.DBConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by: thisum
 * Date      : 2020-09-02
 * Time      : 20:48
 **/

public class HospitalRepository
{


    public List selectHotelsWithBeds()
    {
        try
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
        catch(Exception e)
        {

        }

        return null;
    }

}
