package lk.sparkx.ncms.controllers;

import com.google.gson.JsonArray;
import lk.sparkx.ncms.enums.HttpStatus;
import lk.sparkx.ncms.helpers.DBConnectionPool;
import lk.sparkx.ncms.helpers.Http;
import lk.sparkx.ncms.models.Patient;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

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
        if (req.getParameter("id").isEmpty() && req.getParameter("ref").isEmpty()) {
            errorArray.add("User id is required");

            Http.setResponse(resp, 403);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "forbidden", null, errorArray).flush();
            return;
        }

        // Search using Patient ID -> ref not present OR Search using User ID -> ref=1
        Patient patient = null;
        int ref = Integer.parseInt(req.getParameter("ref"));
        if (ref == 0) {
            patient = new Patient(Integer.parseInt(req.getParameter("id")));
            patient.loadModel();
            patient.loadRelationalModels();
        }else{
            patient = new Patient();
            patient.loadModel(Integer.parseInt(req.getParameter("id")));
            patient.loadRelationalModels();
        }

        Http.setResponse(resp, 200);
        Http.getWriter(resp.getWriter(), HttpStatus.SUCCESS.getStatus(), "loaded", patient.serialize(), null).flush();
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
            Connection connection = DBConnectionPool.getInstance().getConnection();
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
            Connection connection = DBConnectionPool.getInstance().getConnection();
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

            AllocateBedsController allocateBedsController = new AllocateBedsController();
            allocateBedsController.allocateBeds();

            Http.setResponse(resp, 200);
            Http.getWriter(resp.getWriter(), HttpStatus.SUCCESS.getStatus(), "updated", null, null).flush();
        } catch (Exception exception) {
            errorArray = new JsonArray();
            errorArray.add("Database connection failed");

            resp = Http.setResponse(resp, 500);
            Http.getWriter(resp.getWriter(), HttpStatus.ERROR.getStatus(), "server error", null, errorArray).flush();
        }
    }
}
