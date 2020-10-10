package lk.sparkx.ncms.controller;

import lk.sparkx.ncms.dao.Doctor;
import lk.sparkx.ncms.db.DBConnectionPool;
import lk.sparkx.ncms.repository.DoctorRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;


@WebServlet(name = "DoctorServlet")
public class DoctorServlet extends HttpServlet {


    @Override
    public void init() {
        DoctorRepository doctorRepository = new DoctorRepository();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        String name = req.getParameter("name");
        String hospitalId = req.getParameter("hospitalId");
        Boolean isDirector = Boolean.valueOf(req.getParameter("isDirector"));

        Doctor doctor= new Doctor();
        doctor.setId(id);
        doctor.setName(name);
        doctor.setHospitalId(hospitalId);
        doctor.setDirector(isDirector);

        DoctorRepository doctorRepo = new DoctorRepository();
        String doctorRegistered = doctorRepo.regDoctor(doctor);
        if(doctorRegistered.equals("SUCCESS"))   //On success, you can display a message to user on Home page
        {
            System.out.println("Success");
        } else   //On Failure, display a meaningful message to the User.
        {
            System.out.println("Failed");
        }

        try {
            doctorRepo.regDoctor(doctor);
        } catch (Exception e) {
            /* TODO Auto-generated catch block */
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doGet(req, resp);
        Doctor doctor = new Doctor();
        doctor.loadData();
        System.out.println("successfully loaded");

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id;
        id = Integer.parseInt(req.getParameter("id"));
        String name = req.getParameter("name");
        String hospitalId = req.getParameter("hospital_id");
        boolean isDirector = Boolean.parseBoolean(req.getParameter("is_director"));

        try {
            Connection con = DBConnectionPool.getInstance().getConnection();

            int result;
            try (PreparedStatement pstmt = con.prepareStatement("UPDATE doctor SET  id=?,name=?, hospital_id=?, is_director=? WHERE id=?")) {

                pstmt.setInt(1, id);
                pstmt.setString(2, name);
                pstmt.setString(3, hospitalId);
                pstmt.setBoolean(4, isDirector);

                con.close();

                result = pstmt.executeUpdate();
            }
            if (result != 0){
                System.out.println("Successfully updated");
            }else{
                System.out.println("Update failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            Connection con = DBConnectionPool.getInstance().getConnection();

            int doctor_id;
            doctor_id = Integer.parseInt(req.getParameter("id"));

            try (PreparedStatement pstmt = con.prepareStatement("DELETE FROM doctor WHERE id=?")) {
                pstmt.setInt(1, doctor_id);
                pstmt.executeUpdate();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

}
