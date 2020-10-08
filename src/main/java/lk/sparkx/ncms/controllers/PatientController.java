package lk.sparkx.ncms.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lk.sparkx.ncms.enums.HttpStatus;
import lk.sparkx.ncms.enums.PatientStatus;
import lk.sparkx.ncms.enums.Role;
import lk.sparkx.ncms.helpers.Database;
import lk.sparkx.ncms.helpers.DbFunctions;
import lk.sparkx.ncms.helpers.Http;
import lk.sparkx.ncms.models.Hospital;
import lk.sparkx.ncms.models.Patient;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.sql.Date;
import java.util.*;

@WebServlet(name = "PatientController")
public class PatientController extends HttpServlet {

    /**
     * Get patient information
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

        Patient patient = new Patient(Integer.parseInt(req.getParameter("id")));
        patient.loadModel();

        Http.setResponse(resp, 200);
        Http.getWriter(resp.getWriter(), HttpStatus.SUCCESS.getStatus(), "loaded", patient.serialize(), null).flush();
    }

    /**
     * Register new patient in NCMS
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
        if (req.getParameter("email").isEmpty()) {
            errorArray.add("Email is required");
        }
        if (req.getParameter("contact_number").isEmpty()) {
            errorArray.add("Contact number is required");
        }
        if (req.getParameter("district").isEmpty()) {
            errorArray.add("District is required");
        }
        if (req.getParameter("password").isEmpty()) {
            errorArray.add("Password is required");
        }
        if (req.getParameter("geolocation_x").isEmpty()) {
            errorArray.add("Geolocation X is required");
        }
        if (req.getParameter("geolocation_y").isEmpty()) {
            errorArray.add("Geolocation Y is required");
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
        String contactNumber = req.getParameter("contact_number");
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

        int userId = 0, patinetId = 0;

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
                statement.setInt(4, Role.USER.getRole());
                statement.executeUpdate();
                resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) {
                    userId = resultSet.getInt(1);
                }

                statement = connection.prepareStatement("INSERT INTO patients (user_id, geolocation_x, geolocation_y, contact_number, district, status, register_date) VALUES (?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                statement.setInt(1, userId);
                statement.setInt(2, geolocationX);
                statement.setInt(3, geolocationY);
                statement.setString(4, contactNumber);
                statement.setInt(5, district);
                statement.setInt(6, PatientStatus.IN_QUEUE.getStatus());
                statement.setDate(7, new java.sql.Date(new java.util.Date().getTime()));
                statement.executeUpdate();

                resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) {
                    patinetId = resultSet.getInt(1);
                }

                this.allocateBeds();

                JsonObject dataObject = new JsonObject();
                dataObject.addProperty("user_id", userId);
                dataObject.addProperty("patient_id", patinetId);

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

    /**
     * Update patient decease level
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
            errorArray.add("Patient id is required");

            Http.setResponse(resp, 403);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "forbidden", null, errorArray).flush();
            return;
        }

        errorArray = new JsonArray();
        if (req.getParameter("decease_level").isEmpty()) {
            errorArray.add("Patient decease level is required");
        }
        if (errorArray.size() > 0) {
            Http.setResponse(resp, 400);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "invalid data", null, errorArray).flush();
            return;
        }

        try {
            Connection connection = Database.open();
            PreparedStatement statement = connection.prepareStatement("UPDATE patients SET decease_level=? WHERE id=?");
            statement.setInt(1, Integer.parseInt(req.getParameter("decease_level")));
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

    /**
     * Discharge patient form hospital
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
            errorArray.add("Patient id is required");

            Http.setResponse(resp, 403);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "forbidden", null, errorArray).flush();
            return;
        }

        try {
            Connection connection = Database.open();
            PreparedStatement statement;

            int patientId = Integer.parseInt(req.getParameter("id"));

            statement = connection.prepareStatement("UPDATE patients SET status=?, discharged_date=? WHERE id=?");
            statement.setInt(1, 2);
            statement.setDate(2, new java.sql.Date(new java.util.Date().getTime()));
            statement.setInt(3, patientId);
            statement.executeUpdate();

            Patient patient = new Patient(patientId);
            patient.loadModel();

            statement = connection.prepareStatement("UPDATE beds SET serial_no=null WHERE serial_no=?");
            statement.setString(1, patient.getSerialNo());
            statement.executeUpdate();

            this.allocateBeds();

            Http.setResponse(resp, 200);
            Http.getWriter(resp.getWriter(), HttpStatus.SUCCESS.getStatus(), "updated", null, null).flush();
        } catch (Exception exception) {
            errorArray = new JsonArray();
            errorArray.add("Database connection failed");

            resp = Http.setResponse(resp, 500);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "server error", null, errorArray).flush();
        }
    }

    /**
     * Check for patients is the queue and add to hospital
     */
    private void allocateBeds() {
        try {
            Connection connection = Database.open();
            PreparedStatement statement;
            ResultSet resultSet;

            ArrayList<Hospital> hospitals = new ArrayList<Hospital>();
            statement = connection.prepareStatement("SELECT * FROM hospitals");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                hospitals.add(new Hospital(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getInt("user_id"), resultSet.getInt("district"), resultSet.getInt("geolocation_x"), resultSet.getInt("geolocation_y")));
            }

            statement = connection.prepareStatement("SELECT * FROM patients WHERE status=0 ORDER BY id DESC");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                HashMap<Integer, Double> hospitalDistances = new HashMap<Integer, Double>();

                for (int i = 0; i < hospitals.size(); i++) {
                    Hospital hospital = hospitals.get(i);

                    if (hospital.getAvailableBedsCount() > 0) {
                        hospitalDistances.put(hospital.getId(), hospital.getDistanceToHospital(resultSet.getInt("geolocation_x"), resultSet.getInt("geolocation_y")));
                    }
                }

                Map.Entry<Integer, Double> min = null;
                for (Map.Entry<Integer, Double> entry : hospitalDistances.entrySet()) {
                    if (min == null || min.getValue() > entry.getValue()) {
                        min = entry;
                    }
                }

                if (min != null) {
                    UUID uuid = UUID.randomUUID();

                    statement = connection.prepareStatement("UPDATE patients SET serial_no=?, admission_date=?, status=? WHERE id=?");
                    statement.setString(1, uuid.toString());
                    statement.setDate(2, new java.sql.Date(new java.util.Date().getTime()));
                    statement.setInt(3, PatientStatus.ADMITTED.getStatus());
                    statement.setInt(4, resultSet.getInt("id"));
                    statement.executeUpdate();

                    statement = connection.prepareStatement("UPDATE beds SET serial_no=? WHERE hospital_id=? and serial_no IS NULL LIMIT 1");
                    statement.setString(1, uuid.toString());
                    statement.setInt(2, min.getKey());
                    statement.executeUpdate();
                }

                connection.close();
            }
        } catch (Exception exception) {
            // Add system log - For administrative purpose only
        }
    }
}
