package lk.sparkx.ncms.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lk.sparkx.ncms.enums.DeceaseLevel;
import lk.sparkx.ncms.enums.HttpStatus;
import lk.sparkx.ncms.enums.PatientStatus;
import lk.sparkx.ncms.helpers.Database;
import lk.sparkx.ncms.helpers.Http;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Generate stats for display
 */
@WebServlet(name = "StatController")
public class StatController extends HttpServlet {

    /**
     * Get COVID stats
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonArray errorArray;

        try {
            Connection connection = Database.open();
            PreparedStatement statement;
            ResultSet resultSet;
            JsonObject data = new JsonObject();

            JsonObject districtWisePatients = new JsonObject();
            statement = connection.prepareStatement("SELECT district, COUNT(*) as count FROM patients WHERE status=1 GROUP BY district");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                districtWisePatients.addProperty(resultSet.getString("district"), resultSet.getInt("count"));
            }
            data.add("district", districtWisePatients);

            JsonObject dailyStats = new JsonObject();
            statement = connection.prepareStatement("SELECT status, COUNT(*) as count FROM patients WHERE register_date=? or admission_date=? or discharged_date=? GROUP BY status");
            statement.setDate(1, new java.sql.Date(new java.util.Date().getTime()));
            statement.setDate(2, new java.sql.Date(new java.util.Date().getTime()));
            statement.setDate(3, new java.sql.Date(new java.util.Date().getTime()));
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                dailyStats.addProperty(resultSet.getString("status"), resultSet.getInt("count"));
            }
            data.add("daily_status", dailyStats);

            JsonObject totalStats = new JsonObject();
            statement = connection.prepareStatement("SELECT status, COUNT(*) as count FROM patients GROUP BY status");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                totalStats.addProperty(resultSet.getString("status"), resultSet.getInt("count"));
            }
            data.add("total_status", totalStats);

            JsonObject dayVsPatients = new JsonObject();
            statement = connection.prepareStatement("SELECT register_date, COUNT(*) as count FROM patients GROUP BY register_date ORDER BY register_date DESC LIMIT 30");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                dayVsPatients.addProperty(resultSet.getString("register_date"), resultSet.getInt("count"));
            }
            data.add("days", dayVsPatients);

            Http.setResponse(resp, 200);
            Http.getWriter(resp.getWriter(), HttpStatus.SUCCESS.getStatus(), "loaded", data, null).flush();

            connection.close();
        } catch (Exception exception) {
            errorArray = new JsonArray();
            errorArray.add("Database connection failed");

            resp = Http.setResponse(resp, 500);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "server error", null, errorArray).flush();
        }


    }
}
