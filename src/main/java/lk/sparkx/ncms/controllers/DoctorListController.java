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

@WebServlet(name = "DoctorListController")
public class DoctorListController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonArray errorArray;

        new JsonArray();

        try {
            Connection connection = DBConnectionPool.getInstance().getConnection();
            PreparedStatement statement;
            ResultSet resultSet;
            JsonArray doctors = new JsonArray();

            statement = connection.prepareStatement("SELECT users.*, doctors.hospital_id FROM users INNER JOIN doctors ON doctors.user_id = users.id WHERE users.role=2");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                JsonObject doctor = new JsonObject();
                doctor.addProperty("id", resultSet.getString("id"));
                doctor.addProperty("name", resultSet.getString("name"));
                doctor.addProperty("email", resultSet.getString("email"));
                doctor.addProperty("hospital_id", resultSet.getInt("hospital_id"));
                doctors.add(doctor);
            }

            Http.setResponse(resp, 200);
            Http.getWriter(resp.getWriter(), HttpStatus.SUCCESS.getStatus(), "loaded", doctors, null).flush();

            connection.close();
        } catch (Exception exception) {
            errorArray = new JsonArray();
            errorArray.add("Database connection failed");

            resp = Http.setResponse(resp, 500);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "server error", null, errorArray).flush();
        }
    }
}
