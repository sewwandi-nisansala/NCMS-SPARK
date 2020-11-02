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

@WebServlet(name = "UserListController")
public class UserListController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonArray errorArray;

        new JsonArray();

        try {
            Connection connection = DBConnectionPool.getInstance().getConnection();
            PreparedStatement statement;
            ResultSet resultSet;
            JsonArray users = new JsonArray();

            statement = connection.prepareStatement("SELECT users.*, hospitals.name as hospital FROM users LEFT JOIN doctors ON doctors.user_id = users.id LEFT JOIN hospitals ON hospitals.id = doctors.hospital_id WHERE users.role=2 or users.role=1");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                JsonObject user = new JsonObject();
                user.addProperty("id", resultSet.getString("id"));
                user.addProperty("name", resultSet.getString("name"));
                user.addProperty("email", resultSet.getString("email"));
                user.addProperty("hospital", resultSet.getString("hospital"));
                user.addProperty("role", resultSet.getInt("role"));
                users.add(user);
            }

            Http.setResponse(resp, 200);
            Http.getWriter(resp.getWriter(), HttpStatus.SUCCESS.getStatus(), "loaded", users, null).flush();

            connection.close();
        } catch (Exception exception) {
            errorArray = new JsonArray();
            errorArray.add("Database connection failed");

            resp = Http.setResponse(resp, 500);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "server error", null, errorArray).flush();
        }
    }
}
