package lk.sparkx.ncms.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lk.sparkx.ncms.enums.HttpStatus;
import lk.sparkx.ncms.helpers.DBConnectionPool;
import lk.sparkx.ncms.helpers.DbFunctions;
import lk.sparkx.ncms.helpers.Http;
import lk.sparkx.ncms.models.Hospital;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "HospitalController")
public class HospitalController extends HttpServlet {

    /**
     * Get hospital data by id
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
        if (req.getParameter("id").isEmpty()) {
            errorArray.add("Hospital id is required");

            Http.setResponse(resp, 403);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "forbidden", null, errorArray).flush();
            return;
        }

        Hospital hospital = new Hospital(Integer.parseInt(req.getParameter("id")));
        hospital.loadModel();

        Http.setResponse(resp, 200);
        Http.getWriter(resp.getWriter(), HttpStatus.SUCCESS.getStatus(), "loaded", hospital.serialize(), null).flush();
    }

    /**
     * Insert new hospital
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonArray errorArray;

        errorArray = new JsonArray();
        if (req.getParameter("name").isEmpty()) {
            errorArray.add("Name is required");
        }
        if (req.getParameter("user_id").isEmpty()) {
            errorArray.add("Director is required");
        }
        if (req.getParameter("district").isEmpty()) {
            errorArray.add("District is required");
        }
        if (req.getParameter("geolocation_x").isEmpty()) {
            errorArray.add("Geolocation X is required");
        }
        if (req.getParameter("geolocation_y").isEmpty()) {
            errorArray.add("Geolocation Y is required");
        }

        if (errorArray.size() > 0) {
            Http.setResponse(resp, 400);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "invalid data", null, errorArray).flush();
            return;
        }

        String name = req.getParameter("name");
        int userId = Integer.parseInt(req.getParameter("user_id"));
        int district = Integer.parseInt(req.getParameter("district"));

        int geolocationX = 0, geolocationY = 0;

        try {
            geolocationX = Integer.parseInt(req.getParameter("geolocation_x"));
            geolocationY = Integer.parseInt(req.getParameter("geolocation_y"));
        } catch (Exception NumberFormatException) {
            errorArray = new JsonArray();
            errorArray.add("Invalid geolocation data");
            Http.setResponse(resp, 400);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "invalid data", null, errorArray).flush();
            return;
        }

        try {
            Connection connection = DBConnectionPool.getInstance().getConnection();
            PreparedStatement statement;
            ResultSet resultSet;
            int hospitalId = 0;

            statement = connection.prepareStatement("INSERT INTO hospitals (name, user_id, district, geolocation_x, geolocation_y) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, name);
            statement.setInt(2, userId);
            statement.setInt(3, district);
            statement.setInt(4, geolocationX);
            statement.setInt(5, geolocationY);
            statement.executeUpdate();
            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                hospitalId = resultSet.getInt(1);
            }

            statement = connection.prepareStatement("INSERT INTO beds (hospital_id) VALUES (?),(?),(?),(?),(?),(?),(?),(?),(?),(?)");
            statement.setInt(1, hospitalId);
            statement.setInt(2, hospitalId);
            statement.setInt(3, hospitalId);
            statement.setInt(4, hospitalId);
            statement.setInt(5, hospitalId);
            statement.setInt(6, hospitalId);
            statement.setInt(7, hospitalId);
            statement.setInt(8, hospitalId);
            statement.setInt(9, hospitalId);
            statement.setInt(10, hospitalId);
            statement.executeUpdate();

            connection.close();

            JsonObject dataObject = new JsonObject();
            dataObject.addProperty("hospital_id", hospitalId);

            resp = Http.setResponse(resp, 200);
            Http.getWriter(resp.getWriter(), HttpStatus.SUCCESS.getStatus(), "hospital added", dataObject, null).flush();
        } catch (Exception e) {
            errorArray = new JsonArray();
            errorArray.add("Database connection failed");

            resp = Http.setResponse(resp, 500);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "server error", null, errorArray).flush();
        }
    }

    /**
     * Update existing hospital
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonArray errorArray;

        errorArray = new JsonArray();
        if (req.getParameter("id").isEmpty()) {
            errorArray.add("Hospital id is required");

            Http.setResponse(resp, 403);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "forbidden", null, errorArray).flush();
            return;
        }

        errorArray = new JsonArray();
        if (req.getParameter("name").isEmpty()) {
            errorArray.add("Name is required");
        }
        if (req.getParameter("user_id").isEmpty()) {
            errorArray.add("Director is required");
        }
        if (req.getParameter("district").isEmpty()) {
            errorArray.add("District is required");
        }
        if (req.getParameter("geolocation_x").isEmpty()) {
            errorArray.add("Geolocation X is required");
        }
        if (req.getParameter("geolocation_y").isEmpty()) {
            errorArray.add("Geolocation Y is required");
        }

        if (errorArray.size() > 0) {
            Http.setResponse(resp, 400);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "invalid data", null, errorArray).flush();
            return;
        }

        String name = req.getParameter("name");
        int hospitalId = Integer.parseInt(req.getParameter("id"));
        int userId = Integer.parseInt(req.getParameter("user_id"));
        int district = Integer.parseInt(req.getParameter("district"));

        int geolocationX = 0, geolocationY = 0;

        try {
            geolocationX = Integer.parseInt(req.getParameter("geolocation_x"));
            geolocationY = Integer.parseInt(req.getParameter("geolocation_y"));
        } catch (Exception NumberFormatException) {
            errorArray = new JsonArray();
            errorArray.add("Invalid geolocation data");
            Http.setResponse(resp, 400);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "invalid data", null, errorArray).flush();
            return;
        }

        try {
            Connection connection = DBConnectionPool.getInstance().getConnection();
            PreparedStatement statement;
            ResultSet resultSet;

            statement = connection.prepareStatement("UPDATE hospitals SET name=?, user_id=?, district=?, geolocation_x=?, geolocation_y=? WHERE id=?");
            statement.setString(1, name);
            statement.setInt(2, userId);
            statement.setInt(3, district);
            statement.setInt(4, geolocationX);
            statement.setInt(5, geolocationY);
            statement.setInt(6, hospitalId);
            statement.executeUpdate();

            connection.close();

            JsonObject dataObject = new JsonObject();
            dataObject.addProperty("hospital_id", hospitalId);

            resp = Http.setResponse(resp, 200);
            Http.getWriter(resp.getWriter(), HttpStatus.SUCCESS.getStatus(), "hospital updated", dataObject, null).flush();
        } catch (Exception e) {
            errorArray = new JsonArray();
            errorArray.add("Database connection failed");

            resp = Http.setResponse(resp, 500);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "server error", null, errorArray).flush();
        }
    }

    /**
     * Delete existing hospital if no patient is admitted currently
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonArray errorArray;

        errorArray = new JsonArray();
        if (req.getParameter("id").isEmpty()) {
            errorArray.add("Hospital id is required");

            Http.setResponse(resp, 403);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "forbidden", null, errorArray).flush();
            return;
        }

        try {
            Connection connection = DBConnectionPool.getInstance().getConnection();
            PreparedStatement statement;

            int hospitalId = Integer.parseInt(req.getParameter("id"));

            statement = connection.prepareStatement("SELECT COUNT(*) FROM beds WHERE hospital_id=? and serial_no IS NOT NULL");
            statement.setInt(1, hospitalId);
            if (DbFunctions.count(statement.executeQuery()) == 0) {
                statement = connection.prepareStatement("DELETE FROM beds WHERE hospital_id=?");
                statement.setInt(1, hospitalId);
                statement.executeUpdate();

                statement = connection.prepareStatement("DELETE FROM hospitals WHERE id=?");
                statement.setInt(1, hospitalId);
                statement.executeUpdate();

                resp = Http.setResponse(resp, 200);
                Http.getWriter(resp.getWriter(), HttpStatus.SUCCESS.getStatus(), "hospital deleted", null, null).flush();
            } else {
                errorArray = new JsonArray();
                errorArray.add("There are patients in the hospital");

                resp = Http.setResponse(resp, 400);
                Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "invalid data", null, errorArray).flush();
            }
        } catch (Exception e) {
            errorArray = new JsonArray();
            errorArray.add("Database connection failed");

            resp = Http.setResponse(resp, 500);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "server error", null, errorArray).flush();
        }
    }
}
