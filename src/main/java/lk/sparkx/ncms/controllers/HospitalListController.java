package lk.sparkx.ncms.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lk.sparkx.ncms.enums.HttpStatus;
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

@WebServlet (name = "HospitalListController")
public class HospitalListController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonArray errorArray;

        errorArray = new JsonArray();
        if (req.getParameter("id").isEmpty()) {
            errorArray.add("Hospital id is required");

            Http.setResponse(resp, 403);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "forbidden", null, errorArray).flush();
            return;
        }

        try {
            Connection connection = Database.open();
            PreparedStatement statement;
            ResultSet resultSet;
            JsonArray hospitals = new JsonArray();

            int id = Integer.parseInt(req.getParameter("id"));

            statement = connection.prepareStatement("SELECT hospitals.*, users.name AS director, COUNT(*) as patient_count FROM hospitals INNER JOIN users ON users.id = hospitals.user_id INNER JOIN beds ON beds.hospital_id = hospitals.id WHERE hospitals.id=? and beds.serial_no IS NOT NULL");
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                JsonObject hospital = new JsonObject();
                hospital.addProperty("name", resultSet.getString("name"));
                hospital.addProperty("director", resultSet.getString("director"));
                hospital.addProperty("user_id", resultSet.getInt("user_id"));
                hospital.addProperty("geolocation_x", resultSet.getInt("geolocation_x"));
                hospital.addProperty("geolocation_y", resultSet.getInt("geolocation_y"));
                hospital.addProperty("district", resultSet.getInt("district"));
                hospital.addProperty("patient_count", resultSet.getInt("patient_count"));
                hospitals.add(hospital);
            }

            Http.setResponse(resp, 200);
            Http.getWriter(resp.getWriter(), HttpStatus.SUCCESS.getStatus(), "loaded", hospitals, null).flush();

            connection.close();
        } catch (Exception exception) {
            errorArray = new JsonArray();
            errorArray.add("Database connection failed");

            resp = Http.setResponse(resp, 500);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "server error", null, errorArray).flush();
        }
    }
}
