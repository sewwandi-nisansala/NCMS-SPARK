package lk.sparkx.ncms.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lk.sparkx.ncms.enums.HttpStatus;
import lk.sparkx.ncms.helpers.DBConnectionPool;
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

/**
 * Handle user registrations based on roles
 */
@WebServlet(name = "RegisterController")
public class RegisterController extends HttpServlet {

    /**
     * Register user in the system
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
        if (req.getParameter("role").isEmpty()) {
            errorArray.add("Role is required");

            Http.setResponse(resp, 403);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "forbidden", null, errorArray).flush();
            return;
        }

        int role = Integer.parseInt(req.getParameter("role"));

        errorArray = new JsonArray();
        switch (role) {
            case 0:
                errorArray = validatePatient(req);
                break;

            case 1:
                errorArray = validateMoh(req);
                break;

            case 2:
                errorArray = validateDoctor(req);
                break;
        }

        if (errorArray.size() > 0) {
            Http.setResponse(resp, 400);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "invalid data", null, errorArray).flush();
            return;
        }

        try {
            Connection connection = DBConnectionPool.getInstance().getConnection();
            PreparedStatement statement;

            statement = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE email=?");
            statement.setString(1, req.getParameter("email"));
            if (DbFunctions.count(statement.executeQuery()) == 0) {
                int userId = 0;
                switch (role) {
                    case 0:
                        userId = addPatient(req);
                        break;

                    case 1:
                        userId = addMoh(req);
                        break;

                    case 2:
                        userId = addDoctor(req);
                        break;
                }

                if (userId > 0) {
                    JsonObject dataObject = new JsonObject();
                    dataObject.addProperty("userId", userId);
                    dataObject.addProperty("role", role);

                    resp = Http.setResponse(resp, 200);
                    Http.getWriter(resp.getWriter(), HttpStatus.SUCCESS.getStatus(), "user added", dataObject, null).flush();
                } else {
                    errorArray = new JsonArray();
                    errorArray.add("Database connection failed");

                    resp = Http.setResponse(resp, 500);
                    Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "server error", null, errorArray).flush();
                }
            } else {
                errorArray = new JsonArray();
                errorArray.add("Email already in the system");

                resp = Http.setResponse(resp, 400);
                Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "invalid data", null, errorArray).flush();
            }

            connection.close();
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
        if (req.getParameter("password").isEmpty()) {
            errorArray.add("Password is required");
        }
        if (!req.getParameter("password").equals(req.getParameter("c_password"))) {
            errorArray.add("Password and confirm password does not match");
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
        if (req.getParameter("password").isEmpty()) {
            errorArray.add("Password is required");
        }
        if (!req.getParameter("password").equals(req.getParameter("c_password"))) {
            errorArray.add("Password and confirm password does not match");
        }
        if (req.getParameter("hospital_id").isEmpty()) {
            errorArray.add("Hospital is required");
        }

        return errorArray;
    }

    /**
     * Validate inputs of Patient
     *
     * @param req
     * @return
     */
    private JsonArray validatePatient(HttpServletRequest req) {
        JsonArray errorArray = new JsonArray();

        if (req.getParameter("name").isEmpty()) {
            errorArray.add("Name is required");
        }
        if (req.getParameter("email").isEmpty()) {
            errorArray.add("Email is required");
        }
        if (req.getParameter("password").isEmpty()) {
            errorArray.add("Password is required");
        }
        if (!req.getParameter("password").equals(req.getParameter("c_password"))) {
            errorArray.add("Password and confirm password does not match");
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
        if (req.getParameter("contact_number").isEmpty()) {
            errorArray.add("Contact number is required");
        }

        return errorArray;
    }

    /**
     * Add patient to database
     *
     * @param req
     * @return
     */
    private int addPatient(HttpServletRequest req) {
        try {
            Connection connection = DBConnectionPool.getInstance().getConnection();
            PreparedStatement statement;
            ResultSet resultSet;
            int userId = 0;

            statement = connection.prepareStatement("INSERT INTO users (name, email, password, role) VALUES (?,?,?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, req.getParameter("name"));
            statement.setString(2, req.getParameter("email"));
            statement.setString(3, Base64.getEncoder().encodeToString(req.getParameter("password").getBytes()));
            statement.setInt(4, 0);
            statement.executeUpdate();
            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                userId = resultSet.getInt(1);
            }

            statement = connection.prepareStatement("INSERT INTO patients (user_id, geolocation_x, geolocation_y, contact_number, district, register_date) VALUES (?,?,?,?,?,?)");
            statement.setInt(1, userId);
            statement.setInt(2, Integer.parseInt(req.getParameter("geolocation_x")));
            statement.setInt(3, Integer.parseInt(req.getParameter("geolocation_y")));
            statement.setString(4, req.getParameter("contact_number"));
            statement.setInt(5, Integer.parseInt(req.getParameter("district")));
            statement.setDate(6, new java.sql.Date(new java.util.Date().getTime()));
            statement.executeUpdate();

            AllocateBedsController allocateBedsController = new AllocateBedsController();
            allocateBedsController.allocateBeds();

            connection.close();

            return userId;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Add moh user to database
     * @param req
     * @return
     */
    private int addMoh(HttpServletRequest req) {
        try {
            Connection connection = DBConnectionPool.getInstance().getConnection();
            PreparedStatement statement;
            ResultSet resultSet;
            int userId = 0;

            statement = connection.prepareStatement("INSERT INTO users (name, email, password, role) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, req.getParameter("name"));
            statement.setString(2, req.getParameter("email"));
            statement.setString(3, Base64.getEncoder().encodeToString(req.getParameter("password").getBytes()));
            statement.setInt(4, 1);
            statement.executeUpdate();
            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                userId = resultSet.getInt(1);
            }

            connection.close();

            return userId;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Add doctor to database
     * @param req
     * @return
     */
    private int addDoctor(HttpServletRequest req) {
        try {
            Connection connection = DBConnectionPool.getInstance().getConnection();
            PreparedStatement statement;
            ResultSet resultSet;
            int userId = 0;

            statement = connection.prepareStatement("INSERT INTO users (name, email, password, role) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, req.getParameter("name"));
            statement.setString(2, req.getParameter("email"));
            statement.setString(3, Base64.getEncoder().encodeToString(req.getParameter("password").getBytes()));
            statement.setInt(4, 2);
            statement.executeUpdate();
            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                userId = resultSet.getInt(1);
            }

            statement = connection.prepareStatement("INSERT INTO doctors (user_id, hospital_id) VALUES (?,?)");
            statement.setInt(1, userId);
            statement.setInt(2, Integer.parseInt(req.getParameter("hospital_id")));
            statement.executeUpdate();

            connection.close();

            return userId;
        } catch (Exception e) {
            return 0;
        }
    }

}
