package lk.sparkx.ncms.controller;

import lk.sparkx.ncms.dao.Doctor;
import lk.sparkx.ncms.repository.DoctorRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


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
        }
        else   //On Failure, display a meaningful message to the User.
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
        doctor.loadData(doctor);
        System.out.println("successfully loaded");
        String doctorId = req.getParameter("doctorId");

    }



}
