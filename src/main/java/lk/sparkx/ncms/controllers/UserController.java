package lk.sparkx.ncms.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lk.sparkx.ncms.enums.HttpStatus;
import lk.sparkx.ncms.enums.Role;
import lk.sparkx.ncms.helpers.DBConnectionPool;
import lk.sparkx.ncms.helpers.DbFunctions;
import lk.sparkx.ncms.helpers.Http;
import lk.sparkx.ncms.models.Doctor;
import lk.sparkx.ncms.models.Patient;
import lk.sparkx.ncms.models.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;

/**
 * Control user operations (only attributes which are common to all user types)
 */
@WebServlet(name = "UserController")
public class UserController extends HttpServlet {

    /**
     * Get user details
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
            errorArray.add("User id is required");

            Http.setResponse(resp, 403);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "forbidden", null, errorArray).flush();
            return;
        }

        User user = new User(Integer.parseInt(req.getParameter("id")));
        user.loadModel();

        JsonObject dataObject = null;

        switch (user.getRole()){
            case DOCTOR:
                Doctor doctor = new Doctor();
                doctor.loadModel(user.getId());
                dataObject = doctor.serialize();
                break;

            case MOH:
                dataObject = user.serialize();
                break;

            case USER:
                Patient patient = new Patient();
                patient.loadModel(user.getId());
                dataObject = patient.serialize();
                break;
        }

        Http.setResponse(resp, 200);
        Http.getWriter(resp.getWriter(), HttpStatus.SUCCESS.getStatus(), "loaded", dataObject, null).flush();
    }

    /**
     * Update user information
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
        if (req.getParameter("id").isEmpty()) {
            errorArray.add("User id is required");

            Http.setResponse(resp, 403);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "forbidden", null, errorArray).flush();
            return;
        }

        User user = new User(Integer.parseInt(req.getParameter("id")));
        user.loadModel();

        errorArray = new JsonArray();
        switch (user.getRole()) {
            case MOH:
                errorArray = validateMoh(req);
                break;

            case DOCTOR:
                errorArray = validateDoctor(req);
                break;
        }

        if (errorArray.size() > 0) {
            Http.setResponse(resp, 400);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "invalid data", null, errorArray).flush();
            return;
        }

        int id = Integer.parseInt(req.getParameter("id"));
        String name = req.getParameter("name");
        String email = req.getParameter("email");

        try {
            Connection connection = DBConnectionPool.getInstance().getConnection();
            PreparedStatement statement;

            statement = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE email=? and id<>?");
            statement.setString(1, email);
            statement.setInt(2, id);
            if (DbFunctions.count(statement.executeQuery()) == 0) {
                statement = connection.prepareStatement("UPDATE users SET name=?, email=? WHERE id=?");
                statement.setString(1, name);
                statement.setString(2, email);
                statement.setInt(3, id);
                statement.executeUpdate();

                // User based updates
                switch (user.getRole()){
                    case DOCTOR:
                        updateDoctor(req);
                }

                JsonObject dataObject = new JsonObject();
                dataObject.addProperty("user_id", id);

                resp = Http.setResponse(resp, 200);
                Http.getWriter(resp.getWriter(), HttpStatus.SUCCESS.getStatus(), "user updated", dataObject, null).flush();
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
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonArray errorArray;

        errorArray = new JsonArray();
        if (req.getParameter("id").isEmpty()) {
            errorArray.add("User id is required");

            Http.setResponse(resp, 403);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "forbidden", null, errorArray).flush();
            return;
        }

        User user = new User(Integer.parseInt(req.getParameter("id")));
        user.loadModel();

        try {
            Connection connection = DBConnectionPool.getInstance().getConnection();
            PreparedStatement statement;

            int userId = Integer.parseInt(req.getParameter("id"));

            if (user.getRole() == Role.MOH){
                statement = connection.prepareStatement("DELETE FROM users WHERE id=?");
                statement.setInt(1, userId);
                statement.executeUpdate();

                resp = Http.setResponse(resp, 200);
                Http.getWriter(resp.getWriter(), HttpStatus.SUCCESS.getStatus(), "moh deleted", null, null).flush();
            }

            if (user.getRole() == Role.DOCTOR){
                statement = connection.prepareStatement("SELECT COUNT(*) FROM hospitals WHERE user_id=?");
                statement.setInt(1, userId);
                if (DbFunctions.count(statement.executeQuery()) == 0) {
                    statement = connection.prepareStatement("DELETE FROM doctors WHERE user_id=?");
                    statement.setInt(1, userId);
                    statement.executeUpdate();

                    statement = connection.prepareStatement("DELETE FROM users WHERE id=?");
                    statement.setInt(1, userId);
                    statement.executeUpdate();

                    resp = Http.setResponse(resp, 200);
                    Http.getWriter(resp.getWriter(), HttpStatus.SUCCESS.getStatus(), "hospital deleted", null, null).flush();
                } else {
                    errorArray = new JsonArray();
                    errorArray.add("Doctor is assinged to a hospital. Please remove him as director and retry");

                    resp = Http.setResponse(resp, 400);
                    Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "doctor data", null, errorArray).flush();
                }
            }

            if (user.getRole() == Role.USER){
                statement = connection.prepareStatement("SELECT COUNT(*) FROM patients WHERE user_id=? AND serial_no IS NOT NULL AND discharged_date IS NULL");
                statement.setInt(1, userId);
                if (DbFunctions.count(statement.executeQuery()) == 0) {
                    statement = connection.prepareStatement("DELETE FROM patients WHERE user_id=?");
                    statement.setInt(1, userId);
                    statement.executeUpdate();

                    statement = connection.prepareStatement("DELETE FROM users WHERE id=?");
                    statement.setInt(1, userId);
                    statement.executeUpdate();

                    resp = Http.setResponse(resp, 200);
                    Http.getWriter(resp.getWriter(), HttpStatus.SUCCESS.getStatus(), "patient deleted", null, null).flush();
                } else {
                    errorArray = new JsonArray();
                    errorArray.add("Patient is already admitted to a hospital. Please discharge him before delete");

                    resp = Http.setResponse(resp, 400);
                    Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "invalid data", null, errorArray).flush();
                }
            }


        } catch (Exception e) {
            errorArray = new JsonArray();
            errorArray.add("Database connection failed");

            resp = Http.setResponse(resp, 500);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "server error", null, errorArray).flush();
        }
    }

    /**
     * Update user password
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Override -> 0 - Check for old password. otherwise ignore!

        JsonArray errorArray;

        errorArray = new JsonArray();
        if (req.getParameter("id").isEmpty() || req.getParameter("override").isEmpty()) {
            errorArray.add("User id is required");

            Http.setResponse(resp, 403);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "forbidden", null, errorArray).flush();
            return;
        }

        int override = Integer.parseInt(req.getParameter("override"));

        errorArray = new JsonArray();
        if (req.getParameter("password").isEmpty()) {
            errorArray.add("Password is required");
        }
        if (override == 0 && req.getParameter("old_password").isEmpty()) {
            errorArray.add("Old password is required");
        }
        if (!req.getParameter("password").equals(req.getParameter("c_password"))) {
            errorArray.add("Password and confirm password does not match");
        }

        if (errorArray.size() > 0) {
            Http.setResponse(resp, 400);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "invalid data", null, errorArray).flush();
            return;
        }

        int id = Integer.parseInt(req.getParameter("id"));
        String password = Base64.getEncoder().encodeToString(req.getParameter("password").getBytes());
        String oldPassword = "";

        if (override == 0){
            oldPassword = Base64.getEncoder().encodeToString(req.getParameter("old_password").getBytes());
        }

        try {
            Connection connection = DBConnectionPool.getInstance().getConnection();
            PreparedStatement statement;

            if (override == 0){
                statement = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE password=? and id=?");
                statement.setString(1, oldPassword);
                statement.setInt(2, id);
                if (DbFunctions.count(statement.executeQuery()) != 1) {
                    errorArray = new JsonArray();
                    errorArray.add("Old password is wrong");

                    resp = Http.setResponse(resp, 400);
                    Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "invalid data", null, errorArray).flush();
                    return;
                }
            }

            statement = connection.prepareStatement("UPDATE users SET password=? WHERE id=?");
            statement.setString(1, password);
            statement.setInt(2, id);
            statement.executeUpdate();

            connection.close();

            JsonObject dataObject = new JsonObject();
            dataObject.addProperty("user_id", id);

            resp = Http.setResponse(resp, 200);
            Http.getWriter(resp.getWriter(), HttpStatus.SUCCESS.getStatus(), "password updated", dataObject, null).flush();
        } catch (Exception e) {
            errorArray = new JsonArray();
            errorArray.add("Database connection failed");

            resp = Http.setResponse(resp, 500);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "server error", null, errorArray).flush();
        }
    }


    /**
     * Validate inputs of Moh
     *
     * @param req
     * @return
     */
    private JsonArray validateMoh(HttpServletRequest req) {
        JsonArray errorArray = new JsonArray();

        if (req.getParameter("name").isEmpty()) {
            errorArray.add("Name is required");
        }
        if (req.getParameter("email").isEmpty()) {
            errorArray.add("Email is required");
        }

        return errorArray;
    }

    /**
     * Validate inputs of Doctor
     *
     * @param req
     * @return
     */
    private JsonArray validateDoctor(HttpServletRequest req) {
        JsonArray errorArray = new JsonArray();

        if (req.getParameter("name").isEmpty()) {
            errorArray.add("Name is required");
        }
        if (req.getParameter("email").isEmpty()) {
            errorArray.add("Email is required");
        }
        if (req.getParameter("hospital_id").isEmpty()) {
            errorArray.add("Hospital is required");
        }

        return errorArray;
    }

    /**
     * Add moh user to database
     * @param req
     * @return
     */
    private void updateDoctor(HttpServletRequest req) {
        try {
            Connection connection = DBConnectionPool.getInstance().getConnection();
            PreparedStatement statement;
            ResultSet resultSet;
            int userId = 0;

            statement = connection.prepareStatement("UPDATE doctors SET hospital_id=? WHERE user_id=?");
            statement.setString(1, req.getParameter("hospital_id"));
            statement.setString(2, req.getParameter("id"));
            statement.executeUpdate();

            connection.close();
        } catch (Exception e) {

        }
    }

}
