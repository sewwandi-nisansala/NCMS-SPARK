package lk.sparkx.ncms.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lk.sparkx.ncms.enums.HttpStatus;
import lk.sparkx.ncms.helpers.DBConnectionPool;
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
 * Get patient data list
 */
@WebServlet(name = "PatientListController")
public class PatientListController extends HttpServlet {

    /**
     * Patient list with filtering
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonArray errorArray;

        errorArray = new JsonArray();
        if (req.getParameter("hospital").isEmpty()) {
            errorArray.add("Hospital id is required");
        }
        if (req.getParameter("status").isEmpty()) {
            errorArray.add("Patient status is required");
        }

        if (errorArray.size() > 0) {
            Http.setResponse(resp, 400);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "invalid data", null, errorArray).flush();
            return;
        }

        try {
            Connection connection = DBConnectionPool.getInstance().getConnection();
            PreparedStatement statement;
            ResultSet resultSet;
            JsonArray patients = new JsonArray();

            int hospital = Integer.parseInt(req.getParameter("hospital"));
            int status = Integer.parseInt(req.getParameter("status"));

            if (hospital == 0) {
                statement = connection.prepareStatement("SELECT patients.*, users.name, users.email, hospitals.name as hospital FROM patients INNER JOIN users ON users.id = patients.user_id LEFT JOIN beds ON beds.serial_no = patients.serial_no LEFT JOIN hospitals ON beds.hospital_id = hospitals.id WHERE patients.status=?");
                statement.setInt(1, status);
            } else {
                statement = connection.prepareStatement("SELECT patients.*, users.name, users.email, hospitals.name as hospital FROM patients INNER JOIN users ON users.id = patients.user_id LEFT JOIN beds ON beds.serial_no = patients.serial_no LEFT JOIN hospitals ON beds.hospital_id = hospitals.id WHERE patients.status=? and hospitals.id=?");
                statement.setInt(1, status);
                statement.setInt(2, hospital);
            }
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                JsonObject patient = new JsonObject();
                patient.addProperty("id", resultSet.getInt("id"));
                patient.addProperty("user_id", resultSet.getInt("user_id"));
                patient.addProperty("name", resultSet.getString("name"));
                patient.addProperty("email", resultSet.getString("email"));
                patient.addProperty("serial_no", resultSet.getString("serial_no") != null ? resultSet.getString("serial_no") : null);
                patient.addProperty("hospital", resultSet.getString("hospital") != null ? resultSet.getString("hospital") : null);
                patient.addProperty("geolocation_x", resultSet.getInt("geolocation_x"));
                patient.addProperty("geolocation_y", resultSet.getInt("geolocation_y"));
                patient.addProperty("district", resultSet.getInt("district"));
                patient.addProperty("contact_number", resultSet.getString("contact_number"));
                patient.addProperty("decease_level", resultSet.getInt("decease_level"));
                patient.addProperty("status", resultSet.getInt("status"));
                patient.addProperty("register_date", resultSet.getDate("register_date").toString());
                patient.addProperty("admission_date", resultSet.getDate("admission_date") != null ? resultSet.getDate("admission_date").toString() : null);
                patient.addProperty("discharged_date", resultSet.getDate("discharged_date") != null ? resultSet.getDate("discharged_date").toString() : null);
                patients.add(patient);
            }

            Http.setResponse(resp, 200);
            Http.getWriter(resp.getWriter(), HttpStatus.SUCCESS.getStatus(), "loaded", patients, null).flush();

            connection.close();
        } catch (Exception exception) {
            errorArray = new JsonArray();
            errorArray.add("Database connection failed");

            resp = Http.setResponse(resp, 500);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "server error", null, errorArray).flush();
        }
    }
}
