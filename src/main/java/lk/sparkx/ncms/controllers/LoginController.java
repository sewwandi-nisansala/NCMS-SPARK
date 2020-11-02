package lk.sparkx.ncms.controllers;

import com.google.gson.JsonArray;
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
import java.sql.ResultSet;
import java.util.Base64;

/**
 * Handle user login details
 */
@WebServlet(name = "LoginController")
public class LoginController extends HttpServlet {

    /**
     * Login user to the system
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
        if (req.getParameter("email").isEmpty()) {
            errorArray.add("Email is required");
        }
        if (req.getParameter("password").isEmpty()) {
            errorArray.add("Password is required");
        }

        if (errorArray.size() > 0) {
            Http.setResponse(resp, 400);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "invalid data", null, errorArray).flush();
            return;
        }

        String email = req.getParameter("email");
        String password = Base64.getEncoder().encodeToString(req.getParameter("password").getBytes());

        try {
            Connection connection = DBConnectionPool.getInstance().getConnection();
            PreparedStatement statement;
            ResultSet resultSet;

            statement = connection.prepareStatement("SELECT Count(*) AS count, id,role FROM users WHERE password=? and email=?");
            statement.setString(1, password);
            statement.setString(2, email);

            if (DbFunctions.count(statement.executeQuery()) == 1) {
                resultSet = statement.executeQuery();
                int id = 0;
                while (resultSet.next()) {
                    id = resultSet.getInt("id");
                }

                User user = new User(id);
                user.loadModel();

                Http.setResponse(resp, 200);
                Http.getWriter(resp.getWriter(), HttpStatus.SUCCESS.getStatus(), "loaded", user.serialize(), null).flush();
            } else {
                errorArray = new JsonArray();
                errorArray.add("Email or Password is wrong");

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
