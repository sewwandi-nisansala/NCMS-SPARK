package lk.sparkx.ncms.controller;

import com.google.gson.JsonObject;
import lk.sparkx.ncms.ObjectRepo;
import lk.sparkx.ncms.dao.Doctor;
import lk.sparkx.ncms.dao.Hospital;
import lk.sparkx.ncms.db.DBConnectionPool;
import lk.sparkx.ncms.repository.HospitalRepository;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.List;

@WebServlet(name = "HospitalServlet")
public class HospitalServlet extends HttpServlet
{

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String district = req.getParameter("district");

        Connection connection = null;
        PreparedStatement statement = null;
        int result = 0;

        try {
            connection = DBConnectionPool.getInstance().getConnection();

            //PreparedStatement statement;
            ResultSet resultSet;

            statement = connection.prepareStatement("SELECT * FROM hospital WHERE district=?");
            statement.setString(1, district);
            System.out.println(statement);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                int locationX = resultSet.getInt("location_x");
                int locationY = resultSet.getInt("location_y");
                Date buildDate = resultSet.getDate("build_date");

                PrintWriter printWriter = resp.getWriter();

                printWriter.println("Id: " + id);
                printWriter.println("Name: " + name);
                printWriter.println("District: " + district);
                printWriter.println("Location_X: " + locationX);
                printWriter.println("Location_Y: " + locationY);
                printWriter.println("Build Date: " + buildDate);
                System.out.println("doGet doctor success");

            }
            connection.close();

        } catch (Exception exception) {

        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String id = req.getParameter("id");
        String name = req.getParameter("name");
        String district = req.getParameter("district");
        int locationX = Integer.parseInt(req.getParameter("locationX"));
        int locationY = Integer.parseInt(req.getParameter("locationY"));
        java.util.Date dateBuild = new java.util.Date();
        Date buildDate = new Date(dateBuild.getTime());

        Hospital hospital = new Hospital();
        hospital.setId(id);
        hospital.setName(name);
        hospital.setDistrict(district);
        hospital.setLocationX(locationX);
        hospital.setLocationY(locationY);
        hospital.setBuildDate(buildDate);

        HospitalRepository hospitalRepository = new HospitalRepository();
        String hospitalRegistered = hospitalRepository.registerHospital(hospital);

        if(hospitalRegistered.equals("SUCCESS"))   //On success, you can display a message to user on Home page
        {
            System.out.println("Success");
        }
        else   //On Failure, display a meaningful message to the User.
        {
            System.out.println("Failed");
        }

        try {
            hospitalRepository.registerHospital(hospital);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {

    }

    //discharge patient by director
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        Connection connection = null;
        PreparedStatement statement = null;

        String patientId = req.getParameter("id");
        String hospitalId = req.getParameter("hospital_id");

//        Doctor doctor = new Doctor();
//        doctor.dischargePatients(patientId, hospitalId);
//
//        Bed bed = new Bed();
//        bed.makeAvailable(patientId);

    }



}
