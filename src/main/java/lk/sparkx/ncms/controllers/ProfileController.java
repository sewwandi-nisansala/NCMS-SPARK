package lk.sparkx.ncms.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lk.sparkx.ncms.enums.HttpStatus;
import lk.sparkx.ncms.helpers.DBConnectionPool;
import lk.sparkx.ncms.helpers.DbFunctions;
import lk.sparkx.ncms.helpers.Http;
import lk.sparkx.ncms.models.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Base64;

/**
 * Manage logged user profile
 */
@WebServlet (name = "ProfileController")
public class ProfileController extends HttpServlet {

    /**
     * Get user profile
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

        Http.setResponse(resp, 200);
        Http.getWriter(resp.getWriter(), HttpStatus.SUCCESS.getStatus(), "loaded", user.serialize(), null).flush();
    }

    /**
     * Update user profile
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

        errorArray = new JsonArray();
        if (req.getParameter("name").isEmpty()) {
            errorArray.add("Name is required");
        }
        if (req.getParameter("email").isEmpty()) {
            errorArray.add("Email is required");
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

                connection.close();

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

            connection.close();
        } catch (Exception e) {
            errorArray = new JsonArray();
            errorArray.add("Database connection failed");

            resp = Http.setResponse(resp, 500);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "server error", null, errorArray).flush();
        }
    }

    /**
     * Update user password
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
            errorArray.add("User id is required");

            Http.setResponse(resp, 403);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "forbidden", null, errorArray).flush();
            return;
        }

        errorArray = new JsonArray();
        if (req.getParameter("password").isEmpty()) {
            errorArray.add("Password is required");
        }
        if (req.getParameter("old_password").isEmpty()) {
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
        String oldPassword = Base64.getEncoder().encodeToString(req.getParameter("old_password").getBytes());

        try {
            //Connection connection = Database.open();
            Connection connection = DBConnectionPool.getInstance().getConnection();
            PreparedStatement statement;

            statement = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE password=? and id=?");
            statement.setString(1, oldPassword);
            statement.setInt(2, id);
            if (DbFunctions.count(statement.executeQuery()) == 1) {
                statement = connection.prepareStatement("UPDATE users SET password=? WHERE id=?");
                statement.setString(1, password);
                statement.setInt(2, id);
                statement.executeUpdate();

                JsonObject dataObject = new JsonObject();
                dataObject.addProperty("user_id", id);

                resp = Http.setResponse(resp, 200);
                Http.getWriter(resp.getWriter(), HttpStatus.SUCCESS.getStatus(), "password updated", dataObject, null).flush();
            }else{
                errorArray = new JsonArray();
                errorArray.add("Old password is wrong");

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
}
