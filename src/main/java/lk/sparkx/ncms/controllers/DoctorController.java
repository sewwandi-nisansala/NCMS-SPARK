package lk.sparkx.ncms.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lk.sparkx.ncms.enums.HttpStatus;
import lk.sparkx.ncms.enums.Role;
import lk.sparkx.ncms.helpers.Database;
import lk.sparkx.ncms.helpers.DbFunctions;
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
import java.sql.Statement;
import java.util.Base64;

@WebServlet(name = "DoctorController")
public class DoctorController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonArray errorArray;

        errorArray = new JsonArray();
        if (req.getParameter("name").isEmpty()) {
            errorArray.add("Name is required");
        }
        if (req.getParameter("email").isEmpty()) {
            errorArray.add("Email is required");
        }
        if (req.getParameter("hospital_id").isEmpty()) {
            errorArray.add("Assinged hospital is required");
        }
        if (req.getParameter("password").isEmpty()) {
            errorArray.add("Password is required");
        }
        if (!req.getParameter("password").equals(req.getParameter("c_password"))) {
            errorArray.add("Password and confirm password does not match");
        }

        if (errorArray.size() > 0) {
            Http.setResponse(resp, 400);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "invalid data", null, errorArray).flush();
            return;
        }

        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        int hospitalId = Integer.parseInt(req.getParameter("hospital_id"));

        int userId = 0, doctorId = 0;

        try {
            Connection connection = Database.open();
            PreparedStatement statement;
            ResultSet resultSet;

            statement = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE email=?");
            statement.setString(1, email);
            if (DbFunctions.count(statement.executeQuery()) == 0) {
                statement = connection.prepareStatement("INSERT INTO users (name, email, password, role) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, name);
                statement.setString(2, email);
                statement.setString(3, Base64.getEncoder().encodeToString(password.getBytes()));
                statement.setInt(4, Role.DOCTOR.getRole());
                statement.executeUpdate();
                resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) {
                    userId = resultSet.getInt(1);
                }

                statement = connection.prepareStatement("INSERT INTO doctors (user_id, hospital_id) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
                statement.setInt(1, userId);
                statement.setInt(2, hospitalId);
                statement.executeUpdate();
                resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) {
                    doctorId = resultSet.getInt(1);
                }

                JsonObject dataObject = new JsonObject();
                dataObject.addProperty("user_id", userId);
                dataObject.addProperty("doctor_id", doctorId);

                resp = Http.setResponse(resp, 200);
                Http.getWriter(resp.getWriter(), HttpStatus.SUCCESS.getStatus(), "user added", dataObject, null).flush();
            } else {
                errorArray = new JsonArray();
                errorArray.add("Email already in the system");

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

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonArray errorArray;

        errorArray = new JsonArray();
        if (req.getParameter("id").isEmpty()) {
            errorArray.add("Doctor id is required");

            Http.setResponse(resp, 403);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "forbidden", null, errorArray).flush();
            return;
        }

        errorArray = new JsonArray();
        if (req.getParameter("hospital_id").isEmpty()) {
            errorArray.add("Hospital is required");
        }
        if (errorArray.size() > 0) {
            Http.setResponse(resp, 400);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "invalid data", null, errorArray).flush();
            return;
        }

        try {
            Connection connection = Database.open();
            PreparedStatement statement = connection.prepareStatement("UPDATE doctors SET hospital_id=? WHERE id=?");
            statement.setInt(1, Integer.parseInt(req.getParameter("hospital_id")));
            statement.setInt(2, Integer.parseInt(req.getParameter("id")));
            statement.executeUpdate();

            Http.setResponse(resp, 200);
            Http.getWriter(resp.getWriter(), HttpStatus.SUCCESS.getStatus(), "updated", null, null).flush();
        } catch (Exception exception) {
            errorArray = new JsonArray();
            errorArray.add("Database connection failed");

            resp = Http.setResponse(resp, 500);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "server error", null, errorArray).flush();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonArray errorArray;

        errorArray = new JsonArray();
        if (req.getParameter("id").isEmpty()) {
            errorArray.add("Doctor id is required");

            Http.setResponse(resp, 403);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "forbidden", null, errorArray).flush();
            return;
        }

        try {
            Connection connection = Database.open();
            PreparedStatement statement;

            int userId = Integer.parseInt(req.getParameter("id"));

            statement = connection.prepareStatement("SELECT COUNT(*) FROM hospitals WHERE user_id=?");
            statement.setInt(1, userId);
            if (DbFunctions.count(statement.executeQuery()) == 0) {
                statement = connection.prepareStatement("DELETE FROM doctors WHERE id=?");
                statement.setInt(1, userId);
                statement.executeUpdate();

                resp = Http.setResponse(resp, 200);
                Http.getWriter(resp.getWriter(), HttpStatus.SUCCESS.getStatus(), "deleted", null, null).flush();
            } else {
                errorArray = new JsonArray();
                errorArray.add("Doctor cannot be deleted until appointed as director of a hospital");

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
